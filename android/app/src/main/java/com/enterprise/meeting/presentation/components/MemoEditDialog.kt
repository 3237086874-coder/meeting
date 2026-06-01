package com.enterprise.meeting.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.enterprise.meeting.presentation.theme.*

@Composable
fun MemoEditDialog(
    title: String,
    initialTitle: String = "",
    initialContent: String = "",
    initialTime: String = "",
    initialLocation: String = "",
    onConfirm: (title: String, content: String, time: String, location: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var memoTitle by remember { mutableStateOf(initialTitle) }
    var memoContent by remember { mutableStateOf(initialContent) }
    var memoTime by remember { mutableStateOf(initialTime) }
    var memoLocation by remember { mutableStateOf(initialLocation) }
    val role = LocalRoleColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = cardBg,
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = memoTitle,
                    onValueChange = { memoTitle = it },
                    label = { Text("事项", color = textSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = textPrimary),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = role.primary,
                        unfocusedBorderColor = colorBorder,
                        focusedContainerColor = surfaceWhite,
                        unfocusedContainerColor = surfaceWhite,
                        cursorColor = role.primary,
                        focusedLabelColor = role.primary,
                    ),
                )
                OutlinedTextField(
                    value = memoContent,
                    onValueChange = { memoContent = it },
                    label = { Text("内容", color = textSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = textPrimary),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = role.primary,
                        unfocusedBorderColor = colorBorder,
                        focusedContainerColor = surfaceWhite,
                        unfocusedContainerColor = surfaceWhite,
                        cursorColor = role.primary,
                        focusedLabelColor = role.primary,
                    ),
                )
                OutlinedTextField(
                    value = memoTime,
                    onValueChange = { memoTime = it },
                    label = { Text("时间", color = textSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = textPrimary),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("例如：今天 15:00", color = textMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = role.primary,
                        unfocusedBorderColor = colorBorder,
                        focusedContainerColor = surfaceWhite,
                        unfocusedContainerColor = surfaceWhite,
                        cursorColor = role.primary,
                        focusedLabelColor = role.primary,
                    ),
                )
                OutlinedTextField(
                    value = memoLocation,
                    onValueChange = { memoLocation = it },
                    label = { Text("地点", color = textSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = textPrimary),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("例如：A 栋 3 楼会议室", color = textMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = role.primary,
                        unfocusedBorderColor = colorBorder,
                        focusedContainerColor = surfaceWhite,
                        unfocusedContainerColor = surfaceWhite,
                        cursorColor = role.primary,
                        focusedLabelColor = role.primary,
                    ),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(memoTitle, memoContent, memoTime, memoLocation) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = role.primary),
                enabled = memoTitle.isNotBlank(),
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = textSecondary)
            }
        },
    )
}
