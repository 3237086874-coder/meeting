package com.enterprise.meeting.presentation.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String = "",
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    // Auth
    data object Login : Screen("login")

    // Main tabs
    data object Home : Screen(
        route = "home",
        title = "首页",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Meetings : Screen(
        route = "meetings",
        title = "会议",
        selectedIcon = Icons.Filled.MeetingRoom,
        unselectedIcon = Icons.Outlined.MeetingRoom
    )

    data object Tasks : Screen(
        route = "tasks",
        title = "任务",
        selectedIcon = Icons.Filled.Assignment,
        unselectedIcon = Icons.Outlined.Assignment
    )

    data object Notifications : Screen(
        route = "notifications",
        title = "消息",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications
    )

    data object Profile : Screen(
        route = "profile",
        title = "我的",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    data object Admin : Screen(
        route = "admin",
        title = "管理",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    // Meeting sub-screens
    data object Recording : Screen(route = "meetings/recording")
    data object UploadRecording : Screen(route = "meetings/upload-recording")
    data object AIProcessing : Screen(route = "meetings/processing?uploaded={uploaded}") {
        fun createRoute(uploaded: Boolean = false) = "meetings/processing?uploaded=$uploaded"
    }
    data object MeetingDetail : Screen(route = "meetings/{meetingId}") {
        fun createRoute(meetingId: Long) = "meetings/$meetingId"
    }
    data object MeetingReview : Screen(route = "meetings/{meetingId}/review") {
        fun createRoute(meetingId: Long) = "meetings/$meetingId/review"
    }
    data object ReviewTranscript : Screen(route = "meetings/{meetingId}/review-transcript") {
        fun createRoute(meetingId: Long) = "meetings/$meetingId/review-transcript"
    }
    data object ReviewTasks : Screen(route = "meetings/{meetingId}/review-tasks") {
        fun createRoute(meetingId: Long) = "meetings/$meetingId/review-tasks"
    }
    data object MeetingTrace : Screen(route = "meetings/{meetingId}/trace") {
        fun createRoute(meetingId: Long) = "meetings/$meetingId/trace"
    }
    data object MeetingTasks : Screen(route = "meetings/{meetingId}/tasks") {
        fun createRoute(meetingId: Long) = "meetings/$meetingId/tasks"
    }

    // Task sub-screens
    data object TaskDetail : Screen(route = "tasks/{taskId}?state={state}") {
        fun createRoute(taskId: Long, state: String? = null) =
            if (state != null) "tasks/$taskId?state=$state" else "tasks/$taskId"
    }

    // Memo
    data object Memo : Screen(route = "memo?new={new}&editTitle={editTitle}&editContent={editContent}&editTime={editTime}&editLocation={editLocation}") {
        fun createRoute(new: Boolean = false, editTitle: String = "", editContent: String = "", editTime: String = "", editLocation: String = ""): String {
            return "memo?new=$new&editTitle=${Uri.encode(editTitle)}&editContent=${Uri.encode(editContent)}&editTime=${Uri.encode(editTime)}&editLocation=${Uri.encode(editLocation)}"
        }
    }

    // Admin sub-screens
    data object OrgManagement : Screen(route = "admin/org")
    data object AccountManagement : Screen(route = "admin/accounts")

    companion object {
        val bottomNavItems = listOf(Home, Meetings, Tasks, Notifications, Profile)

        fun bottomNavItemsForRole(role: String): List<Screen> = when (role) {
            "staff" -> listOf(Tasks, Notifications, Profile)
            "superadmin" -> listOf(Home, Admin, Notifications, Profile)
            else -> bottomNavItems
        }
    }
}
