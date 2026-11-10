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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

/**
 * 中控
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
    Column(
        modifier = modifier
            .testTag("vehicle_3d_container")
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. 车模视口 (固定高度 230.dp，配合外层滚动，绝不使用 weight 导致黑屏或截断)
        VehicleCanvasViewport(
            state = state,
            titleText = titleText,
            gearText = gearText,
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        )

        // 2. 底部控制台
        if (showControls) {
            VehicleControlCard(
                state = state, onStateUpdated = onStateUpdated
            )
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
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. 顶部：标题 + 车门锁状态 (Row 左右分布，绝不覆盖车模)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vehicle_3d_top_panel"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titleText,
                    color = Color(0xFF80D8FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("vehicle_3d_title")
                )

                Text(
                    text = when {
                        state.isAnyDoorOpen -> "⚠️ 车门开启告警"
                        state.isDoorLocked -> "🔒 车门全锁"
                        else -> "🔓 车门解锁"
                    },
                    color = when {
                        state.isAnyDoorOpen -> Color(0xFFFF9100)
                        state.isDoorLocked -> Color(0xFF00E676)
                        else -> Color(0xFFFFB74D)
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.testTag("vehicle_3d_lock_status")
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 1.1 新增：精确显示 4 个车门（左前、右前、左后、右后）开启/关闭状态的 4 个独立 Text 指示器
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF141820), shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DoorStatusText(label = "左前", isOpen = state.isFrontLeftDoorOpen)
                DoorStatusText(label = "右前", isOpen = state.isFrontRightDoorOpen)
                DoorStatusText(label = "左后", isOpen = state.isRearLeftDoorOpen)
                DoorStatusText(label = "右后", isOpen = state.isRearRightDoorOpen)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 2. 中间：纯净的 3D 车模 Canvas 画布 (固定高度 105.dp，绝不使用 weight 导致黑屏)
            Canvas(
                modifier = Modifier
                    .testTag("vehicle_3d_canvas")
                    .fillMaxWidth()
                    .height(105.dp)
            ) {
                val cx = size.width / 2f
                val cy = size.height / 2f

                if (state.isDriving) drawMotionWindLines(cx, cy, motionOffset)
                drawVehicleBody(cx, cy, state.isDriving)
                drawHeadlights(cx, cy, state.isFrontLeftLightOn, state.isFrontRightLightOn)
                drawTailLights(cx, cy, state.isRearLeftLightOn, state.isRearRightLightOn)
                drawDoors(cx, cy, state)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3. 底部：车速档位与胎压信息条 (独占一行，带独立背景，与车模彻底隔离、永不覆盖)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vehicle_3d_bottom_panel")
                    .background(Color(0xFF141820), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🛞 胎压: ${String.format(Locale.US, "%.1f", state.tirePressure)} bar (正常)",
                    color = Color(0xFFB0BEC5),
                    fontSize = 11.sp,
                    modifier = Modifier.testTag("vehicle_3d_tire_pressure")
                )

                val displaySpeed = gearText ?: if (state.speed > 0) "${state.speed.toInt()} km/h" else "车辆静止 P档"

                Text(
                    text = "🚀 $displaySpeed",
                    color = Color(0xFF00E5FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("vehicle_3d_speed_gear")
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
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 顶部快捷控制条
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎛️ 车身控制台 (8路总线开关)",
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

            // 第 1 行：4路灯光控制开关 (前左灯、前右灯、后左灯、后右灯)
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
                    label = "后左灯",
                    isOn = state.isRearLeftLightOn,
                    onToggle = { onStateUpdated(state.copy(isRearLeftLightOn = !state.isRearLeftLightOn)) },
                    modifier = Modifier.weight(1f)
                )
                ControlChip(
                    label = "后右灯",
                    isOn = state.isRearRightLightOn,
                    onToggle = { onStateUpdated(state.copy(isRearRightLightOn = !state.isRearRightLightOn)) },
                    modifier = Modifier.weight(1f)
                )
            }

            // 第 2 行：4路车门控制开关 (左前门、右前门、左后门、右后门)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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
                ControlChip(
                    label = "左后门",
                    isOn = state.isRearLeftDoorOpen,
                    activeColor = Color(0xFFFF9100),
                    onToggle = { onStateUpdated(state.copy(isRearLeftDoorOpen = !state.isRearLeftDoorOpen)) },
                    modifier = Modifier.weight(1f)
                )
                ControlChip(
                    label = "右后门",
                    isOn = state.isRearRightDoorOpen,
                    activeColor = Color(0xFFFF9100),
                    onToggle = { onStateUpdated(state.copy(isRearRightDoorOpen = !state.isRearRightDoorOpen)) },
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
        val startX = (cx - 150f + (offset + i * 60) % 300f)
        val y = cy - 35f + i * 18f
        drawLine(
            color = Color(0x3300E5FF),
            start = Offset(startX, y),
            end = Offset(startX + 30f, y),
            strokeWidth = 2.5f
        )
    }
}

private fun DrawScope.drawVehicleBody(cx: Float, cy: Float, isDriving: Boolean) {
    // 阴影
    drawRoundRect(
        color = Color(0x66000000),
        topLeft = Offset(cx - 95f, cy - 28f),
        size = Size(190f, 56f),
        cornerRadius = CornerRadius(14f, 14f)
    )
    // 车身外廓
    val bodyColor = if (isDriving) Color(0xFF00E5FF) else Color(0xFF37474F)
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(cx - 90f, cy - 24f),
        size = Size(180f, 48f),
        cornerRadius = CornerRadius(16f, 16f),
        style = Stroke(width = 3f)
    )
    // 车顶棚
    drawRoundRect(
        color = bodyColor.copy(alpha = 0.6f),
        topLeft = Offset(cx - 35f, cy - 16f),
        size = Size(70f, 32f),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = 1.5f)
    )
}

private fun DrawScope.drawHeadlights(cx: Float, cy: Float, leftOn: Boolean, rightOn: Boolean) {
    // 前左大灯 + 光束
    if (leftOn) {
        val path = Path().apply {
            moveTo(cx + 88f, cy - 15f)
            lineTo(cx + 130f, cy - 30f)
            lineTo(cx + 130f, cy - 5f)
            close()
        }
        drawPath(path = path, color = Color(0x3300E5FF))
    }
    drawCircle(
        color = if (leftOn) Color(0xFF00E5FF) else Color(0xFF455A64),
        radius = 5f,
        center = Offset(cx + 88f, cy - 15f)
    )

    // 前右大灯 + 光束
    if (rightOn) {
        val path = Path().apply {
            moveTo(cx + 88f, cy + 15f)
            lineTo(cx + 130f, cy + 5f)
            lineTo(cx + 130f, cy + 30f)
            close()
        }
        drawPath(path = path, color = Color(0x3300E5FF))
    }
    drawCircle(
        color = if (rightOn) Color(0xFF00E5FF) else Color(0xFF455A64),
        radius = 5f,
        center = Offset(cx + 88f, cy + 15f)
    )
}

private fun DrawScope.drawTailLights(cx: Float, cy: Float, leftOn: Boolean, rightOn: Boolean) {
    listOf(cy - 15f to leftOn, cy + 15f to rightOn).forEach { (y, isOn) ->
        val lightColor = if (isOn) Color(0xFFFF5252) else Color(0xFF455A64)
        drawCircle(color = lightColor, radius = 5f, center = Offset(cx - 88f, y))
        if (isOn) {
            drawCircle(color = Color(0x66FF5252), radius = 9f, center = Offset(cx - 88f, y))
        }
    }
}

private fun DrawScope.drawDoors(cx: Float, cy: Float, state: Vehicle3dState) {
    val doorColor = Color(0xFFFF9100)
    if (state.isFrontLeftDoorOpen) {
        drawLine(
            doorColor, Offset(cx + 15f, cy - 24f), Offset(cx + 25f, cy - 38f), strokeWidth = 3.5f
        )
    }
    if (state.isFrontRightDoorOpen) {
        drawLine(
            doorColor, Offset(cx + 15f, cy + 24f), Offset(cx + 25f, cy + 38f), strokeWidth = 3.5f
        )
    }
    if (state.isRearLeftDoorOpen) {
        drawLine(
            doorColor, Offset(cx - 30f, cy - 24f), Offset(cx - 20f, cy - 38f), strokeWidth = 3.5f
        )
    }
    if (state.isRearRightDoorOpen) {
        drawLine(
            doorColor, Offset(cx - 30f, cy + 24f), Offset(cx - 20f, cy + 38f), strokeWidth = 3.5f
        )
    }
}

@Composable
private fun DoorStatusText(label: String, isOpen: Boolean) {
    val color = if (isOpen) Color(0xFFFF9100) else Color(0xFF00E676)
    val statusText = if (isOpen) "$label: 开启" else "$label: 关闭"
    Text(
        text = statusText,
        color = color,
        fontSize = 9.5.sp,
        fontWeight = if (isOpen) FontWeight.Bold else FontWeight.Normal
    )
}

