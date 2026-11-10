# SYS2 单条功能需求详细格式定义

本文档详细说明 SYS2 系统需求文档中单条功能需求的格式结构,帮助理解需求的组织方式和包含的信息要素。

## 需求层级结构

系统需求通常包含两级结构:
1. **主需求** (如 5.1 Bluetooth Connection Handling)
2. **子需求** (如 5.1.1 Device Inquiry)

每个层级都包含完整的需求信息。

---

## 主需求结构示例

### 5.1 Bluetooth Connection Handling

> **Note:** Requirement title, format is "Section Number Requirement Name". Section numbers use hierarchical numbering (e.g., 5.1, 5.1.1, 5.1.2), requirement name should concisely and clearly describe the functional feature.

#### Metadata

> **Note:** Metadata table containing basic management information of the requirement. All fields are required and used for requirement management and traceability.

| Field | Value |
|-------|-------|
| **CodeBeamer reference** | PR-BT-385801 |
| **Revision** | 46 |
| **Priority** | -- |
| **Severity** | -- |
| **Status** | Released |

**Field description:**
- **CodeBeamer reference**: Unique identifier (Requirement ID) of the requirement in the CodeBeamer system, format is usually PR-XXX-XXXXXX
- **Revision**: Version number of the requirement, incremented after each modification
- **Priority**: Requirement priority (e.g., High/Medium/Low or specific value), "--" means not set
- **Severity**: Requirement importance level (e.g., Critical/High/Medium/Low), "--" means not set
- **Status**: Requirement status (e.g., Draft/In Review/Released), indicates the current lifecycle state of the requirement

#### Upstream References (1)

> **Note:** Upstream references, list the upstream requirements related to this requirement. Format is a list, each item contains requirement ID, optional description separated by "-".

*Example:*

- RELEASE-476284
- GEILA_IHU.X533.01.01(2025-07-31)

#### Downstream References (1)

> **Note:** Downstream references, list the downstream software requirements decomposed or implemented based on this requirement. Format is a list, each item contains requirement ID and brief description, separated by "-".

*Example:*

- PR-Jira-472057 - Bluetooth Connection Handling

#### Associations

> **Note:** Associations, describe the dependency, relationship, conflict, etc. between this requirement and other requirements. If there are no associations, fill in "No Associations". If there are associations, use table format.

*Example:*

No Associations

#### Description

> **Note:** Requirement description section, details the function, behavior, constraints, etc. of the requirement. Should include the following content:
> 1. **Overall description**: Briefly explain what problem this requirement solves and what function it implements
> 2. **Implementation logic perspective**: Describe the technical implementation approach, module division, responsible parties, etc. from the implementation perspective
> 3. **Verification perspective**: Explain how to verify the implementation of the requirement from the verification perspective, including verification methods, tools, checkpoints, etc.
>
> Can use paragraphs, lists, subheadings and other formats to organize content, ensuring clear and complete description.

*Example:*

This feature defines the general behavior of Bluetooth in aspects such as BT Inquiry / Pairing, BT connection / disconnection / reconnection, etc.

**From the perspective of implementation logic:**

1. The general logic is located in the Bluetooth service and Bluetooth stack, which is mainly implemented by nFore based on AOSP.
2. With regard to automatic reconnection and OCU Call connection, these are implemented by the extension service.

**From the perspective of verification:**

The system can verify by checking related APP/HMI, such as BT music, BT Phone, and Settings UI, along with system traces, sniffer logs, etc.

#### Verification method

> **Note:** Verification method section, describes in detail how to verify the implementation of this requirement. Should list specific test scenarios, test steps, verification criteria, tools or methods used, etc. Use unordered list format, each verification item should be clear, explicit, and executable.

*Example:*

- Conduct tests on the search and connection functions.
- Test scenarios including Bluetooth phone calls and Bluetooth music.
- Test automatic connection scenarios such as activating the Bluetooth switch.
- Test ACN and MEC call scenarios, examine the status of the Bluetooth switch, and determine whether it can be re-activated.

#### Related parties

> **Note:** Related parties section, list the relevant teams, departments or roles involved in this requirement. Usually includes development team, test team, architecture team, product team, etc. Multiple related parties are separated by "/".

*Example:*

nFore/APP/FRM/TEST

---

## 子需求结构示例

### 5.1.1 Device Inquiry

> **Note:** Sub-requirement title, format is the same as main requirement. Sub-requirement section numbers should add hierarchy based on parent requirement section numbers (e.g., 5.1.1, 5.1.2). Each sub-requirement should contain complete structure: Metadata, Upstream References, Downstream References, Associations, Description, Verification method, Related parties.

#### Metadata

> **Note:** Metadata table, format is the same as main requirement. Sub-requirement CodeBeamer reference is usually different from main requirement, Revision is managed independently.

*Example:*

| Field | Value |
|-------|-------|
| **CodeBeamer reference** | PR-BT-449127 |
| **Revision** | 58 |
| **Priority** | -- |
| **Severity** | -- |
| **Status** | Released |

#### Upstream References (13)

> **Note:** Upstream references, format is the same as main requirement. The number in parentheses indicates the number of references.

*Example:*

