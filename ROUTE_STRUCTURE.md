## 二、精准路由结构（ROUTE_STRUCTURE.md）

### 获取方式：基于 UI 页面名称.md 和截图推导

**核心原则：**

- 路由名使用英文，与 Figma Frame 命名一致
- 登录页为 1 个通用页面，通过角色参数控制样式差异
- 个人备忘录为 1 个通用页面，所有角色共用
- 其他页面按角色区分时，使用同一路由，通过角色参数控制内容差异

### 2.1 完整路由清单

```markdown
# ROUTE_STRUCTURE.md

## 路由清单（按模块分组）

### A. 入口模块
| 路由名 | 中文名 | 页面类型 | 可见角色 | 截图确认 |
|---|---|---|---|---|
| login | 登录页 | 独立页面 | 全部 | Super_Administrator_Login.png、Senior_Management_Login.png、Department_Head_Login.png、Executor_Login.png |

### B. 首页模块
| 路由名 | 中文名 | 页面类型 | 可见角色 | 截图确认 |
|---|---|---|---|---|
| home | 首页 | Tab 页面 | superadmin, president, manager | Super_Administrator_Home.png 、Senior_Management_Home.png、Department_Head_Home.png |

### C. 会议模块
| 路由名 | 中文名 | 页面类型 | 可见角色 | 截图确认 |
|---|---|---|---|---|
| meetings | 会议列表 | Tab 页面 | president, manager, superadmin | Senior_Management_Meeting.png 、Department_Head_Meeting.png |
| recording | 开始会议录音 | 子页面 | president, manager | Senior_Management_Start_Meeting_Recording.png、Department_Head_Start_Meeting_Recording.png |
| upload-recording | 上传录音文件 | 子页面 | president, manager | Senior_Management_Upload_Recording.png、Department_Head_Upload_Recording.png |
| processing | AI 处理中 | 子页面 | president, manager, superadmin | Senior_Management_AI_Processing.png、Department_Head_AI_Processing.png |
| ai-review-transcript | 审核会议纪要 | 子页面 | 授权审核人 | Senior_Management_Review_Meeting_Minutes.png、Department_Head_Review_Meeting_Minutes.png |
| ai-review-tasks | 审核任务列表 | 子页面 | 授权审核人 | Senior_Management_Review_Task_List.png、Department_Head_Review_Task_List.png |
| meeting-trace | 会议溯源详情 | 子页面 | 有权限者 | Senior_Management_Meeting_Traceability.png、Department_Head_Meeting_Traceability.png、Executor_Meeting_Traceability.png |

### D. 任务模块
| 路由名 | 中文名 | 页面类型 | 可见角色 | 截图确认 |
|---|---|---|---|---|
| tasks | 任务列表 | Tab 页面 | 全部 | Senior_Management_Task_List.png、Department_Head_Task_List.png、Executor_Task_List.png |
| task-detail | 任务详情 | 子页面 | 全部 | Senior_Management_Task_Details.png、Department_Head_Task_Details.png、Executor_Task_Details.png |
| progress-submit-sheet | 提交进度 | BottomSheet | 执行人员 | Senior_Management_Task_Progress_Submit.png、Department_Head_Task_Progress_Submit.png、Executor_Task_Progress_Submit.png |
| attachment-upload-sheet | 附件上传 | BottomSheet | 执行人员 | Senior_Management_Task_Attachment_Upload.png、Department_Head_Task_Attachment_Upload.png、Executor_Task_Attachment_Upload.png |
| task-reminder-settings | 任务提醒设置 | BottomSheet | 执行人员 | （未导出截图，按设计提示词实现）|

### E. 管理模块（仅 Superadmin）
| 路由名 | 中文名 | 页面类型 | 可见角色 | 截图确认 |
|---|---|---|---|---|
| admin | 管理后台 | Tab 页面 | superadmin | Super_Administrator_Management_Organization_Structure.png、Super_Administrator_Management_Account_Management.png |
| admin-org | 组织架构 | 子页面/Tab | superadmin | Super_Administrator_Management_Organization_Structure.png |
| admin-accounts | 账号管理 | 子页面/Tab | superadmin | Super_Administrator_Management_Account_Management.png |

### F. 消息模块
| 路由名 | 中文名 | 页面类型 | 可见角色 | 截图确认 |
|---|---|---|---|---|
| messages | 消息中心 | Tab 页面 | 全部 | Super_Administrator_Messages.png、Senior_Management_Messages.png、Department_Head_Messages.png、Executor_Messages.png |

### G. 我的模块
| 路由名 | 中文名 | 页面类型 | 可见角色 | 截图确认 |
|---|---|---|---|---|
| profile | 我的 | Tab 页面 | 全部 | Super_Administrator_My.png、Senior_Management_My.png、Department_Head_My.png、 Executor_My.png |

### H. 个人备忘模块
| 路由名 | 中文名 | 页面类型 | 可见角色 | 截图确认 |
|---|---|---|---|---|
| memo | 个人备忘提醒 | 子页面 | 全部 | Super_Administrator_My_Personal_Memo.png |
| memo-edit-dialog | 备忘创建/编辑 | Dialog | 全部 | （未导出截图，按设计提示词实现）|

## 路由总数统计
- 独立路由：18 个
- 通过角色参数控制差异的页面：login, home, meetings, recording, upload-recording, processing, ai-review-transcript, ai-review-tasks, meeting-trace, tasks, task-detail, progress-submit-sheet, attachment-upload-sheet, messages, profile, memo
- 仅 Superadmin 可见：admin, admin-org, admin-accounts
- Staff 默认路由：tasks（不经过 home）
```

### 2.2 导航结构（底部 TabBar）

```markdown
## 底部导航配置

### Superadmin（4 个 Tab）
| Tab | 图标 | 路由 | 徽章 |
|---|---|---|---|
| 首页 | home | home | - |
| 管理 | shield/admin | admin | - |
| 消息 | notifications | messages | 红点数字 |
| 我的 | person | profile | - |

### President / Manager（5 个 Tab）
| Tab | 图标 | 路由 | 徽章 |
|---|---|---|---|
| 首页 | home | home | - |
| 会议 | calendar_today | meetings | - |
| 任务 | task_alt | tasks | 红点数字 |
| 消息 | notifications | messages | 红点数字 |
| 我的 | person | profile | - |

### Staff（3 个 Tab）
| Tab | 图标 | 路由 | 徽章 |
|---|---|---|---|
| 任务 | task_alt | tasks | 红点数字 |
| 消息 | notifications | messages | 红点数字 |
| 我的 | person | profile | - |

**注意：**
- Staff 登录后默认进入 tasks 页面，不展示 home
- 消息 Tab 的红点徽章显示未读消息数量
- 任务 Tab 的红点徽章显示待确认/执行中任务数量
```

## 