package com.example.dacs4.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── TYPOGRAPHY ──────────────────────────────────────────────────────────────
// Sử dụng system font (Inter-like) vì DM Sans cần embed file .ttf
// Để add DM Sans: đặt file .ttf vào res/font/ và khai báo FontFamily
val AppTypography = Typography(
    // Screen titles, job name
    displayLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Bold,
        fontSize    = 22.sp,
        lineHeight  = 28.sp,
        color       = AppColors.TextPrimary
    ),
    // Section headers
    headlineMedium = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 18.sp,
        lineHeight  = 24.sp,
        color       = AppColors.TextPrimary
    ),
    // Card titles, job titles in list
    titleLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 15.sp,
        lineHeight  = 20.sp,
        color       = AppColors.TextPrimary
    ),
    // Subtitle (company name in card)
    titleMedium = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Medium,
        fontSize    = 13.sp,
        lineHeight  = 18.sp,
        color       = AppColors.TextSecondary
    ),
    // Body paragraph
    bodyLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Normal,
        fontSize    = 14.sp,
        lineHeight  = 22.sp,
        color       = AppColors.TextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Normal,
        fontSize    = 13.sp,
        lineHeight  = 20.sp,
        color       = AppColors.TextSecondary
    ),
    // Chips, tags, captions
    bodySmall = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Normal,
        fontSize    = 11.sp,
        lineHeight  = 16.sp,
        color       = AppColors.TextSecondary
    ),
    labelLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 14.sp,
        lineHeight  = 20.sp,
        color       = AppColors.BgPrimary
    ),
    labelSmall = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Medium,
        fontSize    = 10.sp,
        lineHeight  = 14.sp,
        color       = AppColors.TextHint
    )
)
