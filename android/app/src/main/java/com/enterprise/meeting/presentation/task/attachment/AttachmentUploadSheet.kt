package com.enterprise.meeting.presentation.task.attachment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.enterprise.meeting.presentation.components.AttachmentItem
import com.enterprise.meeting.presentation.components.AttachmentStatus
import com.enterprise.meeting.presentation.components.AttachmentUploader
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentUploadSheet(
    onDismiss: () -> Unit,
    onUploadComplete: () -> Unit,
) {
    val role = LocalRoleColors.current
    var attachments by remember { mutableStateOf(listOf<AttachmentItem>()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = cardBg,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "上传附件",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Supported formats
            Text(
                text = "支持格式：docx, xlsx, pdf, jpg, png (单文件上限 500MB)",
                style = MaterialTheme.typography.labelSmall,
                color = textMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            AttachmentUploader(
                attachments = attachments,
                onAdd = {
                    val names = listOf("活动方案.docx", "现场照片.jpg", "预算表.xlsx")
                    val sizes = listOf("1.2 MB", "3.4 MB", "2.1 MB")
                    val idx = attachments.size % names.size
                    attachments = attachments + AttachmentItem(
                        fileName = names[idx],
                        fileSize = sizes[idx],
                        status = AttachmentStatus.PENDING
                    )
                },
                onRetry = { index ->
                    attachments = attachments.toMutableList().apply {
                        this[index] = this[index].copy(status = AttachmentStatus.UPLOADING)
                    }
                },
                onDelete = { index ->
                    attachments = attachments.toMutableList().apply { removeAt(index) }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onUploadComplete,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = attachments.isNotEmpty(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = role.primary)
            ) {
                Text("上传", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
private fun AttachmentUploadSheetPreview() {
    MeetingTheme {
        AttachmentUploadSheet(onDismiss = {}, onUploadComplete = {})
    }
}
