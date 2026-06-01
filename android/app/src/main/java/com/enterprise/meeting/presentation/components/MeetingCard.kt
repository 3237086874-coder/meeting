package com.enterprise.meeting.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
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
import com.enterprise.meeting.domain.model.MeetingState
import com.enterprise.meeting.presentation.theme.*

@Composable
fun MeetingCard(
    title: String,
    department: String,
    host: String,
    duration: String,
    attendeeCount: Int,
    taskCount: Int,
    state: MeetingState,
    modifier: Modifier = Modifier,
    isHostedByMe: Boolean = false,
    meetingTime: String = "",
    onClick: () -> Unit = {},
) {
    val stateColor = meetingStateColor(state.displayName)
    val role = LocalRoleColors.current

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceWhite),
        border = BorderStroke(0.67.dp, colorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            // Left accent strip (matching card corner radius like TaskCard)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
                    .background(stateColor),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
            ) {
                // Top row: title + badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        // State badge
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = stateColor.copy(alpha = 0.1f),
                        ) {
                            Text(
                                text = state.displayName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = stateColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }

                        // Hosted by me badge
                        if (isHostedByMe) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = role.primary.copy(alpha = 0.08f),
                            ) {
                                Text(
                                    text = "我主持的",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = role.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Department · Host
                Text(
                    text = "$department · $host",
                    fontSize = 11.sp,
                    color = textSecondary,
                    maxLines = 1,
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom row: metadata icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    MetaIconText(
                        icon = Icons.Filled.Schedule,
                        text = if (meetingTime.isNotEmpty()) meetingTime else duration,
                        iconSize = 13.dp,
                        fontSize = 11.sp,
                    )
                    MetaIconText(
                        icon = Icons.Filled.People,
                        text = "${attendeeCount}人",
                        iconSize = 13.dp,
                        fontSize = 11.sp,
                    )
                    if (taskCount > 0) {
                        MetaIconText(
                            icon = Icons.Filled.CheckBox,
                            text = "${taskCount}任务",
                            iconSize = 13.dp,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaIconText(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    iconSize: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = textMuted,
        )
        Text(
            text = text,
            fontSize = fontSize,
            color = textSecondary,
        )
    }
}
