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

@Composable
fun ProgressTimeline(
    steps: List<ProgressStep>,
    modifier: Modifier = Modifier
) {
    val role = LocalRoleColors.current
    Column(modifier = modifier.padding(16.dp)) {
        steps.forEachIndexed { index, step ->
            val isCompleted = step.status == ProgressStatus.COMPLETED
            val isCurrent = step.status == ProgressStatus.CURRENT
            val isWaiting = step.status == ProgressStatus.PENDING

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
                        modifier = Modifier.size(12.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = when {
                            isCompleted -> role.primary
                            isCurrent -> role.primary
                            else -> colorDivider
                        }
                    ) {
                        if (isCompleted) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("✓", color = role.onPrimary, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    if (index < steps.lastIndex) {
                        Surface(
                            modifier = Modifier
                                .width(2.dp)
                                .height(32.dp),
                            color = if (isCompleted) role.primary else colorDivider
                        ) {}
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                // Content
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isCurrent) FontWeight.Medium else FontWeight.Normal,
                        color = if (!isWaiting) textPrimary else textMuted
                    )
                    if (step.timestamp != null) {
                        Text(
                            text = step.timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )
                    }
                    if (step.operator != null) {
                        Text(
                            text = step.operator,
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )
                    }
                }
            }
        }
    }
}

enum class ProgressStatus { COMPLETED, CURRENT, PENDING }

data class ProgressStep(
    val title: String,
    val status: ProgressStatus,
    val timestamp: String? = null,
    val operator: String? = null,
)
