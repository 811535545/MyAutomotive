# Software Requirements Document (SRD) Structure

## Document Revision History

| Phase | Revision Date | Reviser | Feature ID | Feature Name | Revise Content |
|-------|---------------|---------|------------|--------------|----------------|
| VR26 | 20250421 | Cynthia Jiang | - | - | create features requirements for VR26, details refer to features marked Vehicle Stage as VR26. |
| VR34 | 20250514 | Cynthia Jiang | - | - | create features requirements for VR34, details refer to features marked Vehicle Stage as VR34. |
| VR34 | 20250514 | Cynthia Jiang | PR-XXX-435524 | DPR_Feature1 | 1.Requirement1:Future functional growth<br>Describe the modification content<br><br>2.Requirement2:System on Chip<br>description |

---

## Documentation

### Explanation of terms

| Terminology/acronym | Explanation |
|---------------------|-------------|
| | |
| | |
| | |

---

## Product Overview

### Requirements background

This section describes the demand background of the product.

### Running environment

This section describes the required operating environment for the product, Identify the impact of system requirements on operating environment mainly from the following three dimensions:

- Peripheral resources, including but not limited to power resources, network resources, vehicle weight and space, etc.;
- Whether the provided interfaces and signals meet the functional requirements;
- Whether the physical environment (light/heat/force/sound/electricity) around the installation environment has an impact on the function;

### Constraints and assumptions

This section describes the constraints and assumptions

---

## Scope of requirements

This section describes the scope of requirements

---

## Functional Requirement

### Description

[Description of functional requirements]

### Feature 01

#### Description

[Feature 01 description]

#### FR_Requirement1

> **Note:** Functional requirement ID, format is FR_Requirement[N], numbered incrementally starting from 1. Each functional requirement should contain the following structured information.

**Summary:**

> **Note:** Briefly describe the core content of this functional requirement, summarize the main purpose and implementation approach of the function in one or a few sentences.

*Example:*

There are two ways to adjust the brightness of AR-HUD, including automatic adjustment and manual adjustment.

**Preconditions:**

> **Note:** Describe the preconditions that must be met before executing this functional requirement, including system state, environmental conditions, prerequisite operations, etc. Use list format to list all preconditions.

*Example:*

- Inactive(max 15 mins per activation), Convenience, active, driving

**Trigger Event:**

> **Note:** Describe the event or condition that triggers the execution of this functional requirement, i.e., under what circumstances the function will be activated. Can be user operations, system events, external signals, etc.

*Example:*

- Driver adjust the AR-HUD brightness level

**Main Flow of Events:**

> **Note:** Describe the main execution flow of the functional requirement, list all steps in chronological order. Each step should clearly describe the actor, the operation performed, and the expected result. Use ordered list (1, 2, 3...) to indicate step sequence.

*Example:*

1. Driver opens the AR-HUD brightness level adjustment control menu in CSD or Switch bottom panel/gesture/voice control. [VR34 update]
2. Driver adjusts the AR-HUD brightness level.
3. AR-HUD will base on the driver setting and environment brightness to adjust the image brightness.
4. Driver decides to use the current brightness.
5. End of Case.

**Post Conditions after Main Flow of Events:**

> **Note:** Describe the system state changes and results after the main flow execution is completed, i.e., the state the system should reach or the effects produced after function execution.

*Example:*

- AR-HUD real time display brightness been changed.
- AR-HUD changes in real time according to the current adjustment information.

**Alternative Flow of Events:**

> **Note:** Describe the alternative flow or exception handling flow of the functional requirement, including branch paths, error handling, behavior under exceptional circumstances, etc. When the main flow cannot be executed or requires special handling, it should be described here.

*Example:*

- The brightness of AR-HUD is adjusted automatically. AR-HUD automatically adjusts brightness based on external illumination.
- AR-HUD brightness is automatically adjusted. AR-HUD automatically adjusts brightness based on external illumination.

#### FR_Requirement2

> **Note:** Second functional requirement, format is the same as FR_Requirement1. Can continue to add FR_Requirement3, FR_Requirement4, etc. based on the actual number of functional requirements.

**Summary:**

> **Note:** Briefly describe the core content of this functional requirement.

*示例:*

There are two ways to adjust the brightness of AR-HUD, including automatic adjustment and manual adjustment.

**Preconditions:**

> **Note:** Describe the preconditions that must be met before executing this functional requirement.

*示例:*

- Inactive(max 15 mins per activation), Convenience, active, driving

**Trigger Event:**

> **Note:** Describe the event or condition that triggers the execution of this functional requirement.

*示例:*

- Driver adjust the AR-HUD brightness level

**Main Flow of Events:**

> **Note:** Describe the main execution flow of the functional requirement.

*示例:*

1. Driver open the AR-HUD brightness level adjustment control menu in CSD or Switch bottom panel/gesture/voice control.驾驶员打开 AR-HUD 亮度级别调整控制菜单在 CSD 或通过开关底板/动作/语音控制。
2. Driver adjust the AR-HUD brightness level.驾驶员调整 AR-HUD 亮度水平。
3. AR-HUD will base on the driver setting and environment brightness to adjust the image brightnessAR-HUD 将基于驱动程序设置和环境亮度调整图像亮度
4. Driver decides to use the current brightness驾驶员决定使用当前的亮度
5. End of Case结束

**Post Conditions after Main Flow of Events:**

> **Note:** Describe the system state changes and results after the main flow execution is completed.

