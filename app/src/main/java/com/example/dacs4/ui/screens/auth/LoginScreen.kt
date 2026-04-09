package com.example.dacs4.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs4.ui.components.*
import com.example.dacs4.ui.theme.*
import kotlin.math.sin

// ═══════════════════════════════════════════════════════════════════
//  LOGIN SCREEN — Redesigned Flagship
//  Neo-Brutalism × Glassmorphism
//  Animated aurora background + Glass card + Live validation
// ═══════════════════════════════════════════════════════════════════

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    val isLoading = authState is AuthState.Loading
    val errorMessage = (authState as? AuthState.Error)?.message

    // ── Content reveal animation ──────────────────────────────────
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        isVisible = true
    }

    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "content_reveal"
    )
    val contentOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 40f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "content_offset"
    )

    // ── Aurora animation ──────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    val auroraAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing)),
        label = "aurora_angle"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JHColors.Background)
    ) {
        // ── Aurora Background ─────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Primary aurora orb – top
            val orb1X = size.width * 0.3f + sin(auroraAngle * 0.017f) * 100f
            val orb1Y = size.height * 0.25f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6366F1).copy(alpha = 0.25f),
                        Color(0xFF8B5CF6).copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = Offset(orb1X, orb1Y),
                    radius = 350f * pulseScale
                ),
                radius = 350f * pulseScale,
                center = Offset(orb1X, orb1Y)
            )
            // Secondary aurora orb – bottom-right
            val orb2X = size.width * 0.75f
            val orb2Y = size.height * 0.75f + sin(auroraAngle * 0.013f) * 80f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF06B6D4).copy(alpha = 0.18f),
                        Color(0xFF0EA5E9).copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(orb2X, orb2Y),
                    radius = 280f
                ),
                radius = 280f,
                center = Offset(orb2X, orb2Y)
            )
            // Subtle grid
            val gridAlpha = 0.03f
            val spacing = 60.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(Color.White.copy(alpha = gridAlpha), Offset(x, 0f), Offset(x, size.height))
                x += spacing
            }
            var y = 0f
            while (y < size.height) {
                drawLine(Color.White.copy(alpha = gridAlpha), Offset(0f, y), Offset(size.width, y))
                y += spacing
            }
        }

        // ── Main Content ──────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = JHSpacing.screen.dp)
                .padding(top = 80.dp, bottom = 40.dp)
                .alpha(contentAlpha)
                .offset(y = contentOffset.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Brand ─────────────────────────────────────────────
            BrandHeader()

            Spacer(modifier = Modifier.height(48.dp))

            // ── Login Card ────────────────────────────────────────
            LoginCard(
                email = email,
                password = password,
                passwordVisible = passwordVisible,
                rememberMe = rememberMe,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onToggleVisibility = { passwordVisible = !passwordVisible },
                onToggleRemember = { rememberMe = !rememberMe },
                onLogin = { viewModel.login(email, password) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Social Divider ───────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientDivider(modifier = Modifier.weight(1f))
                Text("HOẶC", style = JHTypography.LabelS, color = JHColors.TextMuted)
                GradientDivider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Social Buttons ───────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SocialLoginButton(
                    label = "Google",
                    modifier = Modifier.weight(1f),
                    onClick = {}
                )
                SocialLoginButton(
                    label = "LinkedIn",
                    modifier = Modifier.weight(1f),
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Register Link ─────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chưa có tài khoản? ",
                    style = JHTypography.BodyM,
                    color = JHColors.TextSecondary
                )
                Text(
                    text = "Đăng ký ngay",
                    style = JHTypography.BodyM,
                    color = JHColors.AccentPrimary,
                    modifier = Modifier.clickable(onClick = onNavigateToRegister)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Trust indicators ─────────────────────────────────
            TrustIndicators()
        }
    }
}

@Composable
private fun BrandHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Monogram logo
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(JHColors.AccentPrimary, JHColors.AccentSecondary),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "JH", style = JHTypography.HeadingL, color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Chào mừng trở lại",
            style = JHTypography.DisplayM,
            color = JHColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Đăng nhập để tiếp tục hành trình sự nghiệp",
            style = JHTypography.BodyM,
            color = JHColors.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoginCard(
    email: String,
    password: String,
    passwordVisible: Boolean,
    rememberMe: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onToggleRemember: () -> Unit,
    onLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.xxl.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x1AFFFFFF),
                        Color(0x08FFFFFF)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0x40FFFFFF), Color(0x10FFFFFF))
                ),
                shape = RoundedCornerShape(JHRadius.xxl.dp)
            )
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Email
            GlassTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Email,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = JHColors.TextMuted
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            // Password
            GlassTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Mật khẩu",
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = JHColors.TextMuted
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onToggleVisibility) {
                        Icon(
                            if (passwordVisible) Icons.Outlined.VisibilityOff
                            else Icons.Outlined.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            // Remember + Forgot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable { onToggleRemember() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (rememberMe) JHColors.AccentPrimary else Color.Transparent
                            )
                            .border(
                                1.dp,
                                if (rememberMe) JHColors.AccentPrimary else JHColors.BorderMid,
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rememberMe) {
                            Icon(
                                Icons.Outlined.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    Text("Nhớ tôi", style = JHTypography.BodyS, color = JHColors.TextSecondary)
                }

                Text(
                    text = "Quên mật khẩu?",
                    style = JHTypography.BodyS,
                    color = JHColors.AccentPrimary,
                    modifier = Modifier.clickable { }
                )
            }

            // Error state
            if (!errorMessage.isNullOrEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(JHRadius.md.dp))
                        .background(JHColors.StatusError.copy(alpha = 0.1f))
                        .border(
                            1.dp,
                            JHColors.StatusError.copy(alpha = 0.3f),
                            RoundedCornerShape(JHRadius.md.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.ErrorOutline,
                        contentDescription = null,
                        tint = JHColors.StatusError,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = errorMessage,
                        style = JHTypography.BodyS,
                        color = JHColors.StatusError
                    )
                }
            }

            // Login button
            GradientButton(
                text = "ĐĂNG NHẬP",
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                enabled = email.contains("@") && password.length >= 6
            )
        }
    }
}

@Composable
private fun SocialLoginButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .background(JHColors.SurfaceElevated)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.lg.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.AccountCircle,
                contentDescription = null,
                tint = JHColors.TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Text(label, style = JHTypography.LabelM, color = JHColors.TextSecondary)
        }
    }
}

@Composable
private fun TrustIndicators() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("🔒 Bảo mật SSL", "✓ 50K+ Jobs", "⭐ 4.9/5").forEachIndexed { i, label ->
            if (i > 0) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(3.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(JHColors.TextMuted)
                )
            }
            Text(label, style = JHTypography.LabelS, color = JHColors.TextMuted)
        }
    }
}