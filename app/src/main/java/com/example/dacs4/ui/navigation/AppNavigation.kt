package com.example.dacs4.ui.navigation

import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.ui.components.GradientDivider
import com.example.dacs4.ui.screens.application.*
import com.example.dacs4.ui.screens.auth.LoginScreen
import com.example.dacs4.ui.screens.auth.RegisterScreen
import com.example.dacs4.ui.screens.company.CompanyDetailScreen
import com.example.dacs4.ui.screens.company.CompanyListScreen
import com.example.dacs4.ui.screens.home.JobDetailScreen
import com.example.dacs4.ui.screens.home.JobListScreen
import com.example.dacs4.ui.screens.home.sampleJobs
import com.example.dacs4.ui.screens.profile.EditProfileScreen
import com.example.dacs4.ui.screens.profile.ProfileScreen
import com.example.dacs4.ui.screens.splash.SplashScreen
import com.example.dacs4.ui.theme.*

// ═══════════════════════════════════════════════════════════════════
//  APP NAVIGATION — Phase 1 + 2 + 3
// ═══════════════════════════════════════════════════════════════════

object Routes {
    // Phase 1
    const val SPLASH         = "splash"
    const val LOGIN          = "login"
    const val REGISTER       = "register"
    const val HOME           = "home"
    const val JOB_DETAIL     = "job_detail/{jobId}"

    // Phase 2
    const val APPLY_JOB       = "apply_job/{jobId}/{jobName}/{companyName}"
    const val APPLY_SUCCESS   = "apply_success/{applicationId}/{jobName}/{companyName}"
    const val MY_APPLICATIONS = "my_applications"
    const val APP_DETAIL      = "application_detail/{applicationId}"

    // Phase 3
    const val PROFILE         = "profile"
    const val EDIT_PROFILE    = "edit_profile"
    const val COMPANY_LIST    = "company_list"
    const val COMPANY_DETAIL  = "company_detail/{companyId}"

    // Builders
    fun jobDetail(jobId: String)     = "job_detail/$jobId"
    fun applyJob(jobId: String, jobName: String, companyName: String) =
        "apply_job/$jobId/${jobName.enc()}/${companyName.enc()}"
    fun applySuccess(appId: String, jobName: String, companyName: String) =
        "apply_success/$appId/${jobName.enc()}/${companyName.enc()}"
    fun appDetail(id: String)        = "application_detail/$id"
    fun companyDetail(id: String)    = "company_detail/$id"

    private fun String.enc() = java.net.URLEncoder.encode(this, "UTF-8")
    fun String.dec()         = java.net.URLDecoder.decode(this, "UTF-8")

    // Bottom nav tabs
    val bottomNavRoutes = listOf(HOME, MY_APPLICATIONS, COMPANY_LIST, PROFILE)
}

// ──────────────────────────────────────────────────────────────────
//  BOTTOM NAV DATA
// ──────────────────────────────────────────────────────────────────

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME,            Icons.Outlined.Home,        "Trang chủ"),
    BottomNavItem(Routes.COMPANY_LIST,    Icons.Outlined.Business,    "Công ty"),
    BottomNavItem(Routes.MY_APPLICATIONS, Icons.Outlined.WorkHistory,  "Đơn của tôi"),
    BottomNavItem(Routes.PROFILE,         Icons.Outlined.Person,      "Hồ sơ")
)

