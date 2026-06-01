package com.enterprise.meeting.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.enterprise.meeting.presentation.theme.*

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.1f),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

fun meetingStateColor(state: String): Color = when (state) {
    "执行中" -> colorInfo
    "AI处理中", "AI 处理中" -> colorInfo
    "待审核" -> colorWarning
    "待发布" -> colorWarning
    "已发布" -> colorSuccess
    "已驳回", "已拒绝" -> colorDanger
    "已归档" -> textMuted
    else -> textSecondary
}

fun taskStateColor(state: String): Color = when (state) {
    "待确认" -> colorWarning
    "执行中" -> colorInfo
    "已完成" -> colorSuccess
    "已逾期", "已过期" -> colorDanger
    else -> textSecondary
}
