package com.example.dacs4.ui.screens.application

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.dacs4.ui.components.*
import com.example.dacs4.ui.theme.*
import kotlin.math.*
import kotlin.random.Random

// ═══════════════════════════════════════════════════════════════════
//  APPLICATION SUCCESS SCREEN — Phase 2
//  Animated celebration với confetti + checkmark reveal
// ═══════════════════════════════════════════════════════════════════

@Composable
fun ApplicationSuccessScreen(
    applicationId: String,
    jobName: String,
    companyName: String,
    onViewMyApplications: () -> Unit,
    onBackToHome: () -> Unit
) {
    // ── Entry animations ──────────────────────────────────────────
    var phase by remember { mutableStateOf(SuccessPhase.INITIAL) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        phase = SuccessPhase.CIRCLE_IN
        kotlinx.coroutines.delay(500)
        phase = SuccessPhase.CHECK_IN
        kotlinx.coroutines.delay(400)
        phase = SuccessPhase.TEXT_IN
        kotlinx.coroutines.delay(300)
        phase = SuccessPhase.BUTTONS_IN
    }

    // ── Circle reveal ─────────────────────────────────────────────
    val circleScale by animateFloatAsState(
        targetValue = when (phase) {
            SuccessPhase.INITIAL   -> 0f
            else                  -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "circle_scale"
    )

    // ── Checkmark draw ────────────────────────────────────────────
    val checkAlpha by animateFloatAsState(
        targetValue = if (phase >= SuccessPhase.CHECK_IN) 1f else 0f,
        animationSpec = tween(400),
        label = "check_alpha"
    )
    val checkScale by animateFloatAsState(
        targetValue = if (phase >= SuccessPhase.CHECK_IN) 1f else 0.3f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "check_scale"
    )

    // ── Content reveal ────────────────────────────────────────────
    val textAlpha by animateFloatAsState(
        targetValue = if (phase >= SuccessPhase.TEXT_IN) 1f else 0f,
        animationSpec = tween(500),
        label = "text_alpha"
    )
    val textOffset by animateFloatAsState(
        targetValue = if (phase >= SuccessPhase.TEXT_IN) 0f else 30f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "text_offset"
    )

    val buttonsAlpha by animateFloatAsState(
        targetValue = if (phase >= SuccessPhase.BUTTONS_IN) 1f else 0f,
        animationSpec = tween(400),
        label = "buttons_alpha"
    )

    // ── Confetti animation ────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val confettiTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "confetti_time"
    )

    // ── Outer glow pulse ──────────────────────────────────────────
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JHColors.Background),
        contentAlignment = Alignment.Center
    ) {
        // ── Background ────────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawSuccessBackground()
        }

        // ── Confetti (after phase CHECK_IN) ───────────────────────
        if (phase >= SuccessPhase.CHECK_IN) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawConfetti(confettiTime)
            }
        }

        // ── Content ───────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = JHSpacing.screen.dp)
        ) {
            // ── Success circle ────────────────────────────────────
            Box(contentAlignment = Alignment.Center) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(circleScale * glowPulse)
                        .clip(CircleShape)
                        .background(JHColors.StatusSuccess.copy(alpha = 0.08f))
                        .border(1.dp, JHColors.StatusSuccess.copy(alpha = 0.15f), CircleShape)
                )
                // Middle ring
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(circleScale)
                        .clip(CircleShape)
                        .background(JHColors.StatusSuccess.copy(alpha = 0.12f))
                        .border(1.dp, JHColors.StatusSuccess.copy(alpha = 0.25f), CircleShape)
                )
                // Inner filled circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(circleScale)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(JHColors.StatusSuccess, Color(0xFF059669))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(36.dp)
                            .scale(checkScale)
                            .alpha(checkAlpha)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Text content ──────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(textAlpha)
                    .offset(y = textOffset.dp)
            ) {
                Text(
                    "Ứng tuyển thành công! 🎉",
                    style = JHTypography.DisplayM,
                    color = JHColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Đơn của bạn đã được gửi đến",
                    style = JHTypography.BodyM,
                    color = JHColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Job + company summary card
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(JHRadius.xl.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    JHColors.StatusSuccess.copy(alpha = 0.1f),
                                    JHColors.AccentPrimary.copy(alpha = 0.05f)
                                )
                            )
                        )
                        .border(1.dp, JHColors.StatusSuccess.copy(alpha = 0.3f), RoundedCornerShape(JHRadius.xl.dp))
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            jobName,
                            style = JHTypography.HeadingL,
                            color = JHColors.TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Outlined.Business, null, tint = JHColors.TextMuted, modifier = Modifier.size(14.dp))
                            Text(companyName, style = JHTypography.BodyM, color = JHColors.TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // What happens next
                WhatHappensNextCard()
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Buttons ───────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(buttonsAlpha),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientButton(
                    text = "XEM LỊCH SỬ ỨNG TUYỂN",
                    onClick = onViewMyApplications,
                    modifier = Modifier.fillMaxWidth()
                )
                GhostButton(
                    text = "Về trang chủ",
                    onClick = onBackToHome,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun WhatHappensNextCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Bước tiếp theo",
                style = JHTypography.HeadingM,
                color = JHColors.TextPrimary
            )
            GradientDivider()

            listOf(
                Triple(Icons.Outlined.Email, "Nhà tuyển dụng sẽ xem xét hồ sơ", "Thường trong vòng 3–5 ngày làm việc"),
                Triple(Icons.Outlined.PhoneEnabled, "Bạn sẽ nhận được email/điện thoại liên lạc", "Kiểm tra hộp thư thường xuyên"),
                Triple(Icons.Outlined.WorkOutline, "Chuẩn bị cho vòng phỏng vấn", "Xem lại JD và nghiên cứu về công ty")
            ).forEachIndexed { index, (icon, title, sub) ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(JHColors.AccentPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = JHColors.AccentPrimary, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text(title, style = JHTypography.BodyM.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold), color = JHColors.TextPrimary)
                        Text(sub, style = JHTypography.BodyS, color = JHColors.TextMuted)
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  Canvas Draw Helpers
// ──────────────────────────────────────────────────────────────────

private fun DrawScope.drawSuccessBackground() {
    drawRect(brush = Brush.radialGradient(
        colors = listOf(Color(0xFF0A1A0A), Color(0xFF080B14)),
        center = center, radius = size.minDimension
    ))
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF10B981).copy(alpha = 0.12f), Color.Transparent),
            center = center, radius = 400f
        ),
        radius = 400f, center = center
    )
    // Grid
    val gridAlpha = 0.025f
    val spacing = 60.dp.toPx()
    var x = 0f; while (x < size.width) {
        drawLine(Color.White.copy(alpha = gridAlpha), Offset(x, 0f), Offset(x, size.height)); x += spacing
    }
    var y = 0f; while (y < size.height) {
        drawLine(Color.White.copy(alpha = gridAlpha), Offset(0f, y), Offset(size.width, y)); y += spacing
    }
}

