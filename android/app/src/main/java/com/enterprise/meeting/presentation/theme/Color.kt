package com.enterprise.meeting.presentation.theme

import androidx.compose.ui.graphics.Color

// ===== Role Colors (dynamic, based on current role) =====

data class RoleColor(
    val primary: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val primaryPressed: Color,
    val primaryDisabled: Color,
    val onPrimary: Color,
)

object RoleColors {
    val superadmin = RoleColor(
        primary = Color(0xFFB45309),
        gradientStart = Color(0xFF78350F),
        gradientEnd = Color(0xFFB45309),
        primaryPressed = Color(0xFF924108),
        primaryDisabled = Color(0x40B45309),
        onPrimary = Color.White,
    )

    val president = RoleColor(
        primary = Color(0xFF123B6A),
        gradientStart = Color(0xFF1E3A5F),
        gradientEnd = Color(0xFF2C5282),
        primaryPressed = Color(0xFF0E2E55),
        primaryDisabled = Color(0x40123B6A),
        onPrimary = Color.White,
    )

    val manager = RoleColor(
        primary = Color(0xFF1F7A6C),
        gradientStart = Color(0xFF064E3B),
        gradientEnd = Color(0xFF1F7A6C),
        primaryPressed = Color(0xFF185E52),
        primaryDisabled = Color(0x401F7A6C),
        onPrimary = Color.White,
    )

    val staff = RoleColor(
        primary = Color(0xFF2D3748),
        gradientStart = Color(0xFF1F2937),
        gradientEnd = Color(0xFF4B5563),
        primaryPressed = Color(0xFF242D3A),
        primaryDisabled = Color(0x402D3748),
        onPrimary = Color.White,
    )

    fun fromRole(role: String): RoleColor = when (role.lowercase()) {
        "superadmin" -> superadmin
        "president" -> president
        "manager" -> manager
        "staff" -> staff
        else -> president
    }
}

// ===== Status Colors (fixed, unchanged by role) =====
val colorDanger = Color(0xFFC94747)
val colorError = Color(0xFFE53E3E)
val colorSuccess = Color(0xFF38A169)
val colorWarning = Color(0xFFD97706)
val colorInfo = Color(0xFF0284C7)

// ===== Neutral Colors (fixed) =====
val pageBg = Color(0xFFFFFDFC)
val cardBg = Color(0xFFFFFBF5)
val surfaceWhite = Color.White
val colorBorder = Color(0xFFD8D2C8)
val colorSoftBorder = Color(0xFFF0D9A8)
val colorDivider = Color(0xFFEDE6D8)
val colorSkeleton = Color(0xFFF0E9DC)
val colorOverlayScrim = Color(0x401A1A1A)

// ===== Text Colors =====
val textPrimary = Color(0xFF1A1A1A)
val textSecondary = Color(0xFF7A6B53)
val textMuted = Color(0xFFB8A88E)
val textOnDark = Color.White
val textOnDarkSubtle = Color(0x99FFFFFF)

// ===== Legacy/Compatibility Aliases (non-conflicting) =====
val Primary = RoleColors.president.primary
val PrimaryDark = RoleColors.president.gradientStart
val OnPrimary = Color.White

val Background = pageBg
val Surface = Color.White
val SurfaceVariant = Color(0xFFFDF8F0)

val HeaderNavy = RoleColors.president.primary
val HeaderTeal = RoleColors.manager.primary
val HeaderTealDark = RoleColors.manager.gradientStart
val BottomNavBg = Color.White
val BottomNavSelected = RoleColors.president.primary
val BottomNavUnselected = Color(0xFF7A6B53)

val Success = colorSuccess
val Warning = colorWarning
val Error = colorDanger
val Info = colorInfo
val Teal = RoleColors.manager.primary
val Orange = Color(0xFFB45309)

val Slate = Color(0xFF2D3748)
val Cream = Color(0xFFD8D2C8)
val Brown = textSecondary
val CardBorder = colorDivider
val WarmBeige = Color(0xFFF0E0C8)
val StatusBlue = colorInfo
val StatusRed = colorDanger

// Login specific
val LoginBg = pageBg
val InputBorder = colorBorder
