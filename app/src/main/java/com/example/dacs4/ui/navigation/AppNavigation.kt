package com.example.dacs4.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.ui.screens.auth.LoginScreen
import com.example.dacs4.ui.screens.home.HomeScreen
import com.example.dacs4.ui.screens.job.JobDetailScreen

/**
 * BẢN ĐỒ ĐIỀU HƯỚNG TOÀN APP
 * Quyết định xem mở app lên thì vào thẳng Login hay vào Home (Auto-login).
 */
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val JOB_DETAIL = "job_detail/{jobId}"
    const val COMPANY_DETAIL = "company_detail/{companyId}"
    const val JOB_LIST = "job_list"
    const val COMPANY_LIST = "company_list"
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
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // --- MÀN HÌNH CHÍNH ---
        composable(Routes.HOME) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onJobClick = { jobId ->
                    navController.navigate("job_detail/$jobId")
                },
                onCompanyClick = { companyId ->
                    navController.navigate("company_detail/$companyId")
                },
                onViewAllJobsClick = {
                    navController.navigate(Routes.JOB_LIST)
                },
                onViewAllCompaniesClick = {
                    navController.navigate(Routes.COMPANY_LIST)
                }
            )
        }

        // --- MÀN HÌNH CHI TIẾT CÔNG VIỆC ---
        composable(Routes.JOB_DETAIL) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            JobDetailScreen(
                jobId = jobId,
                onBack = { navController.popBackStack() }
            )
        }

        // --- MÀN HÌNH CHI TIẾT CÔNG TY ---
        composable(Routes.COMPANY_DETAIL) { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId") ?: ""
            com.example.dacs4.ui.screens.company.CompanyDetailScreen(
                companyId = companyId,
                onBack = { navController.popBackStack() }
            )
        }

        // --- MÀN HÌNH DANH SÁCH VIỆC LÀM ---
        composable(Routes.JOB_LIST) {
            com.example.dacs4.ui.screens.job.JobListScreen(
                onBackClick = { navController.popBackStack() },
                onJobClick = { jobId -> navController.navigate("job_detail/$jobId") }
            )
        }

        // --- MÀN HÌNH DANH SÁCH CÔNG TY ---
        composable(Routes.COMPANY_LIST) {
            com.example.dacs4.ui.screens.company.CompanyListScreen(
                onBackClick = { navController.popBackStack() },
                onCompanyClick = { companyId -> navController.navigate("company_detail/$companyId") }
            )
        }
    }
}