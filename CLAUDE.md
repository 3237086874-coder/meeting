# Meeting App 开发指南

## 识图能力（Figma 设计稿分析）

你的底层模型不具备原生识图能力。分析 UI 设计截图时，使用以下命令获取精确的结构化 UI 信息：

```bash
# 分析设计图，提取颜色、尺寸、布局用于前端代码生成
node E:/claude_code/image_skill/vision.js "<图片路径>" --mode code

# 分析远程 Figma 截图链接
node E:/claude_code/image_skill/vision.js --url "<图片链接>" --mode code
```

## Figma API 调用规范

**Token 必须通过环境变量 `$FIGMA_TOKEN` 获取，禁止硬编码。**

Token 已配置在 `~/.bashrc` 中：

```bash
# 正确 — 从环境变量读取
curl -s -H "X-Figma-Token: $FIGMA_TOKEN" "https://api.figma.com/v1/files/{file_key}/nodes?ids=..."

# 错误 — 禁止硬编码
# curl -s -H "X-Figma-Token: figd_xxx..."
```

## 自动视觉纠错回路

完整的闭环流程：修改代码 → build → 安装 → 截图 → 对比设计稿 → 输出差异 → 修复。

### 工具链

| 工具 | 路径 | 用途 |
|------|------|------|
| `capture_screen.sh` | `~/.claude/scripts/capture_screen.sh` | 模拟器截图 |
| `compare_design.js` | `~/.claude/scripts/compare_design.js` | 截图 vs Figma 设计对比 |
| `auto_fix.sh` | `~/.claude/scripts/auto_fix.sh` | 一键纠错回路 |

### 使用方式

```bash
# 一键纠错（build → install → 截图 → 对比 → 报告）
bash ~/.claude/scripts/auto_fix.sh <角色> <页面名>

# 分步操作
# 1. 截图
bash ~/.claude/scripts/capture_screen.sh custom_name.png

# 2. 对比特定截图
node ~/.claude/scripts/compare_design.js <截图路径> <角色> <页面名>
```

### 角色与页面名对照

| 角色 | 参数值 | 可用页面 |
|------|--------|----------|
| 超级管理员 | `superadmin` | home, org_structure, account_mgmt, messages, my, memo |
| 高级管理层 | `president` | home, meeting_list, task_list, messages, my, memo, recording, upload, ai_processing, review_minutes, review_tasks, meeting_trace |
| 部门负责人 | `manager` | 同上 |
| 执行人员 | `staff` | task_list, task_detail, messages, my, memo, task_submit |

### 纠错输出

对比报告包含：
- **匹配度评分**（0-100%）
- **逐项检测**：颜色、布局、排版、组件、角色色、状态
- **关键问题列表**（需优先修复项）
- **修复建议**（含具体代码片段）

## 触发场景

- 需要根据截图还原 UI 设计
- 分析 Figma 设计稿中的颜色、间距、字体参数
- 将设计图转换为 Compose 代码并自动验证
- 通过 Figma API 获取节点 JSON 数据
- 修改 UI 后自动截图对比原设计，输出差异并修复
