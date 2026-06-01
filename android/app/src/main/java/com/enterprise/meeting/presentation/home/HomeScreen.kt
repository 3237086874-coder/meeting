package com.enterprise.meeting.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.enterprise.meeting.domain.model.MeetingState
import com.enterprise.meeting.presentation.components.EmptyState
import com.enterprise.meeting.presentation.components.ErrorState
import com.enterprise.meeting.presentation.components.LoadingSkeleton
import com.enterprise.meeting.presentation.components.MeetingCard
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.theme.*

@Composable
fun HomeScreen(
    onCreateMeeting: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onMeetingClick: (Long, MeetingState) -> Unit = { _, _ -> },
    onAdminOrgClick: () -> Unit = {},
    onAdminAccountClick: () -> Unit = {},
    onMemoClick: () -> Unit = {},
    onMemoCreate: (String, String, String) -> Unit = { _, _, _ -> },
    userName: String = "李明",
    userRole: String = "president",
    viewModel: HomeViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
) {
    val uiState by viewModel.uiState.collectAsState()
    val role = LocalRoleColors.current
    val isSuperadmin = userRole == "superadmin"

    when (val state = uiState) {
        is HomeUiState.Loading -> LoadingHomeContent(
            isSuperadmin = isSuperadmin,
            role = role,
        )

        is HomeUiState.Error -> ErrorState(
            message = state.message,
            onRetry = { viewModel.loadData() },
            modifier = Modifier.fillMaxSize(),
        )

        is HomeUiState.Empty -> EmptyState(
            title = "暂无数据",
            subtitle = "当前没有可显示的内容",
            modifier = Modifier.fillMaxSize(),
        )

        is HomeUiState.Success -> HomeContent(
            userInitial = state.userInitial,
            userDisplayName = state.userDisplayName,
            userRole = state.userRole,
            companyName = state.companyName,
            appTitle = state.appTitle,
            stats = state.stats,
            memoPreviewText = state.memoPreview?.title,
            memoPreviewContent = state.memoPreview?.content ?: "",
            memoPreviewTime = state.memoPreview?.let {
                it.reminderAt?.let { time ->
                    val cal = java.util.Calendar.getInstance().apply { timeInMillis = time }
                    String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
                }
            },
            onCreateMeeting = onCreateMeeting,
            onMeetingClick = onMeetingClick,
            onAdminOrgClick = onAdminOrgClick,
            onAdminAccountClick = onAdminAccountClick,
            onMemoClick = onMemoClick,
            onMemoCreate = onMemoCreate,
            role = role,
            isSuperadmin = isSuperadmin,
            onUploadClick = onUploadClick,
        )
    }
}

// ============================================================
// Loading State
// ============================================================