private fun DrawScope.drawConfetti(time: Float) {
    val colors = listOf(
        Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFF06B6D4),
        Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFFEC4899)
    )
    val seed = 77L
    repeat(50) { i ->
        val rng = Random(seed + i)
        val startX = rng.nextFloat() * size.width
        val fallSpeed = 0.8f + rng.nextFloat() * 1.5f
        val swayAmplitude = 20f + rng.nextFloat() * 40f
        val phaseOffset = rng.nextFloat() * 2f * PI.toFloat()
        val confettiSize = 4f + rng.nextFloat() * 8f
        val rotation = time * fallSpeed * 2f + phaseOffset

        val x = startX + sin(time * 0.02f * fallSpeed + phaseOffset) * swayAmplitude
        val y = ((time * fallSpeed * 0.5f + rng.nextFloat() * size.height) % size.height)
        val alpha = (0.4f + rng.nextFloat() * 0.6f) * (1f - y / size.height * 0.3f)

        drawRect(
            color = colors[i % colors.size].copy(alpha = alpha.coerceIn(0f, 1f)),
            topLeft = Offset(x - confettiSize / 2, y - confettiSize / 2),
            size = androidx.compose.ui.geometry.Size(confettiSize, confettiSize * 0.6f)
        )
    }
}

enum class SuccessPhase { INITIAL, CIRCLE_IN, CHECK_IN, TEXT_IN, BUTTONS_IN }
