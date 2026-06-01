package com.enterprise.meeting.presentation.task.detail

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.provider.OpenableColumns
import com.enterprise.meeting.presentation.theme.*

private data class TimelineStep(
    val title: String,
    val subtitle: String,
    val status: StepStatus,
)

private enum class StepStatus { COMPLETED, CURRENT, PENDING }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    onBack: () -> Unit,
    taskState: String? = null,
    userRole: String = "president",
) {
    val role = LocalRoleColors.current
    var state by remember { mutableStateOf(taskState ?: "执行中") }
    var showSubmitProgress by remember { mutableStateOf(false) }
    var showCompleteConfirm by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var reminderHours by remember { mutableStateOf(24) }

    val stateColor = when (state) {
        "待确认" -> colorWarning
        "执行中" -> colorInfo
        "已完成" -> colorSuccess
        "已逾期" -> colorDanger
        else -> role.primary
    }

    val progress = when (state) {
        "待确认" -> 0
        "执行中" -> 30
        "已完成" -> 100
        "已逾期" -> 80
        else -> 0
    }

    val deadlineText = when (taskState) {
        "待确认" -> "05-30 18:00"
        "执行中" -> "05-30 18:00"
        "已完成" -> "05-19 12:00"
        "已逾期" -> "05-20 18:00"
        else -> "—"
    }

    // Real-time countdown
    var countdownText by remember { mutableStateOf("") }
    LaunchedEffect(reminderHours, state) {
        if (state != "执行中" || deadlineText == "—") {
            countdownText = ""
            return@LaunchedEffect
        }
        while (true) {
            countdownText = computeCountdown(deadlineText)
            kotlinx.coroutines.delay(60_000)
        }
    }

    // Auto-detect overdue: if deadline is past and state is 执行中, transition to 已逾期
    LaunchedEffect(taskId) {
        if (state == "执行中") {
            val parts = deadlineText.split(" ")[0].split("-")
            if (parts.size == 2) {
                val month = parts[0].toIntOrNull() ?: 0
                val day = parts[1].toIntOrNull() ?: 0
                // Today is 2026-05-27
                if (month < 5 || (month == 5 && day < 27)) {
                    state = "已逾期"
                }
            }
        }
    }

    fun onStateChanged(newState: String) {
        state = newState
    }

    val timeline = when (state) {
        "待确认" -> listOf(
            TimelineStep("任务创建", "05-23 15:11", StepStatus.COMPLETED),
            TimelineStep("等待执行人确认", "—", StepStatus.CURRENT),
            TimelineStep("任务执行", "", StepStatus.PENDING),
            TimelineStep("任务完成", "", StepStatus.PENDING),
        )
        "执行中" -> listOf(
            TimelineStep("任务创建", "05-23 15:11", StepStatus.COMPLETED),
            TimelineStep("执行人确认接收", "05-23 15:42 · 张伟", StepStatus.COMPLETED),
            TimelineStep("提交进度 30%", "05-25 10:08", StepStatus.CURRENT),
            TimelineStep("上传附件 活动方案.docx", "05-25 17:32", StepStatus.PENDING),
            TimelineStep("提交进度 65%", "05-27 09:14", StepStatus.PENDING),
        )
        "已完成" -> listOf(
            TimelineStep("任务创建", "05-23 15:11", StepStatus.COMPLETED),
            TimelineStep("执行人确认接收", "05-23 15:42 · 张伟", StepStatus.COMPLETED),
            TimelineStep("提交进度 30%", "05-25 10:08", StepStatus.COMPLETED),
            TimelineStep("上传附件 活动方案.docx", "05-25 17:32", StepStatus.COMPLETED),
            TimelineStep("任务完成", "05-27 14:30 · 李明审核", StepStatus.COMPLETED),
        )
        "已逾期" -> listOf(
            TimelineStep("任务创建", "05-19 09:00", StepStatus.COMPLETED),
            TimelineStep("执行人确认接收", "05-19 09:30 · 张伟", StepStatus.COMPLETED),
            TimelineStep("提交进度 40%", "05-21 16:00", StepStatus.COMPLETED),
            TimelineStep("提交进度 80%", "05-25 10:00", StepStatus.CURRENT),
            TimelineStep("任务完成", "已超期 7 天", StepStatus.PENDING),
        )
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("任务详情", fontWeight = FontWeight.SemiBold, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回", tint = textPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "更多", tint = textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceWhite,
                    titleContentColor = textPrimary
                )
            )
        },
        containerColor = pageBg,
        bottomBar = {
            BottomActionBar(
                state = state,
                stateColor = stateColor,
                role = role,
                onStateChanged = ::onStateChanged,
                onSubmitProgress = { showSubmitProgress = true },
                onRequestComplete = { showCompleteConfirm = true },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ===== 1. Header Card =====
            HeaderCard(state = state, stateColor = stateColor, role = role, deadlineText = deadlineText)

            // ===== 2. Reminder Card =====
            ReminderCard(
                reminderHours = reminderHours,
                countdownText = countdownText,
                onClick = { showReminderDialog = true },
            )

            // ===== 4. Description Card =====
            DescriptionCard(state = state)

            // ===== 5. Timeline Card =====
            TimelineCard(timeline = timeline)

            // ===== 6. Attachment Card =====
            if (state != "待确认") {
                AttachmentCard(role = role)
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    // Submit Progress Dialog
    if (showSubmitProgress) {
        SubmitProgressDialog(
            currentProgress = progress,
            state = state,
            role = role,
            onDismiss = { showSubmitProgress = false },
            onConfirm = { showSubmitProgress = false },
        )
    }

    // Complete Confirmation Dialog
    if (showCompleteConfirm) {
        CompleteConfirmDialog(
            role = role,
            onDismiss = { showCompleteConfirm = false },
            onConfirm = {
                onStateChanged("已完成")
                showCompleteConfirm = false
            },
        )
    }

    // Reminder Settings Dialog
    if (showReminderDialog) {
        ReminderDialog(
            currentHours = reminderHours,
            onDismiss = { showReminderDialog = false },
            onConfirm = { hours ->
                reminderHours = hours
                showReminderDialog = false
            },
        )
    }
}

// ============================================================
// Bottom Action Bar
// ============================================================

@Composable
private fun BottomActionBar(
    state: String,
    stateColor: Color,
    role: RoleColor,
    onStateChanged: (String) -> Unit = {},
    onSubmitProgress: () -> Unit = {},
    onRequestComplete: () -> Unit = {},
) {
    Surface(
        color = surfaceWhite,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (state) {
                "待确认" -> {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, colorDanger)
                    ) {
                        Icon(Icons.Filled.Close, null, Modifier.size(16.dp), tint = colorDanger)
                        Spacer(Modifier.width(4.dp))
                        Text("驳回", fontSize = 13.sp, color = colorDanger)
                    }
                    Button(
                        onClick = { onStateChanged("执行中") },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = role.primary)
                    ) {
                        Icon(Icons.Filled.Check, null, Modifier.size(16.dp), tint = Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text("确认任务", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    }
                }
                "执行中" -> {
                    OutlinedButton(
                        onClick = onSubmitProgress,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, role.primary)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, null, Modifier.size(16.dp), tint = role.primary)
                        Spacer(Modifier.width(4.dp))
                        Text("提交进度", fontSize = 13.sp, color = role.primary)
                    }
                    Button(
                        onClick = onRequestComplete,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = role.primary)
                    ) {
                        Icon(Icons.Filled.CheckCircle, null, Modifier.size(16.dp), tint = Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text("完成", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    }
                }
                "已完成" -> {
                    // No actions needed for completed tasks
                }
                "已逾期" -> {
                    Button(
                        onClick = onSubmitProgress,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorDanger)
                    ) {
                        Icon(Icons.Filled.Edit, null, Modifier.size(16.dp), tint = Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text("提交进度", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    }
                }
            }
        }
    }
}

