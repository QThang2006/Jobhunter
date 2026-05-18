package com.example.dacs4.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.ui.screens.auth.ForgotPasswordScreen
import com.example.dacs4.ui.screens.auth.LoginScreen
import com.example.dacs4.ui.screens.auth.RegisterScreen
import com.example.dacs4.ui.screens.main.MainScreen

/**
 * Điểm bắt đầu của toàn bộ App.
 * Chỉ quản lý luồng Auth (Login / Register / ForgotPassword).
 * Sau khi đăng nhập → chuyển vào MainScreen (Bottom Navigation).
 */
object Routes {
    const val LOGIN          = "login"
    const val REGISTER       = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val MAIN           = "main"   // Vào MainScreen (Bottom Nav)
}

@Composable
fun AppNavigation(tokenManager: TokenManager) {
    val navController = rememberNavController()

    val startDestination = if (tokenManager.isLoggedIn()) Routes.MAIN else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {

        // ─── ĐĂNG NHẬP ───────────────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }

        // ─── ĐĂNG KÝ ─────────────────────────────────────────────
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ─── QUÊN MẬT KHẨU ───────────────────────────────────────
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ─── MAIN APP (sau khi đăng nhập) ────────────────────────
        composable(Routes.MAIN) {
            MainScreen(
                tokenManager = tokenManager,
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}
