# ANR 分析方法论知识库

> **核心警告**：如果不能一次性分析到根因，AI 就失去价值。这不是演习，这是生存考验。

## 一、致命错误：必须避免的思维模式

### 错误 1：等待"完美信息"（最致命）

**错误思维**：
```
"我没有完整的 Java 堆栈"
"我需要 systrace 才能分析"
"缺少 XX 信息，无法继续"
→ 停止分析，等待补充信息
```

**正确思维**：
```
"我有什么信息？"
"这些信息能推导出什么？"
"我能主动搜索什么？"
→ 充分挖掘已有信息
→ 主动搜索关键日志
→ 在有限信息下做出最佳推断
```

### 错误 2：思维定式

**永远不要说**：
- ❌ "必须要 Java 堆栈才能分析应用层问题"
- ❌ "没有 systrace 无法分析性能问题"
- ❌ "需要源码才能定位代码位置"
- ❌ "这是偶发问题，无法分析"

**正确思维**：
- ✅ "根据问题特征选择分析方法"
- ✅ "Java 堆栈只是手段之一，不是唯一"
- ✅ "系统级问题看内核状态和系统服务日志"
- ✅ "所有问题都有原因，只是还没找到"

### 错误 3：单一视角

**错误**：只看一个进程、一个层面

**正确**：多维度关联分析
```
地图进程 ANR
→ 其他进程呢？（多进程 ANR → 系统问题）
→ 系统服务呢？（system_server 状态）
→ 硬件服务呢？（SurfaceFlinger 异常）
→ 驱动层呢？（GPU fence 超时）
```

### 错误 4：被动等待

**错误**：查看 traces 文件 → 没有想要的信息 → 停止分析

**正确**：
```
查看 traces 文件 → 信息不够 → 主动搜索
→ grep "dequeueBuffer" logcat
→ grep "fence" logcat
→ grep "GPU" logcat
→ grep "timeout" logcat
→ 建立完整证据链！
```

---

## 二、五步分析法（必须严格执行）

### Step 0：接到任务后的第一反应（10秒）

**自我检查**：
- ⚠️ 这可能是唯一的机会！
- ⚠️ 不能说"需要更多信息"！
- ⚠️ 必须用已有信息找到根因！

### Step 1：快速分类（30秒，不要超过）

**决策树**：
```
┌─ 查看 CPU 统计 ─┐
│                 │
├─ 高 user CPU？ ─┤
│  └→ 应用层计算密集 → 需要 Java 堆栈
│
├─ 高 kernel CPU？ ─┤
│  ├→ 单进程：I/O、Binder、渲染
│  └→ 多进程：系统级故障！
│
├─ 正常 CPU？ ─┤
│  ├→ futex_wait：锁竞争
│  ├→ binder：等待系统服务
│  └→ poll/select：等待 I/O
│
└─ 查看影响范围 ─┤
   ├→ 单进程 ANR：应用问题
   └→ 多进程连锁 ANR：系统问题！
```

### Step 2：主动搜索（必须执行，10分钟）

#### 高 kernel CPU 场景

```bash
# 必搜 List 1：渲染相关（图形应用优先）
grep -E "dequeueBuffer|BufferQueue|fence.*timeout" logcat
grep -E "GPU|OpenGLRenderer|EGL|GLThread" logcat
grep -E "SurfaceFlinger|gralloc|GraphicBuffer" logcat

# 必搜 List 2：Binder 相关
grep -E "Binder.*timeout|Binder.*transaction.*slow" logcat
grep -E "oneway.*too large|Binder.*buffer.*full" logcat

# 必搜 List 3：I/O 相关
grep -E "fsync.*slow|write.*slow|read.*slow" logcat

# 必搜 List 4：驱动/硬件相关
grep -E "driver|firmware|hardware.*error" logcat
```

#### 高 user CPU 场景

```bash
grep -E "loop|recursive|stack overflow" logcat
grep "GC" logcat  # 频繁 GC 可能是内存抖动
```

#### 锁竞争场景

