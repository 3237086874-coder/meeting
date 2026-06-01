package com.enterprise.meeting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enterprise.meeting.presentation.theme.*

@Composable
fun TaskCard(
    title: String,
    sourceMeeting: String,
    assignee: String,
    dueDate: String,
    progress: Int,
    state: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val stateColor = taskStateColor(state)
    val isOverdue = state == "已逾期"
    val isPending = state == "待确认"
    val isCompleted = state == "已完成"

    val borderColor = when {
        isOverdue -> colorDanger
        isPending -> colorWarning
        isCompleted -> colorSuccess
        state == "执行中" -> colorInfo
        else -> Color.Transparent
    }

    val progressColor = when {
        isOverdue -> colorDanger
        isCompleted -> colorSuccess
        state == "执行中" -> colorInfo
        else -> colorInfo
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, colorSoftBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Left colored border strip
            if (borderColor != Color.Transparent) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                        .background(borderColor)
                )
            } else {
                Spacer(modifier = Modifier.width(4.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Row 1: Title + Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(text = state, color = stateColor)
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Row 2: Source meeting
                Text(
                    text = "来自 $sourceMeeting",
                    fontSize = 12.sp,
                    color = textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Row 3: Assignee + Due Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = assignee,
                        fontSize = 11.sp,
                        color = textSecondary,
                        maxLines = 1,
                    )
                    Text(
                        text = "截止 $dueDate",
                        fontSize = 11.sp,
                        color = if (isOverdue) colorDanger else textSecondary,
                        maxLines = 1,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 4: Progress bar (always visible)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(colorDivider, RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress / 100f)
                            .height(4.dp)
                            .background(progressColor, RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}
