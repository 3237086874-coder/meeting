package com.enterprise.meeting.presentation.meeting.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.components.RecordingVisualizer
import com.enterprise.meeting.presentation.theme.*

private data class Reviewer(
    val name: String = "张伟",
    val department: String = "信息技术部 · 总监",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingScreen(
    onBack: () -> Unit,
    onRecordingComplete: () -> Unit,
) {
    val role = LocalRoleColors.current
    var isRecording by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var meetingTitle by remember { mutableStateOf("Q3 产品规划会议") }
    var startTime by remember { mutableStateOf("2026-05-26 14:00") }
    var expectedDuration by remember { mutableStateOf("60 分钟") }
    var host by remember { mutableStateOf("李明") }
    var reviewer by remember { mutableStateOf(Reviewer()) }

    LaunchedEffect(isRecording, isPaused) {
        if (isRecording && !isPaused) {
            while (true) {
                kotlinx.coroutines.delay(1000)
                elapsedSeconds++
            }
        }
    }

    val formatTime: (Int) -> String = { totalSeconds ->
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("开始会议录音", fontWeight = FontWeight.SemiBold, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回", tint = textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceWhite,
                    titleContentColor = textPrimary
                )
            )
        },
        containerColor = pageBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Card 1: Meeting info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, colorSoftBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Meeting title
                    Text("会议标题", fontSize = 12.sp, color = textSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = meetingTitle,
                        onValueChange = { meetingTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = role.primary,
                            unfocusedBorderColor = colorBorder,
                            focusedContainerColor = surfaceWhite,
                            unfocusedContainerColor = surfaceWhite
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row: Start time + Expected duration (side by side)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("开始时间", fontSize = 12.sp, color = textSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = startTime,
                                onValueChange = { startTime = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = role.primary,
                                    unfocusedBorderColor = colorBorder,
                                    focusedContainerColor = surfaceWhite,
                                    unfocusedContainerColor = surfaceWhite
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("预计时长", fontSize = 12.sp, color = textSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = expectedDuration,
                                onValueChange = { expectedDuration = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = role.primary,
                                    unfocusedBorderColor = colorBorder,
                                    focusedContainerColor = surfaceWhite,
                                    unfocusedContainerColor = surfaceWhite
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Host / Department
                    Text("主持人 / 部门", fontSize = 12.sp, color = textSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = host,
                        onValueChange = { host = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = role.primary,
                            unfocusedBorderColor = colorBorder,
                            focusedContainerColor = surfaceWhite,
                            unfocusedContainerColor = surfaceWhite
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card 2: Authorized reviewer
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, colorSoftBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("授权审核人（MVP 单选）", fontSize = 12.sp, color = textSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Inner person card with border and tinted background
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = role.primary.copy(alpha = 0.03f),
                        border = BorderStroke(1.dp, role.primary)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar circle with first character
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = role.primary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = reviewer.name.first().toString(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = role.onPrimary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = reviewer.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textPrimary
                                )
                                Text(
                                    text = reviewer.department,
                                    fontSize = 12.sp,
                                    color = textSecondary
                                )
                            }
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = role.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+ 更换审核人",
                        fontSize = 12.sp,
                        color = role.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { /* change reviewer */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Network status
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = colorSuccess.copy(alpha = 0.07f),
                border = BorderStroke(1.dp, colorSuccess.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Wifi,
                        contentDescription = null,
                        tint = colorSuccess,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "网络正常 · 录音将实时同步",
                        fontSize = 11.sp,
                        color = colorSuccess
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card 3: Recording area
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, colorSoftBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Timer
                    Text(
                        text = formatTime(elapsedSeconds),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRecording && !isPaused) textPrimary else textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Status text
                    Text(
                        text = if (isRecording && !isPaused) "正在录音"
                        else if (isPaused) "已暂停"
                        else "点击开始录音",
                        fontSize = 13.sp,
                        color = if (isRecording && !isPaused) colorDanger else textSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Recording visualizer
                    RecordingVisualizer(isActive = isRecording && !isPaused)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Large record/stop button (96dp) with shadow
                    Surface(
                        onClick = {
                            if (isRecording) {
                                isRecording = false
                                isPaused = false
                            } else {
                                isRecording = true
                                elapsedSeconds = 0
                            }
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(96.dp),
                        color = if (isRecording) textMuted else colorDanger,
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isRecording) {
                                // Pause icon (two vertical bars)
                                Icon(
                                    Icons.Filled.Pause,
                                    contentDescription = "暂停",
                                    modifier = Modifier.size(36.dp),
                                    tint = surfaceWhite,
                                )
                            } else {
                                // Record icon (circle)
                                Surface(
                                    modifier = Modifier.size(36.dp),
                                    shape = CircleShape,
                                    color = surfaceWhite
                                ) {}
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom action row
            if (isRecording || elapsedSeconds > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pause button
                    OutlinedButton(
                        onClick = { isPaused = !isPaused },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, role.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = surfaceWhite
                        )
                    ) {
                        Icon(
                            if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            contentDescription = null,
                            tint = textPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isPaused) "继续" else "暂停",
                            fontSize = 13.sp,
                            color = textPrimary
                        )
                    }

                    // End & upload button
                    Button(
                        onClick = {
                            isRecording = false
                            isPaused = false
                            onRecordingComplete()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = role.primary)
                    ) {
                        Icon(
                            Icons.Filled.UploadFile,
                            contentDescription = null,
                            tint = role.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("结束并上传", fontSize = 13.sp, color = role.onPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun RecordingScreenPreview() {
    MeetingTheme {
        RecordingScreen(onBack = {}, onRecordingComplete = {})
    }
}
