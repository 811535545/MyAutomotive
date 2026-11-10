# ISO 26262 / GB/T 34590 道路车辆功能安全合规指南

## 1. ASIL 汽车安全完整性等级 (Automotive Safety Integrity Level)
ISO 26262 将车载系统安全等级划分为 **ASIL-A (最低)** 到 **ASIL-D (最高)**：

| ASIL 等级 | 严重度 (Severity) | 暴露率 (Exposure) | 可控性 (Controllability) | 代表模块 |
| :--- | :--- | :--- | :--- | :--- |
| **ASIL-D** | S3 (致命) | E4 (高频) | C3 (不可控) | 线控转向、线控制动、BMS 过充保护、ADAS 紧急制动 |
| **ASIL-C** | S3 (致命) | E3 (中频) | C2 (可控) | 仪表速度显示、车身 ESP 电子稳定系统 |
| **ASIL-B** | S2 (中度) | E4 (高频) | C2 (可控) | 行车防分心 UI 拦截、倒车影像 EVS、车灯控制 |
| **ASIL-A** | S1 (轻微) | E3 (中频) | C1 (易控制) | 空调 HVAC 控制、座舱氛围灯 |
| **QM** | 无安全影响 | - | - | 车载多媒体播放器、天气预报 Widget |

## 2. 软件设计安全机制 (Software Safety Mechanisms)

### 2.1 依赖与独立性分析 (Freedom From Interference, FFI)
* 低安全等级组件 (QM/ASIL-A，如多媒体播放器) **决不能影响** 高安全等级组件 (ASIL-B/D，如仪表速度显示或防分心策略)。
* 使用 **Binder 多进程隔离** (:vehicle 守护进程与主 HMI 进程隔离) 或虚拟化 Hypervisor 容器隔离。

### 2.2 信号冗余校验 (Redundancy & Validation)
* 关键总线信号必须具备 **Rolling Counter (循环计数器)** 与 **SAE J1850 CRC8 校验**。
* 接收方一旦检测到 CRC 校验失败或计数器跳变，必须降级并触发失效安全 (Fail-Safe) 逻辑。
