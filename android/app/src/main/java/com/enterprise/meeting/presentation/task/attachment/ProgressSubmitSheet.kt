package com.enterprise.meeting.presentation.task.attachment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressSubmitSheet(
    currentProgress: Int = 0,
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit,
) {
    val role = LocalRoleColors.current
    var progressValue by remember { mutableFloatStateOf(currentProgress.toFloat()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = cardBg,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "提交进度",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${progressValue.toInt()}%",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = role.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = progressValue,
                onValueChange = { progressValue = it },
                valueRange = 0f..100f,
                steps = 19,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = role.primary,
                    activeTrackColor = role.primary,
                    inactiveTrackColor = colorSkeleton
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0%", style = MaterialTheme.typography.labelSmall, color = textMuted)
                Text("100%", style = MaterialTheme.typography.labelSmall, color = textMuted)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSubmit(progressValue.toInt()) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = role.primary)
            ) {
                Text("提交进度", fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onDismiss) {
                Text("取消", color = textSecondary)
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
private fun ProgressSubmitSheetPreview() {
    MeetingTheme {
        ProgressSubmitSheet(onDismiss = {}, onSubmit = {})
    }
}
