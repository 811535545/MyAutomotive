# 车载 SocketCAN 虚拟总线与 DBC 报文实时解码管道指南

## 1. SocketCAN 协议规范
在 Linux 内核 SocketCAN (`<linux/can.h>`) 中，标准 `can_frame` C 结构体固定占 16 字节：
```c
struct can_frame {
    canid_t can_id;  /* 32-bit CAN ID + EFF/RTR/ERR 标志位 (小端序) */
    __u8    can_dlc; /* 帧载荷字节长度 (0..8) */
    __u8    __pad;   /* 补齐字节 */
    __u8    __res0;  /* 保留位 */
    __u8    __res1;  /* 保留位 */
    __u8    data[8]  __attribute__((aligned(8))); /* 8 字节 CAN 载荷 */
};
```

## 2. DBC 报文矩阵编解码 (Message Matrix)
项目支持的标准 DBC CAN ID 信号矩阵：
- `0x120` (VehicleDynamic): 车速 (km/h)、档位 (P/R/N/D)、电源模式、Rolling Counter (0~15)、SAE J1850 CRC8 校验。
- `0x210` (ClimateStatus): 主/副驾温度、风速等级、AC 开关。
- `0x300` (BatteryEnergy): SOC 电量 (%)、电压 (V)、电流 (A带偏置量)、剩余续航 (km)。
- `0x400` (AdasStatus): ACC 巡航、AEB 预警、车道偏离、目标车距 (m)。
