# AAOS 智能座舱应用与中间件架构实战指南 (AAOS Cockpit Architecture Guide)

## 1. 架构总览
Android Automotive OS (AAOS) 是 Google 专为车载座舱打造的原生 Android 系统。座舱架构分为：
1. **HMI 应用层 (App / Compose HMI)**: 基于 Jetpack Compose / View 打造车规级黑暗模式大屏 UI。
2. **应用中间件层 (Middleware Container / Repository)**: 单一真实数据源 (Single Source of Truth)，解耦应用与底层连接。
3. **AAOS CarService 框架层 (`android.car`)**: `CarPropertyService`、`CarPowerManagementService`、`CarAudioService`、`CarInputService`。
4. **VHAL (Vehicle Hardware Abstraction Layer)**: 运行在 Native C++/AIDL 中的车辆硬件抽象层，与 CAN/以太网交互。

## 2. 核心技术实施要点
* **双通道适配 (AAOS Native vs. Mock Binder)**:
  在普通设备/模拟器上走 Binder 多进程 `:vehicle` 模拟通道；在原生 AAOS 系统镜像上通过 `Class.forName("android.car.Car")` 探测后反射接入 `CarPropertyManager`。
* **行车安全限制 (`CarUxRestrictions`)**:
  当车速 $> 0.5\text{km/h}$ 或档位处于 `D / R` 时，动态限制复杂 UI 操作、禁止视频播放，开启键盘锁死防护。
