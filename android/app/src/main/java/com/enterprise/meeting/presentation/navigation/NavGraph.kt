package com.enterprise.meeting.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import com.enterprise.meeting.presentation.admin.OrgManagementScreen
import com.enterprise.meeting.presentation.auth.LoginScreen
import com.enterprise.meeting.presentation.home.HomeScreen
import com.enterprise.meeting.presentation.meeting.create.AIProcessingScreen
import com.enterprise.meeting.presentation.meeting.create.RecordingScreen
import com.enterprise.meeting.presentation.meeting.create.UploadRecordingScreen
import com.enterprise.meeting.presentation.meeting.detail.MeetingTraceScreen
import com.enterprise.meeting.presentation.meeting.list.MeetingListScreen
import com.enterprise.meeting.presentation.meeting.tasks.MeetingTasksScreen
import com.enterprise.meeting.presentation.meeting.review.ReviewTasksScreen
import com.enterprise.meeting.presentation.meeting.review.ReviewTranscriptScreen
import com.enterprise.meeting.presentation.memo.MemoScreen
import com.enterprise.meeting.presentation.notification.NotificationListScreen
import com.enterprise.meeting.presentation.profile.ProfileScreen
import com.enterprise.meeting.presentation.task.detail.TaskDetailScreen
import com.enterprise.meeting.presentation.task.list.TaskListScreen
import com.enterprise.meeting.presentation.theme.*
import com.enterprise.meeting.domain.model.MeetingState

