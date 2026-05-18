package com.example.dacs4.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dacs4.ui.components.CompanyCard
import com.example.dacs4.ui.components.JobCard
import com.example.dacs4.ui.theme.AppColors
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onJobClick: (String) -> Unit = {},
    onCompanyClick: (String) -> Unit = {},
    onViewAllCompaniesClick: () -> Unit = {},
    onViewAllJobsClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ── Time-based greeting ──────────────────────────────────────────────
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val name = viewModel.userName
        when {
            hour < 12 -> "Chào buổi sáng, $name ☀️"
            hour < 18 -> "Chào buổi chiều, $name 🌤"
            else      -> "Chào buổi tối, $name 🌙"
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.BgPrimary)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    val greeting = when {
                        hour < 12 -> "Chào buổi sáng"
                        hour < 18 -> "Chào buổi chiều"
                        else      -> "Chào buổi tối"
                    }
                    Text(
                        text = "$greeting, ${viewModel.userName} 👋",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppColors.TextPrimary,
                        letterSpacing = (-0.4).sp
                    )
                    Text(
                        text = "Khám phá cơ hội mới ngay hôm nay",
                        fontSize = 12.sp,
                        color = AppColors.TextSecondary
                    )
                }

                // Logout Action
                IconButton(
                    onClick = {
                        viewModel.logout()
                        onLogout()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = "Đăng xuất",
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        containerColor = AppColors.BgPrimary
    ) { padding ->
        val state = uiState

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // ─── Case: First Loading (no data yet) ───────────────────────────
            if (state.isLoading && state.jobs.isEmpty() && state.companies.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = AppColors.AccentBlue,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Đang tải dữ liệu...",
                            fontSize = 13.sp,
                            color = AppColors.TextSecondary
                        )
                    }
                }
            } 
            // ─── Case: Error (and no data) ──────────────────────────────────
            else if (state.error != null && state.jobs.isEmpty() && state.companies.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text("😕", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.error,
                            fontSize = 14.sp,
                            color = AppColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.fetchHomeData() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.AccentBlue
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Thử lại", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            // ─── Case: Success (or Loading/Error with existing data) ──────────
            else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // ─── Premium Skills Filter Chips ─────────────────────
                    item {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(state.skills) { skill ->
                                PremiumSkillChip(
                                    label = skill.name,
                                    isSelected = state.selectedSkill == skill.name,
                                    onClick = { viewModel.onSkillSelected(skill.name) }
                                )
                            }
                        }
                    }

                    // ─── Section: Công ty nổi bật ─────────────────
                    item {
                        SectionHeader(
                            title = "Công ty nổi bật",
                            actionLabel = "Xem tất cả",
                            onAction = onViewAllCompaniesClick
                        )
                        Spacer(modifier = Modifier.height(12.dp)) // Title -> Content
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(state.companies) { company ->
                                CompanyCard(
                                    company = company,
                                    onClick = { onCompanyClick(company.id) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp)) // Section -> Section
                    }

                    // ─── Section: Việc làm mới nhất ───────────────
                    item {
                        SectionHeader(
                            title = "Việc làm mới nhất",
                            actionLabel = "Xem tất cả",
                            onAction = onViewAllJobsClick
                        )
                        Spacer(modifier = Modifier.height(12.dp)) // Title -> Content
                    }

                    items(state.jobs) { job ->
                        JobCard(
                            job = job,
                            onClick = { onJobClick(job.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(120.dp)) }
                }
            }
        }
    }
}

// ─── Premium Skill Chip Component ───────────────────────────────────────────
@Composable
private fun PremiumSkillChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animations
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "scale"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) AppColors.AccentBlue.copy(alpha = 0.12f) else AppColors.BgSurface,
        label = "containerColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) AppColors.AccentBlue else AppColors.TextSecondary,
        label = "contentColor"
    )
    val fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium

    Surface(
        modifier = Modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        color = containerColor,
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = fontWeight,
                letterSpacing = 0.2.sp
            )
        }
    }
}

// ─── Reusable Section Header ──────────────────────────────────────────────────
@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = AppColors.TextPrimary,
            letterSpacing = (-0.3).sp
        )
        if (actionLabel.isNotEmpty()) {
            TextButton(onClick = onAction, contentPadding = PaddingValues(0.dp)) {
                Text(
                    text = actionLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.AccentBlue
                )
            }
        }
    }
}
