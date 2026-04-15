package com.example.dacs4.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * MÀN HÌNH CHÍNH TẠM THỜI (HOME)
 * Nơi để test xem Đăng nhập xong có văng được vào đây không, và có Đăng xuất được không.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Màu JobHunter
    val PrimaryColor = Color(0xFF58AAAB)
    val BgColor = Color(0xFF1E2022)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("JobHunter Home", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgColor),
                actions = {
                    IconButton(onClick = { 
                        viewModel.logout()
                        onLogout() 
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        },
        containerColor = BgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
                            Text(
                text = "Chào mừng bạn đã gia nhập\nTrạm Không Gian JobHunter!",
                                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                                color = Color.White,
                lineHeight = 28.sp,
                modifier = Modifier.padding(bottom = 32.dp)
                            )

            // Nút Logout phụ (dễ bấm hơn)
            Button(
                onClick = { 
                    viewModel.logout()
                    onLogout() 
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("ĐĂNG XUẤT (LOGOUT)", fontWeight = FontWeight.Bold)
            }
        }
    }
}
