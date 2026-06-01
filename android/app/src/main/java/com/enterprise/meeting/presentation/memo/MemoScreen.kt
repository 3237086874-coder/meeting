package com.enterprise.meeting.presentation.memo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.components.MemoEditDialog
import com.enterprise.meeting.presentation.theme.*

data class MemoItem(
    val id: Long,
    val title: String,
    val content: String,
    val reminderTime: String,
    val location: String = "",
    val isCompleted: Boolean,
)

private val sampleMemos = listOf(
    MemoItem(1, "下午 3 点确认市场活动场地", "联系王思雨确认行政部预订情况", "今天 15:00", "A 栋 3 楼会议室", false),
    MemoItem(2, "周报：本周复盘要点整理", "提交本周任务进度摘要", "今天 18:00", "", false),
    MemoItem(3, "回复客户简报反馈", "", "明天 10:00", "", true),
)

@Composable
fun MemoScreen(
    onBack: () -> Unit,
    startWithNewMemo: Boolean = false,
    editTitle: String = "",
    editContent: String = "",
    editTime: String = "",
    editLocation: String = "",
) {

    var memos by remember { mutableStateOf(sampleMemos) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingMemo by remember { mutableStateOf<MemoItem?>(null) }
    var showDetailMemo by remember { mutableStateOf<MemoItem?>(null) }
    val role = LocalRoleColors.current

    // Auto-open dialog when navigating with edit data from home
    LaunchedEffect(startWithNewMemo, editTitle) {
        if (startWithNewMemo) {
            if (editTitle.isNotEmpty()) {
                editingMemo = MemoItem(
                    id = -1,
                    title = editTitle,
                    content = editContent,
                    reminderTime = editTime,
                    location = editLocation,
                    isCompleted = false,
                )
            }
            showEditDialog = true
        }
    }

    val todayCount = memos.count { it.reminderTime.startsWith("今天") && !it.isCompleted }

    Scaffold(
        topBar = {
            Column {
                Spacer(modifier = Modifier.statusBarsPadding())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        onClick = onBack,
                        shape = CircleShape,
                        color = Color.Transparent,
                        modifier = Modifier.size(40.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回",
                                tint = textPrimary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "个人备忘提醒",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary,
                    )
                }
            }
        },
        containerColor = pageBg,
        floatingActionButton = {
            Surface(
                onClick = {
                    editingMemo = null
                    showEditDialog = true
                },
                shape = CircleShape,
                color = role.primary,
                modifier = Modifier.size(56.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "新建备忘",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp),
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 1. "今日提醒" summary card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = role.primary.copy(alpha = 0.06f)),
                    border = BorderStroke(0.67.dp, role.primary.copy(alpha = 0.25f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text("今日提醒", fontSize = 12.sp, color = textSecondary)
                            Text(
                                "$todayCount",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = role.primary.copy(alpha = 0.10f),
                            modifier = Modifier.size(48.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Filled.Notifications,
                                    contentDescription = "提醒",
                                    tint = role.primary,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        }
                    }
                }
            }

            // 2. Memo cards by category
            val incompleteMemos = memos.filter { !it.isCompleted }
            val completedMemos = memos.filter { it.isCompleted }

            if (memos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("暂无备忘", fontSize = 14.sp, color = textMuted)
                    }
                }
            } else {
                if (incompleteMemos.isNotEmpty()) {
                    item {
                        Text(
                            "未完成",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        )
                    }
                    items(incompleteMemos, key = { it.id }) { memo ->
                        MemoCard(
                            memo = memo,
                            onClick = { showDetailMemo = memo },
                            onLongClick = {
                                editingMemo = memo
                                showEditDialog = true
                            },
                            onToggleComplete = {
                                memos = memos.toMutableList().apply {
                                    val idx = indexOf(memo)
                                    this[idx] = memo.copy(isCompleted = !memo.isCompleted)
                                }
                            },
                        )
                    }
                }
                if (completedMemos.isNotEmpty()) {
                    item {
                        Text(
                            "已完成",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        )
                    }
                    items(completedMemos, key = { it.id }) { memo ->
                        MemoCard(
                            memo = memo,
                            onClick = { showDetailMemo = memo },
                            onLongClick = {
                                editingMemo = memo
                                showEditDialog = true
                            },
                            onToggleComplete = {
                                memos = memos.toMutableList().apply {
                                    val idx = indexOf(memo)
                                    this[idx] = memo.copy(isCompleted = !memo.isCompleted)
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    // Memo detail dialog
    showDetailMemo?.let { memo ->
        val role = LocalRoleColors.current
        AlertDialog(
            onDismissRequest = { showDetailMemo = null },
            shape = RoundedCornerShape(16.dp),
            containerColor = surfaceWhite,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(role.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.EditNote,
                            contentDescription = null,
                            tint = role.primary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Text(
                        memo.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (memo.content.isNotEmpty()) {
                        Text(
                            memo.content,
                            fontSize = 14.sp,
                            color = textPrimary,
                            lineHeight = 22.sp,
                        )
                    } else {
                        Text(
                            "(无详细内容)",
                            fontSize = 14.sp,
                            color = textMuted,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Filled.Notifications, null, modifier = Modifier.size(14.dp), tint = textMuted)
                        Text(memo.reminderTime, fontSize = 13.sp, color = textSecondary)
                        if (memo.location.isNotBlank()) {
                            Icon(Icons.Filled.LocationOn, null, modifier = Modifier.size(14.dp), tint = textMuted)
                            Text(memo.location, fontSize = 13.sp, color = textSecondary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailMemo = null }) {
                    Text("关闭", color = textSecondary)
                }
            },
        )
    }

    if (showEditDialog) {
        MemoEditDialog(
            title = if (editingMemo != null) "编辑备忘" else "新建备忘",
            initialTitle = editingMemo?.title ?: "",
            initialContent = editingMemo?.content ?: "",
            initialTime = editingMemo?.reminderTime ?: "",
            initialLocation = editingMemo?.location ?: "",
            onConfirm = { title, content, time, location ->
                if (editingMemo != null) {
                    memos = memos.toMutableList().apply {
                        this[indexOf(editingMemo)] = editingMemo!!.copy(
                            title = title.take(50),
                            content = content,
                            reminderTime = time,
                            location = location,
                        )
                    }
                } else {
                    val newId = (memos.maxOfOrNull { it.id } ?: 0) + 1
                    memos = memos + MemoItem(
                        id = newId,
                        title = title.take(50),
                        content = content,
                        reminderTime = time.ifEmpty { "今天" },
                        location = location,
                        isCompleted = false,
                    )
                }
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }
}

@Composable
private fun MemoCard(
    memo: MemoItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onToggleComplete: () -> Unit,
) {
    val role = LocalRoleColors.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (memo.isCompleted) role.primary.copy(alpha = 0.03f) else role.primary.copy(alpha = 0.06f)),
        border = BorderStroke(0.67.dp, role.primary.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp, top = 16.dp)
                    .size(36.dp)
                    .background(role.primary.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.EditNote,
                    contentDescription = null,
                    tint = role.primary,
                    modifier = Modifier.size(18.dp),
                )
            }

            Spacer(Modifier.width(8.dp))

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp, bottom = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Title row with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Checkbox(
                        checked = memo.isCompleted,
                        onCheckedChange = { onToggleComplete() },
                        modifier = Modifier.size(20.dp),
                        colors = CheckboxDefaults.colors(
                            checkedColor = colorSuccess,
                            uncheckedColor = textMuted,
                        ),
                    )
                    Text(
                        memo.title,
                        fontSize = 14.sp,
                        fontWeight = if (memo.isCompleted) FontWeight.Normal else FontWeight.Medium,
                        color = if (memo.isCompleted) textMuted else textPrimary,
                        maxLines = 1,
                        textDecoration = if (memo.isCompleted) TextDecoration.LineThrough else null,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = textMuted,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Text(
                    memo.content.ifEmpty { "(无详细内容)" },
                    fontSize = 12.sp,
                    color = textSecondary,
                    maxLines = 1,
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(role.primary.copy(alpha = 0.1f), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = role.primary,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                    Spacer(Modifier.width(2.dp))
                    Text(
                        " ${memo.reminderTime}",
                        fontSize = 11.sp,
                        color = textSecondary,
                    )
                    if (memo.location.isNotBlank()) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = textMuted,
                            modifier = Modifier.size(12.dp),
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            memo.location,
                            fontSize = 11.sp,
                            color = textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun MemoScreenPreview() {
    MeetingTheme(roleColors = RoleColors.superadmin) {
        MemoScreen(onBack = {})
    }
}
