# Excalidraw File Format Specification

Complete specification for creating valid `.excalidraw` files that can be imported to excalidraw.com without errors.

## File Structure

```json
{
  "type": "excalidraw",
  "version": 2,
  "source": "https://excalidraw.com",
  "elements": [...],
  "appState": {
    "gridSize": null,
    "viewBackgroundColor": "#ffffff"
  },
  "files": {}
}
```

## Required Fields for ALL Elements

Every element (rectangle, text, arrow, ellipse, diamond, line) MUST include these fields:

```json
{
  "id": "unique_string_id",
  "type": "rectangle|text|arrow|ellipse|diamond|line",
  "x": 100,
  "y": 200,
  "width": 120,
  "height": 60,
  "angle": 0,
  "strokeColor": "#000000",
  "backgroundColor": "#ffffff",
  "fillStyle": "solid",
  "strokeWidth": 2,
  "strokeStyle": "solid",
  "roughness": 1,
  "opacity": 100,
  "groupIds": [],
  "roundness": {"type": 3},
  "seed": 1,
  "versionNonce": 1,
  "isDeleted": false,
  "boundElements": null,
  "updated": 1736581515285,
  "link": null,
  "locked": false
}
```

### Field Descriptions

- **id**: Unique identifier (string)
- **type**: Element type (rectangle/text/arrow/ellipse/diamond/line)
- **x, y**: Top-left position coordinates
- **width, height**: Element dimensions (except arrows, which use `points`)
- **angle**: Rotation angle in radians (0 for no rotation)
- **strokeColor**: Border color (hex format)
- **backgroundColor**: Fill color (hex format)
- **fillStyle**: Fill pattern ("solid", "hachure", "cross-hatch")
- **strokeWidth**: Border thickness (1-4 typical)
- **strokeStyle**: Border style ("solid", "dashed", "dotted")
- **roughness**: Hand-drawn roughness (0 = smooth, 1 = rough, 2 = very rough)
- **opacity**: Transparency (0-100, 100 = opaque)
- **groupIds**: Array of group IDs this element belongs to
- **roundness**: Corner rounding configuration
- **seed**: Random seed for hand-drawn style
- **versionNonce**: Version tracking number
- **isDeleted**: Deletion flag (always false for active elements)
- **boundElements**: Array of bound elements (for text binding)
- **updated**: Timestamp in milliseconds
- **link**: URL link (null if none)
- **locked**: Lock status (prevents editing)

## Text Elements

Text elements require additional fields:

```json
{
  "id": "text_element_id",
  "type": "text",
  "x": 110,
  "y": 130,
  "width": 180,
  "height": 20,
  "text": "Label Text",
  "fontSize": 16,
  "fontFamily": 1,
  "textAlign": "center",
  "verticalAlign": "middle",
  "baseline": 14,
  "containerId": null,
  "originalText": "Label Text",
  "lineHeight": 1.25,
  "angle": 0,
  "strokeColor": "#000000",
  "backgroundColor": "transparent",
  "fillStyle": "solid",
  "strokeWidth": 1,
  "strokeStyle": "solid",
  "roughness": 1,
  "opacity": 100,
  "groupIds": [],
  "roundness": null,
  "seed": 1,
  "versionNonce": 1,
  "isDeleted": false,
  "boundElements": null,
  "updated": 1736581515285,
  "link": null,
  "locked": false
}
```

### Text-Specific Fields

- **text**: The actual text content
- **fontSize**: Font size (12-32 typical)
- **fontFamily**: Font family (1 = Virgil, 2 = Helvetica, 3 = Cascadia)
- **textAlign**: Horizontal alignment ("left", "center", "right")
- **verticalAlign**: Vertical alignment ("top", "middle", "bottom")
- **baseline**: Text baseline position
- **containerId**: ID of parent shape (for text-in-shape binding)
- **originalText**: Original unformatted text
- **lineHeight**: Line height multiplier (1.25 typical)

## Arrow/Line Elements

Arrows and lines use `points` array instead of width/height:

```json
{
  "id": "arrow_element_id",
  "type": "arrow",
  "x": 100,
  "y": 200,
  "width": 0,
  "height": 0,
  "angle": 0,
  "strokeColor": "#000000",
  "backgroundColor": "transparent",
  "fillStyle": "solid",
  "strokeWidth": 2,
  "strokeStyle": "solid",
  "roughness": 1,
  "opacity": 100,
  "groupIds": [],
  "roundness": {"type": 2},
  "seed": 1,
  "versionNonce": 1,
  "isDeleted": false,
  "boundElements": null,
  "updated": 1736581515285,
  "link": null,
  "locked": false,
  "startBinding": null,
  "endBinding": null,
  "lastCommittedPoint": null,
  "startArrowhead": null,
  "endArrowhead": "arrow",
  "points": [[0, 0], [100, 50]]
}
```

### Arrow-Specific Fields

- **points**: Array of coordinate pairs (relative to x, y)
- **startBinding**: Binding to start element (null if free)
- **endBinding**: Binding to end element (null if free)
- **lastCommittedPoint**: Last committed point (null typical)
- **startArrowhead**: Arrowhead at start ("arrow", "bar", "dot", null)
- **endArrowhead**: Arrowhead at end ("arrow", "bar", "dot", null)

**Important**: For arrows, `width` and `height` are typically 0. The path is defined by the `points` array with coordinates relative to (x, y).

## Text-in-Shape Binding (CRITICAL)

**⚠️ MOST COMMON ERROR: Text must be independent elements, NOT shape properties!**

### ❌ WRONG - Text as Shape Property
```json
{
  "id": "box1",
  "type": "rectangle",
  "x": 100, "y": 100,
  "width": 200, "height": 80,
  "text": "This will NOT display!",  // ❌ Invalid!
  "fontSize": 16
}
```