// ============================================================
// Header Card
// ============================================================

@Composable
private fun HeaderCard(
    state: String,
    stateColor: Color,
    role: RoleColor,
    deadlineText: String = "—",
) {
    val title = when (state) {
        "待确认" -> "整理客户反馈清单"
        "执行中" -> "提交市场活动执行方案"
        "已完成" -> "行政预算复盘报告"
        "已逾期" -> "整理客户反馈清单"
        else -> "任务"
    }
    val source = when (state) {
        "待确认" -> "客户成功季度回顾"
        "执行中" -> "Q3 产品规划会议"
        "已完成" -> "行政预算复盘"
        "已逾期" -> "客户成功季度回顾"
        else -> "—"
    }
    val assignee = when (state) {
        "待确认" -> "王五 · 运营部"
        "执行中" -> "王五 · 运营部"
        "已完成" -> "王思雨 · 行政部"
        "已逾期" -> "李明 · 产品部"
        else -> "—"
    }
    val assigner = when (state) {
        "待确认" -> "李明 · 产品部"
        "执行中" -> "李明 · 产品部"
        "已完成" -> "李明 · 产品部"
        "已逾期" -> "张伟 · 运营部"
        else -> "—"
    }
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, colorSoftBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = stateColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = state,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = stateColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = colorDivider)
            Spacer(Modifier.height(8.dp))

            TaskDetailRow("来源", source)
            TaskDetailRow("执行人", assignee)
            TaskDetailRow("指派人", assigner)
            TaskDetailRow("截止日期", deadlineText)
            if (state == "已逾期") {
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = colorDanger.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Warning, null, Modifier.size(14.dp), tint = colorDanger)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "已超期 7 天",
                            fontSize = 11.sp,
                            color = colorDanger,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// Reminder Card
