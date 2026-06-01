package com.enterprise.meeting.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.enterprise.meeting.presentation.theme.*

@Composable
fun RoleBadge(
    roleName: String,
    modifier: Modifier = Modifier
) {
    val roleColor = when (roleName) {
        "超级管理员" -> RoleColors.superadmin
        "高级管理层" -> RoleColors.president
        "部门负责人" -> RoleColors.manager
        "执行人员" -> RoleColors.staff
        else -> RoleColors.president
    }
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = roleColor.primary.copy(alpha = 0.1f),
        modifier = modifier
    ) {
        Text(
            text = roleName,
            color = roleColor.primary,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
