package com.enterprise.meeting.presentation.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.theme.*

private data class NotificationData(
    val id: Long,
    val type: String,
    val title: String,
    val subtitle: String,
    val time: String,
    val isUnread: Boolean,
)

private val sampleNotifications = listOf(
    NotificationData(1, "会议", "Q3 产品规划会议", "AI 处理已完成，请审核会议纪要", "刚刚", true),
    NotificationData(2, "任务", "AI 处理已完成", "Q3 产品规划会议已处理完成", "10 分钟前", true),
    NotificationData(3, "提醒", "任务即将截止", "05-30 18:00 前提交市场活动执行方案", "1 小时前", true),
    NotificationData(4, "会议", "客户成功季度回顾", "已通知 8 名执行人员", "今天 09:42", false),
    NotificationData(5, "系统", "系统通知", "您被授权为审核人", "昨天 17:20", false),
    NotificationData(6, "系统", "系统通知", "陈晓东账号已停用", "昨天 11:08", false),
)


private data class FilterItem(val key: String?, val label: String, val count: Int = 0)

private val filters = listOf(
    FilterItem(null, "全部", 12),
    FilterItem("任务", "任务", 5),
    FilterItem("会议", "会议", 4),
    FilterItem("系统", "系统", 3),
)

private fun notificationIcon(type: String): ImageVector = when (type) {
    "任务" -> Icons.AutoMirrored.Filled.Assignment
    "会议" -> Icons.Filled.MeetingRoom
    "提醒" -> Icons.Filled.NotificationsActive
    else -> Icons.Filled.Info
}

private fun notificationIconColor(type: String): Color = when (type) {
    "任务" -> Color(0xFF0284C7)  // blue
    "会议" -> Color(0xFFD97706)  // orange
    "提醒" -> Color(0xFFC94747)  // red
    else -> Color(0xFF7A6B53)    // brown
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen() {
    val role = LocalRoleColors.current
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    val filteredNotifications = sampleNotifications.filter { n ->
        selectedFilter == null || n.type == selectedFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "消息",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = textPrimary,
                    )
                },
                actions = {
                    Surface(
                        onClick = { /* mark all read */ },
                        color = Color.Transparent,
                    ) {
                        Text(
                            "全部已读",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = role.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = pageBg,
                    titleContentColor = textPrimary,
                )
            )
        },
        containerColor = pageBg,
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                filters.forEach { filter ->
                    val isSelected = selectedFilter == filter.key
                    Surface(
                        onClick = { selectedFilter = filter.key },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) role.primary else Color.White,
                        border = if (isSelected) BorderStroke(0.67.dp, role.primary)
                            else BorderStroke(0.67.dp, colorBorder),
                        modifier = Modifier.weight(1f),
                    ) {
                        Row(
                            modifier = Modifier
                                .height(36.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                filter.label,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else textSecondary,
                            )
                            if (filter.count > 0) {
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
                                        "${filter.count}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSelected) Color.White else colorDanger,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier//数字向上微调 3dp 补偿字体内边距造成的位置偏移。
                                            .padding(horizontal = 6.dp)
                                            .offset(y = (-3).dp),
                                    )
                                }
                            }
}
                    }
                }
            }



            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredNotifications.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 80.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "暂无消息",
                                fontSize = 14.sp,
                                color = textMuted,
                            )
                        }
                    }
                } else {
                    items(filteredNotifications, key = { it.id }) { notification ->
                        val cardBgColor = if (notification.isUnread) cardBg else Color.White
                        val cardStrokeColor = if (notification.isUnread) colorSoftBorder else colorBorder
                        val iconColor = notificationIconColor(notification.type)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBgColor),
                            border = BorderStroke(0.67.dp, cardStrokeColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, end = 12.dp, top = 14.dp, bottom = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // 36dp circle with icon
                                Surface(
                                    modifier = Modifier.size(36.dp),
                                    shape = CircleShape,
                                    color = iconColor,
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = notificationIcon(notification.type),
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Title + subtitle
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            notification.title,
                                            fontSize = 14.sp,
                                            fontWeight = if (notification.isUnread) FontWeight.SemiBold else FontWeight.Medium,
                                            color = textPrimary,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f, fill = false),
                                        )
                                        if (notification.isUnread) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                modifier = Modifier.size(8.dp),
                                                shape = CircleShape,
                                                color = colorDanger,
                                            ) {}
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        notification.subtitle,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = textSecondary,
                                        maxLines = 1,
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Time
                                Text(
                                    notification.time,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = textMuted,
                                )
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
fun NotificationListScreenPreview() {
    MeetingTheme(roleColors = RoleColors.president) {
        NotificationListScreen()
    }
}
