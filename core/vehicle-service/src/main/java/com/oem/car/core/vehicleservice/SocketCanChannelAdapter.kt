package com.oem.car.core.vehicleservice

/**
 * ============================================================================
 * SocketCAN 虚拟总线通道适配器 (多模块共享类型别名)
 * ============================================================================
 *
 * 【架构设计说明】
 * `SocketCanChannelAdapter` 的完整实现位于基础数据模型模块 `:core:model`
 * ([com.oem.car.core.model.SocketCanChannelAdapter])。
 *
 * 这样做的好处是：
 * 1. 允许位于 `:core:vehicle` 模块中的 `:vehicle` 服务端进程 Daemon（`CarCoreDaemon`）
 *    直接依赖并使用完整的 SocketCAN 二进制解析、日志记录（CarLogger）与 DBC 解码管道，
 *    无需反向依赖 `:core:vehicle-service`，彻底避免 Gradle 模块间的循环依赖 (Circular Dependency)；
 * 2. 本模块保留类型别名导出（`typealias`），保障 `:core:vehicle-service`
 *    及上层各 Feature 模块无需修改任何 import 路径即可无缝使用。
 */
typealias SocketCanChannelAdapter = com.oem.car.core.model.SocketCanChannelAdapter