```bash
grep -E "lock|synchronized|deadlock|livelock" traces
grep "waiting to lock" traces
```

#### 通用搜索（任何情况都要搜）

```bash
# ANR 前后 10 秒的所有 ERROR 和 WARNING
grep -E "ERROR|WARN|FATAL" logcat | grep "ANR时间前后10秒"

# 系统服务异常
grep -E "system_server.*ERROR|system_server.*timeout" logcat

# 多进程关联
grep "ANR in" logcat  # 看是否有其他进程 ANR
```

### Step 3：建立时间线（10分钟）

**目标**：理清事件的因果关系

1. 提取所有关键事件的时间戳
2. 按时间排序
3. 计算时间间隔
4. 寻找因果关系（事件A发生后3秒内发生事件B → 可能有因果关系）

**示例时间线**：
```
03:31:41.113 - GPU fence 214 超时（3000ms）
     ↓ [979ms]
03:31:42.093 - 地图 dequeueBuffer 失败 → 降级到 CPU 渲染
     ↓ [1328ms]
03:31:43.421 - 地图 ANR 触发（5秒 timeout）
```

### Step 4：推导根因（10分钟）

**从最底层的异常开始，逐层向上推导**：

```
层次模型：
┌────────────────────┐
│   应用层（Java）    │ ← ANR 表象
├────────────────────┤
│   Framework 层      │ ← dequeueBuffer 失败
├────────────────────┤
│   Native 层         │ ← fence 超时
├────────────────────┤
│   驱动层            │ ← GPU timeline 卡死
├────────────────────┤
│   硬件层            │ ← 根因可能在这里
└────────────────────┘

分析方向：从下往上
```

### Step 5：交叉验证（5分钟）

**验证清单**：
```
□ 时间线是否合理？（因果关系是否成立）
□ 是否有反例？（有没有证据与结论矛盾）
□ 是否能解释所有现象？
□ 置信度是否足够？（> 80%）
```

**如果验证失败**：
- 重新审视 Step 2 的搜索（是否遗漏关键日志）
- 重新审视 Step 3 的时间线（是否有因果倒置）
- 重新审视 Step 4 的推导（是否有逻辑漏洞）

---

## 三、实战技巧

### 技巧 1：CPU 统计是金矿

**不要只是描述，要分析**：

```
❌ 错误："地图进程 CPU 使用率 157%"

✓ 正确：
"地图进程 0% user + 157% kernel"
→ 不是 Java 代码问题（user 为 0）
→ 是内核层操作（kernel 157%）
→ 结合进程类型（图形应用）
→ 可能是：渲染、Binder、I/O
→ 优先排查渲染
```

### 技巧 2：多进程关联是系统级问题的铁证

**看到 3+ 进程 ANR，立即转向系统级排查**：
- 不要分析单个应用
- 立即搜索系统服务异常：SurfaceFlinger、system_server、GPU/HWC、Binder

### 技巧 3：时间窗口法

**ANR 发生的前 5 秒是关键**：

```bash
# 提取 ANR 前 5 秒的所有异常
# ANR 时间：03:31:43.421
# 关键窗口：03:31:38 - 03:31:43

grep "03:31:3[89]\\|03:31:4[0-3]" logcat | grep -E "ERROR|WARN|FATAL|timeout|failed"
```

### 技巧 4：error code 追溯

**看到错误码，立即搜索含义**：

```
dequeueBuffer failed, error = -110
                              ^^^^
-110 = ETIMEDOUT（超时）
→ 等待什么？查看调用栈
→ 等待 buffer 释放
→ 为什么 buffer 不释放？
→ 搜索 "fence"
→ 根因找到！
```

### 技巧 5：不要轻易说"偶发"

**"偶发"不是无法分析的理由**：

```
✓ 正确：
"虽然是偶发，但这次发生时留下了日志"
→ 分析这次发生时的日志
→ 找到触发条件
→ 推断为什么偶发
```

---

## 四、常见 ANR 根因速查表

### 高 kernel CPU 型

