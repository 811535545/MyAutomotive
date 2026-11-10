# 技术博客文章格式规范

本模板定义了技术博客文章的标准格式，所有输出的文章必须严格遵守此规范。

---

## 1. 文件头格式 (Frontmatter)

**必须**使用 YAML 格式的文件头，位于文件最开始，用 `---` 包裹：

```yaml
---
title: 文章标题（简洁明了，不超过30字）
date: YYYY-MM-DD（发布日期，格式如 2025-01-05）
description: 文章摘要描述（一句话概括文章内容，50-100字）
tags: [标签1, 标签2, 标签3]（相关技术标签，3-5个）
category: 分类名（如：技术、教程、实践、工具）
draft: false（是否为草稿，true/false）
---
```

### Frontmatter 字段说明

| 字段 | 必填 | 类型 | 说明 |
|:---|:---:|:---|:---|
| title | 是 | string | 文章标题 |
| date | 是 | string | 发布日期，格式 YYYY-MM-DD |
| description | 是 | string | 文章描述/摘要 |
| tags | 是 | array | 标签数组，用于分类检索 |
| category | 是 | string | 文章分类 |
| draft | 否 | boolean | 是否为草稿，默认 false |

---

## 2. Markdown 内容格式规范

### 2.1 标题层级

使用 `#` 符号定义标题层级，文章正文从二级标题开始（一级标题由 frontmatter 的 title 生成）：

```markdown
## 二级标题（章节）

### 三级标题（小节）

#### 四级标题（子小节，尽量少用）
```

**规范要求：**
- 标题层级不要跳跃（如从 ## 直接到 ####）
- 每个标题后空一行
- 标题前空一行（文件开头除外）

### 2.2 文本格式

```markdown
普通文本直接书写。

**粗体文本** - 用于强调重要内容

*斜体文本* - 用于术语或轻度强调

~~删除线文本~~ - 用于标记废弃内容

`行内代码` - 用于代码、命令、文件名等

> 引用文本 - 用于引用他人观点或重要提示
> 可以多行
```

### 2.3 列表

**无序列表：**
```markdown
- 列表项一
- 列表项二
  - 嵌套列表项
  - 嵌套列表项
- 列表项三
```

**有序列表：**
```markdown
1. 第一步
2. 第二步
   1. 子步骤 a
   2. 子步骤 b
3. 第三步
```

### 2.4 代码块

**带语言标识和标题的代码块：**

````markdown
```kotlin title="CarPropertyManager.kt"
// 代码内容
val car = Car.createCar(context)
```
````

**支持的常用语言标识：**
- `kotlin`, `java` - Android/JVM
- `typescript`, `javascript` - Web
- `python` - Python
- `bash`, `shell` - 命令行
- `yaml`, `json`, `xml` - 配置文件
- `sql` - 数据库
- `markdown` - Markdown

### 2.5 表格

```markdown
| 列1标题 | 列2标题 | 列3标题 |
|:---|:---:|---:|
| 左对齐 | 居中对齐 | 右对齐 |
| 内容 | 内容 | 内容 |
```

**表格规范：**
- 表头使用粗体或直接文字
- 使用 `:` 控制对齐方式
- 表格前后各空一行

### 2.6 图片

**本地图片：**
```markdown
![图片描述](./images/image-name.png)
```

**网络图片：**
```markdown
![图片描述](https://example.com/path/to/image.jpg)
```

**带说明的图片：**
```markdown
![Android Automotive 架构](./images/android-auto-architecture.svg)

*图片说明：架构图展示了 AAOS 的分层设计*
```

**图片规范：**
- 必须提供 alt 文本（图片描述）
- 本地图片放在 `./images/` 目录
- 建议使用 SVG 格式的图表
- 图片后可添加斜体说明

### 2.7 链接

**行内链接：**
```markdown
[链接文字](https://example.com)
```

**站内文章引用：**
```markdown
[相关文章标题](/blog/category/article-slug)
```

**带标题的链接：**
```markdown
[Android 官方文档](https://developer.android.com "Android Developers")
```

### 2.8 引用块和提示框

**标准引用：**
```markdown
> 这是一段引用内容
> 可以跨多行
```

**提示框（Callout）：**
```markdown
<Callout type="info">
这是信息提示内容
</Callout>

<Callout type="tip">
这是技巧提示内容
</Callout>

<Callout type="warning">
这是警告提示内容
</Callout>
```

**Callout 类型：**
- `info` - 信息说明（蓝色）
- `tip` - 技巧建议（绿色）
- `warning` - 警告注意（黄色）
- `error` - 错误危险（红色）

### 2.9 分隔线

```markdown
---
```

用于分隔文章的不同部分或在文末分隔正文与附加信息。

---

## 3. 文章结构模板

```markdown
---
title: 文章标题
date: 2025-01-05
description: 文章描述
tags: [标签1, 标签2]
category: 技术
draft: false
---

## 引言/概述

简要介绍文章主题和读者将学到什么。

## 背景知识（可选）

必要的前置知识说明。

## 核心内容

### 小节 1

内容...

### 小节 2

内容...

## 实践/示例

代码示例或实操步骤。

## 常见问题（可选）

Q&A 或 troubleshooting。

## 总结

关键要点回顾和下一步建议。

## 相关文章

- [相关文章1](/blog/category/article1)
- [相关文章2](/blog/category/article2)

---

*如有疑问，欢迎留言讨论！*
```

---

## 4. 格式检查

写作完成后，必须使用 `article-format-checklist.md` 检查清单验证文章格式。

详细检查项请参考：[references/article-format-checklist.md](./article-format-checklist.md)