// ============================================================

@Composable
private fun ReminderCard(
    reminderHours: Int,
    countdownText: String,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, colorSoftBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Notifications,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = colorInfo,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "提醒：截止前 ${reminderHours}h",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary,
                )
                if (countdownText.isNotEmpty()) {
                    Text(
                        countdownText,
                        fontSize = 12.sp,
                        color = colorInfo,
                        fontWeight = FontWeight.Medium,
                    )
                } else {
                    Text(
                        "点击设置自定义提醒",
                        fontSize = 12.sp,
                        color = textSecondary,
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "设置提醒",
                modifier = Modifier.size(20.dp),
                tint = textMuted,
            )
        }
    }
}

// ============================================================
// Description Card
// ============================================================

@Composable
private fun DescriptionCard(state: String) {
    val desc = when (state) {
        "待确认" -> "请及时确认任务并开始执行。确认后任务将进入执行阶段。"
        "执行中" -> "请运营部在本周五 18:00 前完成活动场地确认、预算表整理和执行人员排班，并上传执行方案附件。"
        "已完成" -> "行政预算复盘报告已完成，所有数据已汇总整理，请查看附件确认。"
        "已逾期" -> "整理客户反馈清单已超期 7 天，请尽快完成并提交进度。"
        else -> ""
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, colorSoftBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("任务描述", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
            Spacer(Modifier.height(8.dp))
            Text(
                text = desc,
                fontSize = 13.sp,
                color = textSecondary,
                lineHeight = 22.sp
            )
        }
    }
}

// ============================================================
// Timeline Card
// ============================================================

@Composable
private fun TimelineCard(timeline: List<TimelineStep>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, colorSoftBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("进度时间线", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
            Spacer(Modifier.height(12.dp))

            timeline.forEachIndexed { index, step ->
                TimelineRow(
                    step = step,
                    isLast = index == timeline.lastIndex
                )
            }
        }
    }
}

// ============================================================
// Attachment Card
// ============================================================

private data class AttachmentFile(
    val name: String,
    val size: String,
    val uploader: String,
    val time: String,
)

private val sampleAttachments = listOf(
    AttachmentFile("活动方案.docx", "1.2 MB", "张伟", "05-25 17:32"),
    AttachmentFile("预算表.xlsx", "856 KB", "李明", "05-26 09:15"),
)

@Composable
private fun AttachmentCard(role: RoleColor) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, colorSoftBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "附件",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary,
                )
            }

            // File list
            Spacer(Modifier.height(8.dp))
            sampleAttachments.forEach { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Open file: would launch Intent with file URI */ }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.AttachFile,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = role.primary,
                    )
                    Spacer(Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = file.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${file.uploader} · ${file.time}",
                            fontSize = 11.sp,
                            color = textMuted,
                        )
                    }
                    Text(
                        text = file.size,
                        fontSize = 12.sp,
                        color = textSecondary,
                    )
                }
                if (file != sampleAttachments.last()) {
                    HorizontalDivider(color = colorDivider)
                }
            }

            // File size info
            Spacer(Modifier.height(8.dp))
            Text(
                "单文件最大 50MB",
                fontSize = 11.sp,
                color = textMuted,
            )
        }
    }
}

// ============================================================
// Sub-components
// ============================================================

@Composable
private fun TaskDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = textSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textPrimary)
    }
}

