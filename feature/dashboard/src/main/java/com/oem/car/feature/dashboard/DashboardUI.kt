package com.oem.car.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oem.car.core.designsystem.*
import com.oem.car.core.model.PowerState
import com.oem.car.core.model.VehicleGear

/**
 * ============================================================================
 * AAOS 智能座舱中控大屏 UI 组件 (Dashboard Screen)
 * ============================================================================
 *
 * 【架构设计与数据流转方向 (Data Flow Architecture)】
 *
 * 本屏遵循单向数据流 (Unidirectional Data Flow, UDF) 与响应式分层架构，
 * 包含了【上行数据渲染流】与【下行控制注入流】两条互不干扰的数据通道：
 *
 * 1. ⬆️【上行数据流 (Bottom-Up Rendering Flow)】:
 *    ┌─────────────────────────────────────────────────────────────┐
 *    │ 底层数据源: CAN总线 / SocketCAN / VHAL / :vehicle Daemon 进程 │
 *    └──────────────────────────────┬──────────────────────────────┘
 *                                   │ (Binder IPC 回调 / SocketCAN 二进制流)
 *                                   ▼
 *    ┌─────────────────────────────────────────────────────────────┐
 *    │ 服务提供层: VehicleServiceConnectionManager / AaosProperty   │
 *    └──────────────────────────────┬──────────────────────────────┘
 *                                   │ (信号解码与打包为 CarSignal<T>)
 *                                   ▼
 *    ┌─────────────────────────────────────────────────────────────┐
 *    │ 核心数据仓库: VehicleRepositoryImpl (Single Source of Truth) │
 *    └──────────────────────────────┬──────────────────────────────┘
 *                                   │ (暴露只读 StateFlow<T> 响应式数据流)
 *                                   ▼
 *    ┌─────────────────────────────────────────────────────────────┐
 *    │ 视图层 Model: DashboardViewModel                             │
 *    └──────────────────────────────┬──────────────────────────────┘
 *                                   │ (StateFlow 转换为 State<T>)
 *                                   ▼
 *    ┌─────────────────────────────────────────────────────────────┐
 *    │ 声明式 UI: DashboardScreen (collectAsState 监听重组)          │
 *    │  ├── SpeedGaugeComponent (仪表车速与档位渲染)                 │
 *    │  ├── Vehicle3dVisualizer (3D 车模姿态数字孪生)               │
 *    │  └── SafetyBadge / 防分心卡片 (行车防分心安全拦截)            │
 *    └─────────────────────────────────────────────────────────────┘
 *
 * 2. ⬇️【下行控制流 (Top-Down Injection Command Flow)】:
 *    [DashboardScreen 用户点击 Injector 按键 (e.g. D档 80km/h / PTT)]
 *       ──> [DashboardViewModel.injectSpeed() / triggerPtt()]
 *       ──> [VehicleRepositoryImpl 统一调度代理]
 *       ──> [Binder 跨进程 AIDL 接口: IVehicleCoreManager.injectMockSpeed()]
 *       ──> [:vehicle 服务端 Daemon 进程: CarCoreDaemon]
 *       ──> [更新状态、重算防分心策略 checkSafetyPolicy()，再经上行流广播全车 UI]
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel()
) {
    // ========================================================================
    // 上行流订阅：通过 collectAsState() 将 ViewModel 暴露的 StateFlow 转化为 Compose 状态
    // 当底层数据改变时，仅触发依赖该状态的局域 Composables 发生精确重组（Recomposition）
    // ========================================================================

    /** 1. 订阅座舱底层 Binder 服务/VHAL 连接状态 */
    val isConnected by viewModel.isConnected.collectAsState()

    /** 2. 订阅当前车速信号 (含数值、VALID/INVALID 状态、时间戳) */
    val speedSignal by viewModel.speedSignal.collectAsState()

    /** 3. 订阅当前档位信号 (P / R / N / D) */
    val gearSignal by viewModel.gearSignal.collectAsState()

    /** 4. 订阅当前电源状态 (RUNNING / STR_SLEEP / GARAGE_MODE 等) */
    val powerSignal by viewModel.powerSignal.collectAsState()

    /** 5. 订阅行车防分心限制策略状态 (noVideo / noKeyboard / isRestricted) */
    val uxRestriction by viewModel.uxRestrictionState.collectAsState()

    /** 6. 订阅多音区音频焦点与方向盘 PTT 语音触发状态 */
    val audioState by viewModel.audioZoneState.collectAsState()

    CarTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 使用自适应座舱分栏布局 (CarAdaptiveLayout)，根据屏幕比例自动排版三栏式区域
            CarAdaptiveLayout(
                // ------------------------------------------------------------------
                // 栏位 1 [Primary]: 核心仪表信息与防分心状态卡片 (接收上行数据流)
                // ------------------------------------------------------------------
                primaryContent = {
                    CarCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("AAOS 智能座舱中间件 (Enterprise)", fontSize = 16.sp, color = Color.Gray)
                                // 渲染响应式防分心徽章 (由 uxRestriction.isRestricted 控制)
                                SafetyBadge(isRestricted = uxRestriction.isRestricted)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 实时车速表盘组件 (根据 speedSignal / gearSignal 响应式重组)
                                SpeedGaugeComponent(
                                    speedSignal = speedSignal,
                                    gearDisplay = gearSignal.value.display
                                )
                                Column {
                                    Text("Service 挂载: ${if (isConnected) "🟢 已连接 (车辆属性源)" else "🔴 未连接"}", fontSize = 13.sp)
                                    Text("电源状态: ${powerSignal.value.display}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                    if (audioState.isPttActive) {
                                        Text("🎙️ PTT 语音触发: 已抢占音频焦点 (Duck Music)", color = Color.Magenta, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                },
                // ------------------------------------------------------------------
                // 栏位 2 [Secondary]: 3D 车身姿态与数字孪生实时渲染画布 (接收上行数据流)
                // ------------------------------------------------------------------
                secondaryContent = {
                    Vehicle3dVisualizer(
                        speed = speedSignal.value,
                        isDoorLocked = true,
                        isDriving = gearSignal.value == VehicleGear.DRIVE && speedSignal.value > 0.5f,
                        modifier = Modifier.fillMaxSize()
                    )
                },
                // ------------------------------------------------------------------
                // 栏位 3 [Tertiary]: 总线 Injector 调试控制器 (出发下行控制流) & 视频防分心卡片
                // ------------------------------------------------------------------
                tertiaryContent = {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. 下行流控制入口：总线仿真 Injector (用户点击触发下行 Binder 跨进程注入)
                        CarCard(modifier = Modifier.fillMaxWidth()) {
                            Text("【总线仿真 Injector】", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CarButton("P档 (0 km/h)", onClick = {
                                    // 触发下行流：ViewModel -> Repository -> Binder IPC -> Daemon 重新广播
                                    viewModel.injectGear(VehicleGear.PARK)
                                    viewModel.injectSpeed(0f)
                                }, modifier = Modifier.weight(1f))
                                CarButton("D档 (80 km/h)", onClick = {
                                    // 触发下行流：高车速触发防分心策略开启，锁死影音卡片
                                    viewModel.injectGear(VehicleGear.DRIVE)
                                    viewModel.injectSpeed(80f)
                                }, modifier = Modifier.weight(1f))
                                CarButton("方向盘 PTT", onClick = { viewModel.triggerPtt() }, modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CarButton("STR 休眠", onClick = { viewModel.injectPower(PowerState.STR_SLEEP) }, modifier = Modifier.weight(1f))
                                CarButton("唤醒运行", onClick = { viewModel.injectPower(PowerState.RUNNING) }, modifier = Modifier.weight(1f))
                            }
                        }

                        // 2. 响应式防分心影音安全保护卡片 (根据 uxRestriction.noVideo 上行流自动切换)
                        CarCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            containerColor = if (uxRestriction.noVideo) Color(0xFF211111) else MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (uxRestriction.noVideo) {
                                    Text("🛑 行车视频安全拦截生效", color = Color.Red, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("行车安全策略：移动状态禁止播放影音", color = Color.Gray, fontSize = 13.sp)
                                } else {
                                    Text("🎬 乘客影音系统 (P档独享)", color = Color.Green, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("允许操作复杂多媒体与设置", color = Color.Gray, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
