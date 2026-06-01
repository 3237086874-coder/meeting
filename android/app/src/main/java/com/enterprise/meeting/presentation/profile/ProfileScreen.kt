package com.enterprise.meeting.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.theme.*

private fun roleIcon(role: String): ImageVector = when (role) {
    "superadmin" -> Icons.Filled.AdminPanelSettings
    "manager" -> Icons.Filled.Groups
    else -> Icons.Filled.Person
}

@Composable
fun ProfileScreen(
    userRole: String = "superadmin",
    userName: String = "张明",
    onMemosClick: () -> Unit,
    onOrgManagement: () -> Unit = {},
    onAccountManagement: () -> Unit = {},
    onLogout: () -> Unit,
    onChangeRole: (String) -> Unit = {},
) {
    val role = LocalRoleColors.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    val roleLabel = when (userRole) {
        "superadmin" -> "超级管理员"
        "manager" -> "部门负责人"
        "staff" -> "执行人员"
        else -> "高级管理层"
    }
    val department = when (userRole) {
        "superadmin" -> "信息技术部"
        "manager" -> "运营部"
        "staff" -> "市场部"
        else -> "执行委员会"
    }
    val permissions = when (userRole) {
        "superadmin" -> listOf("管理组织架构、账号、权限", "查看全公司会议与任务概况")
        "president" -> listOf("查看全公司会议与任务概况", "审批会议纪要")
        "manager" -> listOf("管理部门会议与任务", "审核会议纪要")
        "staff" -> listOf("查看个人任务", "提交任务进度")
        else -> listOf("查看全公司会议与任务概况")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBg)
            .verticalScroll(rememberScrollState()),
    ) {
        // ===== Gradient Header =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(role.gradientStart, role.gradientEnd)
                    )
                )
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Avatar
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = role.onPrimary.copy(alpha = 0.18f),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            userName.first().toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = role.onPrimary,
                        )
                    }
                }
                // Name + role + company/dept
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        userName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = role.onPrimary,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = role.onPrimary.copy(alpha = 0.15f),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Icon(
                                    roleIcon(userRole),
                                    contentDescription = null,
                                    tint = role.onPrimary,
                                    modifier = Modifier.size(12.dp),
                                )
                                Text(
                                    roleLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = role.onPrimary,
                                )
                            }
                        }
                        Text(
                            "某某科技有限公司 · $department",
                            fontSize = 12.sp,
                            color = role.onPrimary.copy(alpha = 0.6f),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ===== Content Cards =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 1. Role Permissions
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(0.67.dp, colorSoftBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("当前角色权限说明", fontSize = 12.sp, color = textSecondary)
                    Spacer(Modifier.height(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        permissions.forEach { perm ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = colorSuccess,
                                    modifier = Modifier.size(14.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(perm, fontSize = 13.sp, color = textPrimary)
                            }
                        }
                    }
                }
            }

            // 2. Data Security
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(0.67.dp, colorSoftBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = colorSuccess,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("数据安全", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "业务数据云端加密存储 · 会议录音、转写文本、任务及附件按角色权限访问 · 操作行为全程留痕",
                        fontSize = 12.sp,
                        color = textSecondary,
                        lineHeight = 18.sp,
                    )
                }
            }

            // 3. Settings Menu
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(0.67.dp, colorSoftBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column {
                    MenuItem(Icons.Filled.Person, "个人资料编辑", onClick = {})
                    MenuDivider()
                    MenuItem(Icons.Filled.Lock, "修改密码", onClick = {})
                    MenuDivider()
                    MenuItem(Icons.Filled.Notifications, "消息通知设置", onClick = {})
                    MenuDivider()
                    MenuItem(Icons.Filled.Description, "个人备忘", onClick = onMemosClick)
                    MenuDivider()
                    MenuItem(Icons.Filled.Info, "关于与帮助", onClick = {})
                }
            }

            // 4. Logout
            Surface(
                onClick = { showLogoutDialog = true },
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = BorderStroke(0.67.dp, colorDanger),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = colorDanger,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("退出登录", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = colorDanger)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = cardBg,
            title = { Text("退出登录", fontWeight = FontWeight.SemiBold, color = textPrimary) },
            text = { Text("确定要退出登录吗？", color = textSecondary) },
            confirmButton = {
                Button(
                    onClick = { showLogoutDialog = false; onLogout() },
                    colors = ButtonDefaults.buttonColors(containerColor = colorDanger),
                    shape = RoundedCornerShape(8.dp),
                ) { Text("确定退出", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("取消", color = textSecondary) }
            },
        )
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    Surface(onClick = onClick, color = Color.Transparent) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = textSecondary, modifier = Modifier.size(18.dp))
            Text(
                title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                color = textPrimary,
                modifier = Modifier.weight(1f),
            )
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = textMuted,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = colorDivider,
    )
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun ProfileScreenPreview() {
    MeetingTheme(roleColors = RoleColors.superadmin) {
        ProfileScreen(
            userRole = "superadmin",
            onMemosClick = {},
            onOrgManagement = {},
            onAccountManagement = {},
            onLogout = {},
        )
    }
}
