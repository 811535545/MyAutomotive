# 中国车载强标与 SOTIF / 智能网联法规体系指南

## 1. 核心标准体系
1. **GB/T 34590-2022 (道路车辆功能安全)**: 对应 ISO 26262:2018，规范从系统、软件到硬件的全生命周期安全。
2. **GB/T 43267-2023 / CSAE 316.1 (SOTIF 预期功能安全)**: 识别感知传感器算法局限性与未知危险场景。
3. **GB 44495-2024 / UNECE R155 (车载信息安全管理体系)**: 车规级 CSMS 强制合规。
4. **GB 44496-2024 / UNECE R156 (软件升级管理体系)**: 车载 SUMS 静默升级与软件版本追踪。

## 2. 合规检查清单 (Compliance Checklist)
- [x] 代码符合 MISRA C / MISRA C++ 规范，零未定义行为。
- [x] 敏感密钥与证书必须通过 HSM / StrongBox SE 加密存储。
- [x] 关键 CAN / CAN-FD 报文必须具备 SecOC 消息认证或 Rolling Counter + CRC 校验。
- [x] D 档移动状态下强制启用 `CarUxRestrictions` 防分心 UI 拦截。
