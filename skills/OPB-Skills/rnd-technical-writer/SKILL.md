---
name: rnd-technical-writer
description: Technical article writing assistant. Use when users need to (1) Write complete technical blog articles or tutorials, (2) Structure developer-focused content with code examples, (3) Create technical documentation with proper formatting, (4) Draft engaging technical posts for publication. Trigger keywords: 写技术文章、技术博客、写教程、technical article, blog post, tutorial writing, technical content, developer blog.
---

# Technical Article Writer

Write complete, engaging technical blog articles with proper structure, clear explanations, practical code examples, and professional quality suitable for publication.

## When to Use This Skill

Use this skill when you need to:
- Write a complete technical blog article or tutorial
- Structure developer-focused content
- Create engaging technical posts with code examples
- Draft articles for publication on tech blogs or documentation sites

**Note:** For planning multi-article series, use `rnd-blog-series-planner` skill instead.

## Article Writing Workflow

### Step 1: Opening Section

**Hook Creation Patterns:**

```markdown
# Pattern 1: Pain Point Hook
"如果你曾经花了三个小时调试一个Docker容器，最后发现是少了一个环境变量，
那么这篇文章就是为你准备的..."

# Pattern 2: Question Hook
"为什么你的API响应时间总是比预期慢？答案可能不在代码里..."

# Pattern 3: Scenario Hook
"周五下午5点，生产环境突然报警，日志显示OOM错误。这时候你会怎么办？"
```

**Context Setting Elements:**
- Background information
- Problem statement clarity
- Relevance establishment
- Scope definition
- Expected outcomes preview

### Step 2: Main Content Development

**Section Structure Template:**

```markdown
## Section Title

### Key Concept Introduction
[Brief explanation of what this section covers]

### Detailed Explanation
[In-depth technical content with examples]

### Practical Application
[How to apply this concept in real scenarios]

### Common Pitfalls
[Mistakes to avoid and troubleshooting tips]
```

**Content Elements to Include:**
- Clear explanations with analogies
- Code examples with inline comments
- Configuration snippets
- Command outputs
- Diagrams and flowcharts (describe or use ASCII)
- Real-world use cases

**Writing Style Guidelines:**
- **Professional yet conversational**: Technical accuracy with approachability
- **Humor where appropriate**: Light developer humor, self-deprecating anecdotes
- **"我踩过的坑" moments**: Share real debugging experiences
- **Balance depth and readability**: 深入源码，但不堆砌代码

**Core Principles:**
- **技术深度与可读性平衡**: 理论结合实践，案例驱动学习，图文并茂降低门槛
- **实用性优先**: 提供可运行的Demo代码，分享可直接使用的工具脚本，真实案例解决实际问题
- **知识体系化**: 文章间建立逻辑关联，前后呼应，循序渐进，形成完整方法论体系

### Step 3: Code Examples

**Code Block Format:**

````markdown
```language
// Filename: example.js
// Description: What this code demonstrates

// Step 1: Setup
const config = {
  // Configuration explanation
};

// Step 2: Implementation
function doSomething() {
  // Logic explanation
}

// Step 3: Usage
doSomething();
// Output: expected result
```
````

**Code Quality Requirements:**
- Working, tested code
- Proper syntax highlighting
- Inline comments for clarity
- Error handling included
- Both success and failure cases shown

### Step 4: Visual Elements

**Diagram Placeholders:**

```markdown
[图片: Architecture diagram showing component interactions]
[Description for designer/creator]

[图片: Screenshot of successful deployment]
[Where to capture this screenshot]

[图片: Flowchart of the decision process]
[Mermaid/ASCII diagram or description]
```

**ASCII Diagrams for Quick Visualization:**

```
┌─────────┐     ┌─────────┐     ┌─────────┐
│ Client  │────▶│   API   │────▶│   DB    │
└─────────┘     └─────────┘     └─────────┘
```

**For complex diagrams**: Suggest using `rnd-tech-arch-diagrammer` skill

### Step 5: Closing Section

**Summary Elements:**
- Key takeaways recap (3-5 bullet points)
- Main concepts reinforcement
- Practical action items
- Further reading suggestions
- Next steps guidance

**Call to Action:**
- Comment invitation
- Social sharing encouragement
- Newsletter subscription
- Related article links
- Community engagement

**Example Closing:**

```markdown
## 总结

今天我们学习了三个关键点:
1. [Key Point 1] - 为什么它很重要
2. [Key Point 2] - 如何正确实现
3. [Key Point 3] - 避免常见错误

下一步，你可以:
- [ ] 在本地环境尝试本文的示例代码
- [ ] 将学到的技巧应用到你的项目中
- [ ] 阅读系列下一篇: [Next Article Title]

有任何问题，欢迎在评论区留言讨论！
```

## Article Structure Template

```markdown
# [Article Title]

> [Subtitle or tagline - one compelling sentence]

## 快速总结（可选）
[3-5 bullet points summarizing key takeaways]

## 引言
[Hook + Context + Promise of value]

## 背景知识 (如需要)
[Prerequisites and foundational concepts]

## 核心内容

### [Section 1 Title]
[Content with code examples...]

### [Section 2 Title]
[Content with explanations...]

### [Section 3 Title]
[Content with practical applications...]

## 实战演练
[Hands-on tutorial or exercise]

## 常见问题
[FAQ or troubleshooting section]

## 总结
[Recap and next steps]

## 参考资料
- [Reference 1]
- [Reference 2]

---
*[Author bio or article metadata]*
```