*示例:*

- AR-HUD real time display brightness been changed.
- AR-HUD 根据当前的调整信息进行实时变化

**Alternative Flow of Events:**

> **Note:** Describe the alternative flow or exception handling flow of the functional requirement.

*示例:*

- The brightness of AR-HUD is adjusted automatically. AR-HUD automatically adjusts brightness based on external illumination.
- AR-HUD 的亮度自动调整。基于外部照明 AR-HUD 自动调整亮度。

---

## Non-Functional Requirement

> **Note:** Non-functional requirement section, describes the system's quality attributes, performance requirements, constraints and other non-functional requirements. Non-functional requirements typically include performance, reliability, security, maintainability, scalability, etc.

### Description

> **Note:** Overall description of non-functional requirements, outlines the non-functional requirements that this system needs to meet.

*Example:*

[Description of non-functional requirements]

### Feature 02

> **Note:** Non-functional requirement feature ID, format is Feature [N]. Each feature can contain multiple non-functional requirement items.

#### Description

> **Note:** Describe the specific content and scope of this non-functional requirement feature.

*Example:*

[Feature 02 description]

#### FR_Requirement1

> **Note:** Non-functional requirement ID, format is the same as functional requirements. Each non-functional requirement should contain structured information such as Summary, Preconditions, Trigger Event, Main Flow of Events, Post Conditions, Alternative Flow of Events, etc.

**Summary:**

> **Note:** Briefly describe the core content of this non-functional requirement, such as performance indicators, quality attributes, constraints, etc.

*Example:*

There are two ways to adjust the brightness of AR-HUD, including automatic adjustment and manual adjustment.

**Preconditions:**

> **Note:** Describe the preconditions applicable to this non-functional requirement.

*Example:*

- Inactive(max 15 mins per activation), Convenience, active, driving

**Trigger Event:**

> **Note:** Describe the event that triggers the evaluation or verification of this non-functional requirement.

*Example:*

- Driver adjust the AR-HUD brightness level

**Main Flow of Events:**

> **Note:** Describe the main evaluation or verification flow of the non-functional requirement.

*Example:*

1. Driver open the AR-HUD brightness level adjustment control menu in CSD or Switch bottom panel/gesture/voice control.驾驶员打开 AR-HUD 亮度级别调整控制菜单在 CSD 或通过开关底板/动作/语音控制。
2. Driver adjust the AR-HUD brightness level.驾驶员调整 AR-HUD 亮度水平。
3. AR-HUD will base on the driver setting and environment brightness to adjust the image brightnessAR-HUD 将基于驱动程序设置和环境亮度调整图像亮度
4. Driver decides to use the current brightness驾驶员决定使用当前的亮度
5. End of Case结束

**Post Conditions after Main Flow of Events:**

> **Note:** Describe the state after non-functional requirement verification is completed.

*Example:*

- AR-HUD real time display brightness been changed.
- AR-HUD 根据当前的调整信息进行实时变化

**Alternative Flow of Events:**

> **Note:** Describe the alternative verification flow or exception handling of the non-functional requirement.

*Example:*

- The brightness of AR-HUD is adjusted automatically. AR-HUD automatically adjusts brightness based on external illumination.
- AR-HUD 的亮度自动调整。基于外部照明 AR-HUD 自动调整亮度。

#### FR_Requirement2

> **Note:** Second non-functional requirement, format is the same as FR_Requirement1. Can continue to add more non-functional requirement items based on the actual number of requirements.

**Summary:**

> **Note:** Briefly describe the core content of this non-functional requirement.

*示例:*

There are two ways to adjust the brightness of AR-HUD, including automatic adjustment and manual adjustment.

**Preconditions:**

> **Note:** Describe the preconditions applicable to this non-functional requirement.

*Example:*

- Inactive(max 15 mins per activation), Convenience, active, driving

**Trigger Event:**

> **Note:** Describe the event that triggers the evaluation or verification of this non-functional requirement.

*Example:*

- Driver adjust the AR-HUD brightness level

**Main Flow of Events:**

> **Note:** Describe the main evaluation or verification flow of the non-functional requirement.

*Example:*

1. Driver open the AR-HUD brightness level adjustment control menu in CSD or Switch bottom panel/gesture/voice control.驾驶员打开 AR-HUD 亮度级别调整控制菜单在 CSD 或通过开关底板/动作/语音控制。
2. Driver adjust the AR-HUD brightness level.驾驶员调整 AR-HUD 亮度水平。
3. AR-HUD will base on the driver setting and environment brightness to adjust the image brightnessAR-HUD 将基于驱动程序设置和环境亮度调整图像亮度
4. Driver decides to use the current brightness驾驶员决定使用当前的亮度
5. End of Case结束

**Post Conditions after Main Flow of Events:**

> **Note:** Describe the state after non-functional requirement verification is completed.

*Example:*

- AR-HUD real time display brightness been changed.
- AR-HUD 根据当前的调整信息进行实时变化

**Alternative Flow of Events:**

> **Note:** Describe the alternative verification flow or exception handling of the non-functional requirement.

*Example:*

- The brightness of AR-HUD is adjusted automatically. AR-HUD automatically adjusts brightness based on external illumination.
- AR-HUD 的亮度自动调整。基于外部照明 AR-HUD 自动调整亮度。

---

## Appendix

> **Note:** Appendix section, used to store supplementary materials, references, detailed diagrams, data tables and other auxiliary information.

*Example:*

Appendix
