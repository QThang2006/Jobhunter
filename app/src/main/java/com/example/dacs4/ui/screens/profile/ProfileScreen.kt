package com.example.dacs4.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dacs4.ui.theme.AppColors

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val updateMsg by viewModel.updateState.collectAsStateWithLifecycle()
    val pwMsg by viewModel.changePasswordState.collectAsStateWithLifecycle()

    var showEditSheet by remember { mutableStateOf(false) }
    var showPasswordSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar khi có message
    LaunchedEffect(updateMsg) {
        updateMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeUpdateState()
        }
    }
    LaunchedEffect(pwMsg) {
        pwMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumePasswordState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppColors.BgPrimary
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ─── Header ─────────────────────────────────────────────
            val userResponse = (uiState as? ProfileUiState.Success)?.user
            val name  = userResponse?.name ?: viewModel.getUserName()
            val email = userResponse?.email ?: viewModel.getUserEmail()
            val role  = userResponse?.role?.name ?: "USER"
            val age = userResponse?.age?.toString() ?: "Chưa rõ"
            val gender = userResponse?.gender ?: "Chưa rõ"
            val displayGender = when (gender.uppercase()) {
                "MALE" -> "Nam"
                "FEMALE" -> "Nữ"
                "OTHER" -> "Khác"
                else -> gender
            }
            val address = userResponse?.address ?: "Chưa có địa chỉ"

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(AppColors.BgAccentLight, AppColors.BgPrimary)
                        )
                    )
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar circle
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(AppColors.AccentBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.firstOrNull()?.uppercase() ?: "U",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                    Spacer(Modifier.height(4.dp))
                    Text(email, fontSize = 13.sp, color = AppColors.TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    // Role badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AppColors.AccentBlueLight)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(role, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AppColors.AccentBlue)
                    }
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = AppColors.Border)
            
            // ─── User Info Details ───────────────────────────────────
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.BgSurface)
                    .border(0.5.dp, AppColors.Border, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "Thông tin chi tiết",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AppColors.TextPrimary
                )
                Spacer(Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Tuổi:", color = AppColors.TextSecondary, fontSize = 14.sp)
                    Text(age, color = AppColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp, color = AppColors.Border)
                Spacer(Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Giới tính:", color = AppColors.TextSecondary, fontSize = 14.sp)
                    Text(displayGender, color = AppColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp, color = AppColors.Border)
                Spacer(Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Địa chỉ:", color = AppColors.TextSecondary, fontSize = 14.sp)
                    Text(
                        address,
                        color = AppColors.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        modifier = Modifier.weight(1f).padding(start = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            // ─── Menu Items ──────────────────────────────────────────
            Text(
                "Cài đặt & Tiện ích",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = AppColors.TextPrimary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            Spacer(Modifier.height(8.dp))

            ProfileMenuItem(
                icon = Icons.Default.Edit,
                label = "Thông tin cá nhân",
                onClick = { showEditSheet = true }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = AppColors.Border)

            ProfileMenuItem(
                icon = Icons.Default.List,
                label = "Lịch sử ứng tuyển",
                onClick = onNavigateToHistory
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = AppColors.Border)

            ProfileMenuItem(
                icon = Icons.Default.Lock,
                label = "Đổi mật khẩu",
                onClick = { showPasswordSheet = true }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = AppColors.Border)

            Spacer(Modifier.height(16.dp))

            // ─── Logout ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLogoutDialog = true }
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = AppColors.Error, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Text("Đăng xuất", color = AppColors.Error, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
            
            // Khoảng trống dưới để nổi lên trên Bottom Nav
            Spacer(Modifier.height(110.dp))
        }
    }

    // ─── Sheets & Dialogs ────────────────────────────────────────────
    if (showEditSheet && uiState is ProfileUiState.Success) {
        ProfileEditSheet(
            user = (uiState as ProfileUiState.Success).user,
            onDismiss = { showEditSheet = false },
            onSave = { name, age, gender, address ->
                viewModel.updateUser(name, age, gender, address)
                showEditSheet = false
            }
        )
    }

    if (showPasswordSheet) {
        ChangePasswordSheet(
            onDismiss = { showPasswordSheet = false },
            onSave = { old, new ->
                viewModel.changePassword(old, new)
                showPasswordSheet = false
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Đăng xuất", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc muốn đăng xuất khỏi tài khoản?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Đăng xuất", color = AppColors.Error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Huỷ")
                }
            }
        )
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = AppColors.TextSecondary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, fontSize = 15.sp, color = AppColors.TextPrimary)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AppColors.TextHint)
    }
}
