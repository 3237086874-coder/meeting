package com.enterprise.meeting.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enterprise.meeting.presentation.theme.*

@Composable
fun AIProcessingStepper(
    currentStep: Int, // 0-4
    stepLabels: List<String> = listOf("上传录音", "语音转文本", "生成摘要", "任务识别", "等待审核"),
    estimatedTime: String = "",
    modifier: Modifier = Modifier
) {
        Row(modifier = modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)) {
            // Left column: indicators + connector lines
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(28.dp),
            ) {
                stepLabels.forEachIndexed { index, label ->
                    val isCompleted = index < currentStep
                    val isCurrent = index == currentStep
                    val isWaiting = index > currentStep

                    // Indicator circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(28.dp),
                    ) {
                        if (isCompleted) {
                            Surface(
                                modifier = Modifier.size(24.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = colorSuccess,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = Color.White,
                                    )
                                }
                            }
                        } else if (isCurrent) {
                            val infiniteTransition = rememberInfiniteTransition(label = "flip")
                            val flipProgress by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    tween(2000, easing = EaseInOutCubic),
                                    RepeatMode.Reverse,
                                ),
                                label = "flip",
                            )
                            val flipAngle = flipProgress * 180f

                            Surface(
                                modifier = Modifier.size(28.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = colorInfo.copy(alpha = 0.10f),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Box(modifier = Modifier.size(16.dp).graphicsLayer(rotationZ = flipAngle)) {
                                        Icon(
                                            Icons.Filled.HourglassBottom,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .graphicsLayer(alpha = 1f - flipProgress),
                                            tint = colorInfo,
                                        )
                                        Icon(
                                            Icons.Filled.HourglassTop,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .graphicsLayer(alpha = flipProgress),
                                            tint = colorInfo,
                                        )
                                    }
                                }
                            }
                        } else {
                            Surface(
                                modifier = Modifier.size(24.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = colorSkeleton,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "${index + 1}",
                                        color = textMuted,
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                            }
                        }
                    }

                    // Connector line (below each step except the last)
                    if (index < stepLabels.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(20.dp)
                                .background(if (index < currentStep) colorSuccess else colorDivider),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right column: labels
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                stepLabels.forEachIndexed { index, label ->
                    val isCurrent = index == currentStep
                    val isWaiting = index > currentStep
                    val isCompleted = index < currentStep

                    Column(
                        modifier = Modifier.height(if (index < stepLabels.lastIndex) 48.dp else 28.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isCurrent) FontWeight.Medium else FontWeight.Normal,
                            color = if (!isWaiting) textPrimary else textMuted,
                        )
                        Text(
                            text = when {
                                isCompleted -> "已完成"
                                isCurrent -> if (estimatedTime.isNotEmpty()) "处理中...预计$estimatedTime" else "处理中..."
                                else -> "等待中"
                            },
                            fontSize = 11.sp,
                            color = textMuted,
                        )
                    }
                }
            }
        }
}

@Composable
fun AuditStepper(
    currentStep: Int, // 0, 1, 2
    stepLabels: List<String> = listOf("审核转写文本", "审核任务列表", "发布任务"),
    modifier: Modifier = Modifier
) {
    val role = LocalRoleColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top row: circles + connector lines
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            stepLabels.forEachIndexed { index, label ->
                val isCompleted = index < currentStep
                val isCurrent = index == currentStep

                // Step circle
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = when {
                            isCompleted -> role.primary
                            isCurrent -> role.primary
                            else -> colorSkeleton
                        },
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isCompleted) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = role.onPrimary,
                                )
                            } else {
                                Text(
                                    "${index + 1}",
                                    color = if (isCurrent) role.onPrimary else textMuted,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                // Connector line (not after the last step)
                if (index < stepLabels.lastIndex) {
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(40.dp)
                            .background(if (index < currentStep) role.primary else colorDivider),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom row: labels below each circle
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            stepLabels.forEachIndexed { index, label ->
                val isCompleted = index < currentStep
                val isCurrent = index == currentStep

                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 11.sp,
                    color = when {
                        isCompleted || isCurrent -> textPrimary
                        else -> textMuted
                    },
                    fontWeight = if (isCurrent) FontWeight.Medium else FontWeight.Normal,
                )

                if (index < stepLabels.lastIndex) {
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }
        }
    }
}

private fun isWaiting(index: Int, currentStep: Int) = index > currentStep
