package com.example.dacs4.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home    : BottomNavItem("home_tab",    Icons.Default.Home,   "Trang chủ")
    object Jobs    : BottomNavItem("jobs_tab",    Icons.Default.Work,   "Việc làm")
    object Profile : BottomNavItem("profile_tab", Icons.Default.Person, "Hồ sơ")
}
