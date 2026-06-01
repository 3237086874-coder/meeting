package com.enterprise.meeting.presentation.task.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.domain.model.TaskState
import com.enterprise.meeting.presentation.components.TaskCard
import com.enterprise.meeting.presentation.theme.*

private data class FilterTab(val key: Any?, val label: String, val count: Int)

private data class TaskListItemData(
    val id: Long,
    val title: String,
    val sourceMeeting: String,
    val assignee: String,
    val dueDate: String,
    val progress: Int,
    val state: TaskState,
    val isOverdue: Boolean = false,
)

private val sampleTasks = listOf(
    TaskListItemData(1, "提交市场活动执行方案", "Q3 产品规划会议", "张伟 · 运营部", "本周五 18:00", 0, TaskState.PENDING_CONFIRM, isOverdue = false),
    TaskListItemData(2, "整理 Q3 预算表并上传执行附件", "Q3 产品规划会议", "李明 · 产品部", "06-02 12:00", 45, TaskState.EXECUTING),
    TaskListItemData(3, "客户成功简报对接", "客户成功季度回顾", "阿布都热合曼 · 市场部", "06-05 18:00", 65, TaskState.EXECUTING),
    TaskListItemData(4, "整理客户反馈清单", "客户成功季度回顾", "李明 · 产品部", "05-20 18:00", 80, TaskState.PENDING_CONFIRM, isOverdue = true),
    TaskListItemData(5, "行政预算复盘报告", "行政预算复盘", "王思雨 · 行政部", "05-19 12:00", 100, TaskState.COMPLETED),
)

private fun displayState(task: TaskListItemData): String = when {
    task.isOverdue -> "已逾期"
    else -> task.state.displayName
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onTaskClick: (Long, String) -> Unit,
) {
    val role = LocalRoleColors.current
    var selectedFilter by remember { mutableStateOf<Any?>(null) }

    fun countFor(label: String): Int = sampleTasks.count { task ->
        val state = displayState(task)
        when (label) {
            "待确认" -> state == "待确认"
            "执行中" -> state == "执行中"
            "已完成" -> state == "已完成"
            "已逾期" -> state == "已逾期"
            else -> true
        }
    }

    val filterTabs = listOf(
        FilterTab(null, "全部", sampleTasks.size),
        FilterTab("pending_confirm", "待确认", countFor("待确认")),
        FilterTab("executing", "执行中", countFor("执行中")),
        FilterTab("completed", "已完成", countFor("已完成")),
        FilterTab("overdue", "已逾期", countFor("已逾期")),
    )

    val filteredTasks = sampleTasks.filter { task ->
        val state = displayState(task)
        when (selectedFilter) {
            null -> true
            "pending_confirm" -> state == "待确认"
            "executing" -> state == "执行中"
            "completed" -> state == "已完成"
            "overdue" -> state == "已逾期"
            else -> true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("任务", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Search, contentDescription = "搜索", tint = textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceWhite,
                    titleContentColor = textPrimary
                )
            )
        },
        containerColor = pageBg
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filterTabs.forEach { tab ->
                    val isSelected = selectedFilter == tab.key

                    Surface(
                        onClick = { selectedFilter = tab.key },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) role.primary else Color.White,
                        border = if (isSelected) BorderStroke(0.67.dp, role.primary)
                            else BorderStroke(0.67.dp, colorBorder),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp).height(36.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = tab.label,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else textSecondary
                            )
                            if (tab.count > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .height(17.dp)
                                        .background(
                                            color = if (isSelected) Color.White.copy(alpha = 0.25f)
                                                else colorDanger.copy(alpha = 0.10f),
                                            shape = RoundedCornerShape(50.dp)
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = tab.count.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSelected) Color.White else colorDanger,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .padding(horizontal = 6.dp)
                                            .offset(y = (-3).dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Task list
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredTasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("暂无任务", style = MaterialTheme.typography.bodyMedium, color = textMuted)
                        }
                    }
                } else {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskCard(
                            title = task.title,
                            sourceMeeting = task.sourceMeeting,
                            assignee = task.assignee,
                            dueDate = task.dueDate,
                            progress = task.progress,
                            state = displayState(task),
                            modifier = Modifier.animateItem(),
                            onClick = { onTaskClick(task.id, displayState(task)) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun TaskListScreenPreview() {
    MeetingTheme {
        TaskListScreen(onTaskClick = { _, _ -> })
    }
}
