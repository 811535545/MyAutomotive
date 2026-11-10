package com.oem.car.core.designsystem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 车规级自适应座舱布局容器 (Automotive Responsive Cockpit Container)。
 *
 * 智能自适应三种典型车机大屏比例与手机屏幕：
 * 1. 贯穿式超宽屏 (Ultra-Wide, >2.1 宽高比，如 1920x720 / 2560x960): 三列并行自适应填充；
 * 2. 标准横屏 (Medium, 16:9/16:10，如 1920x1080 / 1280x800): 左右双列全高自动填充，支持独立垂直滚动；
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
        // 1. 超宽屏模式 (例如 1920x720 贯穿屏) -> 三列水平并排，每列支持独立垂直滚动
        windowSizeClass.isUltraWide -> {
            BoxWithConstraints(modifier = modifier.fillMaxSize()) {
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
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            primaryContent()
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            secondaryContent()
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            tertiaryContent()
                        }
                    }
                }
            }
        }

        // 2. 紧凑小屏 / 竖屏模式 (如 Automotive_Portrait 竖屏车机) -> 全局垂直可滚动布局
        windowSizeClass.widthClass == CarWindowWidthClass.COMPACT -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    primaryContent()
                    secondaryContent()
                    tertiaryContent()
                }
            }
        }

        // 3. 标准横屏模式 (例如 1920x1080 15.6寸屏) -> 左右双列支持独立垂直滚动
        else -> {
            BoxWithConstraints(modifier = modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 左侧列 (Primary + Secondary) -> 独立垂直滚动
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(end = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            primaryContent()
                            secondaryContent()
                        }
                    }

                    // 右侧列 (Tertiary) -> 独立垂直滚动
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            tertiaryContent()
                        }
                    }
                }
            }
        }
    }
}