@Composable
private fun TimelineRow(
    step: TimelineStep,
    isLast: Boolean,
) {
    val dotColor = when (step.status) {
        StepStatus.COMPLETED -> colorSuccess
        StepStatus.CURRENT -> colorInfo
        StepStatus.PENDING -> Color.Transparent
    }
    val dotBorderColor = when (step.status) {
        StepStatus.COMPLETED -> colorSuccess
        StepStatus.CURRENT -> colorInfo
        StepStatus.PENDING -> colorDivider
    }
    val lineColor = when (step.status) {
        StepStatus.COMPLETED -> colorSuccess
        StepStatus.CURRENT -> colorDivider
        StepStatus.PENDING -> colorDivider
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            if (step.status == StepStatus.PENDING) {
                Surface(
                    modifier = Modifier.size(12.dp),
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(1.5.dp, dotBorderColor)
                ) {}
            } else {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(lineColor)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 8.dp)) {
            Text(
                text = step.title,
                fontSize = 13.sp,
                fontWeight = if (step.status == StepStatus.CURRENT) FontWeight.SemiBold else FontWeight.Normal,
                color = if (step.status == StepStatus.PENDING) textMuted else textPrimary
            )
            if (step.subtitle.isNotEmpty()) {
                Text(
                    text = step.subtitle,
                    fontSize = 11.sp,
                    color = textSecondary
                )
            }
        }
    }
}

// ============================================================
// Submit Progress Dialog
// ============================================================

@Composable
private fun SubmitProgressDialog(
    currentProgress: Int,
    state: String,
    role: RoleColor,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    var sliderProgress by remember { mutableStateOf(currentProgress.toFloat()) }
    var progressNote by remember { mutableStateOf("") }
    var selectedFiles by remember { mutableStateOf(listOf<SelectedFile>()) }
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIdx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIdx = it.getColumnIndex(OpenableColumns.SIZE)
                    val name = if (nameIdx >= 0) it.getString(nameIdx) else "未知文件"
                    val sizeBytes = if (sizeIdx >= 0) it.getLong(sizeIdx) else 0L
                    val size = when {
                        sizeBytes >= 1024 * 1024 -> String.format("%.1f MB", sizeBytes / (1024f * 1024f))
                        sizeBytes >= 1024 -> String.format("%.1f KB", sizeBytes / 1024f)
                        else -> "${sizeBytes} B"
                    }
                    selectedFiles = selectedFiles + SelectedFile(name, size, sizeBytes)
                }
            }
        }
    }

    val progressDesc = when (state) {
        "执行中" -> "请运营部在本周五 18:00 前完成活动场地确认、预算表整理和执行人员排班，并上传执行方案附件。"
        "已逾期" -> "整理客户反馈清单已超期 7 天，请尽快完成并提交进度。"
        else -> ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = surfaceWhite,
        title = {
            Text("提交进度", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        },
        text = {
            Column {
                // Progress slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("当前进度", fontSize = 13.sp, color = textSecondary)
                    Text(
                        "${sliderProgress.toInt()}%",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = role.primary,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Slider(
                    value = sliderProgress,
                    onValueChange = { sliderProgress = it },
                    valueRange = 0f..100f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = role.primary,
                        activeTrackColor = role.primary,
                    ),
                )

                // Task progress description
                if (progressDesc.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = progressDesc,
                        fontSize = 12.sp,
                        color = textSecondary,
                        lineHeight = 20.sp,
                    )
                }

                // Progress note input
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = progressNote,
                    onValueChange = { progressNote = it },
                    placeholder = { Text("请说明当前任务执行情况…", fontSize = 13.sp, color = textMuted) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, color = textPrimary),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = role.primary,
                        unfocusedBorderColor = colorBorder,
                        cursorColor = role.primary,
                    ),
                )

                HorizontalDivider(color = colorDivider, modifier = Modifier.padding(vertical = 12.dp))

                // Attachment section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("附件", fontSize = 13.sp, color = textSecondary)
                    Text(
                        "${selectedFiles.size} 个文件",
                        fontSize = 12.sp,
                        color = textMuted
                    )
                }
                Spacer(Modifier.height(8.dp))

                // Selected files list
                if (selectedFiles.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9F7F4), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        selectedFiles.forEach { file ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.AttachFile,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = role.primary
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = file.name,
                                    fontSize = 12.sp,
                                    color = textPrimary,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = file.size,
                                    fontSize = 11.sp,
                                    color = textMuted
                                )
                                Spacer(Modifier.width(4.dp))
                                IconButton(
                                    onClick = { selectedFiles = selectedFiles.filterNot { it == file } },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "删除",
                                        modifier = Modifier.size(14.dp),
                                        tint = textMuted
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                OutlinedButton(
                    onClick = {
                        filePickerLauncher.launch(
                            arrayOf(
                                "application/pdf",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                "image/*"
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, role.primary)
                ) {
                    Icon(Icons.Filled.AttachFile, null, Modifier.size(16.dp), tint = role.primary)
                    Spacer(Modifier.width(4.dp))
                    Text("上传附件", fontSize = 13.sp, color = role.primary)
                }
                Text(
                    "支持 pdf/docx/xlsx/图片 格式",
                    fontSize = 11.sp,
                    color = textMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = role.primary)
            ) {
                Text("确认提交", fontSize = 14.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = textSecondary)
            }
        },
    )
}

