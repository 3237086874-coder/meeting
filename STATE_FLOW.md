## 四、业务状态与数据流文档（STATE_FLOW.md）

## 会议处理状态机

待处理 → AI 处理中 → 待审核 → 待发布 → 已发布 → 已归档
                ↓                     ↓
              失败重试   已驳回

```
| 状态 | 颜色 Token | 图标 | 可进入页面 |
|---|---|---|---|
| 待处理 | colorTextMuted | hourglass_empty | processing |
| AI 处理中 | colorInfo | smart_toy | processing |
| 待审核 | colorWarning | rate_review | ai-review-transcript |
| 待发布 | colorWarning | publish | ai-review-tasks |
| 已发布 | colorSuccess | check_circle | meeting-trace |
| 已驳回 | colorDanger | cancel | meeting-trace |
| 已归档 | colorTextMuted | inventory_2 | meeting-trace |


```

## 任务执行状态机

待确认 → 执行中 → 已完成

```
| 状态 | 颜色 Token | 图标 | 可执行操作 |
|---|---|---|---|
| 待确认 | colorWarning | pending_actions | 确认任务（执行人员）|
| 执行中 | colorInfo | play_circle | 提交进度、上传附件、提交完成（执行人员）|
| 已完成 | colorSuccess | task_alt | 查看记录（全部角色）|
| 已逾期 | colorDanger | warning | 查看记录（红色左边条）|

## 临期/逾期判定规则
- 临期：截止时间前 24 小时内 → TaskCard 左侧 4dp 橙色条（colorWarning）
- 逾期：超过截止时间 → TaskCard 左侧 4dp 红色条（colorDanger）

## AI 处理步骤（AIProcessingStepper）
1. 上传录音（已完成/进行中）
2. 语音转文本（已完成/进行中/等待中）
3. 生成摘要（已完成/进行中/等待中）
4. 任务识别（已完成/进行中/等待中）
5. 等待审核（等待中）
- 当前步骤：角色色高亮 + 图标
- 已完成：角色色勾选图标
- 进行中：角色色旋转进度图标
- 等待中：灰色未激活
- 预计时间：进行中步骤显示"预计 X 分 X 秒"

## 审核步骤（AuditStepper）
1. 审核转写文本 → 2. 审核任务列表 → 3. 发布任务
- 当前步骤：角色色高亮，数字圆圈填充
- 已完成：角色色勾选
- 未完成：灰色数字
- 步骤切换：横向滑动 200ms

## 任务进度时间线（ProgressTimeline）
创建 → 确认 → 执行中 → 提交进度 → 完成
- 每个节点：时间戳 + 操作人
- 当前节点：角色色高亮圆点
- 已完成：角色色勾选
- 未完成：灰色圆点
- 支持多次提交进度，每次新增节点

## 会议溯源时间线（MeetingTraceTimeline）
录音上传 → AI 转写完成 → AI 摘要生成 → 任务抽取 → 人工修改文本 → 审核通过 → 任务发布 → 执行反馈 → 附件上传 → 归档
- 每个节点：图标 + 时间戳 + 操作人（如有）
- 点击节点可展开详情

## 附件上传状态
| 状态 | 颜色 | 图标/操作 |
|---|---|---|
| 等待上传 | colorTextMuted | - |
| 上传中 | colorInfo | 进度条 + 取消按钮 |
| 上传成功 | colorSuccess | 勾选图标 |
| 上传失败 | colorError | 重试按钮 + 删除按钮 |

## 提醒规则
- 任务分配提醒：发布后立即弹窗提醒（类似微信消息提醒）
- 默认提醒：截止前 24h + 截止前 1h
- 自定义提醒：用户自行设置，不能早于当前时间
- 默认提醒已过：不补发，提示"已过期默认提醒不会补发"

## 账号状态
| 状态 | 颜色 | 操作 |
|---|---|---|
| 正常 | colorSuccess | 可编辑、可停用 |
| 停用 | colorDanger | 不可登录，提示"账号已停用，请联系管理员" |

## 部门状态
| 状态 | 颜色 | 操作 |
|---|---|---|
| 正常 | colorSuccess | 可编辑、可删除（无关联人员时）|
| 停用 | colorDanger | 不可删除有关联人员的部门 |
```
