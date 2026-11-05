package com.oem.car.core.model

import com.oem.car.core.common.CarLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * ============================================================================
 * SocketCAN 虚拟总线通道适配器 (SocketCAN Virtual Bus Channel Adapter)
 * ============================================================================
 *
 * 【架构职责】
 * 1. 模拟 Linux SocketCAN 原生网络套接字 (`vcan0` / `can0`) 二进制 I/O 字节流；
 * 2. 接收原生 16-Byte `struct can_frame` 报文，解析为 [SocketCanRawFrame] 和 [CanFrame]；
 * 3. 支持 CAN ID 硬件级/软件级过滤 (Acceptance Filter) 匹配机制；
 * 4. 经由 [DbcMessageMatrixDecoder] 矩阵解码为强类型 [DbcDecodedMessage] 车身信号；
 * 5. 向整个座舱系统与 Daemon 进程广播高时效性的 Flow 信号管道。
 *
 * 【数据管道流向】
 * 原始 16-Byte 二进制流 / CanFrame 
 *   $\rightarrow$ [injectRawBuffer] / [injectCanFrame]
 *   $\rightarrow$ Acceptance Filter 硬件校验
 *   $\rightarrow$ [rawFrameStream] (SocketCanRawFrame)
 *   $\rightarrow$ [canFrameStream] (CanFrame)
 *   $\rightarrow$ DBC 解码器 [DbcMessageMatrixDecoder.decodeFrame]
 *   $\rightarrow$ [decodedMessageStream] (DbcDecodedMessage)
 *   $\rightarrow$ 车载中间件与 Compose UI 重组。
 */
class SocketCanChannelAdapter(
    val channelName: String = "vcan0",
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    companion object {
        private const val TAG = "SocketCanChannelAdapter"
    }

    /** CAN ID 验收过滤器集合 (线程安全)。为空表示不过滤，全量接收。 */
    private val acceptanceFilter = ConcurrentHashMap.newKeySet<Int>()

    /** 原始 SocketCAN 16-Byte 二进制报文 SharedFlow 流 (保留最近 10 帧历史) */
    private val _rawFrameStream = MutableSharedFlow<SocketCanRawFrame>(
        replay = 10,
        extraBufferCapacity = 64
    )
    val rawFrameStream: SharedFlow<SocketCanRawFrame> = _rawFrameStream.asSharedFlow()

    /** 高阶 CanFrame 报文 SharedFlow 流 */
    private val _canFrameStream = MutableSharedFlow<CanFrame>(
        replay = 10,
        extraBufferCapacity = 64
    )
    val canFrameStream: SharedFlow<CanFrame> = _canFrameStream.asSharedFlow()

    /** DBC 矩阵解码后的强类型车载信号 SharedFlow 流 */
    private val _decodedMessageStream = MutableSharedFlow<DbcDecodedMessage>(
        replay = 10,
        extraBufferCapacity = 64
    )
    val decodedMessageStream: SharedFlow<DbcDecodedMessage> = _decodedMessageStream.asSharedFlow()

    /**
     * 设置 CAN ID 验收过滤器。
     *
     * 只有符合 filter 列表中 CAN ID 的报文才会进入 pipeline 分发；
     * 若传入空列表或未设置，则不过滤（全量接收）。
     *
     * @param canIds 需要接收的 CAN ID 变长参数（例如 `0x120, 0x210, 0x300`）
     */
    fun setAcceptanceFilter(vararg canIds: Int) {
        acceptanceFilter.clear()
        canIds.forEach { acceptanceFilter.add(it) }
        val filterHex = canIds.joinToString { "0x${it.toString(16).uppercase()}" }
        CarLogger.i(TAG, "[$channelName] Updated CAN acceptance filter IDs: [$filterHex]")
    }

    /**
     * 清除过滤器，恢复全量接收所有 CAN ID 报文模式。
     */
    fun clearAcceptanceFilter() {
        acceptanceFilter.clear()
        CarLogger.i(TAG, "[$channelName] Acceptance filter cleared; accepting all CAN IDs.")
    }

    /**
     * 接收 Linux SocketCAN 16-Byte 原生 C 结构体二进制字节块并推入管道。
     *
     * @param buffer 包含 `can_frame` 二进制数据的字节数组
     * @param offset 初始解析偏移量（默认为 0）
     */
    fun injectRawBuffer(buffer: ByteArray, offset: Int = 0) {
        runCatching {
            val rawFrame = SocketCanRawFrame.parseFromBuffer(buffer, offset)
            CarLogger.d(TAG, "[$channelName] Injected raw SocketCAN frame: ID=0x${rawFrame.canId.toString(16).uppercase()} DLC=${rawFrame.dlc}")
            dispatchRawFrame(rawFrame)
        }.onFailure { e ->
            CarLogger.e(TAG, "[$channelName] Failed to parse raw SocketCAN buffer: ${e.message}")
        }
    }

    /**
     * 接收高阶 [CanFrame] 报文并推入 SocketCAN 处理管道。
     *
     * @param canFrame 待分发的 [CanFrame] 对象
     */
    fun injectCanFrame(canFrame: CanFrame) {
        val rawFrame = SocketCanRawFrame.fromCanFrame(canFrame)
        CarLogger.d(TAG, "[$channelName] Injected CanFrame: ID=0x${canFrame.id.toString(16).uppercase()} Hex=${canFrame.toHexDump()}")
        dispatchRawFrame(rawFrame)
    }

    /**
     * 内部核心分发管道：校验过滤器 $\rightarrow$ 解析 raw frame $\rightarrow$ 解码 DBC 信号 $\rightarrow$ 广播 SharedFlow。
     */
    private fun dispatchRawFrame(rawFrame: SocketCanRawFrame) {
        val canId = rawFrame.canId

        // 校验 Acceptance Filter 硬件级/软件级过滤器
        if (acceptanceFilter.isNotEmpty() && !acceptanceFilter.contains(canId)) {
            CarLogger.d(TAG, "[$channelName] CAN ID 0x${canId.toString(16).uppercase()} filtered out by acceptance filter.")
            return
        }

        val canFrame = rawFrame.toCanFrame()

        scope.launch {
            _rawFrameStream.emit(rawFrame)
            _canFrameStream.emit(canFrame)

            // 经过 DBC 信号矩阵解码
            val decodedMsg = DbcMessageMatrixDecoder.decodeFrame(canFrame)
            if (decodedMsg != null) {
                CarLogger.i(TAG, "[$channelName] Successfully decoded DBC message: $decodedMsg")
                _decodedMessageStream.emit(decodedMsg)
            }
        }
    }
}
