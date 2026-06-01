package com.enterprise.meeting.presentation.meeting.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.theme.*

private data class UploadFileInfo(
    val fileName: String = "Q3产品规划会议-202611.m4a",
    val fileType: String = "m4a",
    val fileSize: String = "47.8 MB",
    val duration: String = "58:24",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadRecordingScreen(
    onBack: () -> Unit,
    onUploadComplete: () -> Unit,
) {
    val role = LocalRoleColors.current
    var selectedFile by remember { mutableStateOf<UploadFileInfo?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableIntStateOf(0) }

    // Simulate upload progress
    LaunchedEffect(isUploading) {
        if (isUploading) {
            while (uploadProgress < 100) {
                kotlinx.coroutines.delay(150)
                uploadProgress += 1
            }
            onUploadComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("上传录音文件", fontWeight = FontWeight.SemiBold, fontSize = 17.sp) },
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
            // Info banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = colorInfo.copy(alpha = 0.06f),
                border = BorderStroke(1.dp, colorInfo.copy(alpha = 0.06f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = colorInfo,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "支持 mp3、m4a、wav · 单个录音文件最大 200MB",
                        fontSize = 12.sp,
                        color = colorInfo
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // File card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, colorSoftBorder)
            ) {
                if (selectedFile == null) {
                    // Upload drop zone
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clickable {
                                selectedFile = UploadFileInfo()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = role.primary.copy(alpha = 0.07f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Filled.CloudUpload,
                                        contentDescription = null,
                                        tint = role.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "点击选择或拖拽录音文件到此处",
                                fontSize = 13.sp,
                                color = textSecondary
                            )
                            Text(
                                text = "支持 mp3, wav, m4a 格式",
                                fontSize = 11.sp,
                                color = textMuted
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Horizontal row: icon | text | delete (Figma: HORIZONTAL spacing=12dp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Document icon (Figma: 48x48, cornerRadius 10, bg #123B6A @ 7%)
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = role.primary.copy(alpha = 0.07f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Filled.Description,
                                        contentDescription = null,
                                        tint = role.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            // Text info (Figma: VERTICAL spacing=2dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedFile!!.fileName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${selectedFile!!.fileType} · ${selectedFile!!.fileSize} · 时长 ${selectedFile!!.duration}",
                                    fontSize = 11.sp,
                                    color = textSecondary
                                )
                            }

                            // Delete button (Figma: 16x16 icon)
                            IconButton(
                                onClick = { selectedFile = null; isUploading = false; uploadProgress = 0 },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.DeleteOutline,
                                    contentDescription = "删除",
                                    tint = colorDanger,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Upload progress (Figma: always visible when file selected)
                        Spacer(modifier = Modifier.height(12.dp))
                        // Progress text row (Figma: HORIZONTAL space-between)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isUploading) "上传中…" else "待上传",
                                fontSize = 11.sp,
                                color = textSecondary
                            )
                            Text(
                                text = "${uploadProgress}% · 1.2MB/s · 剩余 ${((100 - uploadProgress) * 38 / 100) / 60}:${((100 - uploadProgress) * 38 / 100 % 60).toString().padStart(2, '0')}",
                                fontSize = 11.sp,
                                color = textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        // Progress bar (Figma: track #EDE6D8, fill #123B6A, height 6dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(colorDivider, RoundedCornerShape(3.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(uploadProgress / 100f)
                                    .height(6.dp)
                                    .background(role.primary, RoundedCornerShape(3.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Common errors card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, colorSoftBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("常见错误", fontSize = 12.sp, color = textSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    ErrorListItem(Icons.Outlined.ErrorOutline, "录音文件不能超过 200MB")
                    Spacer(modifier = Modifier.height(10.dp))
                    ErrorListItem(Icons.Outlined.ErrorOutline, "仅支持 mp3、m4a、wav 格式")
                    Spacer(modifier = Modifier.height(10.dp))
                    ErrorListItem(Icons.Outlined.ErrorOutline, "无网络时请稍后重试")
                }
            }

            // Bottom action buttons (only show when file is selected)
            if (selectedFile != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Reselect button
                    OutlinedButton(
                        onClick = { selectedFile = null; isUploading = false; uploadProgress = 0 },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, role.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = surfaceWhite,
                            contentColor = role.primary
                        )
                    ) {
                        Text("重新选择", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }

                    // Upload button
                    Button(
                        onClick = {
                            if (!isUploading) {
                                isUploading = true
                                uploadProgress = 0
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        enabled = !isUploading,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = role.primary)
                    ) {
                        Icon(
                            Icons.Filled.CloudUpload,
                            contentDescription = null,
                            tint = role.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("上传并转写", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ErrorListItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = colorDanger,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 13.sp, color = textPrimary)
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun UploadRecordingScreenPreview() {
    MeetingTheme {
        UploadRecordingScreen(onBack = {}, onUploadComplete = {})
    }
}
