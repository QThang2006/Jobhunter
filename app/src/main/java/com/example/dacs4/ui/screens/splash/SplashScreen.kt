package com.example.dacs4.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.material3.Text
import com.example.dacs4.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

// ═══════════════════════════════════════════════════════════════════
//  SPLASH SCREEN — Flagship Animated Brand Reveal
//  Neo-Brutalism × Glassmorphism × Particle Field
// ═══════════════════════════════════════════════════════════════════

data class Particle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speedX: Float,
    val speedY: Float,
    val alpha: Float,
    val color: Color
)

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit
) {
    // ── Phase States ─────────────────────────────────────────────
    var phase by remember { mutableStateOf(SplashPhase.PARTICLES_IN) }

    // ── Logo animation ───────────────────────────────────────────
    val logoAlpha by animateFloatAsState(
        targetValue = when (phase) {
            SplashPhase.PARTICLES_IN -> 0f
            SplashPhase.LOGO_REVEAL  -> 1f
            SplashPhase.HOLD         -> 1f
            SplashPhase.EXIT         -> 0f
        },
        animationSpec = tween(
            durationMillis = when (phase) {
                SplashPhase.LOGO_REVEAL -> 800
                SplashPhase.EXIT        -> 500
                else -> 300
            },
            easing = FastOutSlowInEasing
        ),
        label = "logo_alpha"
    )
    val logoScale by animateFloatAsState(
        targetValue = when (phase) {
            SplashPhase.PARTICLES_IN -> 0.7f
            SplashPhase.LOGO_REVEAL  -> 1f
            SplashPhase.HOLD         -> 1f
            SplashPhase.EXIT         -> 1.1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )

    val taglineAlpha by animateFloatAsState(
        targetValue = when (phase) {
            SplashPhase.HOLD  -> 1f
            SplashPhase.EXIT  -> 0f
            else -> 0f
        },
        animationSpec = tween(600, easing = LinearEasing),
        label = "tagline_alpha"
    )

    val taglineOffset by animateFloatAsState(
        targetValue = when (phase) {
            SplashPhase.HOLD -> 0f
            else -> 20f
        },
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "tagline_offset"
    )

    // ── Particle animation tick ──────────────────────────────────
    val time by remember {
        val state = mutableFloatStateOf(0f)
        state
    }
    val animTime = rememberInfiniteTransition(label = "time")
    val t by animTime.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(100000, easing = LinearEasing)),
        label = "t"
    )

    // ── Sequencer ────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        delay(600)
        phase = SplashPhase.LOGO_REVEAL
        delay(900)
        phase = SplashPhase.HOLD
        delay(1200)
        phase = SplashPhase.EXIT
        delay(500)
        onNavigateToLogin()
    }

    // ── Background radial glow ────────────────────────────────────
    val glowAlpha by animateFloatAsState(
        targetValue = if (phase == SplashPhase.EXIT) 0f else 1f,
        animationSpec = tween(500),
        label = "glow_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JHColors.Background),
        contentAlignment = Alignment.Center
    ) {
        // ── Animated Background Mesh ──────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBackgroundMesh(t, glowAlpha)
        }

        // ── Floating Particles Canvas ──────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawParticleField(t, glowAlpha)
        }

        // ── Glow Orb behind logo ──────────────────────────────────
        Canvas(
            modifier = Modifier
                .size(300.dp)
                .alpha(glowAlpha * logoAlpha)
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        JHColors.AccentPrimary.copy(alpha = 0.3f),
                        JHColors.AccentSecondary.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    radius = size.minDimension / 2
                )
            )
        }

        // ── Brand Content ─────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo mark (geometric J monogram)
            Box(
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(logoAlpha)
            ) {
                Canvas(modifier = Modifier.size(80.dp)) {
                    drawJobHunterLogo()
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Brand name
            Text(
                text = "JobHunter",
                style = JHTypography.DisplayL.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            JHColors.TextPrimary,
                            JHColors.AccentPrimary,
                            JHColors.AccentTertiary
                        )
                    )
                ),
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(logoAlpha)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline
            Text(
                text = "Find your next opportunity",
                style = JHTypography.BodyM,
                color = JHColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(taglineAlpha)
                    .offset(y = taglineOffset.dp)
            )
        }

        // ── Bottom wordmark ───────────────────────────────────────
        Text(
            text = "POWERED BY DACS4",
            style = JHTypography.LabelS,
            color = JHColors.TextMuted,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(taglineAlpha)
        )
    }
}

