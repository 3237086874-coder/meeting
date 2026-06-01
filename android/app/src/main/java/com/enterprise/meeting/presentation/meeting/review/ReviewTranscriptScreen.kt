package com.enterprise.meeting.presentation.meeting.review

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.components.AuditStepper
import com.enterprise.meeting.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewTranscriptScreen(
    meetingId: Long,
    onBack: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
) {
    val role = LocalRoleColors.current
    var transcript by remember {
        mutableStateOf(
            "今天的项目周会主要讨论了以下事项：\n\n" +
            "1. 前端开发进度正常，预计下周二完成首页和登录页的界面开发。" +
            "2. 后端API文档已更新，新增了用户管理和权限控制的接口说明。" +
            "3. 需要安排下周的用户测试，测试范围包括核心业务流程。\n\n" +
            "会议中重点强调了Q3产品规划的推进节奏，各部门需在下周五前提交详细执行方案。"
        )
    }
    var isEditing by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var showRerunConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "审核会议纪要",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Audit stepper
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(0.67.dp, colorBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                AuditStepper(currentStep = 0)
            }

            // Meeting info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceWhite),
                border = BorderStroke(0.67.dp, Color(0xFFE0E0E0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Title row with status badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Q3 产品规划会议",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = textPrimary,
                            modifier = Modifier.weight(1f),
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = colorWarning.copy(alpha = 0.12f),
                        ) {
                            Text(
                                text = "待审核",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorWarning,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                    // Detail rows
                    ReviewDetailRow(icon = Icons.Filled.Schedule, label = "会议时间", value = "2025-07-15 14:00")
                    ReviewDetailRow(icon = Icons.Filled.Person, label = "主持人", value = "李明")
                    ReviewDetailRow(icon = Icons.Filled.CheckCircle, label = "授权审核人", value = "王思雨")
                    ReviewDetailRow(icon = Icons.Filled.Lock, label = "审核权限来源", value = "Q3产品规划 · 负责人")
                }
            }

            // Audio playback card
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
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = role.primary.copy(alpha = 0.12f),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = "播放",
                                modifier = Modifier.size(24.dp),
                                tint = role.primary,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "会议录音.mp3",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = textPrimary,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "00:00 / 58:24",
                            fontSize = 12.sp,
                            color = textSecondary,
                        )
                    }
                }
            }

            // Low confidence warning
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = colorDanger.copy(alpha = 0.06f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val s = 1.3.dp.toPx()
                        val p = 3.dp.toPx()
                        val w = size.width
                        val h = size.height
                        // Triangle outline
                        drawPath(
                            Path().apply {
                                moveTo(w / 2f, p)
                                lineTo(w - p, h - p)
                                lineTo(p, h - p)
                                close()
                            },
                            color = colorDanger,
                            style = Stroke(width = s, pathEffect = PathEffect.cornerPathEffect(4.dp.toPx()))
                        )
                        // Exclamation vertical line
                        drawLine(colorDanger, Offset(w / 2f, h * 0.35f), Offset(w / 2f, h * 0.58f), s * 0.9f)
                        // Dot
                        drawCircle(colorDanger, s * 1f, Offset(w / 2f, h * 0.73f))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI 识别置信度较低",
                        fontSize = 13.sp,
                        color = colorDanger,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = colorDanger.copy(alpha = 0.10f),
                    ) {
                        Text(
                            text = "置信度 72%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorDanger,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                }
            }

            // Transcript card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(0.67.dp, colorBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "转写文本",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = textPrimary,
                        )
                        Row {
                            TextButton(
                                onClick = { isEditing = !isEditing },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Icon(
                                    if (isEditing) Icons.Filled.Check else Icons.Filled.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = role.primary,
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (isEditing) "完成" else "编辑",
                                    fontSize = 12.sp,
                                    color = role.primary,
                                )
                            }
                            TextButton(
                                onClick = { showRerunConfirm = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Icon(
                                    Icons.Filled.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = role.primary,
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "重新生成",
                                    fontSize = 12.sp,
                                    color = role.primary,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (isEditing) {
                        OutlinedTextField(
                            value = transcript,
                            onValueChange = { transcript = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 180.dp),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 13.sp,
                                color = textPrimary,
                                lineHeight = 22.sp,
                            ),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = role.primary,
                                unfocusedBorderColor = colorBorder,
                                focusedContainerColor = surfaceWhite,
                                unfocusedContainerColor = surfaceWhite,
                            ),
                        )
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8F7F4),
                            border = BorderStroke(0.67.dp, role.primary)
                        ) {
                            Text(
                                text = transcript,
                                fontSize = 13.sp,
                                color = textPrimary,
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(14.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { showRejectDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colorDanger),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorDanger,
                    ),
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("驳回", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick = onApprove,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = role.primary,
                        contentColor = role.onPrimary,
                    ),
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("通过", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Reject dialog
    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = surfaceWhite,
            title = {
                Text(
                    "驳回会议",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    color = textPrimary,
                )
            },
            text = {
                Text(
                    "确定驳回此会议？驳回后任务不会发布给执行人员，会议将返回重新处理。",
                    fontSize = 14.sp,
                    color = textSecondary,
                    lineHeight = 22.sp,
                )
            },
            confirmButton = {
                Button(
                    onClick = { showRejectDialog = false; onReject() },
                    colors = ButtonDefaults.buttonColors(containerColor = colorDanger),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("确定驳回", fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("取消", color = textSecondary)
                }
            },
        )
    }

    // Rerun confirm dialog
    if (showRerunConfirm) {
        AlertDialog(
            onDismissRequest = { showRerunConfirm = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = surfaceWhite,
            title = {
                Text(
                    "重新生成",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    color = textPrimary,
                )
            },
            text = {
                Text(
                    "重新运行 AI 处理？当前编辑的内容将丢失。",
                    fontSize = 14.sp,
                    color = textSecondary,
                    lineHeight = 22.sp,
                )
            },
            confirmButton = {
                Button(
                    onClick = { showRerunConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = role.primary),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("确认重新生成", fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRerunConfirm = false }) {
                    Text("取消", color = textSecondary)
                }
            },
        )
    }
}

@Composable
private fun ReviewDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = textMuted,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = textSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = textPrimary,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun ReviewTranscriptScreenPreview() {
    MeetingTheme(roleColors = RoleColors.manager) {
        ReviewTranscriptScreen(
            meetingId = 1L,
            onBack = {},
            onApprove = {},
            onReject = {},
        )
    }
}
