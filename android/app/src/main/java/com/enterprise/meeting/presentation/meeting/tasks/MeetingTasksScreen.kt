package com.enterprise.meeting.presentation.meeting.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.theme.*

private data class PublishedTask(
    val id: Long,
    val title: String,
    val assignee: String,
    val dept: String,
    val dueDate: String,
    val status: String,
    val attachments: Int = 0,
    val progress: String = "0%",
)

private val sampleTasks = listOf(
    PublishedTask(1, "完成前端页面开发，包括首页和登录页面", "王五", "研发部", "2026-05-30", "已完成", 2, "100%"),
    PublishedTask(2, "补充后端API接口文档", "赵六", "研发部", "2026-05-28", "已完成", 1, "100%"),
    PublishedTask(3, "安排用户测试并收集反馈", "产品部", "产品部", "2026-06-05", "执行中", 0, "65%"),
    PublishedTask(4, "配置CI/CD自动化部署流水线", "张三", "研发部", "2026-05-31", "执行中", 1, "45%"),
    PublishedTask(5, "编写数据库迁移脚本", "李四", "研发部", "2026-05-29", "已完成", 0, "100%"),
    PublishedTask(6, "设计系统监控告警方案", "钱七", "运维部", "2026-06-02", "待确认", 0, "0%"),
)

private fun taskStatusColor(status: String): Color = when (status) {
    "待确认" -> colorWarning
    "执行中" -> colorInfo
    "已完成" -> colorSuccess
    "已逾期", "已过期" -> colorDanger
    else -> textSecondary
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingTasksScreen(
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
                        "相关任务",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Meeting info header
            item {
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
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                "2026-05-23 14:30",
                                fontSize = 13.sp,
                                color = textSecondary,
                            )
                        }
                    }
                }
            }

            // Task stats
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(0.67.dp, colorBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        TaskStat(label = "全部", value = sampleTasks.size.toString(), color = textPrimary)
                        TaskStat(label = "待确认", value = sampleTasks.count { it.status == "待确认" }.toString(), color = colorWarning)
                        TaskStat(label = "执行中", value = sampleTasks.count { it.status == "执行中" }.toString(), color = colorInfo)
                        TaskStat(label = "已完成", value = sampleTasks.count { it.status == "已完成" }.toString(), color = colorSuccess)
                    }
                }
            }

            // Task list header
            item {
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
                        "共 ${sampleTasks.size} 项",
                        fontSize = 12.sp,
                        color = textMuted,
                    )
                }
            }

            // Task items
            items(sampleTasks, key = { it.id }) { task ->
                val statusColor = taskStatusColor(task.status)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTaskClick(task.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceWhite),
                    border = BorderStroke(0.67.dp, colorBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        // Left status strip
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
                                .background(statusColor)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
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
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = statusColor.copy(alpha = 0.10f),
                                ) {
                                    Text(
                                        task.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = statusColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = textMuted,
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        "${task.assignee} · ${task.dept}",
                                        fontSize = 11.sp,
                                        color = textSecondary,
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Event,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = textMuted,
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        task.dueDate,
                                        fontSize = 11.sp,
                                        color = textSecondary,
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Text(
                                    "附件 ${task.attachments} 份",
                                    fontSize = 11.sp,
                                    color = textMuted,
                                )
                                Text(
                                    "进度 ${task.progress}",
                                    fontSize = 11.sp,
                                    color = if (task.progress == "100%") colorSuccess else textMuted,
                                )
                            }
                        }
                        // Chevron
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.ChevronRight,
                                contentDescription = null,
                                tint = textMuted,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun TaskStat(
    label: String,
    value: String,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            label,
            fontSize = 11.sp,
            color = textSecondary,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun MeetingTasksScreenPreview() {
    MeetingTheme(roleColors = RoleColors.president) {
        MeetingTasksScreen(meetingId = 1L, onBack = {}, onTaskClick = {})
    }
}
