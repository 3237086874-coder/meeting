package com.enterprise.meeting.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalRoleColors = staticCompositionLocalOf { RoleColors.president }

private val LightColorScheme = lightColorScheme(
    primary = RoleColors.president.primary,
    onPrimary = Color.White,
    primaryContainer = RoleColors.president.primary.copy(alpha = 0.12f),
    onPrimaryContainer = RoleColors.president.gradientStart,
    secondary = textSecondary,
    onSecondary = Color.White,
    secondaryContainer = textSecondary.copy(alpha = 0.12f),
    onSecondaryContainer = textSecondary,
    tertiary = RoleColors.manager.primary,
    onTertiary = Color.White,
    background = pageBg,
    onBackground = textPrimary,
    surface = surfaceWhite,
    onSurface = textPrimary,
    surfaceVariant = cardBg,
    onSurfaceVariant = textSecondary,
    error = colorDanger,
    onError = Color.White,
    errorContainer = colorDanger.copy(alpha = 0.12f),
    outline = colorBorder,
    outlineVariant = colorDivider,
)

@Composable
fun MeetingTheme(
    roleColors: RoleColor = RoleColors.president,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = roleColors.gradientStart.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    CompositionLocalProvider(LocalRoleColors provides roleColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MeetingTypography,
            content = content
        )
    }
}
