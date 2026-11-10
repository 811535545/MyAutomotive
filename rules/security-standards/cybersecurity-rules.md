# ISO/SAE 21434 & UNECE R155/R156 车载信息安全规范

## 1. 概述
ISO/SAE 21434 是车载网络安全 (Cybersecurity Management System, CSMS) 的国际标准，要求从车辆架构设计、编码到 OTA 升级全生命周期实现防御。

## 2. 核心安全规则 (Security Rules)

### 2.1 密钥管理与安全存储 (Key Management & Secure Storage)
* 严禁在源代码中硬编码明文密钥或私钥。
* 敏感密钥与数字证书必须存储在硬件安全模块 (HSM / Hardware Security Module) 或 Android Keystore StrongBox 中。

### 2.2 消息认证与总线防刷写 (SecOC & Bus Protection)
* 车载以太网与 CAN-FD 关键总线消息必须使用 **SecOC (Secure On-Board Communication)** 进行 MAC (Message Authentication Code) 消息认证。
* 诊断与刷写通道 (UDS 0x27 Security Access) 必须经过强质询响应 (Challenge-Response) 身份鉴权。

### 2.3 OTA 安全升级 (Secure OTA Update Rules)
* 升级包必须使用 RSA-4096 / ECC P-384 算法进行数字签名。
* 升级流程必须具备**防回滚机制 (Anti-Rollback)** 与 **双分区 (A/B Partition) 容错静默升级** 能力。
