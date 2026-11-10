# Diagram Best Practices and Common Mistakes

Advanced layout strategies, optimization techniques, and troubleshooting guide for creating professional technical diagrams.

## Layout Strategies

### 1. Grid-Based Positioning

Use consistent spacing for professional appearance:

```
Minimum spacing between elements: 150-200px
Grid units: Multiples of 50 (50, 100, 150, 200...)
Alignment: Snap to grid for clean layout
```

**Example coordinates:**
```
Element 1: (100, 100) - 200x80
Element 2: (100, 230) - 200x80  // 150px vertical gap
Element 3: (350, 100) - 200x80  // 250px horizontal gap
```

### 2. Flow Direction Patterns

#### Top-to-Bottom (Vertical)
Best for: Process flows, hierarchical structures, sequence diagrams

```
       ┌──────┐
       │ Start│
       └───┬──┘
           │
       ┌───▼──┐
       │Process│
       └───┬──┘
           │
       ┌───▼──┐
       │  End │
       └──────┘
```

#### Left-to-Right (Horizontal)
Best for: Data pipelines, request/response flows, timelines

```
┌──────┐    ┌──────┐    ┌──────┐    ┌──────┐
│Source│───▶│Process│───▶│Transform│───▶│Sink │
└──────┘    └──────┘    └──────┘    └──────┘
```

#### Layered (Tiered)
Best for: Architecture diagrams, OSI model, MVC patterns

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
├─────────────────────────────────────────┤
│         Business Logic Layer            │
├─────────────────────────────────────────┤
│         Data Access Layer               │
├─────────────────────────────────────────┤
│         Database Layer                  │
└─────────────────────────────────────────┘
```

### 3. Component Grouping

Group related elements visually:

**Clustering Strategy:**
- Related services: 200px apart
- Different subsystems: 400-500px apart
- External systems: Separate with visual boundaries

**Visual Grouping Techniques:**
- Use background rectangles for subsystem boundaries
- Color-code related components
- Add spacing between logical groups

## Color Schemes

### Standard Color Palette

```
Primary Components:    #1e90ff (Blue)
Secondary/Support:     #6c757d (Gray)
Databases/Storage:     #28a745 (Green)
External Services:     #fd7e14 (Orange)
Error/Critical:        #dc3545 (Red)
Success/Completion:    #28a745 (Green)
Warning/Attention:     #ffc107 (Yellow)
```

### Color Usage Guidelines

1. **Consistency**: Use same color for same component types
2. **Contrast**: Ensure text is readable on background colors
3. **Accessibility**: Avoid relying solely on color for meaning
4. **Restraint**: Limit to 3-4 colors per diagram

## Text Positioning

### Centered Text in Shapes

Calculate text position for centering:

```javascript
// Given shape at (shapeX, shapeY) with dimensions (shapeWidth, shapeHeight)
// And text dimensions (textWidth, textHeight)

textX = shapeX + (shapeWidth - textWidth) / 2
textY = shapeY + (shapeHeight - textHeight) / 2
```

**Example:**
```
Shape: x=100, y=100, width=200, height=80
Text: width=120, height=20

textX = 100 + (200 - 120) / 2 = 140
textY = 100 + (80 - 20) / 2 = 130
```

### Text Size Guidelines

- **Component labels**: 18-24px
- **Connection labels**: 14-16px
- **Annotations**: 12-14px
- **Titles**: 24-32px

## Arrow Routing

### Avoid Line Crossings

**Bad:**
```
A ───┐     ┌─── C
     └──X──┘
     ┌──X──┐
B ───┘     └─── D
```

**Good:**
```
A ─────────────▶ C
    ↓
B ──┴───────────▶ D
```

### Connection Patterns

#### Straight Connections
Use for: Direct dependencies, simple flows
```
points: [[0, 0], [150, 0]]  // Horizontal
points: [[0, 0], [0, 150]]  // Vertical
```

#### Right-Angle Connections
Use for: Organized layouts, avoiding crossings
```
points: [[0, 0], [100, 0], [100, 80]]  // L-shape
```

#### Multi-Segment Connections
Use for: Complex routing around obstacles
```
points: [[0, 0], [50, 0], [50, 100], [150, 100]]
```

## Common Diagram Patterns

### Microservices Architecture Template

```
Positioning:
- Frontend: (100, 50)
- API Gateway: (400, 50)
- Services: (100, 200), (250, 200), (400, 200)
- Databases: (100, 350), (250, 350), (400, 350)
- Message Queue: (700, 200)

Connections:
- Frontend → API Gateway (horizontal)
- API Gateway → Services (vertical + horizontal)
- Services → Databases (vertical)
- Services ↔ Message Queue (horizontal)
```

### Sequence Diagram Template

```
Positioning:
- Actors (vertical): x=100, 300, 500, 700
- All at y=50 (top alignment)
- Lifelines: Vertical lines from y=100 to y=500
- Messages: Horizontal arrows at y=150, 200, 250, 300...

