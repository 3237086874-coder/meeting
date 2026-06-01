package com.enterprise.meeting.presentation.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.theme.*

data class AccountItem(
    val id: Long,
    val name: String,
    val role: String,
    val phone: String,
    val department: String,
    val title: String,
    val isActive: Boolean,
)

private val mockAccounts = listOf(
    AccountItem(1, "张伟", "超级管理员", "138 0010 8866", "信息技术部", "CTO", isActive = true),
    AccountItem(2, "李明", "部门负责人", "139 1234 0011", "产品部", "总监", isActive = true),
    AccountItem(3, "王思雨", "部门负责人", "186 7788 9920", "运营部", "总监", isActive = true),
    AccountItem(4, "阿布都热合曼", "执行人员", "159 9920 1183", "市场部", "一线员工", isActive = true),
    AccountItem(5, "陈晓东", "执行人员", "133 4400 1188", "行政部", "行政专员", isActive = false),
)

private fun roleBadgeColor(role: String): Color = when (role) {
    "超级管理员" -> RoleColors.superadmin.primary // #B45309
    "高级管理层" -> RoleColors.president.primary   // #123B6A
    "部门负责人" -> RoleColors.manager.primary     // #1F7A6C
    else -> RoleColors.staff.primary              // #2D3748
}

private fun roleIcon(role: String): ImageVector = when (role) {
    "超级管理员" -> Icons.Filled.AdminPanelSettings
    "部门负责人" -> Icons.Filled.Groups
    else -> Icons.Filled.Person
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagementScreen(
    onNavigateToOrg: () -> Unit = {},
    onBack: () -> Unit = {},
    onEditAccount: (Long) -> Unit = {},
    onNewAccount: () -> Unit = {},
) {
    val role = LocalRoleColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "管理后台",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = role.primary,
                    titleContentColor = role.onPrimary,
                )
            )
        },
        containerColor = pageBg,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab toggle (0 gap to search row per Figma)
            item {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 12.dp)
                        .padding(bottom = 12.dp)
                        .fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(0.67.dp, colorBorder),
                        tonalElevation = 0.dp,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp),
                        ) {
                            // 组织架构 (unselected)
                            Surface(
                                onClick = onNavigateToOrg,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                shape = RoundedCornerShape(14.dp),
                                color = Color.Transparent,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "组织架构",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = textSecondary,
                                    )
                                }
                            }
                            // 账号管理 (selected)
                            Surface(
                                onClick = { /* already on accounts */ },
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                shape = RoundedCornerShape(14.dp),
                                color = role.primary,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "账号管理",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Search bar + New button (0 gap from tabs per Figma)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 12.dp, bottom = 16.dp)
                        .height(36.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Search input
                    Surface(
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(50.dp),
                        color = Color.White,
                        border = BorderStroke(0.67.dp, colorBorder),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = null,
                                tint = textMuted,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "搜索姓名 / 手机号",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = textPrimary.copy(alpha = 0.5f),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // New button
                    Surface(
                        onClick = onNewAccount,
                        shape = RoundedCornerShape(10.dp),
                        color = role.primary,
                    ) {
                        Row(
                            modifier = Modifier
                                .height(32.dp)
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "新建",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                            )
                        }
                    }
                }
            }

            // Account cards (0 gap from search per Figma)
            items(mockAccounts, key = { it.id }) { account ->
                val badgeColor = roleBadgeColor(account.role)
                val statusColor = if (account.isActive) colorSuccess else colorDanger
                val statusText = if (account.isActive) "正常" else "停用"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(0.67.dp, colorSoftBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 17.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Avatar
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = badgeColor,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    account.name.first().toString(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))

                        // Name + info
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    account.name,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textPrimary,
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(50.dp),
                                    color = badgeColor.copy(alpha = 0.12f),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            roleIcon(account.role),
                                            contentDescription = null,
                                            tint = badgeColor,
                                            modifier = Modifier.size(12.dp),
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            account.role,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = badgeColor,
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                account.phone,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = textSecondary,
                            )
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                "${account.department} / ${account.title}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = textSecondary,
                            )
                        }

                        // Status + Edit
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = statusColor.copy(alpha = 0.12f),
                            ) {
                                Text(
                                    statusText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = statusColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                onClick = { onEditAccount(account.id) },
                                shape = RoundedCornerShape(8.dp),
                                color = role.primary.copy(alpha = 0.12f),
                            ) {
                                Text(
                                    "编辑",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = role.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun AccountManagementScreenPreview() {
    MeetingTheme(roleColors = RoleColors.superadmin) {
        AccountManagementScreen(onBack = {})
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun AccountManagementScreenLoadingPreview() {
    MeetingTheme(roleColors = RoleColors.superadmin) {
        Box(modifier = Modifier.background(pageBg)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(modifier = Modifier.fillMaxWidth().height(18.dp), shape = RoundedCornerShape(4.dp), color = colorSkeleton) {}
                repeat(5) {
                    Surface(modifier = Modifier.fillMaxWidth().height(81.dp), shape = RoundedCornerShape(14.dp), color = colorSkeleton) {}
                }
            }
        }
    }
}
