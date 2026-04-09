package com.example.dacs4.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.dacs4.ui.components.*
import com.example.dacs4.ui.theme.*

// ═══════════════════════════════════════════════════════════════════
//  REGISTER SCREEN — Neo-Brutalism × Glassmorphism
//  Multi-step with live validation & smooth transitions
// ═══════════════════════════════════════════════════════════════════

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Password strength
    val passwordStrength = remember(password) { calcPasswordStrength(password) }

    // Ambient animation
    val infiniteTransition = rememberInfiniteTransition(label = "register_ambient")
    val ambientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "ambient"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JHColors.Background)
    ) {
        // ── Ambient Background ──────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Top-left glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        JHColors.AccentPrimary.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.1f, size.height * 0.15f),
                    radius = 350f
                ),
                radius = 350f,
                center = Offset(size.width * 0.1f, size.height * 0.15f)
            )
            // Bottom-right glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        JHColors.AccentTertiary.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.9f, size.height * 0.85f),
                    radius = 280f
                ),
                radius = 280f,
                center = Offset(size.width * 0.9f, size.height * 0.85f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = JHSpacing.screen.dp)
                .padding(top = 56.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header ───────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateToLogin) {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = JHColors.TextSecondary
                    )
                }
                StatusBadge(label = "Bước ${currentStep + 1}/2")
                Box(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Brand Mark ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(JHColors.GradientButton)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "JH",
                    style = JHTypography.HeadingM,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Title ────────────────────────────────────────────
            Text(
                text = if (currentStep == 0) "Tạo tài khoản" else "Bảo mật tài khoản",
                style = JHTypography.DisplayM,
                color = JHColors.TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (currentStep == 0)
                    "Bắt đầu hành trình tìm việc của bạn"
                else
                    "Thiết lập mật khẩu mạnh để bảo vệ tài khoản",
                style = JHTypography.BodyM,
                color = JHColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Step Progress Bar ────────────────────────────────
            StepProgressBar(currentStep = currentStep, totalSteps = 2)

            Spacer(modifier = Modifier.height(32.dp))

            // ── Form Content (Animated) ───────────────────────────
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "form_step"
            ) { step ->
                when (step) {
                    0 -> StepOne(
                        fullName = fullName,
                        email = email,
                        onFullNameChange = { fullName = it },
                        onEmailChange = { email = it },
                        onNext = { currentStep = 1 }
                    )
                    1 -> StepTwo(
                        password = password,
                        confirmPassword = confirmPassword,
                        passwordVisible = passwordVisible,
                        passwordStrength = passwordStrength,
                        agreedToTerms = agreedToTerms,
                        isLoading = isLoading,
                        onPasswordChange = { password = it },
                        onConfirmPasswordChange = { confirmPassword = it },
                        onToggleVisibility = { passwordVisible = !passwordVisible },
                        onToggleTerms = { agreedToTerms = !agreedToTerms },
                        onRegister = {
                            isLoading = true
                            // simulate API call
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Footer Link ───────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đã có tài khoản? ",
                    style = JHTypography.BodyM,
                    color = JHColors.TextSecondary
                )
                Text(
                    text = "Đăng nhập",
                    style = JHTypography.BodyM,
                    color = JHColors.AccentPrimary,
                    modifier = Modifier.clickable(onClick = onNavigateToLogin)
                )
            }
        }
    }
}

@Composable
private fun StepProgressBar(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(totalSteps) { index ->
            val isActive = index <= currentStep
            val progressFraction by animateFloatAsState(
                targetValue = if (isActive) 1f else 0f,
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                label = "step_$index"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(JHRadius.full.dp))
                    .background(JHColors.SurfaceElevated)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressFraction)
                        .background(
                            brush = Brush.horizontalGradient(JHColors.GradientButton)
                        )
                )
            }
        }
    }
}