@Composable
fun MeetingNavGraph() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Development: set to desired role; change startDestination below to skip login
    var roleName by remember { mutableStateOf("superadmin") }
    val roleColor = RoleColors.fromRole(roleName)

    val bottomBarScreens = Screen.bottomNavItemsForRole(roleName).map { it.route }
    val showBottomBar = currentDestination?.route in bottomBarScreens ||
            currentDestination?.route?.startsWith("admin/") == true

    MeetingTheme(roleColors = roleColor) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    MeetingBottomBar(
                        navController = navController,
                        roleName = roleName
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Login.route,
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) }
            ) {
                // Auth
                composable(Screen.Login.route) {
                    LoginScreen(
                        defaultRole = roleName,
                        onLoginSuccess = { role ->
                            roleName = role
                            val start = if (role == "staff") Screen.Tasks.route else Screen.Home.route
                            navController.navigate(start) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    )
                }

                // Main tabs
                composable(Screen.Home.route) {
                    HomeScreen(
                        userRole = roleName,
                        onCreateMeeting = { navController.navigate(Screen.Recording.route) },
                        onUploadClick = { navController.navigate(Screen.UploadRecording.route) },
                        onMeetingClick = { id, state ->
                            val route = when (state) {
                                MeetingState.AI_PROCESSING -> Screen.AIProcessing.createRoute(false)
                                MeetingState.PENDING_REVIEW -> Screen.ReviewTranscript.createRoute(id)
                                MeetingState.PENDING_PUBLISH -> Screen.ReviewTasks.createRoute(id)
                                MeetingState.PENDING -> Screen.MeetingTasks.createRoute(id)
                                else -> Screen.MeetingTrace.createRoute(id)
                            }
                            navController.navigate(route)
                        },
                        onAdminOrgClick = { navController.navigate(Screen.Admin.route) },
                        onAdminAccountClick = { navController.navigate(Screen.Admin.route) },
                        onMemoClick = { navController.navigate(Screen.Memo.createRoute()) },
                        onMemoCreate = { title, content, time ->
                            navController.navigate(Screen.Memo.createRoute(new = true, editTitle = title, editContent = content, editTime = time))
                        },
                    )
                }

                composable(Screen.Meetings.route) {
                    MeetingListScreen(
                        onMeetingClick = { id, state ->
                            val route = when (state) {
                                MeetingState.AI_PROCESSING -> Screen.AIProcessing.createRoute(false)
                                MeetingState.PENDING_REVIEW -> Screen.ReviewTranscript.createRoute(id)
                                MeetingState.PENDING_PUBLISH -> Screen.ReviewTasks.createRoute(id)
                                MeetingState.PENDING -> Screen.MeetingTasks.createRoute(id)
                                else -> Screen.MeetingTrace.createRoute(id)
                            }
                            navController.navigate(route)
                        },
                        onCreateMeeting = { navController.navigate(Screen.Recording.route) },
                        onUploadClick = { navController.navigate(Screen.UploadRecording.route) },
                    )
                }

                composable(Screen.Tasks.route) {
                    TaskListScreen(
                        onTaskClick = { id, state -> navController.navigate(Screen.TaskDetail.createRoute(id, state)) }
                    )
                }

                // Meeting tasks (published meeting)
                composable(
                    route = Screen.MeetingTasks.route,
                    arguments = listOf(navArgument("meetingId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val meetingId = backStackEntry.arguments?.getLong("meetingId") ?: return@composable
                    MeetingTasksScreen(
                        meetingId = meetingId,
                        onBack = { navController.popBackStack() },
                        onTaskClick = { taskId -> navController.navigate(Screen.TaskDetail.createRoute(taskId)) }
                    )
                }

                composable(Screen.Notifications.route) {
                    NotificationListScreen()
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        userRole = roleName,
                        onMemosClick = { navController.navigate(Screen.Memo.createRoute()) },
                        onOrgManagement = { navController.navigate(Screen.Admin.route) },
                        onAccountManagement = { navController.navigate(Screen.Admin.route) },
                        onLogout = {
                            scope.launch {
                                navController.popBackStack(Screen.Home.route, true)
                                navController.navigate(Screen.Login.route)
                            }
                        },
                        onChangeRole = { roleName = it }
                    )
                }

                // Admin (superadmin only) — unified management screen
                composable(Screen.Admin.route) {
                    if (roleName != "superadmin") {
                        LaunchedEffect(Unit) { navController.popBackStack() }
                        Box(Modifier.fillMaxSize().background(pageBg))
                        return@composable
                    }
                    OrgManagementScreen(
                        onBack = { navController.popBackStack() },
                    )
                }

                composable(Screen.OrgManagement.route) {
                    navController.navigate(Screen.Admin.route) {
                        popUpTo(Screen.OrgManagement.route) { inclusive = true }
                    }
                    Box(Modifier.fillMaxSize().background(pageBg))
                }

                composable(Screen.AccountManagement.route) {
                    navController.navigate(Screen.Admin.route) {
                        popUpTo(Screen.AccountManagement.route) { inclusive = true }
                    }
                    Box(Modifier.fillMaxSize().background(pageBg))
                }

                // Meeting recording & processing flows
                composable(Screen.Recording.route) {
                    RecordingScreen(
                        onBack = { navController.popBackStack() },
                        onRecordingComplete = {
                            navController.navigate(Screen.AIProcessing.createRoute(uploaded = false)) {
                                popUpTo(Screen.Recording.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.UploadRecording.route) {
                    UploadRecordingScreen(
                        onBack = { navController.popBackStack() },
                        onUploadComplete = {
                            navController.navigate(Screen.AIProcessing.createRoute(uploaded = true)) {
                                popUpTo(Screen.UploadRecording.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = Screen.AIProcessing.route,
                    arguments = listOf(navArgument("uploaded") { type = NavType.BoolType; defaultValue = false })
                ) { backStackEntry ->
                    val uploaded = backStackEntry.arguments?.getBoolean("uploaded") ?: false
                    AIProcessingScreen(
                        uploaded = uploaded,
                        onBack = { navController.popBackStack() },
                        onProcessingComplete = {
                            navController.navigate(Screen.Meetings.route) {
                                popUpTo(Screen.AIProcessing.route) { inclusive = true }
                            }
                        },
                        onReviewClick = { meetingId ->
                            navController.navigate(Screen.ReviewTranscript.createRoute(meetingId))
                        }
                    )
                }

                // Meeting trace
                composable(
                    route = Screen.MeetingTrace.route,
                    arguments = listOf(navArgument("meetingId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val meetingId = backStackEntry.arguments?.getLong("meetingId") ?: return@composable
                    MeetingTraceScreen(
                        meetingId = meetingId,
                        onBack = { navController.popBackStack() },
                        onTaskClick = { taskId -> navController.navigate(Screen.TaskDetail.createRoute(taskId)) }
                    )
                }

                // Review flows
                composable(
                    route = Screen.ReviewTranscript.route,
                    arguments = listOf(navArgument("meetingId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val meetingId = backStackEntry.arguments?.getLong("meetingId") ?: return@composable
                    ReviewTranscriptScreen(
                        meetingId = meetingId,
                        onBack = { navController.popBackStack() },
                        onApprove = {
                            navController.navigate(Screen.ReviewTasks.createRoute(meetingId)) {
                                popUpTo(Screen.ReviewTranscript.createRoute(meetingId)) { inclusive = true }
                            }
                        },
                        onReject = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.ReviewTasks.route,
                    arguments = listOf(navArgument("meetingId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val meetingId = backStackEntry.arguments?.getLong("meetingId") ?: return@composable
                    ReviewTasksScreen(
                        meetingId = meetingId,
                        onBack = { navController.popBackStack() },
                        onPublish = {
                            navController.navigate(Screen.MeetingTasks.createRoute(meetingId)) {
                                popUpTo(Screen.Meetings.route)
                            }
                        }
                    )
                }

                // Task detail
                composable(
                    route = Screen.TaskDetail.route,
                    arguments = listOf(
                        navArgument("taskId") { type = NavType.LongType },
                        navArgument("state") { type = NavType.StringType; nullable = true; defaultValue = null }
                    )
                ) { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
                    val taskState = backStackEntry.arguments?.getString("state")
                    TaskDetailScreen(
                        taskId = taskId,
                        taskState = taskState,
                        onBack = { navController.popBackStack() },
                        userRole = roleName
                    )
                }

                // Memo
                composable(
                    route = Screen.Memo.route,
                    arguments = listOf(
                        navArgument("new") { type = NavType.BoolType; defaultValue = false },
                        navArgument("editTitle") { type = NavType.StringType; defaultValue = "" },
                        navArgument("editContent") { type = NavType.StringType; defaultValue = "" },
                        navArgument("editTime") { type = NavType.StringType; defaultValue = "" },
                        navArgument("editLocation") { type = NavType.StringType; defaultValue = "" },
                    )
                ) { backStackEntry ->
                    val startNew = backStackEntry.arguments?.getBoolean("new") ?: false
                    val editTitle = backStackEntry.arguments?.getString("editTitle") ?: ""
                    val editContent = backStackEntry.arguments?.getString("editContent") ?: ""
                    val editTime = backStackEntry.arguments?.getString("editTime") ?: ""
                    val editLocation = backStackEntry.arguments?.getString("editLocation") ?: ""
                    MemoScreen(
                        onBack = { navController.popBackStack() },
                        startWithNewMemo = startNew,
                        editTitle = editTitle,
                        editContent = editContent,
                        editTime = editTime,
                        editLocation = editLocation,
                    )
                }
            }
        }
    }
}

@Composable
private fun MeetingBottomBar(
    navController: NavHostController,
    roleName: String
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val roleColor = RoleColors.fromRole(roleName)

    val items = Screen.bottomNavItemsForRole(roleName)

    Column(Modifier.background(BottomNavBg)) {
        HorizontalDivider(thickness = 1.dp, color = colorDivider) // Figma: #EDE6D8 top border w=1
        NavigationBar(
            modifier = Modifier.height(64.dp),
            containerColor = BottomNavBg,
            tonalElevation = 0.dp
        ) {
            items.forEach { screen ->
                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true ||
                        (screen.route == Screen.Admin.route && currentDestination?.route?.startsWith("admin/") == true)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (!selected) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Box(modifier = Modifier.offset(y = 8.dp)) {
                            Icon(
                                imageVector = if (selected) screen.selectedIcon!! else screen.unselectedIcon!!,
                                contentDescription = screen.title,
                                tint = if (selected) roleColor.primary else BottomNavUnselected,
                            )
                            if (screen.route == Screen.Notifications.route && roleName == "superadmin") {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 8.dp, y = (-6).dp)
                                        .size(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(50.dp),
                                        color = colorDanger,
                                        modifier = Modifier.size(16.dp),
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                "5",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Text(
                            text = screen.title,
                            fontSize = 12.sp,
                            color = if (selected) roleColor.primary else BottomNavUnselected,
                        )
                    }
                }
            }
        }
    }
}
