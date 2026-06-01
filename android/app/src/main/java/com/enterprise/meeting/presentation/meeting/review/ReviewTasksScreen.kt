package com.enterprise.meeting.presentation.meeting.review

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.anhaki.picktime.PickDate
import com.anhaki.picktime.PickHourMinute
import com.anhaki.picktime.utils.PickDateOrder
import com.anhaki.picktime.utils.PickTimeFocusIndicator
import com.anhaki.picktime.utils.PickTimeTextStyle
import com.anhaki.picktime.utils.TimeFormat
import com.enterprise.meeting.presentation.components.AuditStepper
import com.enterprise.meeting.presentation.theme.*

private enum class TaskAttachmentState {
    REQUIRED, OPTIONAL
}

private data class ReviewTaskItem(
    val id: Long,
    val title: String,
    val assignee: String,
    val assigneeDept: String,
    val dueDate: String,
    val description: String = "",
    val requirement: String = "",
    val attachmentState: TaskAttachmentState,
    val source: String = "会议转写文本",
)

private val peopleByDept = mapOf(
    "研发部" to mapOf(
        "前端开发组" to listOf("王五", "赵六"),
        "后端开发组" to listOf("张三", "李四"),
    ),
    "运维部" to mapOf(
        "系统运维组" to listOf("钱七"),
    ),
    "产品部" to mapOf(
        "产品设计组" to listOf("产品部"),
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewTasksScreen(
    meetingId: Long,
    onBack: () -> Unit,
    onPublish: () -> Unit,
) {
    val role = LocalRoleColors.current
    var tasks by remember {
        mutableStateOf(listOf(
            ReviewTaskItem(1, "完成前端页面开发，包括首页和登录页面", "王五", "研发部", "2026-05-30", attachmentState = TaskAttachmentState.REQUIRED),
            ReviewTaskItem(2, "补充后端API接口文档", "赵六", "研发部", "2026-05-28", attachmentState = TaskAttachmentState.OPTIONAL),
            ReviewTaskItem(3, "安排用户测试并收集反馈", "产品部", "产品部", "2026-06-05", attachmentState = TaskAttachmentState.REQUIRED),
            ReviewTaskItem(4, "配置CI/CD自动化部署流水线", "张三", "研发部", "2026-05-31", attachmentState = TaskAttachmentState.OPTIONAL),
            ReviewTaskItem(5, "编写数据库迁移脚本", "李四", "研发部", "2026-05-29", attachmentState = TaskAttachmentState.REQUIRED),
            ReviewTaskItem(6, "设计系统监控告警方案", "钱七", "运维部", "2026-06-02", attachmentState = TaskAttachmentState.OPTIONAL),
        ))
    }
    var showPublishAllDialog by remember { mutableStateOf(false) }
    var showRejectAllDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<ReviewTaskItem?>(null) }
    var regenerateText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "审核任务列表",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = role.onPrimary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = role.onPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = role.primary,
                    titleContentColor = role.onPrimary,
                )
            )
        },
        containerColor = pageBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editingTask = ReviewTaskItem(
                    id = (tasks.maxOfOrNull { it.id } ?: 0) + 1,
                    title = "", assignee = "", assigneeDept = "", dueDate = "",
                    attachmentState = TaskAttachmentState.REQUIRED, source = "",
                )},
                shape = CircleShape,
                containerColor = role.primary,
                contentColor = role.onPrimary,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "添加任务", modifier = Modifier.size(24.dp))
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = surfaceWhite,
                shadowElevation = 8.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = { showRejectAllDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, colorWarning),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorWarning),
                    ) {
                        Text("驳回全部", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    }
                    Button(
                        onClick = { showPublishAllDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = role.primary,
                            contentColor = role.onPrimary,
                        ),
                    ) {
                        Text("发布全部任务", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Audit stepper
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(0.67.dp, colorBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                AuditStepper(currentStep = 1)
            }

            // AI Summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceWhite),
                border = BorderStroke(0.67.dp, Color(0xFFE0E0E0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Analytics,
                            contentDescription = null,
                            tint = role.primary,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "AI 摘要",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = textPrimary,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            "识别到 ${tasks.size} 项任务",
                            fontSize = 12.sp,
                            color = colorSuccess,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    // Confidence progress bar
                    LinearProgressIndicator(
                        progress = { 0.85f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = role.primary,
                        trackColor = Color(0xFFE5E7EB),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "会议讨论了前端开发进度、后端API文档更新及用户测试安排。共识别出${tasks.size}项待执行任务，涉及前端、后端和测试、运维团队。",
                        fontSize = 13.sp,
                        color = textSecondary,
                        lineHeight = 20.sp,
                    )
                }
            }

            // Task list header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "任务列表",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = textPrimary,
                )
                Text(
                    "共 ${tasks.size} 项",
                    fontSize = 12.sp,
                    color = textMuted,
                )
            }

            // Task items
            tasks.forEach { task ->
                TaskReviewCard(
                    task = task,
                    role = role,
                    onClick = { editingTask = task },
                )
            }

            // Regenerate input
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceWhite),
                border = BorderStroke(0.67.dp, Color(0xFFE0E0E0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "基于当前文本重新生成任务",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = textPrimary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = regenerateText,
                        onValueChange = { regenerateText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp),
                        placeholder = { Text("如需调整，请描述修改意见...", fontSize = 13.sp, color = textMuted) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = textPrimary),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = role.primary,
                            unfocusedBorderColor = colorBorder,
                            focusedContainerColor = surfaceWhite,
                            unfocusedContainerColor = Color(0xFFF9FAFB),
                        ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { /* regenerate */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = role.primary,
                            contentColor = role.onPrimary,
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("重新生成", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Publish all dialog
    if (showPublishAllDialog) {
        AlertDialog(
            onDismissRequest = { showPublishAllDialog = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = surfaceWhite,
            title = {
                Text("发布全部任务", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary)
            },
            text = {
                Text(
                    "确认发布全部 ${tasks.size} 项任务到执行人员？发布后执行人员将收到任务通知。",
                    fontSize = 14.sp, color = textSecondary, lineHeight = 22.sp,
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPublishAllDialog = false; onPublish() },
                    colors = ButtonDefaults.buttonColors(containerColor = role.primary),
                    shape = RoundedCornerShape(8.dp),
                ) { Text("确认发布", fontWeight = FontWeight.Medium) }
            },
            dismissButton = {
                TextButton(onClick = { showPublishAllDialog = false }) {
                    Text("取消", color = textSecondary)
                }
            },
        )
    }

    // Task edit dialog
    editingTask?.let { task ->
        TaskEditDialog(
            task = task,
            role = role,
            onDismiss = { editingTask = null },
            onSave = { updated ->
                tasks = tasks.toMutableList().apply {
                    this[indexOf(task)] = updated
                }
                editingTask = null
            },
            onPublish = { /* per-task publish */ },
            onDelete = {
                tasks = tasks.toMutableList().apply { remove(task) }
                editingTask = null
            },
        )
    }

    // Reject all dialog
    if (showRejectAllDialog) {
        AlertDialog(
            onDismissRequest = { showRejectAllDialog = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = surfaceWhite,
            title = {
                Text("驳回全部任务", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary)
            },
            text = {
                Text(
                    "确定驳回全部任务？驳回后任务将返回重新处理，不会发布给执行人员。",
                    fontSize = 14.sp, color = textSecondary, lineHeight = 22.sp,
                )
            },
            confirmButton = {
                Button(
                    onClick = { showRejectAllDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = colorDanger),
                    shape = RoundedCornerShape(8.dp),
                ) { Text("确认驳回", fontWeight = FontWeight.Medium) }
            },
            dismissButton = {
                TextButton(onClick = { showRejectAllDialog = false }) {
                    Text("取消", color = textSecondary)
                }
            },
        )
    }
}

@Composable
private fun TaskReviewCard(
    task: ReviewTaskItem,
    role: com.enterprise.meeting.presentation.theme.RoleColor,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceWhite),
        border = BorderStroke(0.67.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Text(
                text = task.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textPrimary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Creator and due date
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = textMuted)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${task.assignee} · ${task.assigneeDept}",
                    fontSize = 12.sp,
                    color = textSecondary,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Filled.Event, contentDescription = null, modifier = Modifier.size(14.dp), tint = textMuted)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = task.dueDate,
                    fontSize = 12.sp,
                    color = textSecondary,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status chip + source
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (task.attachmentState == TaskAttachmentState.REQUIRED)
                        Color(0xFFF59E0B).copy(alpha = 0.12f)
                    else
                        Color(0xFFE5E7EB),
                ) {
                    Text(
                        text = if (task.attachmentState == TaskAttachmentState.REQUIRED) "需附件" else "可选",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (task.attachmentState == TaskAttachmentState.REQUIRED)
                            Color(0xFFF59E0B)
                        else
                            Color(0xFF6B7280),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Filled.Subtitles, contentDescription = null, modifier = Modifier.size(13.dp), tint = textMuted)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "来源: ${task.source}",
                    fontSize = 11.sp,
                    color = textMuted,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskEditDialog(
    task: ReviewTaskItem,
    role: com.enterprise.meeting.presentation.theme.RoleColor,
    onDismiss: () -> Unit,
    onSave: (ReviewTaskItem) -> Unit,
    onPublish: (ReviewTaskItem) -> Unit,
    onDelete: (ReviewTaskItem) -> Unit,
) {
    var title by remember(task.id) { mutableStateOf(task.title) }
    var assignee by remember(task.id) { mutableStateOf(task.assignee) }
    var assigneeDept by remember(task.id) { mutableStateOf(task.assigneeDept) }
    var dueDate by remember(task.id) { mutableStateOf(task.dueDate) }
    var description by remember(task.id) { mutableStateOf(task.description) }
    var requirement by remember(task.id) { mutableStateOf(task.requirement) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showPersonPicker by remember { mutableStateOf(false) }
    var cascadeDept by remember { mutableStateOf<String?>(null) }
    var cascadeSubDept by remember { mutableStateOf<String?>(null) }
    var cascadePerson by remember { mutableStateOf<String?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var pickDateYear by remember(task.id) {
        mutableStateOf(runCatching { task.dueDate.substring(0, 4).toInt() }.getOrDefault(2026))
    }
    var pickDateMonth by remember(task.id) {
        mutableStateOf(runCatching { task.dueDate.substring(5, 7).trimStart('0').toInt() }.getOrDefault(1))
    }
    var pickDateDay by remember(task.id) {
        mutableStateOf(runCatching { task.dueDate.substring(8, 10).trimStart('0').toInt() }.getOrDefault(1))
    }
    var pickTimeHour by remember(task.id) {
        mutableStateOf(runCatching { task.dueDate.substring(11, 13).trimStart('0').toInt() }.getOrDefault(12))
    }
    var pickTimeMinute by remember(task.id) {
        mutableStateOf(0)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = surfaceWhite,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
            // Title
            Text(
                if (task.title.isEmpty()) "添加任务" else "修改任务",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = textPrimary,
            )

            // Task name
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("任务名称", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = textPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = role.primary,
                        unfocusedBorderColor = colorBorder,
                        focusedContainerColor = surfaceWhite,
                        unfocusedContainerColor = surfaceWhite,
                    ),
                )
            }

            // Assignee - cascading selector
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("执行人员", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                Box {
                    OutlinedTextField(
                        value = if (assigneeDept.isNotEmpty()) "$assigneeDept · $assignee" else "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = textPrimary),
                        trailingIcon = {
                            Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(18.dp), tint = textMuted)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = role.primary,
                            unfocusedBorderColor = colorBorder,
                            focusedContainerColor = surfaceWhite,
                            unfocusedContainerColor = surfaceWhite,
                        ),
                    )
                    Surface(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showPersonPicker = true },
                        color = Color.Transparent,
                    ) {}
                }
            }

            // Due date - split into date picker and time picker
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("时间期限", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Left: Date field (year/month/day)
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = "%04d-%02d-%02d".format(pickDateYear, pickDateMonth, pickDateDay),
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = textPrimary),
                            trailingIcon = {
                                Icon(Icons.Filled.Event, contentDescription = null, modifier = Modifier.size(18.dp), tint = textMuted)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = role.primary,
                                unfocusedBorderColor = colorBorder,
                                focusedContainerColor = surfaceWhite,
                                unfocusedContainerColor = surfaceWhite,
                            ),
                        )
                        Surface(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true },
                            color = Color.Transparent,
                        ) {}
                    }
                    // Right: Time field (hour/minute)
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = "%02d:%02d".format(pickTimeHour, pickTimeMinute),
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = textPrimary),
                            trailingIcon = {
                                Icon(Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.size(18.dp), tint = textMuted)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = role.primary,
                                unfocusedBorderColor = colorBorder,
                                focusedContainerColor = surfaceWhite,
                                unfocusedContainerColor = surfaceWhite,
                            ),
                        )
                        Surface(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showTimePicker = true },
                            color = Color.Transparent,
                        ) {}
                    }
                }
            }

            // Description
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("内容说明", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = textPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = role.primary,
                        unfocusedBorderColor = colorBorder,
                        focusedContainerColor = surfaceWhite,
                        unfocusedContainerColor = surfaceWhite,
                    ),
                )
            }

            // Requirement
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("要求", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                OutlinedTextField(
                    value = requirement,
                    onValueChange = { requirement = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = textPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = role.primary,
                        unfocusedBorderColor = colorBorder,
                        focusedContainerColor = surfaceWhite,
                        unfocusedContainerColor = surfaceWhite,
                    ),
                )
            }

            // Save button (full width)
            Button(
                onClick = {
                    dueDate = pickDateYear.toString() + "-" +
                        (if (pickDateMonth < 10) "0" else "") + pickDateMonth.toString() + "-" +
                        (if (pickDateDay < 10) "0" else "") + pickDateDay.toString() + " " +
                        (if (pickTimeHour < 10) "0" else "") + pickTimeHour.toString() + ":" +
                        (if (pickTimeMinute < 10) "0" else "") + pickTimeMinute.toString()
                    assignee = cascadePerson ?: assignee
                    assigneeDept = cascadeSubDept ?: assigneeDept
                    onSave(task.copy(
                        title = title, assignee = assignee, assigneeDept = assigneeDept,
                        dueDate = dueDate, description = description, requirement = requirement,
                    ))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = role.primary,
                    contentColor = role.onPrimary,
                ),
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("保存", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            // Action buttons: 发布 | 删除
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = {
                        dueDate = pickDateYear.toString() + "-" +
                            (if (pickDateMonth < 10) "0" else "") + pickDateMonth.toString() + "-" +
                            (if (pickDateDay < 10) "0" else "") + pickDateDay.toString() + " " +
                            (if (pickTimeHour < 10) "0" else "") + pickTimeHour.toString() + ":" +
                            (if (pickTimeMinute < 10) "0" else "") + pickTimeMinute.toString()
                        assignee = cascadePerson ?: assignee
                        assigneeDept = cascadeSubDept ?: assigneeDept
                        onSave(task.copy(
                            title = title, assignee = assignee, assigneeDept = assigneeDept,
                            dueDate = dueDate, description = description, requirement = requirement,
                        ))
                        onPublish(task)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, role.primary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = role.primary),
                ) {
                    Icon(Icons.Filled.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("发布", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, colorDanger),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorDanger),
                ) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("删除", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
            }
        }
    }

    // Person picker dialog (three-level horizontal cascading selector)
    if (showPersonPicker) {
        AlertDialog(
            onDismissRequest = { showPersonPicker = false; cascadeDept = null; cascadeSubDept = null },
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = surfaceWhite,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                ) {
                    // Title
                    Text(
                        "选择执行人员",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = textPrimary,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )

                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                    // Column headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("部门", modifier = Modifier.weight(1f), fontSize = 12.sp, color = textMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Text("具体部门", modifier = Modifier.weight(1f), fontSize = 12.sp, color = textMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Text("执行人员", modifier = Modifier.weight(1f), fontSize = 12.sp, color = textMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }

                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                    // Three-column horizontal layout
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                    ) {
                        // Column 1: Departments
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                        ) {
                            peopleByDept.keys.forEach { dept ->
                                TextButton(
                                    onClick = {
                                        cascadeDept = dept
                                        cascadeSubDept = null
                                        cascadePerson = null
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    shape = RoundedCornerShape(0.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                ) {
                                    Text(
                                        dept,
                                        fontSize = if (cascadeDept == dept) 15.sp else 13.sp,
                                        color = if (cascadeDept == dept) role.primary else textPrimary,
                                        fontWeight = if (cascadeDept == dept) FontWeight.SemiBold else FontWeight.Normal,
                                    )
                                }
                            }
                        }

                        // Vertical divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(Color(0xFFF0F0F0))
                        )

                        // Column 2: Sub-departments
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                        ) {
                            val subDepts = cascadeDept?.let { peopleByDept[it]?.keys } ?: emptyList()
                            if (cascadeDept == null) {
                                Text(
                                    "请先选择部门",
                                    fontSize = 12.sp,
                                    color = textMuted,
                                    modifier = Modifier.padding(8.dp),
                                )
                            } else {
                                subDepts.forEach { sub ->
                                    TextButton(
                                        onClick = {
                                            cascadeSubDept = sub
                                            cascadePerson = null
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp),
                                        shape = RoundedCornerShape(0.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    ) {
                                        Text(
                                            sub,
                                            fontSize = if (cascadeSubDept == sub) 15.sp else 13.sp,
                                            color = if (cascadeSubDept == sub) role.primary else textPrimary,
                                            fontWeight = if (cascadeSubDept == sub) FontWeight.SemiBold else FontWeight.Normal,
                                        )
                                    }
                                }
                            }
                        }

                        // Vertical divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(Color(0xFFF0F0F0))
                        )

                        // Column 3: People
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                        ) {
                            val people = cascadeSubDept?.let { peopleByDept[cascadeDept]?.get(it) } ?: emptyList()
                            if (cascadeSubDept == null) {
                                Text(
                                    "请先选择组别",
                                    fontSize = 12.sp,
                                    color = textMuted,
                                    modifier = Modifier.padding(8.dp),
                                )
                            } else {
                                people.forEach { person ->
                                    TextButton(
                                        onClick = {
                                            cascadePerson = person
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp),
                                        shape = RoundedCornerShape(0.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    ) {
                                        Text(
                                            person,
                                            fontSize = if (cascadePerson == person) 15.sp else 13.sp,
                                            color = if (cascadePerson == person) role.primary else textPrimary,
                                            fontWeight = if (cascadePerson == person) FontWeight.SemiBold else FontWeight.Normal,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                    // Bottom buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = { showPersonPicker = false; cascadeDept = null; cascadeSubDept = null }) {
                            Text("取消", color = textSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (cascadePerson != null) {
                                    assignee = cascadePerson!!
                                    assigneeDept = "${cascadeDept} · ${cascadeSubDept}"
                                }
                                showPersonPicker = false
                                cascadeDept = null
                                cascadeSubDept = null
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = role.primary,
                                contentColor = role.onPrimary,
                            ),
                            enabled = cascadePerson != null,
                        ) {
                            Text("确定", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }

    // Date picker dialog (PickDate wheel)
    if (showDatePicker) {
        val currentYear = runCatching { java.time.LocalDate.now().year }.getOrDefault(2026)

        AlertDialog(
            onDismissRequest = { showDatePicker = false },
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = surfaceWhite,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                ) {
                    // Title row with cancel/confirm
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("取消", color = textSecondary)
                        }
                        Text(
                            "选择日期",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            color = textPrimary,
                        )
                        TextButton(onClick = {
                            showDatePicker = false
                            showTimePicker = true
                        }) {
                            Text("确定", color = role.primary)
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                    PickDate(
                        initialDay = pickDateDay,
                        dayRange = 1..31,
                        onDayChange = { pickDateDay = it },
                        initialMonth = pickDateMonth,
                        monthList = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"),
                        dateOrder = PickDateOrder.YMD,
                        onMonthChange = { pickDateMonth = it },
                        initialYear = pickDateYear,
                        yearRange = (currentYear - 5..currentYear + 5),
                        onYearChange = { pickDateYear = it },
                        selectedTextStyle = PickTimeTextStyle(
                            color = role.primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        unselectedTextStyle = PickTimeTextStyle(
                            color = textSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                        verticalSpace = 6.dp,
                        horizontalSpace = 6.dp,
                        containerColor = Color.Transparent,
                        isLooping = false,
                        extraRow = 1,
                        focusIndicator = PickTimeFocusIndicator(
                            enabled = true,
                            widthFull = true,
                            background = role.primary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.dp, Color.Transparent),
                        ),
                    )
                }
            }
        }
    }

    // Time picker dialog (PickHourMinute wheel)
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = surfaceWhite,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                ) {
                    // Title row with cancel/confirm
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("取消", color = textSecondary)
                        }
                        Text(
                            "选择时间",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            color = textPrimary,
                        )
                        TextButton(onClick = {
                            showTimePicker = false
                        }) {
                            Text("确定", color = role.primary)
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                    PickHourMinute(
                        initialHour = pickTimeHour,
                        onHourChange = { pickTimeHour = it },
                        initialMinute = pickTimeMinute,
                        onMinuteChange = { pickTimeMinute = it },
                        timeFormat = TimeFormat.HOUR_24,
                        selectedTextStyle = PickTimeTextStyle(
                            color = role.primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        unselectedTextStyle = PickTimeTextStyle(
                            color = textSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                        containerColor = Color.Transparent,
                        isLooping = false,
                        extraRow = 1,
                        focusIndicator = PickTimeFocusIndicator(
                            enabled = true,
                            widthFull = true,
                            background = role.primary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.dp, Color.Transparent),
                        ),
                    )
                }
            }
        }
    }

    // Delete confirmation
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = surfaceWhite,
            title = {
                Text("删除任务", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary)
            },
            text = {
                Text("确定删除此任务？删除后不可恢复。", fontSize = 14.sp, color = textSecondary, lineHeight = 22.sp)
            },
            confirmButton = {
                Button(
                    onClick = { showDeleteConfirm = false; onDelete(task) },
                    colors = ButtonDefaults.buttonColors(containerColor = colorDanger),
                    shape = RoundedCornerShape(8.dp),
                ) { Text("确定删除", fontWeight = FontWeight.Medium) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("取消", color = textSecondary)
                }
            },
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun ReviewTasksScreenPreview() {
    MeetingTheme(roleColors = RoleColors.manager) {
        ReviewTasksScreen(
            meetingId = 1L,
            onBack = {},
            onPublish = {},
        )
    }
}