### ✅ CORRECT - Text as Independent Element with Binding
```json
{
  "id": "box1",
  "type": "rectangle",
  "x": 100, "y": 100,
  "width": 200, "height": 80,
  "boundElements": [{"type": "text", "id": "box1_text"}],  // ✅ Declare binding
  "angle": 0,
  "strokeColor": "#000000",
  "backgroundColor": "#ffffff",
  "fillStyle": "solid",
  "strokeWidth": 2,
  "strokeStyle": "solid",
  "roughness": 1,
  "opacity": 100,
  "groupIds": [],
  "roundness": {"type": 3},
  "seed": 1,
  "versionNonce": 1,
  "isDeleted": false,
  "updated": 1736581515285,
  "link": null,
  "locked": false
},
{
  "id": "box1_text",
  "type": "text",
  "x": 110, "y": 130,  // Position inside parent box
  "width": 180, "height": 20,
  "text": "This will display correctly!",
  "fontSize": 16,
  "fontFamily": 1,
  "textAlign": "center",
  "verticalAlign": "middle",
  "containerId": "box1",  // ✅ Link to parent shape
  "baseline": 14,
  "originalText": "This will display correctly!",
  "lineHeight": 1.25,
  "angle": 0,
  "strokeColor": "#000000",
  "backgroundColor": "transparent",
  "fillStyle": "solid",
  "strokeWidth": 1,
  "strokeStyle": "solid",
  "roughness": 1,
  "opacity": 100,
  "groupIds": [],
  "roundness": null,
  "seed": 1,
  "versionNonce": 1,
  "isDeleted": false,
  "boundElements": null,
  "updated": 1736581515285,
  "link": null,
  "locked": false
}
```

### Binding Steps

1. **Shape element**: Add `"boundElements": [{"type": "text", "id": "text_element_id"}]`
2. **Text element**:
   - Create as independent element with unique `id`
   - Set `"containerId": "shape_element_id"` to link to parent
   - Position text coordinates **inside** the shape boundary
   - Include ALL required text fields (fontSize, fontFamily, textAlign, verticalAlign, baseline, originalText, lineHeight)

## Common Mistakes to Avoid

### Critical Errors (Will cause "invalid file")
- ❌ Text as shape property instead of independent element
- ❌ Missing required fields (angle, fillStyle, strokeStyle, roughness, opacity)
- ❌ Missing seed, versionNonce, isDeleted, updated fields
- ❌ Using width/height for arrows instead of points array
- ❌ Missing fontFamily, textAlign, baseline, lineHeight for text
- ❌ Missing startBinding, endBinding, startArrowhead, endArrowhead for arrows
- ❌ Forgetting `"files": {}` in root object

### Binding Errors
- ❌ Text element missing `containerId` field (won't bind to shape)
- ❌ Shape missing `boundElements` declaration (binding incomplete)
- ❌ Text positioned outside shape boundary (won't appear inside)

## Complete Example

```json
{
  "type": "excalidraw",
  "version": 2,
  "source": "https://excalidraw.com",
  "elements": [
    {
      "id": "rect1",
      "type": "rectangle",
      "x": 100,
      "y": 100,
      "width": 200,
      "height": 80,
      "angle": 0,
      "strokeColor": "#1e90ff",
      "backgroundColor": "#e3f2fd",
      "fillStyle": "solid",
      "strokeWidth": 2,
      "strokeStyle": "solid",
      "roughness": 1,
      "opacity": 100,
      "groupIds": [],
      "roundness": {"type": 3},
      "seed": 12345,
      "versionNonce": 1,
      "isDeleted": false,
      "boundElements": [{"type": "text", "id": "rect1_text"}],
      "updated": 1736581515285,
      "link": null,
      "locked": false
    },
    {
      "id": "rect1_text",
      "type": "text",
      "x": 120,
      "y": 125,
      "width": 160,
      "height": 30,
      "text": "用户服务",
      "fontSize": 20,
      "fontFamily": 1,
      "textAlign": "center",
      "verticalAlign": "middle",
      "baseline": 21,
      "containerId": "rect1",
      "originalText": "用户服务",
      "lineHeight": 1.25,
      "angle": 0,
      "strokeColor": "#000000",
      "backgroundColor": "transparent",
      "fillStyle": "solid",
      "strokeWidth": 1,
      "strokeStyle": "solid",
      "roughness": 1,
      "opacity": 100,
      "groupIds": [],
      "roundness": null,
      "seed": 12346,
      "versionNonce": 1,
      "isDeleted": false,
      "boundElements": null,
      "updated": 1736581515285,
      "link": null,
      "locked": false
    },
    {
      "id": "arrow1",
      "type": "arrow",
      "x": 300,
      "y": 140,
      "width": 0,
      "height": 0,
      "angle": 0,
      "strokeColor": "#000000",
      "backgroundColor": "transparent",
      "fillStyle": "solid",
      "strokeWidth": 2,
      "strokeStyle": "solid",
      "roughness": 1,
      "opacity": 100,
      "groupIds": [],
      "roundness": {"type": 2},
      "seed": 12347,
      "versionNonce": 1,
      "isDeleted": false,
      "boundElements": null,
      "updated": 1736581515285,
      "link": null,
      "locked": false,
      "startBinding": null,
      "endBinding": null,
      "lastCommittedPoint": null,
      "startArrowhead": null,
      "endArrowhead": "arrow",
      "points": [[0, 0], [100, 0]]
    }
  ],
  "appState": {
    "gridSize": null,
    "viewBackgroundColor": "#ffffff"
  },
  "files": {}
}
```

This example creates a blue rectangle with centered Chinese text "用户服务" and a horizontal arrow pointing right.
