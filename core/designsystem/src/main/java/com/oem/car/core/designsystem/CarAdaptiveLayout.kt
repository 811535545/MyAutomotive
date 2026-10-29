package com.oem.car.core.designsystem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 车规级自适应座舱布局容器 (Automotive Responsive Cockpit Container)。
 *
 * 智能自适应三种典型车机大屏比例与手机屏幕：
 * 1. 贯穿式超宽屏 (Ultra-Wide, >2.1 宽高比，如 1920x720 / 2560x960): 三列并行自适应填充；
 * 2. 标准横屏 (Medium, 16:9/16:10，如 1920x1080 / 1280x800): 左右双列全高自动填充，当屏幕偏矮时自动切为独立滚动；
 * 3. 紧凑/手机竖屏/低分辨率屏 (Compact, <850dp 宽度，如 800x480 / 手机竖屏): 单列垂直流动全页可滚动布局。
 */
@Composable
fun CarAdaptiveLayout(
    primaryContent: @Composable () -> Unit,
    secondaryContent: @Composable () -> Unit,
    tertiaryContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    windowSizeClass: CarWindowSizeClass = rememberCarWindowSizeClass()
) {
    when {
        // 1. 超宽屏模式 (例如 1920x720 贯穿屏) -> 三列水平并排，自动填充全高
        windowSizeClass.isUltraWide -> {
            BoxWithConstraints(modifier = modifier.fillMaxSize()) {
                val needScroll = this.maxHeight < 420.dp
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .then(if (needScroll) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                    ) { primaryContent() }

                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight()
                            .then(if (needScroll) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                    ) { secondaryContent() }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .then(if (needScroll) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                    ) { tertiaryContent() }
                }
            }
        }

        // 2. 紧凑小屏 / 竖屏模式 (如 Automotive_Portrait 竖屏车机) -> 垂直自动平铺填满全屏无黑框
        windowSizeClass.widthClass == CarWindowWidthClass.COMPACT -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) { primaryContent() }
                Box(modifier = Modifier.fillMaxWidth()) { secondaryContent() }
                Box(modifier = Modifier.fillMaxWidth()) { tertiaryContent() }
            }
        }

        // 3. 标准横屏模式 (例如 1920x1080 15.6寸屏) -> 左右双列自动打底填充全高，100% 铺满全屏无黑框
        else -> {
            BoxWithConstraints(modifier = modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 左侧列 (Primary + Secondary)
                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) { primaryContent() }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) { secondaryContent() }
                    }

                    // 右侧列 (Tertiary)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        tertiaryContent()
                    }
                }
            }
        }
    }
}