private data class SelectedFile(
    val name: String,
    val size: String,
    val bytes: Long = 0L,
)

// ============================================================
// Complete Confirmation Dialog
// ============================================================

@Composable
private fun CompleteConfirmDialog(
    role: RoleColor,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = surfaceWhite,
        title = {
            Text("确认完成", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colorWarning,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "确认完成任务？此操作不可撤回。",
                    fontSize = 14.sp,
                    color = textPrimary,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = role.primary)
            ) {
                Text("确认完成", fontSize = 14.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = textSecondary)
            }
        },
    )
}

// ============================================================
// Countdown helper
// ============================================================

private fun computeCountdown(deadlineText: String): String {
    try {
        val parts = deadlineText.split(" ")
        if (parts.size != 2) return ""
        val dateParts = parts[0].split("-")
        val timeParts = parts[1].split(":")
        if (dateParts.size != 2 || timeParts.size != 2) return ""
        val month = dateParts[0].toIntOrNull() ?: return ""
        val day = dateParts[1].toIntOrNull() ?: return ""
        val hour = timeParts[0].toIntOrNull() ?: return ""
        val minute = timeParts[1].toIntOrNull() ?: return ""

        val deadline = java.time.LocalDateTime.of(2026, month, day, hour, minute)
        val now = java.time.LocalDateTime.now()
        val dur = java.time.Duration.between(now, deadline)
        if (dur.isNegative) return "已截止"

        val d = dur.toDays()
        val h = dur.toHours() % 24
        val m = dur.toMinutes() % 60
        return buildString {
            if (d > 0) append("${d}天 ")
            if (h > 0 || d > 0) append("${h}小时 ")
            append("${m}分")
        }
    } catch (_: Exception) {
        return ""
    }
}

// ============================================================
// Reminder Settings Dialog
// ============================================================

private val presetHours = listOf(48, 24, 12, 6, 1, 0)

@Composable
private fun ReminderDialog(
    currentHours: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var selectedHours by remember { mutableStateOf(currentHours) }
    var customInput by remember { mutableStateOf(currentHours.toString()) }
    var isCustom by remember { mutableStateOf(currentHours !in presetHours) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = surfaceWhite,
        title = {
            Text("提醒设置", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        },
        text = {
            Column {
                // Preset options
                presetHours.forEach { h ->
                    val label = if (h == 0) "不提醒" else "截止前 ${h}h"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCustom = false; selectedHours = h }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = !isCustom && selectedHours == h,
                            onClick = { isCustom = false; selectedHours = h },
                            colors = RadioButtonDefaults.colors(selectedColor = colorInfo),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = label, fontSize = 14.sp, color = textPrimary)
                    }
                }

                HorizontalDivider(color = colorDivider, modifier = Modifier.padding(vertical = 4.dp))

                // Custom input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isCustom = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = isCustom,
                        onClick = { isCustom = true },
                        colors = RadioButtonDefaults.colors(selectedColor = colorInfo),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("自定义", fontSize = 14.sp, color = textPrimary)
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = customInput,
                        onValueChange = {
                            customInput = it
                            it.toIntOrNull()?.let { n -> if (isCustom) selectedHours = n }
                        },
                        modifier = Modifier.width(80.dp).height(48.dp),
                        enabled = isCustom,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorInfo,
                            unfocusedBorderColor = colorBorder,
                        ),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("小时前", fontSize = 13.sp, color = textSecondary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedHours) },
                colors = ButtonDefaults.buttonColors(containerColor = colorInfo)
            ) {
                Text("确定", fontSize = 14.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = textSecondary)
            }
        },
    )
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun TaskDetailScreenPreview() {
    MeetingTheme {
        TaskDetailScreen(
            taskId = 1L,
            onBack = {},
        )
    }
}
