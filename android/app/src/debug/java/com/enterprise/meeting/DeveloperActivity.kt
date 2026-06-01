package com.enterprise.meeting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.enterprise.meeting.presentation.admin.OrgManagementScreen
import com.enterprise.meeting.presentation.auth.LoginScreen
import com.enterprise.meeting.presentation.home.HomeScreen
import com.enterprise.meeting.presentation.meeting.create.AIProcessingScreen
import com.enterprise.meeting.presentation.meeting.create.RecordingScreen
import com.enterprise.meeting.presentation.meeting.create.UploadRecordingScreen
import com.enterprise.meeting.presentation.meeting.detail.MeetingTraceScreen
import com.enterprise.meeting.presentation.meeting.list.MeetingListScreen
import com.enterprise.meeting.presentation.meeting.review.ReviewTasksScreen
import com.enterprise.meeting.presentation.meeting.review.ReviewTranscriptScreen
import com.enterprise.meeting.presentation.memo.MemoScreen
import com.enterprise.meeting.presentation.notification.NotificationListScreen
import com.enterprise.meeting.presentation.profile.ProfileScreen
import com.enterprise.meeting.presentation.task.detail.TaskDetailScreen
import com.enterprise.meeting.presentation.task.list.TaskListScreen
import com.enterprise.meeting.presentation.theme.*
import dagger.hilt.android.AndroidEntryPoint

private data class DevPage(
    val label: String,
    val route: String,
    val role: RoleColor = RoleColors.president,
)

private val devPages = listOf(
    DevPage("首页 (超级管理员)", "home_superadmin", RoleColors.superadmin),
    DevPage("首页 (高级管理层)", "home_president"),
    DevPage("首页 (部门负责人)", "home_manager", RoleColors.manager),
    DevPage("登录页", "login"),
    DevPage("会议列表", "meetings"),
    DevPage("会议录音", "recording"),
    DevPage("上传录音", "upload"),
    DevPage("AI 处理中", "processing"),
    DevPage("审核纪要", "review_transcript"),
    DevPage("审核任务", "review_tasks"),
    DevPage("会议溯源", "meeting_trace"),
    DevPage("任务列表", "tasks"),
    DevPage("任务详情", "task_detail"),
    DevPage("消息中心", "messages", RoleColors.superadmin),
    DevPage("我的", "profile", RoleColors.superadmin),
    DevPage("个人备忘", "memo", RoleColors.superadmin),
    DevPage("管理后台 (组织架构)", "admin", RoleColors.superadmin),
    DevPage("账号管理", "accounts", RoleColors.superadmin),
    DevPage("组织架构", "org", RoleColors.superadmin),
)

// Superadmin bottom nav mapping: dev route -> bottom tab route
private data class BottomTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val devRoute: String,
)

private val superadminBottomTabs = listOf(
    BottomTab("首页", Icons.Filled.Home, Icons.Outlined.Home, "home_superadmin"),
    BottomTab("管理", Icons.Filled.Settings, Icons.Outlined.Settings, "admin"),
    BottomTab("消息", Icons.Filled.Notifications, Icons.Outlined.Notifications, "messages"),
    BottomTab("我的", Icons.Filled.Person, Icons.Outlined.Person, "profile"),
)

@AndroidEntryPoint
class DeveloperActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var selectedRoute by remember { mutableStateOf("login") }
            val currentPage = devPages.find { it.route == selectedRoute } ?: devPages.first()
            val roleColor = currentPage.role

            MeetingTheme(roleColors = roleColor) {
                Scaffold(
                    containerColor = pageBg,
                    bottomBar = {
                        val showBottomBar = selectedRoute in superadminBottomTabs.map { it.devRoute } ||
                                selectedRoute.startsWith("home_") || selectedRoute == "accounts"
                        if (showBottomBar) {
                            Column(Modifier.background(BottomNavBg)) {
                                HorizontalDivider(thickness = 0.5.dp, color = colorDivider)
                                NavigationBar(
                                    containerColor = BottomNavBg,
                                    tonalElevation = 0.dp
                                ) {
                                    superadminBottomTabs.forEach { tab ->
                                        val selected = tab.devRoute == selectedRoute ||
                                                (tab.devRoute == "home_superadmin" && selectedRoute.startsWith("home_")) ||
                                                (tab.devRoute == "admin" && selectedRoute == "accounts")
                                        NavigationBarItem(
                                            icon = {
                                                Icon(
                                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                    contentDescription = tab.title
                                                )
                                            },
                                            label = { Text(tab.title) },
                                            selected = selected,
                                            onClick = { selectedRoute = tab.devRoute },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = roleColor.primary,
                                                selectedTextColor = roleColor.primary,
                                                unselectedIconColor = BottomNavUnselected,
                                                unselectedTextColor = BottomNavUnselected,
                                                indicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        DevPageContent(
                            route = selectedRoute,
                            onNavigate = { selectedRoute = it },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DevPageContent(
    route: String,
    onNavigate: (String) -> Unit = {},
) {
    when (route) {
        "home_superadmin" -> HomeScreen(
            userRole = "superadmin",
            userName = "张",
            onAdminOrgClick = { onNavigate("org") },
            onAdminAccountClick = { onNavigate("accounts") },
            onMemoClick = { onNavigate("memo") },
            onMemoCreate = { _, _, _ -> onNavigate("memo") },
        )
        "home_president" -> HomeScreen(userRole = "president", userName = "李")
        "home_manager" -> HomeScreen(userRole = "manager", userName = "王")
        "login" -> LoginScreen(onLoginSuccess = { onNavigate("home_superadmin") })
        "meetings" -> MeetingListScreen(onMeetingClick = { _, _ -> }, onCreateMeeting = {})
        "recording" -> RecordingScreen(onBack = {}, onRecordingComplete = {})
        "upload" -> UploadRecordingScreen(onBack = {}, onUploadComplete = {})
        "processing" -> AIProcessingScreen(onBack = {}, onProcessingComplete = {})
        "review_transcript" -> ReviewTranscriptScreen(meetingId = 1L, onBack = {}, onApprove = {}, onReject = {})
        "review_tasks" -> ReviewTasksScreen(meetingId = 1L, onBack = {}, onPublish = {})
        "meeting_trace" -> MeetingTraceScreen(meetingId = 1L, onBack = {}, onTaskClick = {})
        "tasks" -> TaskListScreen(onTaskClick = { _, _ -> })
        "task_detail" -> TaskDetailScreen(taskId = 1L, onBack = {}, userRole = "staff")
        "messages" -> NotificationListScreen()
        "profile" -> ProfileScreen(
            userRole = "superadmin",
            onMemosClick = { onNavigate("memo") },
            onOrgManagement = {},
            onAccountManagement = {},
            onLogout = {},
        )
        "memo" -> MemoScreen(onBack = { onNavigate("profile") })
        "admin" -> OrgManagementScreen(
            onBack = {},
        )
        "accounts" -> OrgManagementScreen(onBack = {})
        "org" -> OrgManagementScreen(onBack = { onNavigate("home_superadmin") })
    }
}
