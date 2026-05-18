package com.example.dacs4.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dacs4.ui.theme.AppColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentStep by remember { mutableIntStateOf(1) }

    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (uiState) {
            is ForgotPasswordUiState.OtpSent -> {
                currentStep = 2
                viewModel.resetState()
            }
            is ForgotPasswordUiState.OtpVerified -> {
                currentStep = 3
                viewModel.resetState()
            }
            is ForgotPasswordUiState.PasswordResetSuccess -> {
                snackbarHostState.showSnackbar("Đặt lại mật khẩu thành công!")
                delay(1000)
                onSuccess()
            }
            is ForgotPasswordUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as ForgotPasswordUiState.Error).message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppColors.BgPrimary
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(slideOutHorizontally { width -> width } + fadeOut())
                    }
                },
                label = "step_transition"
            ) { targetStep ->
                when (targetStep) {
                    1 -> Step1Email(
                        email = email,
                        onEmailChange = { email = it },
                        onNext = { viewModel.requestOtp(email) },
                        onBack = onBack,
                        isLoading = uiState is ForgotPasswordUiState.Loading
                    )
                    2 -> Step2Otp(
                        email = email,
                        otp = otp,
                        onOtpChange = { if (it.length <= 6) otp = it },
                        onNext = { viewModel.verifyOtp(email, otp) },
                        onResend = { viewModel.requestOtp(email) },
                        isLoading = uiState is ForgotPasswordUiState.Loading
                    )
                    3 -> Step3NewPassword(
                        newPass = newPass,
                        onNewPassChange = { newPass = it },
                        confirmPass = confirmPass,
                        onConfirmPassChange = { confirmPass = it },
                        onSubmit = { viewModel.resetPassword(email, otp, newPass) },
                        isLoading = uiState is ForgotPasswordUiState.Loading
                    )
                }
            }
        }
    }
}

// ─── Các Step UI ──────────────────────────────────────────────────────────

@Composable
private fun StepHeader(title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(64.dp).background(color = AppColors.AccentBlue, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("JH", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
            Text(subtitle, fontSize = 14.sp, color = AppColors.TextSecondary)
        }
    }
}

@Composable
private fun Step1Email(
    email: String,
    onEmailChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    val isEmailValid = email.matches(emailRegex)

    Column(verticalArrangement = Arrangement.Center) {
        StepHeader("Quên mật khẩu", "Nhập email để nhận mã OTP")
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = email.isNotEmpty() && !isEmailValid,
            colors = defaultTextFieldColors()
        )

        Spacer(Modifier.height(32.dp))

        PrimaryButton(
            text = "GỬI MÃ OTP",
            enabled = email.isNotBlank() && isEmailValid,
            isLoading = isLoading,
            onClick = onNext
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Quay lại đăng nhập",
            color = AppColors.AccentBlue,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth().clickable { onBack() },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Step2Otp(
    email: String,
    otp: String,
    onOtpChange: (String) -> Unit,
    onNext: () -> Unit,
    onResend: () -> Unit,
    isLoading: Boolean
) {
    var timer by remember { mutableIntStateOf(60) }

    LaunchedEffect(Unit) {
        timer = 60
        while (timer > 0) {
            delay(1000)
            timer--
        }
    }

    Column(verticalArrangement = Arrangement.Center) {
        StepHeader("Nhập mã OTP", "Mã đã gửi đến $email")
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = onOtpChange,
            label = { Text("Mã OTP (6 số)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = defaultTextFieldColors()
        )

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            if (timer > 0) {
                Text("Gửi lại sau: 00:${timer.toString().padStart(2, '0')}", color = AppColors.TextSecondary, fontSize = 13.sp)
            } else {
                Text(
                    "Gửi lại mã",
                    color = AppColors.AccentBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onResend(); timer = 60 }
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        PrimaryButton(
            text = "XÁC NHẬN OTP",
            enabled = otp.length == 6,
            isLoading = isLoading,
            onClick = onNext
        )
    }
}

@Composable
private fun Step3NewPassword(
    newPass: String,
    onNewPassChange: (String) -> Unit,
    confirmPass: String,
    onConfirmPassChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean
) {
    var passVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    val isNewPassValid = newPass.length >= 6
    val isConfirmValid = newPass == confirmPass

    Column(verticalArrangement = Arrangement.Center) {
        StepHeader("Đặt mật khẩu mới", "Mật khẩu an toàn phải có ít nhất 6 ký tự")
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = newPass,
            onValueChange = onNewPassChange,
            label = { Text("Mật khẩu mới") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = newPass.isNotEmpty() && !isNewPassValid,
            trailingIcon = {
                val icon = if (passVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passVisible = !passVisible }) { Icon(icon, null, tint = AppColors.TextSecondary) }
            },
            colors = defaultTextFieldColors()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPass,
            onValueChange = onConfirmPassChange,
            label = { Text("Xác nhận mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = confirmPass.isNotEmpty() && !isConfirmValid,
            trailingIcon = {
                val icon = if (confirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { confirmVisible = !confirmVisible }) { Icon(icon, null, tint = AppColors.TextSecondary) }
            },
            colors = defaultTextFieldColors()
        )

        if (confirmPass.isNotEmpty() && !isConfirmValid) {
            Text("Không khớp với mật khẩu mới.", color = AppColors.Error, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(Modifier.height(32.dp))

        PrimaryButton(
            text = "ĐẶT LẠI MẬT KHẨU",
            enabled = newPass.isNotBlank() && confirmPass.isNotBlank() && isNewPassValid && isConfirmValid,
            isLoading = isLoading,
            onClick = onSubmit
        )
    }
}

@Composable
private fun PrimaryButton(text: String, enabled: Boolean, isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.AccentBlue,
            disabledContainerColor = AppColors.BgSurface
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
        } else {
            Text(text, fontWeight = FontWeight.Bold, color = if (enabled) Color.White else AppColors.TextHint)
        }
    }
}
