package com.enterprise.meeting.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.theme.*

data class RoleOption(
    val key: String,
    val label: String,
    val subtitle: String,
)

val roleOptions = listOf(
    RoleOption("superadmin", "超级管理员", "信息技术部 · 系统管理"),
    RoleOption("president", "高级管理层", "公司高管视角"),
    RoleOption("manager", "部门负责人", "部门总监"),
    RoleOption("staff", "执行人员", "一线员工"),
)

@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit,
    defaultRole: String = "superadmin",
) {
    var username by remember { mutableStateOf("user") }
    var password by remember { mutableStateOf("admin") }
    var selectedRole by remember { mutableStateOf(defaultRole) }
    var isLoading by remember { mutableStateOf(false) }

    val roleColor = RoleColors.fromRole(selectedRole)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBg)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // ===== Brand area =====
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(16.dp),
            color = RoleColors.president.primary   // Fixed navy per Figma
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "M",
                    color = RoleColors.president.onPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "企业会议任务闭环管理",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Shield,
                contentDescription = null,
                tint = textSecondary,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "远航科技集团",
                fontSize = 12.sp,
                color = textSecondary
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Demo mode tag
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = colorWarning.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, colorWarning.copy(alpha = 0.3f))
        ) {
            Text(
                text = "演示模式 · 原型展示",
                color = colorWarning,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ===== Role selector =====
        Text(
            text = "选择体验角色",
            fontSize = 12.sp,
            color = textSecondary,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 grid of role cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0..1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0..1) {
                        val index = row * 2 + col
                        if (index < roleOptions.size) {
                            val role = roleOptions[index]
                            val isSelected = selectedRole == role.key
                            val color = RoleColors.fromRole(role.key)

                            Box(modifier = Modifier.weight(1f)) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedRole = role.key },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) color.primary.copy(alpha = 0.07f) else cardBg
                                    ),
                                    border = if (isSelected) BorderStroke(1.5.dp, color.primary)
                                    else BorderStroke(1.dp, colorDivider)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 12.dp)
                                    ) {
                                        Text(
                                            text = role.label,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) textPrimary else textPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = role.subtitle,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isSelected) textSecondary else textSecondary
                                        )
                                    }
                                }

                                // ☑️ Selected badge at top-right corner
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 4.dp, y = (-4).dp)
                                            .size(20.dp)
                                            .background(color.primary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = "已选中",
                                            tint = color.onPrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ===== Form =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Username
            Text(
                text = "账号",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = {
                    Text(
                        "user",
                        fontSize = 14.sp,
                        color = textMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint = textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = roleColor.primary,
                    unfocusedBorderColor = roleColor.primary.copy(alpha = 0.4f),
                    cursorColor = roleColor.primary,
                    focusedContainerColor = surfaceWhite,
                    unfocusedContainerColor = surfaceWhite
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Password
            Text(
                text = "密码",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text(
                        "admin",
                        fontSize = 14.sp,
                        color = textMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = null,
                        tint = textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = roleColor.primary,
                    unfocusedBorderColor = roleColor.primary.copy(alpha = 0.4f),
                    cursorColor = roleColor.primary,
                    focusedContainerColor = surfaceWhite,
                    unfocusedContainerColor = surfaceWhite
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (username.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Test account hint
            Text(
                text = "测试账号: user / admin",
                fontSize = 11.sp,
                color = textMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            Button(
                onClick = {
                    isLoading = true
                    onLoginSuccess(selectedRole)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = roleColor.primary,
                    contentColor = roleColor.onPrimary,
                    disabledContainerColor = roleColor.primary,
                    disabledContentColor = roleColor.onPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = roleColor.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "登录",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.Shield,
                contentDescription = null,
                tint = textMuted,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "企业数据云端加密存储 · 仅授权访问",
                fontSize = 11.sp,
                color = textMuted
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Decorative wave at bottom (Figma: #F6F0E5)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            val waveColor = Color(0xFFF6F0E5)
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, size.height * 0.3f)
                cubicTo(
                    size.width * 0.25f, size.height * 0.7f,
                    size.width * 0.5f, 0f,
                    size.width * 0.65f, size.height * 0.4f
                )
                cubicTo(
                    size.width * 0.8f, size.height * 0.8f,
                    size.width * 0.9f, size.height * 0.2f,
                    size.width, size.height * 0.5f
                )
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, waveColor)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun LoginScreenPreview() {
    MeetingTheme {
        LoginScreen(onLoginSuccess = {})
    }
}