@Composable
fun AppNavigation(tokenManager: TokenManager) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()

    val currentRoute: String? = currentBackStack?.destination?.route

    val showBottomNav = currentRoute in Routes.bottomNavRoutes

    Scaffold(
        containerColor = JHColors.Background,
        bottomBar = {
            if (showBottomNav) {
                JobHunterBottomNav(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) { it } + fadeIn(tween(300))
            },

                    exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) { -it / 3 } + fadeOut(tween(300))
            },

                    popEnterTransition = {
                slideInHorizontally(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) { -it / 3 } + fadeIn(tween(300))
            },

                    popExitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) { it } + fadeOut(tween(300))
            }
        ) {

            // ══════════════════════════════════════════════════════
            //  PHASE 1
            // ══════════════════════════════════════════════════════

            composable(Routes.SPLASH,
                enterTransition = { fadeIn(tween(300)) },
                exitTransition  = { fadeOut(tween(500)) }
            ) {
                SplashScreen(onNavigateToLogin = {
                    val dest = if (tokenManager.isLoggedIn()) Routes.HOME else Routes.LOGIN
                    navController.navigate(dest) { popUpTo(Routes.SPLASH) { inclusive = true } }
                })
            }

            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    }
                )
            }

            composable(Routes.HOME) {
                JobListScreen(
                    onJobClick = { navController.navigate(Routes.jobDetail(it)) },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) { popUpTo(Routes.HOME) { inclusive = true } }
                    }
                )
            }

            composable(
                route = Routes.JOB_DETAIL,
                arguments = listOf(navArgument("jobId") { type = NavType.StringType })
            ) { entry ->
                val jobId = entry.arguments?.getString("jobId") ?: ""
                val job = sampleJobs.find { it.id == jobId } ?: sampleJobs.first()
                JobDetailScreen(
                    jobId = jobId,
                    onBack = { navController.popBackStack() },
                    onApply = { navController.navigate(Routes.applyJob(jobId, job.title, job.company)) }
                )
            }

            // ══════════════════════════════════════════════════════
            //  PHASE 2
            // ══════════════════════════════════════════════════════

            composable(Routes.APPLY_JOB, arguments = listOf(
                navArgument("jobId")       { type = NavType.StringType },
                navArgument("jobName")     { type = NavType.StringType },
                navArgument("companyName") { type = NavType.StringType }
            )) { entry ->
                val jobId       = entry.arguments?.getString("jobId") ?: ""
                val jobName = entry.arguments?.getString("jobName")?.let {
                    java.net.URLDecoder.decode(it, "UTF-8")
                } ?: ""
                val companyName = entry.arguments?.getString("companyName")?.let {
                    java.net.URLDecoder.decode(it, "UTF-8")
                } ?: ""
                ApplyJobScreen(
                    jobId = jobId, jobName = jobName, companyName = companyName,
                    onBack = { navController.popBackStack() },
                    onSuccess = { appId ->
                        navController.navigate(Routes.applySuccess(appId, jobName, companyName)) {
                            popUpTo(Routes.APPLY_JOB) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.APPLY_SUCCESS, arguments = listOf(
                navArgument("applicationId") { type = NavType.StringType },
                navArgument("jobName")       { type = NavType.StringType },
                navArgument("companyName")   { type = NavType.StringType }
            ), enterTransition = { fadeIn(tween(500)) }, exitTransition = { fadeOut(tween(300)) }) { entry ->
                ApplicationSuccessScreen(
                    applicationId = entry.arguments?.getString("applicationId") ?: "",
                    jobName = entry.arguments?.getString("jobName")?.let {
                        java.net.URLDecoder.decode(it, "UTF-8")
                    } ?: "",
                    companyName = entry.arguments?.getString("companyName")?.let {
                        java.net.URLDecoder.decode(it, "UTF-8")
                    } ?: "",
                    onViewMyApplications = {
                        navController.navigate(Routes.MY_APPLICATIONS) { popUpTo(Routes.HOME) }
                    },
                    onBackToHome = {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                    }
                )
            }

            composable(Routes.MY_APPLICATIONS) {
                MyApplicationsScreen(
                    onBack = { navController.popBackStack() },
                    onApplicationClick = { navController.navigate(Routes.appDetail(it)) }
                )
            }

            composable(Routes.APP_DETAIL,
                arguments = listOf(navArgument("applicationId") { type = NavType.StringType })
            ) { entry ->
                ApplicationDetailScreen(
                    applicationId = entry.arguments?.getString("applicationId") ?: "",
                    onBack = { navController.popBackStack() },
                    onWithdrawSuccess = { navController.popBackStack() }
                )
            }

            // ══════════════════════════════════════════════════════
            //  PHASE 3
            // ══════════════════════════════════════════════════════

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onEditProfile    = { navController.navigate(Routes.EDIT_PROFILE) },
                    onMyApplications = { navController.navigate(Routes.MY_APPLICATIONS) },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) { popUpTo(Routes.HOME) { inclusive = true } }
                    }
                )
            }

            composable(Routes.EDIT_PROFILE) {
                EditProfileScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.COMPANY_LIST) {
                CompanyListScreen(
                    onBack          = { navController.popBackStack() },
                    onCompanyClick  = { navController.navigate(Routes.companyDetail(it)) }
                )
            }

            composable(Routes.COMPANY_DETAIL,
                arguments = listOf(navArgument("companyId") { type = NavType.StringType })
            ) { entry ->
                val companyId = entry.arguments?.getString("companyId") ?: ""
                CompanyDetailScreen(
                    companyId  = companyId,
                    onBack     = { navController.popBackStack() },
                    onJobClick = { jobId -> navController.navigate(Routes.jobDetail(jobId)) }
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  BOTTOM NAVIGATION BAR
// ──────────────────────────────────────────────────────────────────

@Composable
fun JobHunterBottomNav(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Column {
        GradientDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(JHColors.Background.copy(alpha = 0.97f))
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.route
                BottomNavTab(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onNavigate(item.route) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavTab(item: BottomNavItem, isSelected: Boolean, onClick: () -> Unit) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) JHColors.AccentPrimary else JHColors.TextMuted,
        animationSpec = tween(200), label = "nav_icon_color"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "nav_scale"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Selected indicator pill
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(48.dp)
                        .clip(RoundedCornerShape(JHRadius.lg.dp))
                        .background(JHColors.AccentPrimary.copy(alpha = 0.12f))
                )
            }
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = item.label,
            style = JHTypography.LabelS,
            color = iconColor
        )
    }
}
