# 🚗 myCarApp 车载开发技能与车规规范指南 (Automotive Skills & Standards Master Index)

本项目已整合来自 **Automotive Claude Code Agents** 的车载工程技能库、车规编码规范与功能安全/信息安全合规指南。

---

## 📚 1. 车载开发技能库 (Automotive Skills Directory: ``)

| 技能文档 | 核心领域 | 适用模块 | 关键技术点 |
| :--- | :--- | :--- | :--- |
| **[AAOS 座舱应用与中间件指南](aaos-cockpit-architecture.md)** | 智能座舱 / HMI | `app`, `core:data`, `core:vehicle-service` | `CarPropertyManager`, 反射双通道, Binder 多进程 |
| **[SocketCAN & DBC 报文管道指南](socketcan-dbc-pipeline.md)** | 车载总线 / 物理协议 | `core:model`, `core:vehicle` | SocketCAN 16-Byte `can_frame`, DBC 矩阵, CRC8 |
| **[多音区 AudioFocus 仲裁矩阵指南](multizone-audio-matrix.md)** | 车载音频系统 | `core:audio` | `AudioBusDevice`, 物理音区隔离, Ducking 降音, Ramp Fader |
| **[SOME/IP 与 SOA 车载中间件指南](skills/someip-zonal-middleware.md)** | 软件定义汽车 / 域控 | `core:telematics`, `core:connected` | SOME/IP 服务发现 (SD), RPC 远程调用, DDS 软总线 |
| **[中国车载强标与 SOTIF 法规指南](china-automotive-standards.md)** | 行业法规 / 标准合规 | 全项目 | GB/T 34590, GB/T 43267 SOTIF, CSMS/SUMS |

---

## 🛡️ 2. 车规编码与安全规范 (Automotive Standards Directory: `../rules`)

| 规范文档 | 标准规范 | 适用范围 |
| :--- | :--- | :--- |
| **[MISRA C++ 2023 编码规范](../rules/coding-standards/misra-cpp-2023.md)** | MISRA C++:2023 | C++ / Kotlin 指针安全、RAII 内存管理与死锁预防 |
| **[ISO 26262 功能安全指南](../rules/safety-standards/iso-26262-compliance.md)** | ISO 26262 / GB/T 34590 | ASIL-A ~ ASIL-D 等级划定、FFI 独立性隔离、冗余校验 |
| **[ISO/SAE 21434 信息安全规范](../rules/security-standards/cybersecurity-rules.md)** | ISO/SAE 21434 / R155 | HSM 密钥存储、SecOC 报文认证、OTA 防回滚 |

---

## 🔧 3. OPB 车载专精技能库 (OPB Specialized Skills: `skills/opb-skills/`)

| 专精技能 | 角色定位 | 核心覆盖范围 |
| :--- | :--- | :--- |
| **[rnd-android-system-architect](skills/opb-skills/rnd-android-system-architect.md)** | Android 系统架构专家 | AOSP 系统定制、Framework 扩展、AIDL HAL 开发与 Binder IPC 设计 |
| **[rnd-cockpit-architect](skills/opb-skills/rnd-cockpit-architect.md)** | 智能座舱架构专家 | 车载 SOA 架构、HMI 分层设计、多屏互联、车云一体 |
| **[rnd-anr-analyzer](skills/opb-skills/rnd-anr-analyzer.md)** | ANR 问题分析专家 | `traces.txt` 解读、时间线链推导、锁竞争与死锁排查 |
| **[rnd-android-performance-analyzer](skills/opb-skills/rnd-android-performance-analyzer.md)** | Android 性能分析专家 | 卡顿优化、掉帧定位、内存泄漏 (Memory Leak)、GC 频率优化 |
| **[rnd-automotive-issue-analyzer](skills/opb-skills/rnd-automotive-issue-analyzer.md)** | 车载系统问题分析专家 | 黑屏/白屏、Watchdog 重启、跨域通信 (Android-QNX-MCU) 诊断 |

---

## 🚀 4. 如何在项目中快速检索使用？
在开发相关模块时，直接参考对应的 Markdown 文档：
* 编写底层总线协议时 $\rightarrow$ 查阅 `socketcan-dbc-pipeline.md`
* 编写多音区/音频打断逻辑时 $\rightarrow$ 查阅 `multizone-audio-matrix.md`
* 分析 ANR 或死锁卡顿时 $\rightarrow$ 查阅 `skills/opb-skills/rnd-anr-analyzer.md`
* 进行代码 Review 或安全审计时 $\rightarrow$ 查阅 `../rules/coding-standards/misra-cpp-2023.md`
