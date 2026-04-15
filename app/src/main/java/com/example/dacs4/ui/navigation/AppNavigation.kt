package com.example.dacs4.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.ui.screens.auth.LoginScreen
import com.example.dacs4.ui.screens.home.HomeScreen

/**
 * BẢN ĐỒ ĐIỀU HƯỚNG TOÀN APP
 * Quyết định xem mở app lên thì vào thẳng Login hay vào Home (Auto-login).
 */
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
}

@Composable
fun AppNavigation(tokenManager: TokenManager) {
    val navController = rememberNavController()
    
    // Kiểm tra xem đã có "chìa khóa" Token trong két sắt chưa để quyết định điểm bắt đầu
    val startDestination = if (tokenManager.isLoggedIn()) Routes.HOME else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {
        
        // --- MÀN HÌNH ĐĂNG NHẬP ---
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    // Xóa sạch lịch sử các màn trước đó (Login) để User không ấn Back quay lại được
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // --- MÀN HÌNH CHÍNH (Sau khi đăng nhập) ---
        composable(Routes.HOME) {
            HomeScreen(
                onLogout = {
                    // Đăng xuất xong thì lộn ngược lại màn Login
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
