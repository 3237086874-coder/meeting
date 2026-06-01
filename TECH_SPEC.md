## 一、项目技术栈约束文档（TECH_SPEC.md）

### 1.1 项目基础信息

```markdown
# TECH_SPEC.md

## 1. 项目基础信息
- 产品名称：企业会议任务闭环管理系统
- 平台：Android 手机 App，仅竖屏 Portrait
- 框架：Jetpack Compose
- 语言：Kotlin

- UI 组件库：Material Design 3
```

### 1.2 角色主色系统（已确认）

| 角色       | 主色    | 渐变起始 | 渐变结束 | 用途                                       |
| ---------- | ------- | -------- | -------- | ------------------------------------------ |
| 超级管理员 | #B45309 | #78350F  | #B45309  | 顶部横幅、登录按钮、角色徽章边框、选中状态 |
| 高级管理层 | #123B6A | #1E3A5F  | #2C5282  | 顶部横幅、登录按钮、角色徽章边框、选中状态 |
| 部门负责人 | #1F7A6C | #064E3B  | #1F7A6C  | 顶部横幅、登录按钮、角色徽章边框、选中状态 |
| 执行人员   | #2D3748 | #1F2937  | #4B5563  | 顶部横幅、登录按钮、角色徽章边框、选中状态 |

**关键规则：**

- 登录页：选择不同角色时，登录按钮颜色随角色主色变化
- 首页：顶部渐变横幅颜色随角色变化
- 角色切换器：选中项的边框和勾选角标使用对应角色色
- 底部导航：当前选中 Tab 的图标和文字使用对应角色色
- 所有页面的主按钮、关键操作使用对应角色色

### 1.3 全局色彩 Token

```markdown
## 3. 全局色彩系统

### 角色色（动态，根据当前角色变化）
| Token | Superadmin | President | Manager | Staff |
|---|---|---|---|---|
| rolePrimary | #B45309 | #123B6A | #1F7A6C | #2D3748 |
| roleGradientStart | #78350F | #1E3A5F | #064E3B | #1F2937 |
| roleGradientEnd | #B45309 | #2C5282 | #1F7A6C | #4B5563 |
| rolePrimaryPressed | #924108 | #0E2E55 | #185E52 | #242D3A |
| rolePrimaryDisabled | rgba(180,83,9,0.25) | rgba(18,59,106,0.25) | rgba(31,122,108,0.25) | rgba(45,55,72,0.25) |

### 状态色（固定，不随角色变化）
| Token | 色值 | 用途 |
|---|---|---|
| colorDanger | #C94747 | 驳回、删除、逾期 |
| colorError | #E53E3E | 错误状态、上传失败 |
| colorSuccess | #38A169 | 完成、上传成功、正常状态 |
| colorWarning | #D97706 | 待办、临期、待确认、待发布 |
| colorInfo | #0284C7 | AI 处理中、执行中 |

### 中性色（固定）
| Token | 色值 | 用途 |
|---|---|---|
| colorBackground | #FFFDFC | 页面背景 |
| colorCard | #FFFBF5 | 卡片背景 |
| colorSurface | #FFFFFF | 表面色、输入框背景 |
| colorBorder | #D8D2C8 | 边框、分割线 |
| colorSoftBorder | #F0D9A8 | 柔和边框（选中状态）|
| colorDivider | #EDE6D8 | 列表分割线 |
| colorSkeleton | #F0E9DC | 骨架屏 |
| colorOverlayScrim | rgba(26,26,26,0.40) | 遮罩层 |
| colorTextPrimary | #1A1A1A | 主文本 |
| colorTextSecondary | #7A6B53 | 次要文本 |
| colorTextMuted | #B8A88E | 弱化文本、占位符 |
| colorTextOnDark | #FFFFFF | 深色背景上的文本 |
| colorTextOnDarkSubtle | rgba(255,255,255,0.60) | 深色背景上的弱化文本 |
```

### 1.4 字体排版映射

```markdown
## 4. 字体系统
- 中文字体：Noto Sans SC / HarmonyOS Sans
- 英文与数字：Roboto
- 数字统计：等宽风格

| Token | 尺寸 | 字重 | 行高 | 特殊属性 | 用途 |
|---|---|---|---|---|---|
| textH1 | 24sp | Semibold | 32sp | - | 页面标题 |
| textH2 | 18sp | Semibold | 24sp | - | 模块标题 |
| textH3 | 16sp | Medium | 24sp | - | 卡片标题 |
| textBody | 14sp | Regular | 22sp | - | 正文 |
| textCompact | 13sp | Regular | 20sp | - | 正文紧凑 |
| textLabel | 12sp | Medium | 16sp | - | 标签 |
| textCaption | 12sp | Regular | 18sp | - | 辅助说明 |
| textMicro | 10sp | Medium | 14sp | - | 微标签 |
| textButton | 14sp | Medium | 20sp | letter-spacing 0.5sp | 按钮文字 |
| textButtonSmall | 12sp | Medium | 16sp | - | 小按钮文字 |
| textNumeric | 24sp | Semibold | 32sp | 等宽 | 数字统计 |
| textNumericSmall | 16sp | Semibold | 22sp | 等宽 | 数字紧凑 |
```

