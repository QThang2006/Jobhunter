package com.example.dacs4.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.ui.navigation.BottomNavItem
import com.example.dacs4.ui.screens.company.CompanyDetailScreen
import com.example.dacs4.ui.screens.company.CompanyListScreen
import com.example.dacs4.ui.screens.home.HomeScreen
import com.example.dacs4.ui.screens.job.JobDetailScreen
import com.example.dacs4.ui.screens.job.JobListScreen
import com.example.dacs4.ui.screens.profile.ProfileScreen
import com.example.dacs4.ui.screens.resume.ApplyHistoryScreen
import com.example.dacs4.ui.theme.AppColors

/**
 * MainScreen: Scaffold chứa BottomNavigationBar + NavHost chính của app.
 * Được gọi sau khi đã đăng nhập thành công.
 */
@Composable
fun MainScreen(
    tokenManager: TokenManager,
    onLogout: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    
    // Initialize Socket Connection when MainScreen is loaded
    LaunchedEffect(Unit) {
        viewModel.initSocket()
    }

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Jobs,
        BottomNavItem.Profile
    )

    Scaffold(
        containerColor = AppColors.BgPrimary,
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // ─── Lớp dưới: Nội dung App (Tràn toàn màn hình) ────────────────
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()) // Chỉ chừa khoảng trống cho StatusBar phía trên
            ) {
                // ─── TAB HOME ─────────────────────────────────────────
                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        onLogout = {
                            tokenManager.clearToken()
                            onLogout()
                        },
                        onJobClick = { jobId -> navController.navigate("job_detail/$jobId") },
                        onCompanyClick = { companyId -> navController.navigate("company_detail/$companyId") },
                        onViewAllJobsClick = {
                            navController.navigate(BottomNavItem.Jobs.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onViewAllCompaniesClick = { navController.navigate("company_list") }
                    )
                }

                // ─── TAB JOBS ─────────────────────────────────────────
                composable(BottomNavItem.Jobs.route) {
                    JobListScreen(
                        onBackClick = { navController.popBackStack() },
                        onJobClick = { jobId -> navController.navigate("job_detail/$jobId") }
                    )
                }

                // ─── TAB PROFILE ──────────────────────────────────────
                composable(BottomNavItem.Profile.route) {
                    ProfileScreen(
                        onLogout = {
                            tokenManager.clearToken()
                            onLogout()
                        },
                        onNavigateToHistory = { navController.navigate("apply_history") }
                    )
                }

                // ─── CHI TIẾT ─────────────────────────────────────────
                composable(
                    route = "job_detail/{jobId}",
                    arguments = listOf(navArgument("jobId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                    JobDetailScreen(jobId = jobId, onBack = { navController.popBackStack() })
                }

                composable(
                    route = "company_detail/{companyId}",
                    arguments = listOf(navArgument("companyId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val companyId = backStackEntry.arguments?.getString("companyId") ?: ""
                    CompanyDetailScreen(
                        companyId = companyId, 
                        onBack = { navController.popBackStack() },
                        onJobClick = { jobId -> navController.navigate("job_detail/$jobId") }
                    )
                }

                composable("company_list") {
                    CompanyListScreen(
                        onBackClick = { navController.popBackStack() },
                        onCompanyClick = { companyId -> navController.navigate("company_detail/$companyId") }
                    )
                }

                composable("apply_history") {
                    ApplyHistoryScreen(onBack = { navController.popBackStack() })
                }
            }

            // ─── Lớp trên: Thanh Bottom Nav (Dynamic Island) ────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding() 
                    .padding(bottom = 10.dp, start = 48.dp, end = 48.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(68.dp),
                    shape = RoundedCornerShape(34.dp),
                    color = AppColors.TextPrimary,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        bottomNavItems.forEach { item ->
                            val currentRoute = currentDestination?.route
                            val isSelected = when (item.route) {
                                BottomNavItem.Home.route -> {
                                    currentRoute == BottomNavItem.Home.route ||
                                    currentRoute?.startsWith("company_detail") == true ||
                                    currentRoute?.startsWith("company_jobs") == true ||
                                    currentRoute == "company_list"
                                }
                                BottomNavItem.Jobs.route -> {
                                    currentRoute == BottomNavItem.Jobs.route ||
                                    currentRoute?.startsWith("job_detail") == true
                                }
                                BottomNavItem.Profile.route -> {
                                    currentRoute == BottomNavItem.Profile.route ||
                                    currentRoute == "apply_history"
                                }
                                else -> currentDestination?.hierarchy?.any { it.route == item.route } == true
                            }
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = if (isSelected) AppColors.AccentBlue else Color.White.copy(alpha = 0.5f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier.size(4.dp).clip(CircleShape).background(AppColors.AccentBlue)
                                        )
                                    } else {
                                        Text(text = item.label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
