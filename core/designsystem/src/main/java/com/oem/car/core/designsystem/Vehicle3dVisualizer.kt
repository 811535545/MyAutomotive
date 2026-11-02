package com.oem.car.core.designsystem

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

/**
 * 3D 车身数字孪生状态模型 (Vehicle 3D Digital Twin State)。
 * 支持前后左右 4 大灯、4 车门开关状态控制。
 */
data class Vehicle3dState(
    val speed: Float = 0f,
    val isDriving: Boolean = false,
    val tirePressure: Float = 2.5f,
    // 4 大灯 / 尾灯
    val isFrontLeftLightOn: Boolean = true,
    val isFrontRightLightOn: Boolean = true,
    val isRearLeftLightOn: Boolean = true,
    val isRearRightLightOn: Boolean = true,
    // 4 车门
    val isFrontLeftDoorOpen: Boolean = false,
    val isFrontRightDoorOpen: Boolean = false,
    val isRearLeftDoorOpen: Boolean = false,
    val isRearRightDoorOpen: Boolean = false
) {
    val isAnyDoorOpen: Boolean
        get() = isFrontLeftDoorOpen || isFrontRightDoorOpen || isRearLeftDoorOpen || isRearRightDoorOpen

    val isAllLightsOn: Boolean
        get() = isFrontLeftLightOn && isFrontRightLightOn && isRearLeftLightOn && isRearRightLightOn

    val isDoorLocked: Boolean
        get() = !isAnyDoorOpen && !isDriving

    // --- 状态流转快捷方法 ---
    fun toggleAllLights(): Vehicle3dState {
        val target = !isAllLightsOn
        return copy(
            isFrontLeftLightOn = target,
            isFrontRightLightOn = target,
            isRearLeftLightOn = target,
            isRearRightLightOn = target
        )
    }

    fun toggleAllDoors(): Vehicle3dState {
        val target = !isAnyDoorOpen
        return copy(
            isFrontLeftDoorOpen = target,
            isFrontRightDoorOpen = target,
            isRearLeftDoorOpen = target,
            isRearRightDoorOpen = target
        )
    }

    fun reset(): Vehicle3dState = copy(
        isFrontLeftDoorOpen = false,
        isFrontRightDoorOpen = false,
        isRearLeftDoorOpen = false,
        isRearRightDoorOpen = false,
        isFrontLeftLightOn = false,
        isFrontRightLightOn = false,
        isRearLeftLightOn = false,
        isRearRightLightOn = false
    )
}

// ==========================================
// 2. 对外暴露的主入口 Composable
// ==========================================

@Composable
fun Vehicle3dVisualizer(
    speed: Float,
    isDoorLocked: Boolean,
    isDriving: Boolean,
    modifier: Modifier = Modifier,
    tirePressure: Float = 2.5f,
    titleText: String = "3D 车身数字孪生 (Digital Twin)",
    gearText: String? = null,
    showControls: Boolean = true,
    onStateChange: ((Vehicle3dState) -> Unit)? = null
) {

    var state by remember(speed, isDriving, isDoorLocked, tirePressure) {
        mutableStateOf(
            Vehicle3dState(
                speed = speed,
                isDriving = isDriving,
                tirePressure = tirePressure,
                isFrontLeftDoorOpen = !isDoorLocked
            )
        )
    }

    Vehicle3dVisualizerContent(
        state = state,
        modifier = modifier,
        titleText = titleText,
        gearText = gearText,
        showControls = showControls,
        onStateUpdated = { newState ->
            state = newState
            onStateChange?.invoke(newState)
        })
}