// ──────────────────────────────────────────────────────────────────
//  Draw Helpers
// ──────────────────────────────────────────────────────────────────

private fun DrawScope.drawJobHunterLogo() {
    // Outer ring
    drawCircle(
        brush = Brush.sweepGradient(
            colors = listOf(
                Color(0xFF6366F1),
                Color(0xFF8B5CF6),
                Color(0xFF06B6D4),
                Color(0xFF6366F1)
            )
        ),
        radius = size.minDimension / 2,
        style = Stroke(width = 3.dp.toPx())
    )
    // Inner filled circle
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF1E1B4B),
                Color(0xFF0F172A)
            )
        ),
        radius = size.minDimension / 2 - 4.dp.toPx()
    )
    // J letter shape using path
    val centerX = size.width / 2
    val centerY = size.height / 2
    val letterPath = Path().apply {
        // Top horizontal bar of J
        moveTo(centerX - 12.dp.toPx(), centerY - 18.dp.toPx())
        lineTo(centerX + 12.dp.toPx(), centerY - 18.dp.toPx())
        // Vertical stem
        moveTo(centerX + 4.dp.toPx(), centerY - 18.dp.toPx())
        lineTo(centerX + 4.dp.toPx(), centerY + 8.dp.toPx())
        // Bottom curve of J
        quadraticBezierTo(
            centerX + 4.dp.toPx(), centerY + 20.dp.toPx(),
            centerX - 8.dp.toPx(), centerY + 20.dp.toPx()
        )
    }
    drawPath(
        path = letterPath,
        color = Color.White,
        style = Stroke(
            width = 3.5.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

private fun DrawScope.drawBackgroundMesh(t: Float, alpha: Float) {
    // Draw subtle grid lines
    val gridSpacing = 80.dp.toPx()
    val lineAlpha = 0.04f * alpha

    var x = 0f
    while (x < size.width) {
        drawLine(
            color = Color.White.copy(alpha = lineAlpha),
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1f
        )
        x += gridSpacing
    }
    var y = 0f
    while (y < size.height) {
        drawLine(
            color = Color.White.copy(alpha = lineAlpha),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
        y += gridSpacing
    }

    // Two large ambient glows
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF6366F1).copy(alpha = 0.12f * alpha),
                Color.Transparent
            ),
            center = Offset(size.width * 0.2f, size.height * 0.3f),
            radius = 400.dp.toPx()
        ),
        radius = 400.dp.toPx(),
        center = Offset(size.width * 0.2f, size.height * 0.3f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF06B6D4).copy(alpha = 0.08f * alpha),
                Color.Transparent
            ),
            center = Offset(size.width * 0.8f, size.height * 0.7f),
            radius = 300.dp.toPx()
        ),
        radius = 300.dp.toPx(),
        center = Offset(size.width * 0.8f, size.height * 0.7f)
    )
}

private fun DrawScope.drawParticleField(t: Float, alpha: Float) {
    val colors = listOf(
        Color(0xFF6366F1),
        Color(0xFF8B5CF6),
        Color(0xFF06B6D4),
        Color(0xFFF59E0B)
    )
    val seed = 42L
    repeat(40) { i ->
        val rng = Random(seed + i)
        val baseX = rng.nextFloat() * size.width
        val baseY = rng.nextFloat() * size.height
        val speed = 0.3f + rng.nextFloat() * 0.5f
        val amplitude = 20f + rng.nextFloat() * 40f
        val phase = rng.nextFloat() * 2f * PI.toFloat()
        val radius = 1.5f + rng.nextFloat() * 3f
        val particleAlpha = (0.3f + rng.nextFloat() * 0.5f) * alpha

        val x = baseX + sin(t * speed * 0.01f + phase) * amplitude
        val y = (baseY + t * speed * 0.2f) % size.height

        drawCircle(
            color = colors[i % colors.size].copy(alpha = particleAlpha),
            radius = radius,
            center = Offset(x, y)
        )
    }
}

enum class SplashPhase {
    PARTICLES_IN,
    LOGO_REVEAL,
    HOLD,
    EXIT
}