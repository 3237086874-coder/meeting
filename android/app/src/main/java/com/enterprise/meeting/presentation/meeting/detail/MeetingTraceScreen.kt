package com.enterprise.meeting.presentation.meeting.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.theme.*

private data class FileItem(
    val label: String,
    val icon: ImageVector,
    val iconBgColor: Color,
)

private data class TimelineEvent(
    val label: String,
    val timestamp: String,
    val operator: String? = null,
    val icon: ImageVector,
    val iconColor: Color,
)

private data class RelatedTaskItem(
    val title: String,
    val status: String,
    val statusColor: Color,
    val assignee: String,
    val attachments: Int,
    val progress: String,
)

private val fileItems = listOf(
    FileItem("原始录音 · 47.8MB", Icons.Filled.Mic, Color(0xFF123B6A)),
    FileItem("AI 转写文本", Icons.Filled.Description, colorInfo),
    FileItem("AI 摘要", Icons.Filled.AutoAwesome, colorWarning),
    FileItem("任务抽取结果", Icons.AutoMirrored.Filled.Assignment, colorSuccess),
)

private val timelineEvents = listOf(
    TimelineEvent("录音上传", "05-23 14:30", icon = Icons.Filled.UploadFile, iconColor = textSecondary),
    TimelineEvent("AI 转写完成", "05-23 14:42", icon = Icons.Filled.Description, iconColor = colorInfo),
    TimelineEvent("AI 摘要生成", "05-23 14:43", icon = Icons.Filled.AutoAwesome, iconColor = colorInfo),
    TimelineEvent("任务抽取", "05-23 14:44", icon = Icons.AutoMirrored.Filled.Assignment, iconColor = colorInfo),
    TimelineEvent("人工修改文本", "05-23 15:02", operator = "李明", icon = Icons.Filled.Edit, iconColor = colorWarning),
    TimelineEvent("审核通过", "05-23 15:10", operator = "张伟", icon = Icons.Filled.CheckCircle, iconColor = colorSuccess),
    TimelineEvent("任务发布（6 项）", "05-23 15:11", icon = Icons.Filled.Publish, iconColor = colorSuccess),
    TimelineEvent("执行反馈 · 附件 2 份", "05-24 11:20", icon = Icons.Filled.Feedback, iconColor = colorSuccess),
    TimelineEvent("归档", "06-10 09:00", icon = Icons.Filled.Archive, iconColor = textSecondary),
)

private val relatedTasks = listOf(
    RelatedTaskItem("整理客户反馈清单", "已完成", colorSuccess, "李明", 2, "100%"),
    RelatedTaskItem("客户成功简报对接", "执行中", colorInfo, "阿布都热合曼", 2, "65%"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingTraceScreen(
    meetingId: Long,
    onBack: () -> Unit,
    onTaskClick: (Long) -> Unit,
) {
    val role = LocalRoleColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "会议溯源",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = role.primary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = role.primary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceWhite,
                    titleContentColor = role.primary,
                )
            )
        },
        containerColor = pageBg,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Meeting info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(0.67.dp, colorBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        "客户成功季度回顾",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary,
                    )
                    Text(
                        "市场部 · 主持人 阿布都热合曼",
                        fontSize = 13.sp,
                        color = textSecondary,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = colorSuccess.copy(alpha = 0.10f),
                        ) {
                            Text(
                                "已发布",
                                fontSize = 12.sp,
                                color = colorSuccess,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = textSecondary.copy(alpha = 0.10f),
                        ) {
                            Text(
                                "已归档",
                                fontSize = 12.sp,
                                color = textSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            "2026-05-23 14:30",
                            fontSize = 13.sp,
                            color = textSecondary,
                        )
                    }
                }
            }

            // File retention card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(0.67.dp, colorBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(
                        "文件留存（云端加密）",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textSecondary,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // 2x2 grid of file buttons
                    fileItems.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            rowItems.forEach { item ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(53.dp)
                                        .clickable { /* download */ },
                                    shape = RoundedCornerShape(10.dp),
                                    color = surfaceWhite,
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(32.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            color = item.iconBgColor.copy(alpha = 0.08f),
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    item.icon,
                                                    contentDescription = item.label,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = item.iconBgColor,
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            item.label,
                                            fontSize = 13.sp,
                                            color = textPrimary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                            // If odd count, fill remaining space
                            if (rowItems.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Trace timeline card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(0.67.dp, colorBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(
                        "溯源时间线",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textSecondary,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    timelineEvents.forEachIndexed { index, event ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                        ) {
                            // Left column: icon circle + connector line
                            Column(
                                modifier = Modifier.width(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Surface(
                                    modifier = Modifier.size(28.dp),
                                    shape = CircleShape,
                                    color = event.iconColor.copy(alpha = 0.10f),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            event.icon,
                                            contentDescription = event.label,
                                            modifier = Modifier.size(14.dp),
                                            tint = event.iconColor,
                                        )
                                    }
                                }
                                if (index < timelineEvents.lastIndex) {
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .weight(1f)
                                            .background(colorDivider)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            // Right column: label + timestamp
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = if (index < timelineEvents.lastIndex) 0.dp else 0.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Text(
                                    event.label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = textPrimary,
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        event.timestamp,
                                        fontSize = 12.sp,
                                        color = textSecondary,
                                    )
                                    if (event.operator != null) {
                                        Text(
                                            "· ${event.operator}",
                                            fontSize = 12.sp,
                                            color = textSecondary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Related tasks card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(0.67.dp, colorBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(
                        "关联任务（6）",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textSecondary,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    relatedTasks.forEach { task ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(66.dp)
                                .padding(bottom = 8.dp)
                                .clickable { onTaskClick(0L) },
                            shape = RoundedCornerShape(10.dp),
                            color = surfaceWhite,
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        task.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textPrimary,
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = task.statusColor.copy(alpha = 0.10f),
                                    ) {
                                        Text(
                                            task.status,
                                            fontSize = 12.sp,
                                            color = task.statusColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        )
                                    }
                                }
                                Text(
                                    "执行人 ${task.assignee} · 附件 ${task.attachments} 份 · 进度 ${task.progress}",
                                    fontSize = 12.sp,
                                    color = textSecondary,
                                )
                            }
                        }
                    }
                }
            }

            // Security footer
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = colorSuccess.copy(alpha = 0.063f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = colorSuccess,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "会议数据已云端加密存储 · 仅授权账号可访问",
                        fontSize = 12.sp,
                        color = colorSuccess,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun MeetingTraceScreenPreview() {
    MeetingTheme(roleColors = RoleColors.president) {
        MeetingTraceScreen(meetingId = 1L, onBack = {}, onTaskClick = {})
    }
}