@Composable
private fun LoadingHomeContent(
    isSuperadmin: Boolean,
    role: RoleColor,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBg)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(role.gradientStart, role.gradientEnd))
                    )
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                modifier = Modifier.width(80.dp).height(11.dp),
                                shape = RoundedCornerShape(4.dp),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {}
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                modifier = Modifier.width(140.dp).height(22.dp),
                                shape = RoundedCornerShape(4.dp),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {}
                        }
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.18f)
                        ) {}
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().height(72.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(3) {
                            Surface(
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White.copy(alpha = 0.14f)
                            ) {}
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Surface(
                    modifier = Modifier.width(160.dp).height(16.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = colorSkeleton
                ) {}
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        modifier = Modifier.weight(1f).height(109.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = colorSkeleton
                    ) {}
                    Surface(
                        modifier = Modifier.weight(1f).height(109.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = colorSkeleton
                    ) {}
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Surface(
                    modifier = Modifier.width(130.dp).height(16.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = colorSkeleton
                ) {}
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth().height(87.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = colorSkeleton
                ) {}
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        modifier = Modifier.width(80.dp).height(16.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = colorSkeleton
                    ) {}
                    Surface(
                        modifier = Modifier.width(60.dp).height(14.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = colorSkeleton
                    ) {}
                }
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth().height(71.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = colorSkeleton
                ) {}
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ============================================================
// Success State — Full Content
// ============================================================

@Composable
private fun HomeContent(
    userInitial: String,
    userDisplayName: String,
    userRole: String,
    companyName: String,
    appTitle: String,
    stats: HomeStats,
    memoPreviewText: String?,
    memoPreviewContent: String = "",
    memoPreviewTime: String?,
    onCreateMeeting: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onMeetingClick: (Long, MeetingState) -> Unit = { _, _ -> },
    onAdminOrgClick: () -> Unit,
    onAdminAccountClick: () -> Unit,
    onMemoClick: () -> Unit,
    onMemoCreate: (String, String, String) -> Unit = { _, _, _ -> },
    role: RoleColor,
    isSuperadmin: Boolean,
) {
    var showMemoDetail by remember { mutableStateOf(false) }
    var showEditMemo by remember { mutableStateOf(false) }
    var memoCompleted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBg)
    ) {
        // ===== Gradient Header =====
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(role.gradientStart, role.gradientEnd)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Top row: subtitle+title | role badge+avatar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isSuperadmin) companyName else "$companyName · 全公司",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isSuperadmin) appTitle else "全公司协同概览",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSuperadmin) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White,
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.Shield,
                                            contentDescription = null,
                                            tint = role.primary,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "超级管理员",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = role.primary,
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            if (!isSuperadmin) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White,
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "高级管理层",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = role.primary,
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.18f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userInitial,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Hero stats row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HeroStat(
                            label = if (isSuperadmin) "待审核" else "全公司待审核",
                            value = stats.pendingReviewCount.toString(),
                            valueColor = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        HeroStat(
                            label = if (isSuperadmin) "待确认" else "执行中",
                            value = stats.pendingConfirmCount.toString(),
                            valueColor = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        HeroStat(
                            label = "即将逾期",
                            value = stats.overdueCount.toString(),
                            valueColor = Color(0xFFFFD8A8),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (isSuperadmin) {
            // ===== Company Stats Section =====
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "全公司员工统计",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = cardBg,
                        border = BorderStroke(0.67.dp, colorSoftBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(87.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                label = "员工数",
                                value = stats.employeeCount.toString(),
                                valueColor = Color(0xFF0A0A0A),
                                modifier = Modifier.weight(1f)
                            )
                            VerticalDivider(
                                modifier = Modifier
                                    .height(54.dp)
                                    .align(Alignment.CenterVertically),
                                thickness = 1.dp,
                                color = Color.Black.copy(alpha = 0.1f)
                            )
                            StatItem(
                                label = "部门数",
                                value = stats.departmentCount.toString(),
                                valueColor = Color(0xFF0A0A0A),
                                modifier = Modifier.weight(1f)
                            )
                            VerticalDivider(
                                modifier = Modifier
                                    .height(54.dp)
                                    .align(Alignment.CenterVertically),
                                thickness = 1.dp,
                                color = Color.Black.copy(alpha = 0.1f)
                            )
                            StatItem(
                                label = "活跃账号",
                                value = stats.activeAccountCount.toString(),
                                valueColor = Color(0xFF38A169),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        } else {
            // ===== Quick Actions Section =====
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionCard(
                            icon = Icons.Filled.Mic,
                            label = "开始新会议",
                            subtitle = "创建会议并录音",
                            iconBgColor = role.primary,
                            onClick = onCreateMeeting,
                            modifier = Modifier.weight(1f),
                        )
                        QuickActionCard(
                            icon = Icons.Filled.FileUpload,
                            label = "上传录音",
                            subtitle = "支持 mp3 / m4a / wav",
                            iconBgColor = role.primary,
                            onClick = onUploadClick,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ===== Pending Review Meetings Section =====
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "全公司待审核会议",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary,
                        )
                        TextButton(onClick = { onMeetingClick(-1, MeetingState.PENDING_REVIEW) }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("查看全部", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = role.primary)
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(Icons.Filled.ChevronRight, null, tint = role.primary, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        sampleMeetings.forEach { meeting ->
                            MeetingCard(
                                title = meeting.title,
                                department = meeting.department,
                                host = meeting.host,
                                duration = meeting.duration,
                                attendeeCount = meeting.attendeeCount,
                                taskCount = meeting.taskCount,
                                state = meeting.state,
                                onClick = { onMeetingClick(meeting.id, meeting.state) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // ===== Pending Confirm Tasks Section =====
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "待确认任务",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary,
                        )
                        TextButton(onClick = { }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("查看全部", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = role.primary)
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(Icons.Filled.ChevronRight, null, tint = role.primary, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = cardBg,
                        border = BorderStroke(0.67.dp, colorSoftBorder)
                    ) {
                        Row(modifier = Modifier.height(80.dp)) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
                                    .background(colorWarning)
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Q3 市场活动方案审核",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textPrimary,
                                    maxLines = 1,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "截止 2026-06-01",
                                    fontSize = 11.sp,
                                    color = textSecondary,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .background(colorDivider, RoundedCornerShape(2.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0f)
                                            .height(4.dp)
                                            .background(colorWarning, RoundedCornerShape(2.dp))
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.ChevronRight,
                                    contentDescription = null,
                                    tint = textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // ===== Personal Memo Section =====
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "个人备忘",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary,
                    )
                    TextButton(onClick = onMemoClick) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "查看全部",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = role.primary,
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                Icons.Filled.ChevronRight,
                                contentDescription = null,
                                tint = role.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (memoPreviewText != null && !memoCompleted) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { showMemoDetail = true },
                                onLongClick = { showEditMemo = true }
                            ),
                        shape = RoundedCornerShape(14.dp),
                        color = role.primary.copy(alpha = 0.06f),
                        border = BorderStroke(0.67.dp, role.primary.copy(alpha = 0.25f)),
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

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(top = 16.dp, bottom = 16.dp, end = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Checkbox(
                                        checked = memoCompleted,
                                        onCheckedChange = { memoCompleted = it },
                                        modifier = Modifier.size(20.dp),
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = colorSuccess,
                                            uncheckedColor = textMuted,
                                        ),
                                    )
                                    Text(
                                        text = memoPreviewText,
                                        fontSize = 14.sp,
                                        fontWeight = if (memoCompleted) FontWeight.Normal else FontWeight.Medium,
                                        color = if (memoCompleted) textMuted else textPrimary,
                                        maxLines = 1,
                                        textDecoration = if (memoCompleted) TextDecoration.LineThrough else null,
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
                                    text = memoPreviewContent.ifEmpty { "(无详细内容)" },
                                    fontSize = 12.sp,
                                    color = textSecondary,
                                    maxLines = 1,
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
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
                                    Text(
                                        text = memoPreviewTime ?: "--:--",
                                        fontSize = 11.sp,
                                        color = textSecondary,
                                    )
                                }
                            }
                        }
                    }
                } else if (memoPreviewText == null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { onMemoCreate(memoPreviewText ?: "", memoPreviewContent, memoPreviewTime ?: "") }),
                        shape = RoundedCornerShape(14.dp),
                        color = role.primary.copy(alpha = 0.06f),
                        border = BorderStroke(0.67.dp, role.primary.copy(alpha = 0.25f)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无备忘，点击创建",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = textMuted,
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Memo detail dialog
    if (showMemoDetail && memoPreviewText != null) {
        AlertDialog(
            onDismissRequest = { showMemoDetail = false },
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
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.EditNote,
                            contentDescription = null,
                            tint = role.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            memoPreviewText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = textPrimary,
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (memoPreviewContent.isNotEmpty()) {
                        Text(
                            memoPreviewContent,
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
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = textMuted,
                        )
                        Text(
                            memoPreviewTime ?: "--:--",
                            fontSize = 13.sp,
                            color = textSecondary,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMemoDetail = false }) {
                    Text("关闭", color = textSecondary)
                }
            },
        )
    }

    // Edit memo dialog (long press)
    if (showEditMemo && memoPreviewText != null) {
        var editTitle by remember { mutableStateOf(memoPreviewText ?: "") }
        var editContent by remember { mutableStateOf(memoPreviewContent) }

        AlertDialog(
            onDismissRequest = { showEditMemo = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = surfaceWhite,
            title = {
                Text("编辑备忘", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("标题", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                        OutlinedTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = textPrimary),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = role.primary,
                                unfocusedBorderColor = colorBorder,
                            ),
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("内容", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                        OutlinedTextField(
                            value = editContent,
                            onValueChange = { editContent = it },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = textPrimary),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = role.primary,
                                unfocusedBorderColor = colorBorder,
                            ),
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showEditMemo = false },
                    colors = ButtonDefaults.buttonColors(containerColor = role.primary),
                    shape = RoundedCornerShape(8.dp),
                ) { Text("保存", fontWeight = FontWeight.Medium) }
            },
            dismissButton = {
                TextButton(onClick = { showEditMemo = false }) {
                    Text("取消", color = textSecondary)
                }
            },
        )
    }
}

// ============================================================
// Sample data for president home sections
// ============================================================

private data class HomeMeetingItem(
    val id: Long,
    val title: String,
    val department: String,
    val host: String,
    val duration: String,
    val attendeeCount: Int,
    val taskCount: Int,
    val state: MeetingState,
)

private val sampleMeetings = listOf(
    HomeMeetingItem(1, "Q3 产品规划会议", "产品部", "李明", "58:24", 9, 6, MeetingState.PENDING_REVIEW),
    HomeMeetingItem(2, "第四季度跨部门协作项目启动与资源协调会议", "运营部", "王思雨", "01:42:08", 14, 11, MeetingState.PENDING_REVIEW),
)

// ============================================================
// Reusable Sub-Components
// ============================================================

@Composable
private fun HeroStat(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.14f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    subtitle: String,
    iconBgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(109.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(0.67.dp, colorBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 12.dp)
        ) {
            IconBox(
                icon = icon,
                iconBgColor = iconBgColor,
                size = 36.dp,
                iconSize = 18.dp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textSecondary,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun IconBox(
    icon: ImageVector,
    iconBgColor: Color,
    size: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(iconBgColor.copy(alpha = 0.082f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconBgColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun HomeScreenPreview() {
    MeetingTheme(roleColors = RoleColors.superadmin) {
        HomeContent(
            userInitial = "张",
            userDisplayName = "张伟",
            userRole = "superadmin",
            companyName = "远航科技集团",
            appTitle = "企业会议中枢",
            stats = HomeStats(
                pendingReviewCount = 8,
                pendingConfirmCount = 23,
                overdueCount = 6,
                employeeCount = 248,
                departmentCount = 12,
                activeAccountCount = 231,
            ),
            memoPreviewText = "下午 3 点确认市场活动场地",
            memoPreviewTime = "15:00",
            onAdminOrgClick = {},
            onAdminAccountClick = {},
            onMemoClick = {},
            role = RoleColors.superadmin,
            isSuperadmin = true,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun HomeScreenLoadingPreview() {
    MeetingTheme(roleColors = RoleColors.superadmin) {
        Box(modifier = Modifier.background(pageBg)) {
            LoadingHomeContent(isSuperadmin = true, role = RoleColors.superadmin)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun HomeScreenPresidentPreview() {
    MeetingTheme(roleColors = RoleColors.president) {
        HomeContent(
            userInitial = "李",
            userDisplayName = "李明",
            userRole = "president",
            companyName = "远航科技集团",
            appTitle = "企业会议中枢",
            stats = HomeStats(
                pendingReviewCount = 3,
                pendingConfirmCount = 12,
                overdueCount = 2,
                employeeCount = 248,
                departmentCount = 12,
                activeAccountCount = 231,
            ),
            memoPreviewText = "下午 3 点确认市场活动场地",
            memoPreviewTime = "15:00",
            onAdminOrgClick = {},
            onAdminAccountClick = {},
            onMemoClick = {},
            role = RoleColors.president,
            isSuperadmin = false,
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = 17.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = textSecondary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
        )
    }
}
