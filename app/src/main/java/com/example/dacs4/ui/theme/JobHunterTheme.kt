package com.example.dacs4.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════════════════════════════
//  JOBHUNTER FLAGSHIP DESIGN SYSTEM
//  Neo-Brutalism × Glassmorphism
//  Inspired by: Linear + Stripe + Apple
// ═══════════════════════════════════════════════════════════════════

object JHColors {

    // ── DARK FOUNDATION ────────────────────────────────────────────
    val Background      = Color(0xFF080B14)   // Near-black void
    val SurfaceDeep     = Color(0xFF0D1117)   // GitHub dark
    val SurfaceMid      = Color(0xFF111827)   // Card background
    val SurfaceElevated = Color(0xFF1A2235)   // Elevated card
    val SurfaceGlass    = Color(0x1AFFFFFF)   // Glassmorphism base

    // ── PRIMARY BRAND GRADIENT ──────────────────────────────────────
    val AccentPrimary   = Color(0xFF6366F1)   // Indigo (Linear-inspired)
    val AccentSecondary = Color(0xFF8B5CF6)   // Violet
    val AccentTertiary  = Color(0xFF06B6D4)   // Cyan (tech pop)
    val AccentGold      = Color(0xFFF59E0B)   // Amber accent
    val AccentGreen     = Color(0xFF10B981)   // Emerald success

    // ── GLOW & AURA ────────────────────────────────────────────────
    val GlowPrimary     = Color(0x406366F1)   // Indigo glow
    val GlowCyan        = Color(0x4006B6D4)   // Cyan glow
    val GlowGold        = Color(0x40F59E0B)   // Gold glow

    // ── BORDER ─────────────────────────────────────────────────────
    val BorderSubtle    = Color(0x1AFFFFFF)   // Ultra-subtle border
    val BorderMid       = Color(0x33FFFFFF)   // Mid border
    val BorderAccent    = Color(0x806366F1)   // Accent border

    // ── TEXT ───────────────────────────────────────────────────────
    val TextPrimary     = Color(0xFFF8FAFC)   // Near white
    val TextSecondary   = Color(0xFF94A3B8)   // Slate 400
    val TextMuted       = Color(0xFF475569)   // Slate 600
    val TextAccent      = Color(0xFF818CF8)   // Indigo 400

    // ── STATUS ─────────────────────────────────────────────────────
    val StatusSuccess   = Color(0xFF10B981)
    val StatusWarning   = Color(0xFFF59E0B)
    val StatusError     = Color(0xFFEF4444)
    val StatusInfo      = Color(0xFF06B6D4)

    // ── GRADIENT PAIRS ─────────────────────────────────────────────
    val GradientHero    = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFF06B6D4))
    val GradientCard    = listOf(Color(0xFF1A2235), Color(0xFF111827))
    val GradientButton  = listOf(Color(0xFF6366F1), Color(0xFF7C3AED))
    val GradientBadge   = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))
}

object JHTypography {
    // Font display sizes – Neo-Brutalism bold typescale
    val DisplayXL = TextStyle(
        fontSize = 48.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-1.5).sp,
        lineHeight = 52.sp
    )
    val DisplayL = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-1).sp,
        lineHeight = 40.sp
    )
    val DisplayM = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
        lineHeight = 34.sp
    )
    val HeadingL = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.3).sp,
        lineHeight = 28.sp
    )
    val HeadingM = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.2).sp,
        lineHeight = 24.sp
    )
    val BodyL = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
        lineHeight = 24.sp
    )
    val BodyM = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
        lineHeight = 20.sp
    )
    val BodyS = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.15.sp,
        lineHeight = 16.sp
    )
    val LabelM = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.4.sp
    )
    val LabelS = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
    )
    val CodeM = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp,
        fontFamily = FontFamily.Monospace
    )
}

object JHSpacing {
    val xs  = 4
    val sm  = 8
    val md  = 12
    val lg  = 16
    val xl  = 20
    val xxl = 24
    val xxxl = 32
    val huge = 48
    val screen = 20  // Standard screen padding
}

object JHRadius {
    val xs  = 4
    val sm  = 8
    val md  = 12
    val lg  = 16
    val xl  = 20
    val xxl = 24
    val full = 999  // Full rounded / pill
}

object JHElevation {
    val card    = 8
    val modal   = 24
    val dropdown = 16
}

// Animation duration constants
object JHAnimation {
    const val FAST   = 150
    const val NORMAL = 250
    const val SLOW   = 400
    const val SPRING_DAMPING = 0.7f
    const val SPRING_STIFFNESS = 400f
}