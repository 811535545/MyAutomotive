# AOSP Platform Signing Guide (平台签名指南)

> 本文档针对车载 Android (AAOS) 开发人员，详细说明如何使用 **AOSP 平台签名 (Platform Signature)** 签署 APK，使其在系统镜像中获得 `signature` / `privileged` 级别的系统权限（如 `CAR_SPEED`、`CAR_CLIMATE` 等）。

---

## 1. 什么是 AOSP 平台签名？

与普通的 Android 应用签名（由开发者自己的 `.jks` 密钥签署、运行在应用沙箱中）不同，**平台签名** 是由 Android 操作系统厂商（OEM）或 AOSP 源码编译系统持有的核心系统密钥：
* **文件格式**：由两个核心文件组成：
  1. `platform.pk8`（PKCS#8 格式的私钥）
  2. `platform.x509.pem`（X.509 格式的公钥证书）
* **核心作用**：只有使用这两份文件签署的应用，并且放置在系统的 `/system/priv-app/` 目录下，才会被 AAOS 系统信任，并授予车规级系统权限。

---

## 2. 如何获取平台签名文件？

1. **AOSP 开源默认测试密钥**：
   在 Android 官方开源源码树中（`build/target/product/security/` 目录），包含了 Google 默认的测试平台密钥：
   - `platform.pk8`
   - `platform.x509.pem`
   - `shared.pk8` / `shared.x509.pem`
   - `media.pk8` / `media.x509.pem`

2. **车企/OEM 定制密钥**：
   在真实量产项目中，车企（如吉利、比亚迪、蔚来等）会使用自己内部通过安全硬件生成的专属 `platform` 密钥对，不对外公开。因此第三方供应商在开发阶段无法直接获取真车量产的平台私钥。

---

## 3. 如何使用平台签名签署 APK？ (`signapk.jar`)

在 AOSP 编译环境中，签名工具是 `signapk.jar`（位于 AOSP 源码的 `out/host/linux-x86/framework/signapk.jar`）。

### 手动签名命令示例：
```bash
java -jar signapk.jar platform.x509.pem platform.pk8 app-unsigned.apk app-platform-signed.apk
```

---

## 4. 为什么在常规 Gradle 开发中不推荐强行使用平台签名？

1. **无法通过 `adb install` 直接安装**：
   即使 APK 签署了平台签名，如果通过 `adb install` 普通安装，Android 包管理器依然会拒绝授予系统权限。
2. **必须放入系统分区**：
   必须通过 `adb root && adb remount`，将签名后的 APK 推送到 `/system/priv-app/myCarApp/myCarApp.apk`，并重启模拟器。
3. **日常迭代效率低**：
   每次修改一行代码，都需要重新编译、重新签名、重新 push、重启车机，耗时极长。

因此，在日常 HMI 与中间件开发中，业界标准做法是采用我们项目中实现的 **Mock 仿真通道 + 动态权限降级**，而在最终交付给车厂进行系统集成时，由车厂的 CI/CD 流水线使用真实的量产平台密钥进行最终编译打包。
