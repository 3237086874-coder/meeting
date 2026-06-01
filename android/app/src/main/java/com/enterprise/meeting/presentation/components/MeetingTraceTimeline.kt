package com.enterprise.meeting.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.enterprise.meeting.presentation.theme.*

data class TraceNode(
    val label: String,
    val timestamp: String,
    val operator: String? = null,
    val isActive: Boolean = true,
)

@Composable
fun MeetingTraceTimeline(
    nodes: List<TraceNode>,
    modifier: Modifier = Modifier
) {
    val role = LocalRoleColors.current
    Column(modifier = modifier.padding(16.dp)) {
        nodes.forEachIndexed { index, node ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Timeline dot and line
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(24.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(10.dp),
                        shape = RoundedCornerShape(5.dp),
                        color = if (node.isActive) role.primary else colorDivider
                    ) {}
                    if (index < nodes.lastIndex) {
                        Surface(
                            modifier = Modifier
                                .width(2.dp)
                                .height(40.dp),
                            color = if (node.isActive) role.primary.copy(alpha = 0.3f) else colorDivider
                        ) {}
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = node.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (node.isActive) FontWeight.Medium else FontWeight.Normal,
                        color = if (node.isActive) textPrimary else textMuted
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = node.timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )
                        if (node.operator != null) {
                            Text(
                                text = node.operator,
                                style = MaterialTheme.typography.labelSmall,
                                color = textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