### 1.5 间距与组件尺寸

```markdown
## 5. 间距与组件尺寸

| Token | 值 | 用途 |
|---|---|---|
| pageHorizontalMargin | 16dp | 页面左右边距 |
| moduleSpacing | 24dp | 模块间距 |
| cardPadding | 16dp | 卡片内边距 |
| cardCornerRadius | 12dp | 卡片圆角 |
| listItemSpacing | 12dp | 列表项间距 |
| appBarHeight | 56dp | 顶部 AppBar |
| bottomNavHeight | 64dp | 底部导航 |
| buttonPrimaryHeight | 48dp | 主按钮高度 |
| buttonSecondaryHeight | 40dp | 次要按钮高度 |
| buttonSmallHeight | 32dp | 小按钮高度 |
| iconButtonTouchTarget | 40dp | 图标按钮热区 |
| iconSize | 24dp | 标准图标尺寸 |
| fabSize | 56dp | FAB 尺寸 |
| inputHeight | 56dp | 输入框高度 |
| dialogMaxWidth | 312dp | Dialog 最大宽度 |
| dialogPadding | 24dp | Dialog 内边距 |
| dialogCornerRadius | 16dp | Dialog 圆角 |
| bottomSheetCornerRadius | 16dp | BottomSheet 顶部圆角 |
| bottomSheetDragHandleWidth | 32dp | BottomSheet 拖拽条宽度 |
| bottomSheetDragHandleHeight | 4dp | BottomSheet 拖拽条高度 |
| recordingButtonSize | 96dp | 录音大圆形按钮 |
| statusBarHeight | 24dp | 顶部状态栏 |
| minTouchTarget | 48dp | 最小触控目标 |
```

### 1.6 组件库规范

```markdown
## 6. 组件库规范
- 基础组件目录：基于 Material Design 3 自定义

### 必须复用的自定义组件清单
| 组件名 | 用途 | 关键属性 |
|---|---|---|
| RoleBadge | 角色徽章 | 支持 4 种角色色，白底+角色色文字或角色色浅底 |
| StatusBadge | 会议/任务状态标签 | 支持所有状态的颜色和文案映射 |
| StatCard | 统计卡 | 大号数字+标题+说明，支持点击 |
| MeetingCard | 会议卡片 | 标题、部门、主持人、时长、人数、任务数、状态徽章 |
| TaskCard | 任务卡片 | 标题、来源会议、负责人、截止日、状态、进度条、临期/逾期标识 |
| BottomNavigation | 角色化底部导航 | 根据角色动态过滤 Tab，带红点徽章 |
| AIProcessingStepper | AI 多步骤进度条 | 5 步：上传→转写→摘要→任务识别→等待审核 |
| RecordingVisualizer | 录音声波可视化 | 循环动画 1.2s，柱状图样式 |
| UploadRecordingCard | 录音文件上传卡 | 文件信息、上传进度、失败重试 |
| AuditStepper | 审核步骤条 | 3 步：审核转写文本→审核任务列表→发布任务 |
| ProgressTimeline | 任务进度时间线 | 创建→确认→执行中→提交进度→完成，支持多次提交 |
| AttachmentUploader | 附件上传组件 | 文件/图片多选、进度、失败重试、容量提示 |
| TaskReminderSheet | 任务提醒设置底部抽屉 | 默认提醒说明、自定义时间选择 |
| MeetingTraceTimeline | 会议溯源时间线 | 10+ 节点，显示操作人和时间戳 |
| MemoEditDialog | 备忘创建/编辑弹窗 | 标题、内容、提醒时间 |
| EmptyState | 空态 | 暖色线性插画+引导文案+操作按钮 |
| LoadingSkeleton | 加载骨架屏 | 列表/卡片/详情页的骨架占位 |
| ErrorState | 错误态 | 错误图标+原因+重试按钮 |
| NoPermissionState | 无权限态 | 锁图标+"您当前角色无此权限"+返回按钮 |
| NoNetworkBanner | 无网络横幅 | 顶部横幅+禁用操作按钮 |
| SuccessState | 成功态 | 成功图标+文案+确认按钮 |
```
