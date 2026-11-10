package com.oem.car.feature.climate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oem.car.core.data.VehicleRepository
import com.oem.car.core.data.VehicleRepositoryImpl
import com.oem.car.core.designsystem.CarButton
import com.oem.car.core.designsystem.CarCard
import com.oem.car.core.designsystem.CarTheme
import com.oem.car.core.model.WindMode

@Composable
fun ClimateScreen(
    repository: VehicleRepository = VehicleRepositoryImpl
) {
    val climateState by repository.climateState.collectAsState()

    CarTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("❄️ 座舱舒适性与双区 HVAC 空调控制", fontSize = 20.sp, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))

            // 全局控制栏: AC 开关 / AUTO 自动 / 空气净化
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CarButton(
                    text = if (climateState.isAcOn) "❄️ AC: 开启" else "⚪ AC: 关闭",
                    onClick = { repository.updateClimate { it.copy(isAcOn = !it.isAcOn) } },
                    containerColor = if (climateState.isAcOn) MaterialTheme.colorScheme.primary else Color(0xFF263238)
                )
                CarButton(
                    text = if (climateState.isAutoMode) "🔄 AUTO: 开启" else "⚪ AUTO: 手动",
                    onClick = { repository.updateClimate { it.copy(isAutoMode = !it.isAutoMode) } },
                    containerColor = if (climateState.isAutoMode) MaterialTheme.colorScheme.secondary else Color(0xFF263238)
                )
                CarButton(
                    text = if (climateState.isAirPurifierOn) "🌿 净化: 开启" else "⚪ 净化: 关闭",
                    onClick = { repository.updateClimate { it.copy(isAirPurifierOn = !it.isAirPurifierOn) } },
                    containerColor = if (climateState.isAirPurifierOn) Color(0xFF00E676) else Color(0xFF263238)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 主副驾温度与座椅加热卡片
            Row(
                modifier = Modifier.fillMaxWidth(0.95f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 主驾温度与加热
                CarCard(modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("主驾温度", fontSize = 14.sp, color = Color.Gray)
                        Text("${climateState.driverTemp} °C", fontSize = 32.sp, color = MaterialTheme.colorScheme.primary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CarButton("-", onClick = {
                                repository.updateClimate { it.copy(driverTemp = (it.driverTemp - 0.5f).coerceAtLeast(16.0f)) }
                            })
                            CarButton("+", onClick = {
                                repository.updateClimate { it.copy(driverTemp = (it.driverTemp + 0.5f).coerceAtMost(32.0f)) }
                            })
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("座椅加热: ${climateState.driverSeatHeating}档", fontSize = 12.sp, color = Color.LightGray)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            (0..3).forEach { level ->
                                CarButton(
                                    text = if (level == 0) "关" else "$level",
                                    onClick = { repository.updateClimate { it.copy(driverSeatHeating = level) } },
                                    containerColor = if (climateState.driverSeatHeating == level) MaterialTheme.colorScheme.primary else Color(0xFF263238)
                                )
                            }
                        }
                    }
                }

                // 副驾温度与加热
                CarCard(modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("副驾温度", fontSize = 14.sp, color = Color.Gray)
                        Text("${climateState.passengerTemp} °C", fontSize = 32.sp, color = MaterialTheme.colorScheme.primary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CarButton("-", onClick = {
                                repository.updateClimate { it.copy(passengerTemp = (it.passengerTemp - 0.5f).coerceAtLeast(16.0f)) }
                            })
                            CarButton("+", onClick = {
                                repository.updateClimate { it.copy(passengerTemp = (it.passengerTemp + 0.5f).coerceAtMost(32.0f)) }
                            })
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("座椅加热: ${climateState.passengerSeatHeating}档", fontSize = 12.sp, color = Color.LightGray)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            (0..3).forEach { level ->
                                CarButton(
                                    text = if (level == 0) "关" else "$level",
                                    onClick = { repository.updateClimate { it.copy(passengerSeatHeating = level) } },
                                    containerColor = if (climateState.passengerSeatHeating == level) MaterialTheme.colorScheme.primary else Color(0xFF263238)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 风量与风向控制卡片
            CarCard(modifier = Modifier.fillMaxWidth(0.95f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("风量: ${climateState.fanSpeed} 挡", fontSize = 16.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CarButton("降低", onClick = {
                                if (climateState.fanSpeed > 1) {
                                    repository.updateClimate { it.copy(fanSpeed = it.fanSpeed - 1) }
                                }
                            })
                            CarButton("提高", onClick = {
                                if (climateState.fanSpeed < 7) {
                                    repository.updateClimate { it.copy(fanSpeed = it.fanSpeed + 1) }
                                }
                            })
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("风向模式: ${climateState.windMode.display}", fontSize = 16.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            WindMode.entries.forEach { mode ->
                                CarButton(
                                    text = mode.display,
                                    onClick = { repository.updateClimate { it.copy(windMode = mode) } },
                                    containerColor = if (climateState.windMode == mode) MaterialTheme.colorScheme.secondary else Color(0xFF263238)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