@Composable
private fun StepOne(
    fullName: String,
    email: String,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Full Name
        GlassTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = "Họ và tên",
            leadingIcon = {
                Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(20.dp))
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Email
        GlassTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email",
            leadingIcon = {
                Icon(Icons.Outlined.Email, contentDescription = null, modifier = Modifier.size(20.dp))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Social sign-up options
        Text(
            text = "HOẶC ĐĂNG KÝ VỚI",
            style = JHTypography.LabelS,
            color = JHColors.TextMuted,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SocialButton(
                label = "Google",
                modifier = Modifier.weight(1f),
                onClick = {}
            )
            SocialButton(
                label = "GitHub",
                modifier = Modifier.weight(1f),
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        GradientButton(
            text = "TIẾP THEO →",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = fullName.isNotBlank() && email.contains("@")
        )
    }
}

@Composable
private fun StepTwo(
    password: String,
    confirmPassword: String,
    passwordVisible: Boolean,
    passwordStrength: PasswordStrength,
    agreedToTerms: Boolean,
    isLoading: Boolean,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onToggleTerms: () -> Unit,
    onRegister: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Password
        GlassTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Mật khẩu",
            leadingIcon = {
                Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Password Strength
        PasswordStrengthIndicator(strength = passwordStrength)

        // Confirm Password
        GlassTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Xác nhận mật khẩu",
            leadingIcon = {
                Icon(Icons.Outlined.LockOpen, contentDescription = null, modifier = Modifier.size(20.dp))
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = confirmPassword.isNotEmpty() && confirmPassword != password,
            modifier = Modifier.fillMaxWidth()
        )

        // Terms checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleTerms() }
                .clip(RoundedCornerShape(JHRadius.md.dp))
                .background(JHColors.SurfaceElevated.copy(alpha = 0.5f))
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (agreedToTerms) JHColors.AccentPrimary
                        else Color.Transparent
                    )
                    .border(
                        1.dp,
                        if (agreedToTerms) JHColors.AccentPrimary else JHColors.BorderMid,
                        RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (agreedToTerms) {
                    Icon(
                        Icons.Outlined.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Text(
                text = "Tôi đồng ý với Điều khoản dịch vụ và Chính sách bảo mật",
                style = JHTypography.BodyS,
                color = JHColors.TextSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        GradientButton(
            text = "TẠO TÀI KHOẢN",
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth(),
            isLoading = isLoading,
            enabled = password.length >= 8 && password == confirmPassword && agreedToTerms
        )
    }
}

@Composable
private fun PasswordStrengthIndicator(strength: PasswordStrength) {
    if (strength == PasswordStrength.EMPTY) return

    val (label, color, fraction) = when (strength) {
        PasswordStrength.WEAK   -> Triple("Yếu", JHColors.StatusError, 0.25f)
        PasswordStrength.FAIR   -> Triple("Trung bình", JHColors.StatusWarning, 0.5f)
        PasswordStrength.GOOD   -> Triple("Tốt", JHColors.AccentTertiary, 0.75f)
        PasswordStrength.STRONG -> Triple("Rất mạnh", JHColors.StatusSuccess, 1f)
        else -> Triple("", Color.Transparent, 0f)
    }

    val animFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(300),
        label = "strength_bar"
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Độ mạnh mật khẩu", style = JHTypography.BodyS, color = JHColors.TextMuted)
            Text(label, style = JHTypography.LabelS, color = color)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(JHRadius.full.dp))
                .background(JHColors.SurfaceElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animFraction)
                    .background(
                        brush = Brush.horizontalGradient(listOf(color.copy(alpha = 0.7f), color))
                    )
                    .clip(RoundedCornerShape(JHRadius.full.dp))
            )
        }
    }
}

@Composable
private fun SocialButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GhostButton(
        text = label,
        onClick = onClick,
        modifier = modifier.height(48.dp)
    )
}

// ──────────────────────────────────────────────────────────────────
//  Helpers
// ──────────────────────────────────────────────────────────────────

enum class PasswordStrength { EMPTY, WEAK, FAIR, GOOD, STRONG }

fun calcPasswordStrength(password: String): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength.EMPTY
    var score = 0
    if (password.length >= 8) score++
    if (password.length >= 12) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    return when {
        score <= 1 -> PasswordStrength.WEAK
        score == 2 -> PasswordStrength.FAIR
        score == 3 -> PasswordStrength.GOOD
        else       -> PasswordStrength.STRONG
    }
}