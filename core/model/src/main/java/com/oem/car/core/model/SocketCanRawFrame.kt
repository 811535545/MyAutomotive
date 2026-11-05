package com.oem.car.core.model

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * ============================================================================
 * Linux SocketCAN 原生 C 结构体 (can_frame) 二进制协议封装
 * ============================================================================
 *
 * 在 Linux 内核 SocketCAN (`<linux/can.h>`) 中，标准 `can_frame` 结构体固定占 16 字节：
 * ```c
 * struct can_frame {
 *     canid_t can_id;  /* 32-bit CAN ID + EFF/RTR/ERR 标志位 (小端序 / Little Endian) */
 *     __u8    can_dlc; /* 帧载荷字节长度 (0..8) */
 *     __u8    __pad;   /* 补齐字节 */
 *     __u8    __res0;  /* 保留位 */
 *     __u8    __res1;  /* 保留位 */
 *     __u8    data[8]  __attribute__((aligned(8))); /* 8 字节 CAN 载荷 */
 * };
 * ```
 *
 * 特别注意位掩码规则：
 * - `CAN_EFF_FLAG` (0x80000000U): 扩展帧格式标志 (29-bit Extended Frame)
 * - `CAN_RTR_FLAG` (0x40000000U): 远程传输请求标志 (Remote Transmission Request)
 * - `CAN_ERR_FLAG` (0x20000000U): 错误帧标志 (Error Frame)
 * - `CAN_SFF_MASK` (0x000007FFU): 标准 11-bit CAN ID 掩码
 * - `CAN_EFF_MASK` (0x1FFFFFFFU): 扩展 29-bit CAN ID 掩码
 */
data class SocketCanRawFrame(
    val rawCanId: Int,
    val dlc: Int,
    val data: ByteArray,
    val timestampMs: Long = System.currentTimeMillis()
) {
    companion object {
        const val RAW_CAN_FRAME_SIZE = 16

        // Linux <linux/can.h> 掩码与标志常量
        const val CAN_EFF_FLAG: Int = 0x80000000.toInt()
        const val CAN_RTR_FLAG: Int = 0x40000000.toInt()
        const val CAN_ERR_FLAG: Int = 0x20000000.toInt()

        const val CAN_SFF_MASK: Int = 0x000007FF
        const val CAN_EFF_MASK: Int = 0x1FFFFFFF

        /**
         * 从 Linux SocketCAN 原生 16-Byte 二进制 Buffer 解析 SocketCanRawFrame。
         */
        fun parseFromBuffer(buffer: ByteArray, offset: Int = 0): SocketCanRawFrame {
            require(buffer.size - offset >= RAW_CAN_FRAME_SIZE) {
                "Buffer size must be at least 16 bytes for Linux SocketCAN frame."
            }

            val byteBuffer = ByteBuffer.wrap(buffer, offset, RAW_CAN_FRAME_SIZE)
                .order(ByteOrder.LITTLE_ENDIAN) // Linux 内核 struct can_frame 中的 can_id 是小端序

            val rawCanId = byteBuffer.int
            val dlc = (byteBuffer.get().toInt() and 0xFF).coerceIn(0, 8)
            // 跳过 3 个 padding / reserved 字节
            byteBuffer.get()
            byteBuffer.get()
            byteBuffer.get()

            val payload = ByteArray(8)
            byteBuffer.get(payload, 0, 8)

            return SocketCanRawFrame(
                rawCanId = rawCanId,
                dlc = dlc,
                data = payload
            )
        }

        /**
         * 从高级 [CanFrame] 构造 SocketCanRawFrame。
         */
        fun fromCanFrame(frame: CanFrame, isExtended: Boolean = false): SocketCanRawFrame {
            val rawId = if (isExtended) {
                (frame.id and CAN_EFF_MASK) or CAN_EFF_FLAG
            } else {
                frame.id and CAN_SFF_MASK
            }
            val paddedData = ByteArray(8)
            System.arraycopy(frame.payload, 0, paddedData, 0, minOf(frame.payload.size, 8))

            return SocketCanRawFrame(
                rawCanId = rawId,
                dlc = frame.dlc.coerceIn(0, 8),
                data = paddedData,
                timestampMs = frame.timestampMs
            )
        }
    }

    /** 提取清除标志位后的真实 CAN ID (11-bit 或 29-bit) */
    val canId: Int
        get() = if (isExtended) rawCanId and CAN_EFF_MASK else rawCanId and CAN_SFF_MASK

    /** 是否为扩展帧 (29-bit CAN ID) */
    val isExtended: Boolean
        get() = (rawCanId and CAN_EFF_FLAG) != 0

    /** 是否为远程请求帧 (RTR) */
    val isRtr: Boolean
        get() = (rawCanId and CAN_RTR_FLAG) != 0

    /** 是否为错误帧 (ERR) */
    val isError: Boolean
        get() = (rawCanId and CAN_ERR_FLAG) != 0

    /**
     * 将本帧序列化为符合 Linux SocketCAN C 结构体 (16 字节) 的二进制字节流。
     */
    fun toRawBuffer(): ByteArray {
        val buffer = ByteArray(RAW_CAN_FRAME_SIZE)
        val byteBuffer = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN)

        byteBuffer.putInt(rawCanId)
        byteBuffer.put(dlc.toByte())
        byteBuffer.put(0x00.toByte()) // pad
        byteBuffer.put(0x00.toByte()) // res0
        byteBuffer.put(0x00.toByte()) // res1

        val payloadToWrite = ByteArray(8)
        System.arraycopy(data, 0, payloadToWrite, 0, minOf(data.size, 8))
        byteBuffer.put(payloadToWrite)

        return buffer
    }

    /**
     * 转换为项目高阶 [CanFrame] 对象。
     */
    fun toCanFrame(): CanFrame {
        val payloadCopy = ByteArray(dlc)
        System.arraycopy(data, 0, payloadCopy, 0, dlc)
        return CanFrame(
            id = canId,
            dlc = dlc,
            payload = payloadCopy,
            timestampMs = timestampMs
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SocketCanRawFrame
        if (rawCanId != other.rawCanId) return false
        if (dlc != other.dlc) return false
        if (!data.contentEquals(other.data)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = rawCanId
        result = 31 * result + dlc
        result = 31 * result + data.contentHashCode()
        return result
    }
}
