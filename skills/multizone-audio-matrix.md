# 车载多音区 (Multi-Zone Audio) 物理路由与 Focus Matrix 焦点仲裁指南

## 1. 音区与物理 Bus 绑定 (Audio Bus Devices)
AAOS 将车辆划分为物理隔离的音频音区 (`AudioZone`) 与音频总线 (`AudioBusDevice`)：
- **DRIVER_ZONE_0 (主驾)**: `bus0_media_driver`, `bus1_nav_driver`, `bus2_call_headrest` (头枕专属隐私音响)。
- **PASSENGER_ZONE_1 (副驾)**: `bus3_media_passenger` (独立影音)。
- **REAR_ZONE_2 (后排)**: `bus4_rear_entertainment` (后排娱乐屏)。

## 2. 焦点仲裁矩阵规则 (Focus Interaction Matrix)
- **EMERGENCY_ALARM (紧急告警)**: 最高优先级独占打断全车音区，报警生效期间驳回低优先级请求。
- **CALL (蓝牙电话)**: 独占分配主驾头枕音响，保护隐私通话。
- **VOICE_COMMAND (PTT 语音)**: 压低全车媒体音量至 10% (Ducking)。
- **NAVIGATION (导航播报)**: 仅对 `DRIVER_ZONE_0` 降低媒体音量至 30%，副驾与后排音量保持 100% 不变（物理音区隔离）。

## 3. 音量渐变平滑调度 (Volume Ramp Smoothing)
使用 `AudioVolumeFader` 采用 20ms 步长阶梯式线性/指数增益插值，在 200ms ~ 500ms 内平滑完成音量切换，彻底消除 Pop 爆音。
