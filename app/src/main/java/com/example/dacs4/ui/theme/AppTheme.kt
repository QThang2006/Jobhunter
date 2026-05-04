package com.example.dacs4.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─── COLOR TOKENS ────────────────────────────────────────────────────────────
object AppColors {
    // Backgrounds
    val BgPrimary    = Color(0xFFFFFFFF)   // Screen background
    val BgSurface    = Color(0xFFF8F9FA)   // Cards, chips surface
    val BgAccentLight = Color(0xFFEFF6FF)  // Active chip, highlighted card
    val BgAccentLighter = Color(0xFFDBEAFE) // Gradient highlight

    // Text
    val TextPrimary   = Color(0xFF1A1A2E)  // Main text
    val TextSecondary = Color(0xFF6B7280)  // Sub-labels, captions
    val TextHint      = Color(0xFFADB5BD)  // Placeholder, disabled

    // Accent (USE SPARINGLY - CTA only)
    val AccentBlue    = Color(0xFF2563EB)  // Buttons, salary, active state
    val AccentBlueMid = Color(0xFFBFDBFE)  // Chip border active
    val AccentBlueLight = Color(0xFFEFF6FF) // Chip fill active

    // Status colors
    val Success = Color(0xFF16A34A)
    val Warning = Color(0xFFF59E0B)
    val Error   = Color(0xFFDC2626)

    // Borders
    val Border    = Color(0xFFE5E7EB)   // Dividers, card border
    val BorderMid = Color(0xFFD1D5DB)   // Focused border
}

// ─── MATERIAL3 COLOR SCHEME ──────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary          = AppColors.AccentBlue,
    onPrimary        = Color.White,
    primaryContainer = AppColors.AccentBlueLight,
    onPrimaryContainer = AppColors.AccentBlue,
    secondary        = AppColors.TextSecondary,
    onSecondary      = Color.White,
    background       = AppColors.BgPrimary,
    onBackground     = AppColors.TextPrimary,
    surface          = AppColors.BgSurface,
    onSurface        = AppColors.TextPrimary,
    surfaceVariant   = AppColors.BgSurface,
    onSurfaceVariant = AppColors.TextSecondary,
    outline          = AppColors.Border,
    error            = AppColors.Error,
    onError          = Color.White,
)

// ─── APP THEME ────────────────────────────────────────────────────────────────
@Composable
fun JobHunterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
