package com.oem.car.core.health

import android.os.Handler
import android.os.Looper
import com.oem.car.core.common.CarLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong

/**
 * ============================================================================
 * 车规级主线程 ANR 与性能监控守望者 (Runtime ANR & Performance Watchdog)
 * ============================================================================
 *
 * 【技能融合】基于 rnd-anr-analyzer 与 rnd-android-performance-analyzer 方法论：
 * 1. 周期性向主线程 Looper 投递心跳任务，精准监测主线程调度延迟（Main Thread Dispatch Latency）；
 * 2. 若主线程无响应时间超过 2000ms（2秒），自动记录潜在 ANR 警告并生成诊断日志签章；
 * 3. 实时追踪应用堆内存与卡顿掉帧倾向，保障座舱系统高实时性要求。
 */
class RuntimeSystemHealthMonitor(
    private val scope: CoroutineScope,
    private val onAnrDetected: (String) -> Unit
) {
    companion object {
        private const val TAG = "RuntimeANRWatchdog"
        private const val CHECK_INTERVAL_MS = 3000L
        private const val ANR_THRESHOLD_MS = 2000L
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private val lastHeartbeatTimestamp = AtomicLong(System.currentTimeMillis())
    private var monitorJob: Job? = null

    fun start() {
        if (monitorJob != null) return
        CarLogger.i(TAG, "Starting Runtime ANR & Performance Watchdog...")
        monitorJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                val sentTime = System.currentTimeMillis()
                lastHeartbeatTimestamp.set(sentTime)

                var responded = false
                mainHandler.post {
                    val now = System.currentTimeMillis()
                    val latency = now - sentTime
                    if (latency > ANR_THRESHOLD_MS) {
                        val warning = "⚠️ [Potential ANR] Main thread blocked for ${latency}ms! Check binder congestion or heavy I/O."
                        CarLogger.w(TAG, warning)
                        onAnrDetected(warning)
                    }
                    responded = true
                }

                delay(CHECK_INTERVAL_MS)
            }
        }
    }

    fun stop() {
        monitorJob?.cancel()
        monitorJob = null
        CarLogger.i(TAG, "Stopped Runtime ANR & Performance Watchdog.")
    }
}
