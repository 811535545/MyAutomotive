# 技术规格说明书模板

## 目录
- [文档结构](#文档结构)
- [完整模板](#完整模板)
- [API 规格](#api-规格)
- [数据模型规格](#数据模型规格)
- [示例](#示例)

---

## 文档结构

```
1. 概述
2. 系统架构
3. API 设计
4. 数据模型
5. 安全设计
6. 性能设计
7. 错误处理
8. 测试策略
9. 部署方案
```

## 完整模板

```markdown
# [功能名称] 技术规格说明书

| 属性 | 值 |
|------|-----|
| 版本 | v1.0 |
| 作者 | [姓名] |
| 创建日期 | YYYY-MM-DD |
| 关联 PRD | [PRD链接] |
| 状态 | 草稿/评审中/已批准 |

---

## 1. 概述

### 1.1 目的
[本文档描述 XXX 功能的技术实现方案]

### 1.2 范围
[涉及的系统、模块、服务]

### 1.3 术语定义
| 术语 | 定义 |
|-----|------|

---

## 2. 系统架构

### 2.1 架构图
[系统架构图]

### 2.2 组件说明
| 组件 | 职责 | 技术栈 |
|-----|------|--------|
| [组件1] | [职责] | [技术] |

### 2.3 数据流
[数据流向描述]

### 2.4 依赖服务
| 服务 | 用途 | 接口 |
|-----|------|------|

---

## 3. API 设计

### 3.1 接口列表
| 方法 | 路径 | 描述 |
|-----|------|------|
| POST | /api/v1/xxx | [描述] |

### 3.2 接口详情

#### POST /api/v1/xxx

**描述**
[接口功能描述]

**请求头**
| 名称 | 类型 | 必填 | 描述 |
|-----|------|-----|------|
| Authorization | string | 是 | Bearer token |

**请求体**
```json
{
  "field1": "string",
  "field2": 123
}
```

**响应**
```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

**错误码**
| 错误码 | 描述 | 处理方式 |
|-------|------|---------|
| 400 | 参数错误 | 检查请求参数 |
| 401 | 未授权 | 重新登录 |

---

## 4. 数据模型

### 4.1 数据库表设计

#### 表名：xxx_table
| 字段 | 类型 | 约束 | 描述 |
|-----|------|-----|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| name | VARCHAR(100) | NOT NULL | 名称 |
| created_at | TIMESTAMP | DEFAULT NOW() | 创建时间 |

**索引**
| 索引名 | 字段 | 类型 |
|-------|-----|------|
| idx_name | name | BTREE |

### 4.2 数据关系
[ER图或关系描述]

---

## 5. 安全设计

### 5.1 认证方式
- JWT Token
- Token 有效期：7天

### 5.2 权限控制
| 操作 | 权限要求 |
|-----|---------|
| 读取 | 登录用户 |
| 修改 | 资源所有者 |
| 删除 | 管理员 |

### 5.3 数据安全
- 敏感字段加密存储
- SQL 注入防护
- XSS 防护

---

## 6. 性能设计

### 6.1 性能目标
| 指标 | 目标值 |
|-----|--------|
| P99 延迟 | < 500ms |
| QPS | 1000 |

### 6.2 优化策略
- 缓存策略：[描述]
- 数据库优化：[描述]
- 异步处理：[描述]

### 6.3 限流策略
- 接口级别：100 QPS/用户
- 全局级别：10000 QPS

---

## 7. 错误处理

### 7.1 错误码规范
| 范围 | 类型 |
|-----|------|
| 1000-1999 | 参数错误 |
| 2000-2999 | 业务错误 |
| 3000-3999 | 系统错误 |

### 7.2 异常处理流程
[异常处理和降级策略]

---

## 8. 测试策略

### 8.1 单元测试
- 覆盖率要求：> 80%
- 关键测试点：[列表]

### 8.2 集成测试
[测试场景]

### 8.3 性能测试
[测试方案]

---

## 9. 部署方案

### 9.1 部署架构
[部署图]

### 9.2 配置项
| 配置 | 描述 | 默认值 |
|-----|------|--------|

### 9.3 发布策略
- [ ] 灰度发布
- [ ] 蓝绿部署
- [ ] 金丝雀发布

### 9.4 回滚方案
[回滚步骤]

---

## 10. 附录

### 修订历史
| 版本 | 日期 | 作者 | 变更 |
|-----|------|-----|------|
```

## API 规格

### RESTful API 设计原则

```
# 资源命名
GET    /api/v1/users          # 获取用户列表
GET    /api/v1/users/{id}     # 获取单个用户
POST   /api/v1/users          # 创建用户
PUT    /api/v1/users/{id}     # 更新用户（全量）
PATCH  /api/v1/users/{id}     # 更新用户（部分）
DELETE /api/v1/users/{id}     # 删除用户

# 嵌套资源
GET    /api/v1/users/{id}/orders    # 获取用户订单
POST   /api/v1/users/{id}/orders    # 为用户创建订单

# 查询参数
GET    /api/v1/users?page=1&size=20&sort=created_at:desc
GET    /api/v1/users?filter[status]=active
```

### 统一响应格式

```json
// 成功响应
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "name": "example"
  }
}

// 分页响应
{
  "code": 0,
  "message": "success",
  "data": {
    "items": [],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 100,
      "pages": 5
    }
  }
}

// 错误响应
{
  "code": 1001,
  "message": "参数错误",
  "errors": [
    {"field": "email", "message": "邮箱格式不正确"}
  ]
}
```

## 数据模型规格

### 字段命名规范

```sql
-- 通用字段
id              -- 主键
created_at      -- 创建时间
updated_at      -- 更新时间
deleted_at      -- 软删除时间
created_by      -- 创建人
updated_by      -- 更新人

-- 状态字段
status          -- 状态（枚举）
is_active       -- 是否启用（布尔）
is_deleted      -- 是否删除（布尔）

-- 外键
user_id         -- 用户ID
order_id        -- 订单ID
```

### 数据类型选择

| 场景 | 推荐类型 |
|-----|---------|
| 主键 | BIGINT UNSIGNED |
| 金额 | DECIMAL(20, 4) |
| 状态枚举 | TINYINT |
| 短文本 | VARCHAR(255) |
| 长文本 | TEXT |
| 时间戳 | TIMESTAMP |
| JSON数据 | JSON |
| UUID | CHAR(36) 或 BINARY(16) |

## 示例

### API 接口示例

```markdown
#### POST /api/v1/auth/login

**描述**
用户登录，获取访问令牌

**请求体**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**响应 200**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIs...",
    "token_type": "Bearer",
    "expires_in": 604800,
    "user": {
      "id": 1,
      "email": "user@example.com",
      "name": "用户名"
    }
  }
}
```

**错误响应**
| 状态码 | code | 描述 |
|-------|------|------|
| 400 | 1001 | 邮箱格式不正确 |
| 401 | 2001 | 邮箱或密码错误 |
| 429 | 3001 | 请求过于频繁 |
```

### 数据表设计示例

```markdown
#### 表名：users

| 字段 | 类型 | 约束 | 描述 |
|-----|------|-----|------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | 用户ID |
| email | VARCHAR(255) | UNIQUE, NOT NULL | 邮箱 |
| password_hash | VARCHAR(255) | NOT NULL | 密码哈希 |
| name | VARCHAR(100) | NOT NULL | 用户名 |
| avatar_url | VARCHAR(500) | NULL | 头像URL |
| status | TINYINT | DEFAULT 1 | 状态：0禁用，1正常 |
| last_login_at | TIMESTAMP | NULL | 最后登录时间 |
| created_at | TIMESTAMP | DEFAULT NOW() | 创建时间 |
| updated_at | TIMESTAMP | ON UPDATE NOW() | 更新时间 |

**索引**
| 索引名 | 字段 | 类型 | 说明 |
|-------|-----|------|------|
| uk_email | email | UNIQUE | 邮箱唯一索引 |
| idx_status | status | BTREE | 状态查询索引 |
```
