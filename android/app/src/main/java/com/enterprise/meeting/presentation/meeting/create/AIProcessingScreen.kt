package com.enterprise.meeting.presentation.meeting.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.components.AIProcessingStepper
import com.enterprise.meeting.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIProcessingScreen(
    uploaded: Boolean = false,
    meetingId: Long = 1L,
    onBack: () -> Unit,
    onProcessingComplete: () -> Unit = {},
    onReviewClick: (Long) -> Unit = {},
) {
    val role = LocalRoleColors.current
    var currentStep by remember { mutableIntStateOf(if (uploaded) 1 else 0) }
    val isComplete = currentStep >= 4

    // Simulate AI processing steps
    LaunchedEffect(Unit) {
        while (currentStep < 4) {
            kotlinx.coroutines.delay(2500)
            currentStep++
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI 处理中",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = textPrimary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = textPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceWhite,
                    titleContentColor = textPrimary,
                )
            )
        },
        containerColor = pageBg,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Meeting info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(0.67.dp, colorBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = role.primary.copy(alpha = 0.12f),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = role.primary,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Q3 产品规划会议",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "时长 58:24 · 47.8 MB",
                            fontSize = 12.sp,
                            color = textSecondary,
                        )
                    }
                }
            }

            // Processing stepper
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(0.67.dp, colorBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                AIProcessingStepper(
                    currentStep = currentStep,
                    estimatedTime = when (currentStep) {
                        0 -> "约30秒"
                        1 -> "约20秒"
                        2 -> "约15秒"
                        3 -> "约10秒"
                        else -> ""
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI processing hint card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0F4F8),
                border = BorderStroke(0.67.dp, Color(0xFFE2E8F0)),
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = colorInfo,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isComplete) "AI 处理已完成，请审核会议内容"
                               else "AI 正在后台处理，可以离开当前页面，完成后将消息通知您",
                        fontSize = 13.sp,
                        color = colorInfo,
                        lineHeight = 20.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { currentStep = 0 },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, role.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = role.primary,
                        disabledContentColor = role.primary.copy(alpha = 0.38f),
                    ),
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("重新处理", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                Button(
                    onClick = { onReviewClick(meetingId) },
                    enabled = isComplete,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = role.primary,
                        contentColor = role.onPrimary,
                        disabledContainerColor = role.primary.copy(alpha = 0.38f),
                        disabledContentColor = role.onPrimary.copy(alpha = 0.6f),
                    ),
                ) {
                    Text("查看审核入口", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun AIProcessingScreenPreview() {
    MeetingTheme(roleColors = RoleColors.president) {
        AIProcessingScreen(
            onBack = {},
            onReviewClick = {},
        )
    }
}