Arrow Directions:
- Request: Left → Right
- Response: Right → Left
- Self-call: Actor → Self (loop back)
```

### Data Flow Template

```
Positioning:
- Source: (100, 200)
- Processors: (350, 200), (600, 200)
- Sink: (850, 200)
- All aligned horizontally

Connections:
- All horizontal with right-pointing arrows
- Labels above arrows showing data format
```

## Common Mistakes and Solutions

### Mistake 1: Text Not Displaying

**Problem:** Text added as shape property
```json
{"type": "rectangle", "text": "Label"}  // ❌ Won't display
```

**Solution:** Create independent text element with binding
```json
{"type": "rectangle", "boundElements": [{"type": "text", "id": "text1"}]},
{"type": "text", "id": "text1", "containerId": "rect1", "text": "Label"}  // ✅
```

### Mistake 2: Arrows Not Connecting Properly

**Problem:** Using width/height instead of points
```json
{"type": "arrow", "width": 100, "height": 50}  // ❌ Invalid
```

**Solution:** Use points array
```json
{"type": "arrow", "points": [[0, 0], [100, 50]]}  // ✅
```

### Mistake 3: Overlapping Elements

**Problem:** Insufficient spacing between components

**Solution:** Use consistent grid spacing
- Minimum 150px between elements
- 200-250px for comfortable spacing
- 400-500px between different subsystems

### Mistake 4: Inconsistent Sizing

**Problem:** Different sizes for same component types

**Solution:** Standardize dimensions
- Services: 200x80 or 180x70
- Databases: 180x80 (same height as services)
- Small components: 120x60
- Large systems: 300x100

### Mistake 5: Poor Color Contrast

**Problem:** Light text on light background, dark on dark

**Solution:** High contrast combinations
- Black text (#000000) on light backgrounds
- White text (#ffffff) on dark backgrounds (#1e90ff, #28a745, #dc3545)
- Test readability before finalizing

### Mistake 6: Unclear Flow Direction

**Problem:** Arrows without direction indicators

**Solution:** Always use arrowheads
- Set `endArrowhead: "arrow"` for directional flow
- Use `startArrowhead: null` for one-way
- Use both for bidirectional (rare in technical diagrams)

### Mistake 7: Cramped Layout

**Problem:** Elements too close together

**Solution:** Add whitespace
- Minimum 150px gaps
- Group related elements but separate groups with 300-400px
- Leave margins around the diagram (50-100px)

### Mistake 8: Missing Context

**Problem:** Diagram without title or legend

**Solution:** Add context elements
- Title at top: Large text (24-32px)
- Legend if using colors: Bottom-right corner
- Annotations for complex relationships

## Performance Optimization

### Large Diagrams (50+ Elements)

**Strategies:**
1. **Split into multiple diagrams**: One per subsystem
2. **Use abstraction**: Show high-level, link to detailed views
3. **Limit connections**: Show only critical dependencies
4. **Group elements**: Use grouping feature to manage complexity

### Element Reuse

**Template Pattern:**
```javascript
// Define base template
const serviceTemplate = {
  type: "rectangle",
  width: 200,
  height: 80,
  strokeColor: "#1e90ff",
  backgroundColor: "#e3f2fd",
  // ... other common fields
}

// Create instances with position override
const userService = {...serviceTemplate, x: 100, y: 100, id: "service1"}
const orderService = {...serviceTemplate, x: 100, y: 230, id: "service2"}
```

## Validation Checklist

Before finalizing a diagram:

- [ ] All text is bound correctly (using containerId)
- [ ] All arrows use points array (not width/height)
- [ ] All elements have required fields (angle, fillStyle, etc.)
- [ ] Color scheme is consistent
- [ ] Text is readable (proper size and contrast)
- [ ] Layout is balanced (no cramped areas)
- [ ] Flow direction is clear
- [ ] Connection crossings are minimized
- [ ] Elements are properly aligned
- [ ] Spacing is consistent

## Troubleshooting

### "Invalid file" error on import

1. Check all elements have required fields (see excalidraw-file-format.md)
2. Verify text elements are independent (not shape properties)
3. Ensure arrows use points array
4. Confirm root object has "files": {} field

### Text not visible

1. Check text has containerId linking to parent shape
2. Verify parent shape has boundElements declaration
3. Ensure text coordinates are inside parent boundaries
4. Check fontSize is not too small (<12px)

### Arrows not rendering

1. Verify points array exists and is valid
2. Check endArrowhead or startArrowhead is set
3. Ensure width and height are 0 (arrows don't use them)
4. Confirm coordinates are reasonable (not off-canvas)

### Layout looks messy

1. Use grid-based positioning (multiples of 50)
2. Increase spacing between elements (minimum 150px)
3. Align elements horizontally or vertically
4. Group related components visually
5. Reduce line crossings by rerouting arrows
