package com.example.dacs4.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * GIAO DIỆN ĐĂNG NHẬP (JETPACK COMPOSE)
 * Tương đương bằng giao diện login.tsx Ant-Design trên web, xịn xò mượt mà mướt rượt hơn vì chạy lõi Native!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, // Router hàm báo thành công để văng qua màn Trang Chủ
    viewModel: AuthViewModel = hiltViewModel() // Tự động moi ViewModel cái rụp từ "đám mây" Hilt
) {
    // Thu thập trạng thái dòng chữ (StateFlow)
    val authState by viewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Màu sắc chủ đạo của JobHunter (Xanh biển đậm & Trắng)
    val PrimaryColor = Color(0xFF58AAAB) // Xanh lá ngọc JobHunter
    val BgColor = Color(0xFF1E2022) // Xám đen bóng đêm

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- TEXT TIÊU ĐỀ
        Text(
            text = "JobHunter",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Đích đến của Lập trình viên VVIP",
            fontSize = 14.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // --- HỘP EMAIL ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email hoặc Username", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon", tint = PrimaryColor) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- HỘP MẬT KHẨU ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu vừng ơi mở ra", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Key Icon", tint = PrimaryColor) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- NÚT ĐĂNG NHẬP (Lắng nghe State) ---
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            enabled = authState !is AuthState.Loading // Khóa nút khi đang xoay
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("VÀO TRẠM KHÔNG GIAN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // --- BÁO LỖI NẾU SAI ---
        if (authState is AuthState.Error) {
            val errorMsg = (authState as AuthState.Error).message
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMsg, color = Color.Red, fontSize = 14.sp)
        }

        // --- TRIGGER THÀNH CÔNG ---
        // LaunchedEffect sẽ chạy khi AuthState chuyển hóa
        LaunchedEffect(authState) {
            if (authState is AuthState.Success) {
                onLoginSuccess() // Gửi tín hiệu nhảy router
                viewModel.resetState() // Tránh bị dội đi dội lại nếu User ấn Back.
            }
        }
    }
}
