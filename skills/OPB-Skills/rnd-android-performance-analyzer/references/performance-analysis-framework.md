# Android 性能分析框架知识库

> **核心警告**：不知道就是不知道，绝对不要胡诌！错误的结论会误导后续排查，比没有结论更危险。

## 一、性能问题分类与识别

### 1.1 CPU 性能问题

**关键指标**:
- CPU 使用率（需结合核数判断）
- 主线程状态
- 线程调度情况

**常见问题模式**:
| 模式 | 表现 | 根因 |
|------|------|------|
| CPU 峰值 | 某时刻 CPU 突然飙高 | 计算密集型操作、死循环 |
| 持续高占用 | CPU 长时间高负载 | 低效算法、频繁轮询 |
| 主线程阻塞 | 主线程 CPU 占用高 | 主线程执行耗时操作 |

**分析要点**:
```
CPU 使用率判断（结合核数）:
- 4核设备: 50% = 2核满载 → 需要关注
- 8核设备: 50% = 4核满载 → 相对更严重
- 单核100% vs 多核平均50% → 性能影响不同
```

### 1.2 内存性能问题

**关键指标**:
- 内存使用量（需结合总内存判断）
- GC 频率和耗时
- 内存分配模式

**常见问题模式**:
| 模式 | 表现 | 根因 |
|------|------|------|
| 内存泄漏 | 内存持续增长不释放 | 对象引用未释放 |
| 频繁 GC | GC 频率高、耗时长 | 大量临时对象分配 |
| OOM | 内存耗尽应用崩溃 | 内存使用超限 |

**分析要点**:
```
内存使用率判断（结合总内存）:
- 4GB设备: 2GB使用(50%) → 高风险，关注 OOM
- 8GB设备: 2GB使用(25%) → 相对正常
```

### 1.3 IO 性能问题

**关键指标**:
- IO 操作频率
- IO 阻塞时间
- 主线程 IO 操作

**常见问题模式**:
| 模式 | 表现 | 根因 |
|------|------|------|
| 主线程 IO | 主线程阻塞等待 IO | 同步 IO 操作在主线程 |
| 频繁 IO | IO 操作过于频繁 | 缺少缓存、批量处理 |
| 数据库阻塞 | 数据库操作耗时长 | 未优化查询、锁竞争 |

### 1.4 应用响应性问题

**关键指标**:
- ANR 触发情况
- 主线程消息队列
- UI 渲染帧率

**常见问题模式**:
| 模式 | 表现 | 根因 |
|------|------|------|
| Input ANR | 输入事件无响应 | 主线程阻塞超过 5 秒 |
| Broadcast ANR | 广播超时 | BroadcastReceiver 执行超时 |
| 掉帧 | UI 不流畅 | 主线程耗时操作、渲染过重 |

---

## 二、性能数据类型与解析

### 2.1 Logcat 日志

**关键搜索词**:
```bash
# ANR 相关
grep -E "ANR in|Application Not Responding|Input dispatching timed out"

# GC 相关
grep -E "GC_|concurrent GC|GC paused|clamp target"

# 内存相关
grep -E "OutOfMemoryError|Low memory|kswapd"

# CPU 相关
grep -E "CPU usage|Load:"
```

### 2.2 Systrace / Perfetto

**仅支持 JSON 格式的 trace 文件**

**关键事件类型**:
- CPU 调度事件: `sched_switch`, `sched_wakeup`
- 线程状态变化: `thread_state`
- Binder 调用: `binder_transaction`
- 锁竞争: `lock_contention`
- IO 操作: `block_rq_issue`
- 渲染帧: `Choreographer`, `doFrame`

**转换指导**:
```bash
# Perfetto trace 转换
traceconv json trace.pb > trace.json

# 或使用 trace_processor
trace_processor trace.pb --query "SELECT * FROM sched"
```

### 2.3 Dumpsys 数据

**常用命令**:
```bash
# CPU 信息
dumpsys cpuinfo

# 内存信息
dumpsys meminfo [package_name]

# Activity 状态
dumpsys activity

# 电池状态
dumpsys batterystats
```

---

## 三、硬件配置信息提取

### 3.1 CPU 核数提取

```bash
# 方法1: /proc/cpuinfo
cat /proc/cpuinfo | grep processor | wc -l

# 方法2: dumpsys
dumpsys cpuinfo | grep "CPU usage"

# 方法3: 系统属性
getprop ro.hardware.cpu
```

### 3.2 内存大小提取

```bash
# 方法1: /proc/meminfo
cat /proc/meminfo | grep MemTotal

# 方法2: dumpsys
dumpsys meminfo | grep "Total RAM"

# 方法3: 系统属性
getprop ro.config.low_ram
```

---

## 四、问题定位优先级框架

### 4.1 问题优先级定义

| 优先级 | 问题类型 | 影响 | 处理策略 |
|--------|---------|------|---------|
| P0 | 严重阻塞 | 应用不可用 | 立即修复 |
| P1 | 资源耗尽 | 应用崩溃 | 尽快修复 |
| P2 | 性能瓶颈 | 体验下降 | 计划修复 |
| P3 | 次要优化 | 轻微影响 | 有空修复 |

### 4.2 P0 严重阻塞问题

**ANR (Application Not Responding)**:
- Input dispatching timeout (5 秒)
- BroadcastReceiver timeout (前台 10 秒, 后台 60 秒)
- Service timeout (前台 20 秒, 后台 200 秒)

**死锁**:
- 多个线程相互等待
- Binder 死锁
- 数据库锁竞争

