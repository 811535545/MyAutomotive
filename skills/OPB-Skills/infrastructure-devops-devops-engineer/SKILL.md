---
name: infrastructure-devops-devops-engineer
description: |
  DevOps助手 - 专业的DevOps实践与自动化专家。适用场景：
  (1) CI/CD流水线设计与实现
  (2) 部署策略与发布管理
  (3) 基础设施即代码（IaC）
  (4) 容器化与Kubernetes部署
  (5) 监控告警与可观测性
  (6) DevOps工具链选型
  (7) DevOps文化与实践推广
  触发关键词：DevOps、CI/CD、持续集成、持续部署、Jenkins、GitLab CI、GitHub Actions、Docker、Kubernetes、Terraform、自动化部署
---

# DevOps助手

## 核心工作流程

### 1. CI/CD流水线设计

**CI/CD流水线阶段：**
```
持续集成（CI）
├── 代码提交
│   ├── 代码规范检查（Lint）
│   ├── 提交信息规范
│   └── 触发构建
├── 自动构建
│   ├── 依赖安装
│   ├── 编译构建
│   └── 单元测试
├── 质量门禁
│   ├── 代码覆盖率
│   ├── 静态代码分析
│   └── 安全扫描
└── 制品管理
    ├── 版本标记
    ├── 制品上传
    └── 元数据记录

持续部署（CD）
├── 环境准备
│   ├── 基础设施配置
│   ├── 配置管理
│   └── 密钥注入
├── 部署执行
│   ├── 部署策略执行
│   ├── 健康检查
│   └── 冒烟测试
├── 验证确认
│   ├── 自动化测试
│   ├── 性能验证
│   └── 人工确认（可选）
└── 发布完成
    ├── 流量切换
    ├── 监控确认
    └── 回滚准备
```

**流水线配置示例（GitLab CI）：**
```yaml
stages:
  - build
  - test
  - security
  - deploy

build:
  stage: build
  script:
    - npm install
    - npm run build
  artifacts:
    paths:
      - dist/

test:
  stage: test
  script:
    - npm run test:coverage
  coverage: '/Coverage: \d+%/'

security_scan:
  stage: security
  script:
    - npm audit
    - trivy image $IMAGE

deploy_staging:
  stage: deploy
  environment: staging
  script:
    - kubectl apply -f k8s/
  only:
    - develop

deploy_prod:
  stage: deploy
  environment: production
  script:
    - kubectl apply -f k8s/
  when: manual
  only:
    - main
```

### 2. 部署策略

**部署策略对比：**

| 策略 | 特点 | 风险 | 适用场景 |
|------|------|------|----------|
| 滚动部署 | 逐步替换实例 | 中 | 无状态服务 |
| 蓝绿部署 | 两套环境切换 | 低 | 需要快速回滚 |
| 金丝雀部署 | 小流量验证 | 低 | 需要灰度验证 |
| A/B测试 | 按特征分流 | 低 | 功能对比测试 |
| 重建部署 | 先停后启 | 高 | 开发/测试环境 |

**部署策略详解：**
```
滚动部署（Rolling）
├── 逐批替换旧版本Pod
├── 配置：maxSurge/maxUnavailable
├── 优点：资源占用少
└── 缺点：回滚较慢

蓝绿部署（Blue-Green）
├── 同时运行两套环境
├── 切换负载均衡指向
├── 优点：快速回滚
└── 缺点：资源成本翻倍

金丝雀部署（Canary）
├── 小比例流量到新版本
├── 逐步增加流量比例
├── 优点：风险最小
└── 缺点：实现复杂

渐进式交付（Progressive）
├── 自动化金丝雀+分析
├── 基于指标自动推进/回滚
├── 工具：Argo Rollouts/Flagger
└── 优点：智能化、低风险
```

### 3. 基础设施即代码（IaC）

**IaC工具选型：**
```
配置管理
├── Ansible：无代理、YAML
├── Chef：Ruby DSL、中心化
├── Puppet：声明式、大规模
└── SaltStack：高性能、Python

基础设施编排
├── Terraform：多云、状态管理
├── Pulumi：通用编程语言
├── CloudFormation：AWS原生
└── ARM/Bicep：Azure原生

容器编排
├── Kubernetes：容器编排标准
├── Docker Compose：单机编排
├── Helm：K8s包管理
└── Kustomize：K8s配置管理
```

**Terraform最佳实践：**
```
目录结构
├── modules/           # 可复用模块
│   ├── vpc/
│   ├── ecs/
│   └── rds/
├── environments/      # 环境配置
│   ├── dev/
│   ├── staging/
│   └── prod/
├── main.tf           # 主配置
├── variables.tf      # 变量定义
├── outputs.tf        # 输出定义
└── backend.tf        # 状态后端

代码规范
├── 使用模块化设计
├── 变量使用类型约束
├── 敏感信息使用变量/Vault
├── 资源命名规范统一
└── 状态文件远程存储
```

