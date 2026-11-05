package com.oem.car.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CarCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val borderModifier = if (borderColor != null) {
        Modifier.border(1.dp, borderColor, RoundedCornerShape(16.dp))
    } else Modifier

    Surface(
        modifier = modifier.then(borderModifier),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun CarButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 52.dp), // Automotive large touch target
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, fontSize = 16.sp)
    }
}

@Composable
fun SafetyBadge(isRestricted: Boolean, modifier: Modifier = Modifier) {
    val color = if (isRestricted) Color(0xFFFF5252) else Color(0xFF00E676)
    val text = if (isRestricted) "⚠️ 行车防分心策略已启用" else "✅ 安全允许"

    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .border(1.dp, color, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = color, fontSize = 13.sp)
    }
}

@Composable
fun SpeedGaugeComponent(
    speedSignal: com.oem.car.core.model.CarSignal<Float>,
    gearDisplay: String
) {
    val isValid = speedSignal.status == com.oem.car.core.model.SignalStatus.VALID
    val speedText = if (isValid) "${speedSignal.value.toInt()}" else "--"
    val textColor = if (isValid) MaterialTheme.colorScheme.primary else Color(0xFFFF9100)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = speedText,
            fontSize = 64.sp,
            color = textColor
        )
        Text(
            text = if (isValid) "KM/H" else "信号降级/离线",
            fontSize = 14.sp,
            color = if (isValid) Color.Gray else Color(0xFFFF9100)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "档位: $gearDisplay",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 18.sp
            )
        }
    }
}

/**
 * 车规级动态状态栏安全内边距 (优先动态获取，固定高度兜底)
 *
 * 逻辑：
 * 1. 优先读取系统动态 WindowInsets.statusBars 的顶部落差高度；
 * 2. 如果动态高度 > 0 (标准手机/平板/带状态栏车机)，则使用动态高度；
 * 3. 如果动态高度为 0 (部分纯全屏车机、无状态栏定制系统或特殊IVI车机)，则自动使用 [fallback] (默认 24.dp) 兜底，
 *    确保顶部绝不会紧贴屏幕边缘或发生误触。
 */
@Composable
fun Modifier.safeStatusBarPadding(fallback: androidx.compose.ui.unit.Dp = 24.dp): Modifier {
    val dynamicTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val topPadding = if (dynamicTop > 0.dp) dynamicTop else fallback
    return this.padding(top = topPadding)
}