**主线程长时间阻塞**:
- 同步 IO 操作
- 耗时计算
- 锁等待

### 4.3 P1 资源耗尽问题

**内存泄漏**:
- Activity/Fragment 泄漏
- 监听器未注销
- 静态引用持有 Context

**OOM**:
- Bitmap 过大
- 缓存无限制增长
- Native 内存泄漏

**CPU 满载**:
- 死循环
- 无限递归
- 频繁轮询

---

## 五、置信度打分体系

### 5.1 证据类型与权重

| 证据类型 | 权重 | 示例 |
|---------|------|------|
| 确凿证据 | ⭐⭐⭐⭐⭐ | 性能数据明确显示 |
| 强关联证据 | ⭐⭐⭐⭐ | 时间吻合+技术原理支持 |
| 中等证据 | ⭐⭐⭐ | 有相关日志但非直接证据 |
| 弱证据 | ⭐⭐ | 基于经验推测 |
| 猜测 | ⭐ | 无直接证据支持 |

### 5.2 置信度计算

```
基础置信度: 50%

加分项:
+ 性能数据明确显示 (+30%)
+ 时间线精确吻合 (+20%)
+ 代码/源码验证 (+20%)
+ 无反证 (+10%)

减分项:
- 证据链不完整 (-20%)
- 存在反证 (-30%)
- 纯推测 (-40%)
```

### 5.3 置信度输出规范

```markdown
分析结论 (置信度: X%)
置信度说明: [说明证据来源和不确定性]
```

**示例**:
```markdown
根因: 主线程执行耗时数据库查询导致 ANR (置信度: 85%)
置信度说明: Systrace 显示主线程在 SQLite 操作耗时 8 秒，与 ANR 时间吻合
```

---

## 六、解决方案设计框架

### 6.1 方案层次

| 层次 | 目标 | 时间 |
|------|------|------|
| 立即修复 | 快速止血 | 小时级 |
| 根本解决 | 彻底修复 | 天级 |
| 架构优化 | 长期改进 | 周级 |

### 6.2 常见优化模式

**CPU 优化**:
```kotlin
// 将耗时操作移到后台线程
CoroutineScope(Dispatchers.IO).launch {
    val result = heavyComputation()
    withContext(Dispatchers.Main) {
        updateUI(result)
    }
}
```

**内存优化**:
```kotlin
// 避免内存泄漏
class MyActivity : AppCompatActivity() {
    private var callback: Callback? = null

    override fun onDestroy() {
        super.onDestroy()
        callback = null  // 释放引用
    }
}
```

**IO 优化**:
```kotlin
// 异步 IO 操作
suspend fun loadData(): Data = withContext(Dispatchers.IO) {
    database.query("SELECT * FROM table")
}
```

---

## 七、质量保证检查清单

### 分析前检查
- [ ] 是否获取了完整的性能数据？
- [ ] 是否明确了硬件配置（CPU 核数、内存大小）？
- [ ] Trace 文件是否为 JSON 格式？

### 分析中检查
- [ ] 是否结合硬件配置判断性能指标？
- [ ] 是否搜索了所有关键日志？
- [ ] 是否建立了完整的时间线？

### 结论前检查
- [ ] 每条结论是否有证据支持？
- [ ] 置信度是否诚实反映证据充分程度？
- [ ] 如果置信度 < 50%，是否说明"无法确定"？

### 方案检查
- [ ] 解决方案是否具体可执行？
- [ ] 是否提供了代码示例？
- [ ] 是否说明了预期效果和风险？

---

## 八、常见问题模式速查表

### CPU 问题

| 表现 | 可能原因 | 排查方向 |
|------|---------|---------|
| CPU 持续 100% | 死循环、低效算法 | 查看热点函数 |
| 主线程 CPU 高 | 主线程耗时操作 | 检查主线程堆栈 |
| 多线程 CPU 高 | 并发过多、锁竞争 | 分析线程状态 |

### 内存问题

| 表现 | 可能原因 | 排查方向 |
|------|---------|---------|
| 内存持续增长 | 内存泄漏 | heap dump 分析 |
| 频繁 GC | 临时对象过多 | 检查对象分配 |
| OOM 崩溃 | 内存超限 | 检查大对象分配 |

### IO 问题

| 表现 | 可能原因 | 排查方向 |
|------|---------|---------|
| 主线程阻塞 | 主线程同步 IO | 检查 IO 调用栈 |
| IO 耗时长 | 频繁读写、无缓存 | 分析 IO 模式 |
| 数据库慢 | 未优化查询、锁竞争 | 检查 SQL 语句 |

### ANR 问题

| 表现 | 可能原因 | 排查方向 |
|------|---------|---------|
| Input ANR | 主线程阻塞 > 5s | 检查主线程堆栈 |
| Broadcast ANR | 广播处理超时 | 检查 Receiver |
| Service ANR | 服务启动超时 | 检查 Service |

---

## 九、黄金法则

在给出性能分析结论前，问自己三个问题：

```
1. 我是否结合硬件配置判断了性能指标的实际意义？
   (4核50% vs 8核50%)

2. 我的结论是否有性能数据直接支持？
   (是实际数据还是推测)

3. 我的置信度是否诚实反映了证据的充分程度？
   (如果不确定就说不确定)

如果任何一个答案是"否"，重新审视结论！
```

---

**一句话总结**：

**"基于数据分析问题，结合硬件判断指标，诚实标注置信度。不知道就说不知道，绝对不要胡诌！"**