### 4. 容器化与Kubernetes

**容器化最佳实践：**
```
Dockerfile优化
├── 使用多阶段构建
├── 最小化基础镜像
├── 合并RUN指令
├── 使用.dockerignore
├── 非root用户运行
└── 固定依赖版本

镜像安全
├── 扫描已知漏洞
├── 签名验证
├── 使用可信基础镜像
└── 定期更新基础镜像
```

**Kubernetes部署配置：**
```yaml
# Deployment示例
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    spec:
      containers:
      - name: app
        image: app:v1.0
        resources:
          requests:
            cpu: 100m
            memory: 128Mi
          limits:
            cpu: 500m
            memory: 512Mi
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /ready
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```

### 5. 监控与可观测性

**可观测性三支柱：**
```
Metrics（指标）
├── 系统指标：CPU/内存/磁盘/网络
├── 应用指标：QPS/延迟/错误率
├── 业务指标：订单数/转化率
├── 工具：Prometheus + Grafana
└── 最佳实践：RED/USE方法

Logs（日志）
├── 结构化日志
├── 日志级别规范
├── 关联追踪ID
├── 工具：ELK/Loki
└── 最佳实践：统一格式、集中管理

Traces（链路追踪）
├── 分布式追踪
├── 服务调用链
├── 性能瓶颈定位
├── 工具：Jaeger/Zipkin/SkyWalking
└── 最佳实践：采样策略、上下文传递
```

**告警设计原则：**

| 原则 | 说明 | 实践 |
|------|------|------|
| 可行动 | 收到告警能采取行动 | 避免无意义告警 |
| 分级 | 不同严重程度不同处理 | P1/P2/P3分级 |
| 聚合 | 相似告警合并 | 避免告警风暴 |
| 抑制 | 依赖故障不重复告警 | 告警依赖关系 |
| 静默 | 计划维护期间静默 | 维护窗口 |

### 6. DevOps工具链

**工具链选型：**

| 领域 | 开源方案 | 商业方案 |
|------|----------|----------|
| 版本控制 | GitLab/Gitea | GitHub/Bitbucket |
| CI/CD | Jenkins/GitLab CI | CircleCI/Travis CI |
| 制品管理 | Nexus/Harbor | JFrog Artifactory |
| 配置管理 | Ansible/Terraform | Puppet Enterprise |
| 监控 | Prometheus/Grafana | Datadog/New Relic |
| 日志 | ELK/Loki | Splunk/Sumo Logic |
| APM | SkyWalking/Jaeger | Dynatrace/AppDynamics |

**DevOps成熟度模型：**
```
Level 1: 初始
├── 手工部署
├── 无版本控制
└── 无自动化测试

Level 2: 可重复
├── 版本控制
├── 基本CI
└── 脚本化部署

Level 3: 已定义
├── 自动化CI/CD
├── 基础设施即代码
└── 自动化测试

Level 4: 可管理
├── 监控告警完善
├── 发布流程规范
└── 度量指标

Level 5: 优化
├── 持续改进
├── 自动化决策
└── 混沌工程
```

## 输出模板

### CI/CD流水线设计文档
```
【项目名称】___________
【设计日期】___________

【流水线概览】
[流水线图]

【阶段详情】
| 阶段 | 任务 | 工具 | 超时 | 失败处理 |
|------|------|------|------|----------|

【环境配置】
| 环境 | 触发条件 | 审批 | 部署策略 |
|------|----------|------|----------|

【质量门禁】
| 检查项 | 阈值 | 阻断/警告 |
|--------|------|----------|

【制品管理】
- 制品类型：
- 存储位置：
- 保留策略：

【回滚策略】
```

### 部署方案文档
```
【应用名称】___________
【部署环境】___________

【部署架构】
[部署架构图]

【部署策略】
- 策略类型：□滚动 □蓝绿 □金丝雀
- 具体配置：

【资源配置】
| 资源 | 规格 | 副本数 | 说明 |
|------|------|--------|------|

【健康检查】
- 就绪探针：
- 存活探针：
- 启动探针：

【发布流程】
1.
2.
3.

【回滚流程】
1.
2.

【监控告警】
| 指标 | 阈值 | 告警级别 |
|------|------|----------|
```

## 最佳实践

- **自动化一切**：能自动化的都自动化
- **小步快跑**：频繁小发布优于大爆炸
- **失败快速**：尽早发现问题，快速修复
- **持续反馈**：监控指标驱动改进
- **文化先行**：技术工具之前是文化转变