- RELEASE-476284
- GEILA_IHU.X533.01.01(2025-07-31)
- GEI-272872
- GEI-272871
- GEI-272869
- GEI-272868
- GEI-272866
- GEI-272865
- GEI-272864
- GEI-272863
- GEI-272862
- GEI-272861
- GEI-272860
- GEI-272859

#### Downstream References (12)

> **Note:** Downstream references, format is the same as main requirement. Sub-requirements are usually decomposed into more detailed software requirements, each downstream reference should contain ID and brief description.

*Example:*

- SYSAD-BT-472440 - BluetoothChip_Interface
- SYSAD-BT-472439 - Uart_Interface
- SYSAD-BT-472435 - Bluetooth_APPS_IF interface
- SWRS-DTS-BSP-457983 - BT/WIFI HSIS Config
- SWRS-BT-490141 - Device Inquiry
- SYSAD-BT-472419 - VendorBluetoothService
- SYSAD-BT-472656 - SYS_SEQ_BT_INQUIRY_001
- SYSAD-BT-472691 - SYS_SEQ_BT_INQUIRY_010
- SYSAD-BT-472690 - SYS_SEQ_BT_INQUIRY_009
- SYSAD-BT-472657 - SYS_SEQ_BT_INQUIRY_003
- SWRS-DTS-BSP-457986 - BT/WIFI GPIO Config
- SWRS-DTS-BSP-457984 - BT/WIFI Channel Config

#### Associations (2)

> **Note:** Associations, format is the same as main requirement. Sub-requirements may have dependency, relationship, etc. with other requirements. Use table format to list all associations.

*Example:*

| From | Association Type | To |
|------|------------------|-----|
| PR-CP-436743 | depends | <this> |
| DPR-PERFORMANCE-437053 | related | <this> |

**Association type description:**
- **depends**: Dependency relationship, current requirement depends on source requirement
- **related**: Related relationship, current requirement is related to source requirement but not directly dependent
- **conflicts**: Conflict relationship, current requirement conflicts with source requirement
- **implements**: Implementation relationship, current requirement implements source requirement

#### Description

> **Note:** Requirement description section, format is the same as main requirement. Sub-requirement description should be more specific and detailed, usually includes:
> 1. **Summary**: Requirement summary, use ordered list to list functional points, each point should be clear and explicit
> 2. **Detailed description**: Detailed description of each functional point, including behavior, constraints, boundary conditions, etc.
> 3. **Implementation details**: Technical implementation related descriptions, configuration items, customizable parameters, etc. (marked with square brackets, e.g., [Need customize based on AOSP])
>
> Can use nested lists to organize hierarchical information.

*Example:*

**Summary:**

1. User can search for discoverable Bluetooth devices and manually select one for connection.
2. System ensures uninterrupted A2DP playback and ensure audio quality during Bluetooth inquiry.
3. Ongoing inquiry can be aborted. System isn't blocked during abort procedure; user can't start another inquiry.
4. System filters inquiry results by showing supported Bluetooth profiles according to configuration and switches; [Need customize based on AOSP]
   - Devices with unknown supported profiles are filtered out until service discovery;
   - Only supported-profile and Secure Connections devices are shown;
   - For known devices, no service discovery and it can connect directly.
5. System supports Extended Inquiry.
6. System uses complete local name and 16-bit UUIDs from extended inquiry responses, not for known device list update.
7. System runs periodic inquiry scans [HMI can customize periodic] and responds to external requests.

#### Verification method

> **Note:** Verification method section, format is the same as main requirement. Sub-requirement verification method should be more specific, verifying the functional points of this sub-requirement.

*Example:*

- The car system can search for external Bluetooth devices.
- Sniffer log to check Extended Inquiry feedback result.

#### Related parties

> **Note:** Related parties section, format is the same as main requirement. Sub-requirement related parties may be the same as main requirement, or may be more specific.

*Example:*

nFore/TEST

---

## 关键理解要点

### 1. 层级结构
- 主需求定义大的功能模块
- 子需求细化具体的技术实现
- 每一层都有完整的元数据和追溯信息

### 2. 追溯关系
- **Upstream References**: 指向更上游的产品需求或用户需求
- **Downstream References**: 指向基于此需求拆解出的软件需求或架构元素
- **Associations**: 与其他需求的横向关系(依赖、相关、冲突等)

### 3. 描述结构
- **整体说明**: 解决什么问题,实现什么功能
- **实现逻辑视角**: 如何实现,涉及哪些模块
- **验证视角**: 如何验证,用什么方法和工具

### 4. 可定制标记
- `[Need customize based on AOSP]` 等标记表示需要根据平台定制的部分
- `[HMI can customize periodic]` 等标记表示 HMI 可配置的参数

### 5. Summary 的编写方式
- 使用编号列表清晰列出所有功能点
- 每个功能点独立、完整、可验证
- 可以使用嵌套列表表示子功能或条件分支

理解这些结构后,在拆解 SWE1 时可以:
1. 准确识别系统需求的边界和约束
2. 正确追溯上游需求来源
3. 理解实现逻辑和验证方式,以便转换为软件层面的描述
4. 识别可配置项,在 SWE1 中明确默认值和配置方式
