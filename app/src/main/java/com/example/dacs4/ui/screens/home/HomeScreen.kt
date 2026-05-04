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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
        when {
            hour < 12 -> "Chào buổi sáng ☀️"
            hour < 18 -> "Chào buổi chiều 🌤"
            else      -> "Chào buổi tối 🌙"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = greeting,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = AppColors.TextPrimary
                        )
                        Text(
                            text = "Tìm công việc phù hợp với bạn",
                            fontSize = 11.sp,
                            color = AppColors.TextSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AppColors.BgSurface)
                                .border(0.5.dp, AppColors.Border, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = "Đăng xuất",
                                tint = AppColors.TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.BgPrimary
                )
            )
        },
        containerColor = AppColors.BgPrimary
    ) { padding ->
        when (val state = uiState) {

            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
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

            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text("😕", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.message,
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

            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {

                    // ─── Search Bar hint ──────────────────────────
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColors.BgSurface)
                                .border(0.5.dp, AppColors.Border, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = AppColors.TextHint,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    "Tìm kiếm công việc, công ty...",
                                    fontSize = 14.sp,
                                    color = AppColors.TextHint
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
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 10.dp),
                        ) {
                            items(state.companies) { company ->
                                CompanyCard(
                                    company = company,
                                    onClick = { onCompanyClick(company.id) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // ─── Divider ──────────────────────────────────
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            thickness = 0.5.dp,
                            color = AppColors.Border
                        )
                    }

                    // ─── Section: Việc làm mới nhất ───────────────
                    item {
                        SectionHeader(
                            title = "Việc làm mới nhất",
                            actionLabel = "Xem tất cả",
                            onAction = onViewAllJobsClick
                        )
                    }

                    items(state.jobs) { job ->
                        JobCard(
                            job = job,
                            onClick = { onJobClick(job.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
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
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = AppColors.TextPrimary
        )
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