# MISRA C++ 2023 车规级 C++/Kotlin 编码安全规范

## 1. 核心概述
MISRA C++:2023 是车载嵌入式、中央计算与微服务系统中最严苛的代码安全标准，旨在彻底消除未定义行为 (Undefined Behavior)、数据竞态 (Data Races) 与内存泄漏。

## 2. 核心规则分类 (Rule Classification)

### 2.1 内存与指针安全 (Memory & Pointer Safety)
* **Rule 5.0.1 (Mandatory)**: 严禁使用未初始化的变量或未绑定的指针。
* **Rule 6.5.2 (Required)**: 禁止直接使用裸指针管理动态内存，必须优先使用 `std::unique_ptr` / `std::shared_ptr` 或受控的 RAII 容器。
* **Rule 6.6.1 (Mandatory)**: 内存释放后必须立即重置为 `nullptr`，防止悬挂指针 (Dangling Pointer) 与二次释放 (Double Free)。

### 2.2 并发与线程安全 (Concurrency & Multithreading Rules)
* **Rule 18.2.1 (Required)**: 共享状态的所有读写访问必须经过显式的互斥锁 (`std::mutex` / Kotlin `@Synchronized` / `AtomicState`) 保护。
* **Rule 18.3.4 (Mandatory)**: 严格禁止在持有锁期间调用可能阻塞或未知的回调函数，防止死锁 (Deadlock)。
* **Rule 18.5.1 (Required)**: 跨线程通信优先使用无锁队列、原子变量 (Atomic) 或响应式 Flow 管道。

### 2.3 失效安全与类型转换 (Fail-Safe & Type Casting)
* **Rule 8.2.3 (Required)**: 禁止强制类型转换 (Reinterpret Cast) 破坏类型系统。
* **Rule 15.0.2 (Mandatory)**: 所有可能抛出异常的函数必须提供清晰的 Fail-Safe (失效安全) 兜底逻辑，防止应用崩溃。
