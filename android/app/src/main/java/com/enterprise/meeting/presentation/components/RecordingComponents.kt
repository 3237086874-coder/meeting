package com.enterprise.meeting.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.enterprise.meeting.presentation.theme.*

@Composable
fun RecordingVisualizer(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()

    val anim0 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "bar_0"
    )
    val anim1 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "bar_1"
    )
    val anim2 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "bar_2"
    )
    val anim3 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "bar_3"
    )
    val anim4 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "bar_4"
    )

    val barAnimations = listOf(anim0, anim1, anim2, anim3, anim4)

    Canvas(
        modifier = modifier
            .height(48.dp)
            .width(64.dp)
    ) {
        val barWidth = 8.dp.toPx()
        val spacing = 4.dp.toPx()
        val totalWidth = 5 * barWidth + 4 * spacing
        val startX = (size.width - totalWidth) / 2
        val maxHeight = size.height - 4.dp.toPx()

        barAnimations.forEachIndexed { index, anim ->
            val height = if (isActive) maxHeight * anim else maxHeight * 0.15f
            val x = startX + index * (barWidth + spacing)
            val y = (size.height - height) / 2
            drawRoundRect(
                color = Color(0xFFC94747),
                topLeft = Offset(x, y),
                size = Size(barWidth, height),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }
}

@Composable
fun UploadRecordingCard(
    fileName: String,
    fileSize: String,
    progress: Float = 0f,
    isUploading: Boolean = false,
    isError: Boolean = false,
    onRetry: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val role = LocalRoleColors.current
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = fileName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = textPrimary)
                    Text(text = fileSize, style = MaterialTheme.typography.labelSmall, color = textSecondary)
                }
                when {
                    isError -> {
                        TextButton(onClick = onRetry) { Text("重试", color = colorDanger) }
                        TextButton(onClick = onDelete) { Text("删除", color = textMuted) }
                    }
                    isUploading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = role.primary
                        )
                    }
                    else -> {
                        Text("✓", color = colorSuccess, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            if (isUploading) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = role.primary,
                    trackColor = colorSkeleton
                )
            }
        }
    }
}

@Composable
fun AttachmentUploader(
    attachments: List<AttachmentItem>,
    onAdd: () -> Unit,
    onRetry: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val role = LocalRoleColors.current
    Column(modifier = modifier) {
        attachments.forEachIndexed { index, item ->
            UploadRecordingCard(
                fileName = item.fileName,
                fileSize = item.fileSize,
                progress = item.progress,
                isUploading = item.status == AttachmentStatus.UPLOADING,
                isError = item.status == AttachmentStatus.FAILED,
                onRetry = { onRetry(index) },
                onDelete = { onDelete(index) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        OutlinedButton(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("+ 添加附件", color = role.primary)
        }
    }
}

enum class AttachmentStatus { PENDING, UPLOADING, SUCCESS, FAILED }

data class AttachmentItem(
    val fileName: String,
    val fileSize: String,
    val progress: Float = 0f,
    val status: AttachmentStatus = AttachmentStatus.PENDING,
)