//布局渲染
@Composable
fun Vehicle3dVisualizerContent(
    state: Vehicle3dState,
    modifier: Modifier = Modifier,
    titleText: String = "3D 车身数字孪生 (Digital Twin)",
    gearText: String? = null,
    showControls: Boolean = true,
    onStateUpdated: (Vehicle3dState) -> Unit = {}
) {
    BoxWithConstraints(modifier = modifier) {
        // 判断父容器是否有明确的高度限制
        val hasMaxHeight = maxHeight != Dp.Unspecified && maxHeight < Dp.Infinity

        Column(
            modifier = Modifier
                .testTag("vehicle_3d_container")
                .fillMaxWidth()
                .then(
                    // 关键点：有约束就占满卡片，无约束就 wrap_content 自适应内容
                    if (hasMaxHeight) Modifier.fillMaxHeight() else Modifier.wrapContentHeight()
                )
        ) {
            // 1. 车模视口 (带自适应高度)
            VehicleCanvasViewport(
                state = state,
                titleText = titleText,
                gearText = gearText,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (hasMaxHeight) {
                            // 在固定高度卡片里：动态吸取所有剩余高度，不再写死 110dp
                            Modifier.weight(1f)
                        } else {
                            // 在滚动列表或无限制时：按车模比例 wrap 自适应高度
                            Modifier
                                .aspectRatio(2.2f)
                                .heightIn(min = 120.dp, max = 200.dp)
                        }
                    )
            )

            // 2. 底部控制台 (按自身内容自适应 wrap_content)
            if (showControls) {
                Spacer(modifier = Modifier.height(6.dp))
                VehicleControlCard(
                    state = state, onStateUpdated = onStateUpdated
                )
            }
        }
    }
}