## Format Standards & Templates

**IMPORTANT**: All technical articles **MUST** follow the standardized format specifications to ensure consistency and professional quality.

### Markdown Format Specification

For complete Markdown format requirements, including:
- **Frontmatter structure** (YAML metadata)
- **Heading hierarchy** rules
- **Code block** formatting with language identifiers
- **Image and link** conventions
- **Table and list** standards
- **Callout boxes** for tips and warnings

See detailed specification: [references/article-format-template.md](references/article-format-template.md)

### Format Quality Checklist

**Before publishing any article**, verify compliance using the comprehensive format checklist covering:
- Frontmatter completeness
- Heading structure validity
- Code block syntax
- Image alt text and paths
- Link formatting
- Table alignment
- File encoding (UTF-8)

Complete checklist: [references/article-format-checklist.md](references/article-format-checklist.md)

**Key Requirements Summary**:
- ✅ YAML frontmatter with title, date, description, tags, category
- ✅ Start content with H2 (##), no H1 in body
- ✅ Language-tagged code blocks (```kotlin, ```python, etc.)
- ✅ Alt text for all images
- ✅ No heading level skips (## → ### is OK, ## → #### is NOT)

## Word Count Guidelines

| Article Type | Word Count | Sections | Writing Time |
|-------------|------------|----------|--------------|
| Quick Tip | 800-1,200 | 3-4 | 1-2 hours |
| Tutorial | 2,000-3,000 | 5-7 | 3-5 hours |
| Deep Dive | 4,000-6,000 | 8-12 | 6-10 hours |
| Comprehensive Guide | 6,000+ | 10+ | 10+ hours |

## Quality Checklist

### Technical Quality

- [ ] All code examples tested and working
- [ ] Commands and outputs verified
- [ ] Version numbers accurate
- [ ] Links functional
- [ ] Technical claims supported

### Writing Quality

- [ ] Clear and logical flow
- [ ] Appropriate reading level for target audience
- [ ] Engaging opening that hooks the reader
- [ ] Strong conclusion with actionable next steps
- [ ] Proper grammar and spelling
- [ ] Technical terminology used correctly

### SEO Optimization (Optional)

- [ ] Keyword-rich title (60 characters or less)
- [ ] Descriptive headings (H2, H3)
- [ ] Alt text for images
- [ ] Internal linking to related content
- [ ] Meta description ready (150-160 characters)

## Review Checklist

### Pre-Publication

- [ ] Technical accuracy verified by testing
- [ ] Code examples run successfully
- [ ] Links checked and functional
- [ ] Images/diagrams ready (or placeholders documented)
- [ ] Formatting consistent throughout
- [ ] Spelling and grammar checked
- [ ] Mobile readability verified
- [ ] SEO elements completed (if applicable)

### Post-Publication

- [ ] Social media promotion planned
- [ ] Community sharing (Reddit, HN, etc.)
- [ ] Comment monitoring setup
- [ ] Feedback collection mechanism
- [ ] Analytics tracking configured

## Writing Tips

### Opening Hooks That Work

1. **Personal Story**: "Last week, I spent three hours debugging..."
2. **Question**: "Have you ever wondered why...?"
3. **Statistic**: "95% of developers encounter this issue..."
4. **Bold Claim**: "There's a better way to do X that nobody talks about"
5. **Scenario**: "Imagine you're deploying to production and..."

### Code Example Best Practices

- **Start simple**: Basic example first, then build complexity
- **Explain why**: Don't just show what, explain why it works
- **Show alternatives**: Mention other approaches and trade-offs
- **Include gotchas**: Warn about common mistakes
- **Provide context**: Where would this code be used?

### Maintaining Reader Engagement

- **Break up text**: Use code blocks, diagrams, quotes
- **Vary sentence length**: Mix short and long sentences
- **Use transitions**: "Now that we've...", "Next, let's...", "Finally..."
- **Ask rhetorical questions**: Keep reader thinking
- **Use examples**: Abstract concepts → Concrete examples

### Common Writing Pitfalls to Avoid

- ❌ **Assuming too much knowledge**: Define prerequisites clearly
- ❌ **Code without context**: Always explain what code does
- ❌ **Inconsistent tone**: Pick casual or formal, stick with it
- ❌ **No visuals**: Add diagrams for complex concepts
- ❌ **Weak conclusion**: Summarize and provide next steps
- ❌ **Untested code**: All examples must work as shown
- ❌ **Missing error handling**: Show both success and failure

## Example Workflow

**User Request**: "帮我写一篇关于Docker容器优化的技术文章"

**Your Response**:
1. **Clarify requirements**:
   - Target audience? (Beginner/Intermediate/Advanced)
   - Article length? (Quick tip / Tutorial / Deep dive)
   - Specific optimization areas? (Image size / Networking / Performance)

2. **Create outline**:
   - Title: "Docker容器优化实战指南：让你的容器又快又轻"
   - Sections: 介绍、镜像优化、网络优化、性能监控、总结
   - Estimated length: 3000 words

3. **Write article** following the structure template
4. **Include code examples** for each optimization technique
5. **Add diagrams** (or use ASCII art for before/after comparisons)
6. **Provide checklist** for readers to verify their optimization

Always prioritize **reader value** and **practical applicability** over technical showmanship.
