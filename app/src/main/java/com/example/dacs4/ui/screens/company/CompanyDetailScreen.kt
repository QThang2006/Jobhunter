package com.example.dacs4.ui.screens.company

import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.example.dacs4.core.utils.AppConstants
import com.example.dacs4.ui.components.JobCard
import com.example.dacs4.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailScreen(
    companyId: String,
    onBack: () -> Unit,
    onJobClick: (String) -> Unit,
    viewModel: CompanyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(companyId) { viewModel.fetchCompanyDetail(companyId) }

    Scaffold(
        containerColor = AppColors.BgPrimary
    ) { padding ->
        val state = uiState
        Box(modifier = Modifier.fillMaxSize()) {
            // ─── Case: Loading (first time, no data) ─────────────────────────
            if (state.isLoading && state.company == null) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AppColors.AccentBlue,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            // ─── Case: Error (and no data) ──────────────────────────────────
            else if (state.error != null && state.company == null) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😕", fontSize = 40.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(state.error, fontSize = 14.sp, color = AppColors.TextSecondary)
                    }
                }
            }
            // ─── Case: Data (or loading with existing data) ──────────────────
            else if (state.company != null) {
                val company = state.company
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                ) {
                    // ─── Hero Area ────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColors.BgAccentLight)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Logo large
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.BgPrimary)
                                    .border(1.dp, AppColors.Border, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    // Sửa ở đây: Sử dụng trực tiếp logo từ backend
                                    model = company.logo,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(76.dp).clip(CircleShape)
                                )
                            }

                            Spacer(Modifier.height(14.dp))

                            Text(
                                text = company.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = AppColors.TextPrimary
                            )

                            if (!company.address.isNullOrBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = AppColors.AccentBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        company.address,
                                        fontSize = 13.sp,
                                        color = AppColors.TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // ─── Stats badges ─────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CompanyBadge(
                            label = "Ngày tạo",
                            value = company.createdAt?.substringBefore("T") ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                        CompanyBadge(
                            label = "Số lượng job",
                            value = "${state.jobCount} công việc",
                            valueColor = AppColors.AccentBlue,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { 
                                    scope.launch {
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = AppColors.Border
                    )

                    // ─── About section ────────────────────────────
                    Spacer(Modifier.height(20.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            "Giới thiệu công ty",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = AppColors.TextPrimary
                        )
                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColors.BgSurface)
                                .border(0.5.dp, AppColors.Border, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            AndroidView(
                                factory = { ctx ->
                                    TextView(ctx).apply {
                                        setTextColor(android.graphics.Color.parseColor("#374151"))
                                        textSize = 14f
                                        setLineSpacing(6f, 1.3f)
                                    }
                                },
                                update = { tv ->
                                    tv.text = HtmlCompat.fromHtml(
                                        company.description
                                            ?: "Hiện chưa có thông tin mô tả cho công ty này.",
                                        HtmlCompat.FROM_HTML_MODE_COMPACT
                                    )
                                }
                            )
                        }
                    }

                    // ─── Jobs section ────────────────────────────
                    if (state.jobs.isNotEmpty()) {
                        Spacer(Modifier.height(32.dp))
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                "Việc làm tại công ty",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = AppColors.TextPrimary
                            )
                            Spacer(Modifier.height(12.dp))
                            state.jobs.forEach { job ->
                                JobCard(
                                    job = job,
                                    onClick = { onJobClick(job.id) }
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // Thêm khoảng trống ở dưới để nổi lên trên Bottom Nav
                    Spacer(Modifier.height(110.dp))
                }

                // Floating Back Button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(top = 12.dp, start = 12.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Trở về",
                        tint = AppColors.TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ─── Badge component ─────────────────────────────────────────────────────────
@Composable
private fun CompanyBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = AppColors.TextPrimary
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AppColors.BgSurface)
            .border(0.5.dp, AppColors.Border, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Text(label, fontSize = 11.sp, color = AppColors.TextHint)
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}