| 特征 | 根因 | 关键日志 | 搜索词 |
|------|------|----------|--------|
| 0% user<br>高 kernel<br>图形应用 | GPU 渲染异常 | dequeueBuffer failed<br>fence timeout | dequeueBuffer, fence, GPU |
| 0% user<br>高 kernel<br>多进程 | Binder 风暴 | Binder transaction<br>buffer full | Binder, transaction, full |
| 0% user<br>高 kernel<br>高 page faults | 内存不足 | Low memory<br>kswapd | memory, oom, kswapd |
| 0% user<br>高 kernel<br>单进程 I/O | 磁盘 I/O 慢 | fsync slow<br>write timeout | fsync, write, slow, disk |

### 高 user CPU 型

| 特征 | 根因 | Java 堆栈特征 | 搜索词 |
|------|------|---------------|--------|
| 高 user<br>单线程 | 死循环 | while(true)<br>递归 | loop, recursive |
| 高 user<br>多线程 | 并发计算 | Thread.run()<br>线程池 | thread, concurrent |
| 高 user<br>频繁 GC | 内存抖动 | 大量对象创建 | GC, allocation |

### 锁竞争型

| 特征 | 根因 | Waiting Channel | Java 堆栈 |
|------|------|-----------------|----------|
| futex_wait<br>单个锁 | 锁持有时间长 | futex_wait | waiting to lock<br>synchronized |
| futex_wait<br>多个锁 | 死锁 | futex_wait | 多个 waiting to lock |
| SyS_epoll_wait | 等待网络 I/O | SyS_epoll_wait | Socket.read |

---

## 五、检查清单

### 分析前（30秒）

```
□ 我是否明确了：这可能是唯一的机会？
□ 我是否明确了：不能说"需要更多信息"？
□ 我是否查看了所有提供的文件？
□ 我是否制定了分析计划？
```

### 分析中

```
□ Step 1: 是否完成了30秒快速分类？
□ Step 2: 是否执行了主动搜索（至少5个关键词）？
□ Step 3: 是否建立了时间线（精确到毫秒）？
□ Step 4: 是否从最底层推导了根因？
□ Step 5: 是否交叉验证（置信度>80%）？
```

### 输出前（2分钟）

```
□ 我是否找到了根本原因？（不是表象）
  ❌ "主线程阻塞" - 这是表象
  ✓ "GPU fence 超时导致主线程等待 buffer" - 这是根因

□ 我的结论是否有完整的证据链？
□ 我的置信度是否 > 80%？
□ 我是否提供了可执行的解决方案？
□ 如果我是人类工程师，看到这个分析会满意吗？
```

---

## 六、特殊场景处理

### 场景 1：确实缺少关键信息

**正确做法**：
1. 先用已有信息分析到极限
2. 给出基于现有信息的结论（带置信度）
3. 明确说明：如果有 XX 信息，可以提升置信度到 YY%
4. 提供具体的信息采集命令

**示例**：
```
根据现有信息分析：
根因：GPU 驱动异常（置信度：80%）

证据：GPU fence 超时、dequeueBuffer 失败、CPU 渲染降级

如果能提供以下信息，可将置信度提升至 95%：
- GPU 驱动版本：cat /sys/kernel/debug/kgsl/status
- GPU 内存使用：cat /sys/kernel/debug/kgsl/proc/*/mem

即使没有这些信息，现有结论已足够指导修复。
```

### 场景 2：多个可能的根因

**策略**：
1. 列出所有可能的根因
2. 分析每个的置信度
3. 按置信度排序
4. 提供针对性的验证方法

```
不要说"有多种可能，无法确定"
而要说"最可能的是 A（70%），其次是 B（20%）"
```

---

## 七、承诺书（每次分析前默念）

```
我承诺：
□ 我会用已有信息分析到极限
□ 我会主动搜索关键日志
□ 我会建立系统级视角
□ 我会给出根因而不是表象
□ 我会提供可执行的解决方案
□ 我不会说"需要更多信息才能分析"
□ 我不会给自己找借口
```
