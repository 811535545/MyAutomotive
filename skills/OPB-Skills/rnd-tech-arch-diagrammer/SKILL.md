---
name: rnd-tech-arch-diagrammer
description: Technical architecture diagram creator using Excalidraw MCP tools. Use when users need to (1) Create system architecture diagrams, (2) Draw flowcharts, sequence diagrams, or topology diagrams, (3) Visualize technical concepts (data flows, deployment architectures), (4) Generate documentation diagrams for code or infrastructure. Trigger keywords: 画图、架构图、流程图、时序图、diagram, flowchart, architecture, sequence diagram, data flow, topology, visualization.
---

# Technical Architecture Diagrammer

Create clear, professional technical diagrams using Excalidraw MCP tools. Translate technical concepts into visual representations including architecture diagrams, flowcharts, sequence diagrams, and more.

## When to Use This Skill

Use this skill when you need to:
- Visualize system architecture (microservices, layers, modules)
- Create process flowcharts with decision trees
- Draw sequence diagrams for API interactions
- Design deployment or network topology diagrams
- Illustrate data flows and ETL pipelines
- Document component relationships

## Diagram Types Supported

### 1. 静态架构图 (Static Architecture)
System components, modules, layers, service dependencies
- **Use for**: Microservices architecture, layered architectures, component diagrams
- **Shapes**: Rectangles for services, arrows for dependencies

### 2. 流程图 (Flowcharts)
Process flows, decision trees, business logic
- **Use for**: Algorithm visualization, workflow documentation, decision processes
- **Shapes**: Rectangles (process), diamonds (decision), ellipses (start/end)

### 3. 时序图 (Sequence Diagrams)
Interaction sequences, API calls, message flows
- **Use for**: API interaction flows, authentication sequences, request/response cycles
- **Shapes**: Rectangles (actors/services), arrows (messages/calls)

### 4. 部署图 (Deployment Diagrams)
Infrastructure, servers, containers, cloud services
- **Use for**: Cloud infrastructure, container orchestration, server topology
- **Shapes**: Rectangles (servers/containers), nested groups for clustering

### 5. 数据流图 (Data Flow Diagrams)
Data transformation, ETL pipelines, event streams
- **Use for**: Data processing pipelines, event-driven architectures, ETL workflows
- **Shapes**: Rectangles (processors), arrows (data flow), cylinders (storage)

## Quick Start Workflow

### Step 1: Understand Requirements
- Parse user's description of what needs to be visualized
- Identify all components, actors, and relationships
- Determine the most appropriate diagram type
- Ask clarifying questions if requirements are ambiguous

### Step 2: Plan Layout
- List all elements to be drawn
- Define positioning strategy (top-to-bottom, left-to-right, grid-based)
- Plan connection routing to minimize overlaps
- Choose color scheme if applicable

### Step 3: Create Elements
For each element:
1. Calculate coordinates based on layout plan
2. Create shape with `mcp__Excalidraw__create_element`
3. Add text label (as independent text element with binding)
4. Verify placement before proceeding

**CRITICAL - Text Binding:**
- Text must be **independent elements**, NOT shape properties
- Shape: Add `boundElements: [{"type": "text", "id": "text_id"}]`
- Text: Set `containerId: "shape_id"` to link to parent
- Position text coordinates **inside** the shape boundary

### Step 4: Draw Connections
For each connection:
1. Identify source and target positions
2. Create arrow with `mcp__Excalidraw__create_element` (type: "arrow")
3. Use `points` array for arrow path (relative coordinates)
4. Add labels to connections if needed

### Step 5: Save Diagram
- Export with `mcp__Excalidraw__get_resource` and save to file
- Use descriptive filename: `{描述性名称}.excalidraw` or `.png`
- Examples: `微服务架构图.excalidraw`, `用户登录时序图.excalidraw`

## Excalidraw Element Standards

### Shape Guidelines
- **Rectangles**: Services, components, modules, databases
- **Diamonds**: Decision points in flowcharts
- **Ellipses**: Start/end points, actors, external systems
- **Arrows**: Direction of flow/dependency (use `points` array)

### Layout Strategy
- **Grid-Based**: Consistent spacing (150-200px between elements)
- **Logical Grouping**: Cluster related components visually
- **Flow Direction**: Maintain consistent direction (top→bottom or left→right)
- **Hierarchy**: Use vertical positioning for layers/tiers

### Text Guidelines
- **Font Size**: 16-24 for labels, 14-18 for descriptions
- **Position**: Center text within shapes (use text binding)
- **Language**: Match user's language (Chinese/English)
- **Clarity**: Use concise, meaningful labels

### Color Coding (Optional)
- **Blue** (#1e90ff): Core services, primary components
- **Green** (#28a745): Databases, storage, success paths
- **Orange** (#fd7e14): External services, third-party APIs
- **Red** (#dc3545): Critical paths, error handling
- **Gray** (#6c757d): Infrastructure, supporting components

## Common Patterns

### Microservices Architecture
```
[前端]  →  [API网关]  →  [用户服务]
                ↓
              [订单服务]
                ↓
            [支付服务]
                ↓
            [数据库]
```

### Login Sequence
```
[用户] → [前端] → [API Gateway] → [认证服务] → [数据库]
                                      ↓
                                   [返回Token]
```

### Data Pipeline
```
[Kafka] → [Flink处理] → [ClickHouse] → [Grafana展示]
```

## Quality Checklist

- [ ] All mentioned components represented
- [ ] Connections accurately reflect relationships
- [ ] Text readable and correctly positioned (using binding)
- [ ] Arrows point in correct direction
- [ ] Layout balanced and organized
- [ ] No overlapping elements (unless intentional)
- [ ] Consistent styling throughout
- [ ] Filename describes diagram content

## Error Handling

- If MCP tool fails, retry with adjusted parameters
- If positioning is off, recalculate coordinates
- If diagram becomes too complex, suggest splitting
- Always verify tool execution results before proceeding

## Advanced Topics

For detailed Excalidraw file format specifications and text binding requirements, see:
- **references/excalidraw-file-format.md** - Complete .excalidraw JSON format specification
- **references/diagram-best-practices.md** - Advanced layout strategies and common mistakes

Read these references when:
- Creating complex diagrams with many elements
- Debugging file format errors ("invalid file" on import)
- Implementing advanced features (grouping, locking, distribution)
- Troubleshooting text display issues

## Communication Style

- Explain diagram design decisions briefly
- Confirm understanding of complex requirements before drawing
- Provide summary of what was created after completion
- Suggest improvements or alternative visualizations when appropriate

Respond in 中文 by default, matching user's language preference. Use English for technical terms (API, Database, Service) but provide Chinese labels in diagrams when user communicates in Chinese.