// ==========================================
// 3. 视口与信息悬浮层 (Viewport & Overlays)
// ==========================================
@Composable
private fun VehicleCanvasViewport(
    state: Vehicle3dState, titleText: String, gearText: String?, modifier: Modifier = Modifier
) {

    val motionOffset by rememberSpeedMotionOffset(speed = state.speed)

    Box(
        modifier = modifier
            .testTag("vehicle_3d_root")
            .background(Color(0xFF0F131A), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp), contentAlignment = Alignment.Center
    ) {

        // --- 核心画布 (完全居中释放，不再被中间的黑块盖住) ---
        Canvas(
            modifier = Modifier
                .testTag("vehicle_3d_canvas")
                .fillMaxSize()
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            if (state.isDriving) drawMotionWindLines(cx, cy, motionOffset)
            drawVehicleBody(cx, cy, state.isDriving)
            drawHeadlights(cx, cy, state.isFrontLeftLightOn, state.isFrontRightLightOn)
            drawTailLights(cx, cy, state.isRearLeftLightOn, state.isRearRightLightOn)
            drawDoors(cx, cy, state)
        }

        // --- 顶部 HUD 通栏：左状态、右车速胎压，左右分开彻底解决重叠 ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {


            // 左上角：标题 + 车门锁状态
            Column(modifier = Modifier.testTag("vehicle_3d_top_panel")) {

                Text(
                    text = titleText,
                    color = Color(0xFF80D8FF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("vehicle_3d_title")
                )


                Text(
                    text = when {
                        state.isAnyDoorOpen -> "⚠️ 车门开启"
                        state.isDoorLocked -> "🔒 车门全锁"
                        else -> "🔓 车门解锁"
                    },
                    color = when {
                        state.isAnyDoorOpen -> Color(0xFFFF9100)
                        state.isDoorLocked -> Color(0xFF00E676)
                        else -> Color(0xFFFFB74D)
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.testTag("vehicle_3d_lock_status")
                )
            }

            // 右上角：车速档位 + 胎压 (从居中位置挪到右上角)
            Column(
                modifier = Modifier
                    .testTag("vehicle_3d_bottom_panel")
                    .background(Color(0x55000000), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalAlignment = Alignment.End
            ) {

                val displaySpeed = gearText
                    ?: if (state.speed > 0) "${state.speed.toInt()} km/h" else "车辆静止 P档"

                Text(
                    text = displaySpeed,
                    color = Color(0xFF00E5FF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("vehicle_3d_speed_gear")
                )

                Text(
                    text = "胎压 ${String.format(Locale.US, "%.1f", state.tirePressure)} bar",
                    color = Color(0xFFB0BEC5),
                    fontSize = 9.sp,
                    modifier = Modifier.testTag("vehicle_3d_tire_pressure")
                )

            }
        }
    }
}

@Composable
private fun VehicleTopInfoPanel(
    titleText: String, state: Vehicle3dState, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .testTag("vehicle_3d_top_panel")
            .padding(8.dp)
    ) {
        Text(
            text = titleText,
            color = Color(0xFF80D8FF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("vehicle_3d_title")
        )

        val lockColor = when {
            state.isAnyDoorOpen -> Color(0xFFFF9100)
            state.isDoorLocked -> Color(0xFF00E676)
            else -> Color(0xFFFFB74D)
        }
        val lockText = when {
            state.isAnyDoorOpen -> "⚠️ 车门开启中"
            state.isDoorLocked -> "🔒 车门全锁止"
            else -> "🔓 车门解锁"
        }

        Text(
            text = lockText,
            color = lockColor,
            fontSize = 11.sp,
            modifier = Modifier.testTag("vehicle_3d_lock_status")
        )
    }
}

@Composable
private fun VehicleTelemetryPanel(
    tirePressure: Float, speed: Float, gearText: String?, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .testTag("vehicle_3d_bottom_panel")
            .background(color = CommonColors.Black60, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "胎压: ${String.format(Locale.US, "%.1f", tirePressure)} bar (正常)",
            color = CommonColors.White,
            fontSize = 11.sp,
            modifier = Modifier
                .testTag("vehicle_3d_tire_pressure")
                .padding(bottom = 2.dp)
        )

        val displaySpeed =
            gearText ?: if (speed > 0) "实时车速: ${speed.toInt()} km/h" else "车辆静止 P 档"
        Text(
            text = displaySpeed,
            color = Color(0xFF00E5FF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("vehicle_3d_speed_gear")
        )
    }
}

// ==========================================
// 4. 控制台组件 (Controls Area)
// ==========================================

@Composable
private fun VehicleControlCard(
    state: Vehicle3dState, onStateUpdated: (Vehicle3dState) -> Unit, modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vehicle_3d_controls_card"),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF141820),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // 顶部快捷控制条
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎛️ 车身控制台",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    MiniButton(
                        text = if (state.isAllLightsOn) "💡 关灯" else "💡 开灯",
                        color = Color(0xFF00B8D4),
                        onClick = { onStateUpdated(state.toggleAllLights()) })
                    MiniButton(
                        text = if (state.isAnyDoorOpen) "🔒 关门" else "🔓 开门",
                        color = Color(0xFFE65100),
                        onClick = { onStateUpdated(state.toggleAllDoors()) })
                    MiniButton(
                        text = "⚡ 复位",
                        color = Color(0xFF263238),
                        onClick = { onStateUpdated(state.reset()) })
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 单项 Chip 控制条
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ControlChip(
                    label = "前左灯",
                    isOn = state.isFrontLeftLightOn,
                    onToggle = { onStateUpdated(state.copy(isFrontLeftLightOn = !state.isFrontLeftLightOn)) },
                    modifier = Modifier.weight(1f)
                )
                ControlChip(
                    label = "前右灯",
                    isOn = state.isFrontRightLightOn,
                    onToggle = { onStateUpdated(state.copy(isFrontRightLightOn = !state.isFrontRightLightOn)) },
                    modifier = Modifier.weight(1f)
                )
                ControlChip(
                    label = "左前门",
                    isOn = state.isFrontLeftDoorOpen,
                    activeColor = Color(0xFFFF9100),
                    onToggle = { onStateUpdated(state.copy(isFrontLeftDoorOpen = !state.isFrontLeftDoorOpen)) },
                    modifier = Modifier.weight(1f)
                )
                ControlChip(
                    label = "右前门",
                    isOn = state.isFrontRightDoorOpen,
                    activeColor = Color(0xFFFF9100),
                    onToggle = { onStateUpdated(state.copy(isFrontRightDoorOpen = !state.isFrontRightDoorOpen)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MiniButton(
    text: String, color: Color, onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(26.dp),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(text, fontSize = 10.sp, color = Color.White)
    }
}

@Composable
private fun ControlChip(
    label: String,
    isOn: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF00E5FF)
) {
    val bgColor = if (isOn) activeColor.copy(alpha = 0.25f) else Color(0xFF1F2633)
    val borderColor = if (isOn) activeColor else Color(0xFF37474F)
    val textColor = if (isOn) activeColor else Color(0xFF90A4AE)

    Box(
        modifier = modifier
            .height(34.dp)
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(horizontal = 4.dp, vertical = 2.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "$label:${if (isOn) "ON" else "OFF"}",
            fontSize = 10.sp,
            fontWeight = if (isOn) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}

// ==========================================
// 5. 动画与 Canvas 细分绘制函数 (DrawScope)
// ==========================================

@Composable
private fun rememberSpeedMotionOffset(speed: Float): State<Float> {
    val transition = rememberInfiniteTransition(label = "CarMotion")
    val duration = if (speed > 0) {
        (10000 / speed.coerceAtLeast(1f)).toInt().coerceIn(200, 2000)
    } else 5000

    return transition.animateFloat(
        initialValue = 0f, targetValue = 100f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "SpeedLines"
    )
}

private fun DrawScope.drawMotionWindLines(cx: Float, cy: Float, offset: Float) {
    for (i in 0..4) {
        val startX = (cx - 200f + (offset + i * 80) % 400f)
        val y = cy - 60f + i * 30f
        drawLine(
            color = Color(0x3300E5FF),
            start = Offset(startX, y),
            end = Offset(startX + 40f, y),
            strokeWidth = 3f
        )
    }
}

private fun DrawScope.drawVehicleBody(cx: Float, cy: Float, isDriving: Boolean) {
    // 阴影
    drawRoundRect(
        color = Color(0x66000000),
        topLeft = Offset(cx - 130f, cy - 45f),
        size = Size(260f, 90f),
        cornerRadius = CornerRadius(20f, 20f)
    )
    // 车身外廓
    val bodyColor = if (isDriving) Color(0xFF00E5FF) else Color(0xFF37474F)
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(cx - 120f, cy - 40f),
        size = Size(240f, 80f),
        cornerRadius = CornerRadius(24f, 24f),
        style = Stroke(width = 4f)
    )
    // 车顶棚
    drawRoundRect(
        color = bodyColor.copy(alpha = 0.6f),
        topLeft = Offset(cx - 50f, cy - 30f),
        size = Size(100f, 60f),
        cornerRadius = CornerRadius(12f, 12f),
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawHeadlights(cx: Float, cy: Float, leftOn: Boolean, rightOn: Boolean) {
    // 前左大灯 + 光束
    if (leftOn) {
        val path = Path().apply {
            moveTo(cx + 115f, cy - 25f)
            lineTo(cx + 180f, cy - 50f)
            lineTo(cx + 180f, cy - 10f)
            close()
        }
        drawPath(path = path, color = Color(0x3300E5FF))
    }
    drawCircle(
        color = if (leftOn) Color(0xFF00E5FF) else Color(0xFF455A64),
        radius = 8f,
        center = Offset(cx + 115f, cy - 25f)
    )

    // 前右大灯 + 光束
    if (rightOn) {
        val path = Path().apply {
            moveTo(cx + 115f, cy + 25f)
            lineTo(cx + 180f, cy + 10f)
            lineTo(cx + 180f, cy + 50f)
            close()
        }
        drawPath(path = path, color = Color(0x3300E5FF))
    }
    drawCircle(
        color = if (rightOn) Color(0xFF00E5FF) else Color(0xFF455A64),
        radius = 8f,
        center = Offset(cx + 115f, cy + 25f)
    )
}

private fun DrawScope.drawTailLights(cx: Float, cy: Float, leftOn: Boolean, rightOn: Boolean) {
    listOf(cy - 25f to leftOn, cy + 25f to rightOn).forEach { (y, isOn) ->
        val lightColor = if (isOn) Color(0xFFFF5252) else Color(0xFF455A64)
        drawCircle(color = lightColor, radius = 7f, center = Offset(cx - 115f, y))
        if (isOn) {
            drawCircle(color = Color(0x66FF5252), radius = 12f, center = Offset(cx - 115f, y))
        }
    }
}

private fun DrawScope.drawDoors(cx: Float, cy: Float, state: Vehicle3dState) {
    val doorColor = Color(0xFFFF9100)
    if (state.isFrontLeftDoorOpen) {
        drawLine(
            doorColor, Offset(cx + 20f, cy - 40f), Offset(cx + 35f, cy - 62f), strokeWidth = 5f
        )
    }
    if (state.isFrontRightDoorOpen) {
        drawLine(
            doorColor, Offset(cx + 20f, cy + 40f), Offset(cx + 35f, cy + 62f), strokeWidth = 5f
        )
    }
    if (state.isRearLeftDoorOpen) {
        drawLine(
            doorColor, Offset(cx - 40f, cy - 40f), Offset(cx - 25f, cy - 62f), strokeWidth = 5f
        )
    }
    if (state.isRearRightDoorOpen) {
        drawLine(
            doorColor, Offset(cx - 40f, cy + 40f), Offset(cx - 25f, cy + 62f), strokeWidth = 5f
        )
    }
}
