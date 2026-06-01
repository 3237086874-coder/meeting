---
​---
description: 企业会议任务闭环管理系统 - Figma MCP 集成规则
alwaysApply: true
---

## Figma MCP 集成规则

### 必须执行的工作流
1. 首先运行 `get_design_context` 获取精确节点的结构化表示
2. 如果响应过大或被截断，运行 `get_metadata` 获取高层节点地图
3. 运行 `get_screenshot` 获取正在实现的节点变体的视觉参考
4. 只有在获取到 `get_design_context` 和 `get_screenshot` 后，才下载资源并开始实现
5. 将输出翻译为 Android 项目约定的代码风格
6. 在标记完成前，对照 Figma 验证 1:1 的外观和行为

### 角色主色系统（必须严格遵守）
| 角色       | 主色    | 渐变起始 | 渐变结束 |
| ---------- | ------- | -------- | -------- |
| 超级管理员 | #B45309 | #78350F  | #B45309  |
| 高级管理层 | #123B6A | #1E3A5F  | #2C5282  |
| 部门负责人 | #1F7A6C | #064E3B  | #1F7A6C  |
| 执行人员   | #2D3748 | #1F2937  | #4B5563  |

- 所有主按钮、关键操作、选中状态、当前导航必须使用当前角色主色
- 顶部渐变横幅必须使用当前角色渐变
- 登录按钮随选中角色变化颜色
- 角色切换器选中项使用角色色边框

### Android 平台约束
- 目标平台：Android 手机 App，仅竖屏 Portrait
- 设计基准：360dp × 800dp，适配 393dp × 852dp
- 单位：尺寸 dp，字体 sp，禁止使用 px
- 最小触控目标：48dp × 48dp
- 顶部 Status Bar：24dp
- 底部 Navigation Bar：64dp
- 字体：中文 Noto Sans SC / HarmonyOS Sans，英文与数字 Roboto
- 图标：Material Symbols Outlined，24dp 标准尺寸
- 禁止使用 Material 默认紫色
- 禁止使用娱乐化插画
- 禁止使用大面积纯白背景（使用 #FFFDFC）

### 角色与权限
- Staff 默认进入 Tasks 页面，不展示 Home
- Admin Tab 仅 Superadmin 可见
- Meetings Tab 仅 President / Manager 可见
- 无权限时显示 NoPermissionState（锁图标 + "您当前角色无此权限" + 返回按钮）
- 审核权限来源必须在审核页面顶部显示

### 业务状态
- 会议状态：待处理 → AI 处理中 → 待审核 → 待发布 → 已发布 → 已归档
- 任务状态：待确认 → 执行中 → 已完成
- 临期任务：截止时间前 24 小时内，橙色左边条
- 逾期任务：超过截止时间，红色左边条

### 组件复用
- 必须复用项目现有组件：RoleBadge, StatusBadge, StatCard, MeetingCard, TaskCard, BottomNavigation, AIProcessingStepper, RecordingVisualizer, UploadRecordingCard, AuditStepper, ProgressTimeline, AttachmentUploader, TaskReminderSheet, MeetingTraceTimeline, MemoEditDialog, EmptyState, LoadingSkeleton, ErrorState, NoPermissionState, NoNetworkBanner, SuccessState

### 资源处理
- Figma MCP 服务器提供可获取图片和 SVG 资源的端点
- 如果 Figma 返回 localhost 源的图片或 SVG，直接使用
- 不要导入/添加新的图标包
- 不要使用或创建占位符

### 可访问性
- 所有图标需要中文 contentDescription
- 所有点击区域不小于 48dp
- 输入错误必须有文字说明
- 正文对比度满足 WCAG AA

### 交互动效
- 页面转场：Material Shared Axis X，300ms，Fast Out Slow In
- 底部抽屉：自底向上滑入，250ms
- Dialog：Fade + Scale，200ms
- 列表项点击：Material Ripple，角色色 8% 透明度
- 录音声波：循环动画 1.2s
- AI 处理中：环形进度缓慢旋转
- 上传进度：线性进度条实时增长
- 任务卡展开：高度动画 200ms
- AuditStepper 步骤切换：横向滑动 200ms