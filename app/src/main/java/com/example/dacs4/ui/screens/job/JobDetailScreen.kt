package com.example.dacs4.ui.screens.job

import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.dacs4.core.utils.AppConstants
import com.example.dacs4.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: String,
    onBack: () -> Unit,
    viewModel: JobDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(jobId) { viewModel.fetchJobDetail(jobId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chi tiết công việc",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = AppColors.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Trở về",
                            tint = AppColors.TextPrimary
                        )
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
            is JobDetailUiState.Loading -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AppColors.AccentBlue,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            is JobDetailUiState.Error -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😕", fontSize = 40.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(state.message, fontSize = 14.sp, color = AppColors.TextSecondary)
                    }
                }
            }
            is JobDetailUiState.Success -> {
                val job = state.job
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // ─── Hero Area: centered + gradient ──────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        AppColors.BgAccentLight,
                                        AppColors.BgAccentLighter
                                    )
                                )
                            )
                            .padding(horizontal = 24.dp, vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Company logo 80dp — centered with fallback
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(AppColors.BgPrimary)
                                    .border(0.5.dp, AppColors.Border, RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                val logoUrl = if (job.company?.logo != null)
                                    "${AppConstants.IMAGE_BASE_URL}${job.company.logo}"
                                else null
                                if (logoUrl != null) {
                                    SubcomposeAsyncImage(
                                        model = logoUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(66.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                    ) {
                                        when (painter.state) {
                                            is AsyncImagePainter.State.Success ->
                                                SubcomposeAsyncImageContent()
                                            else -> HeroLogoFallback(job.company?.name)
                                        }
                                    }
                                } else {
                                    HeroLogoFallback(job.company?.name)
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            Text(
                                text = job.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = AppColors.TextPrimary,
                                lineHeight = 26.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = job.company?.name ?: "",
                                fontSize = 13.sp,
                                color = AppColors.TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }


                    Spacer(Modifier.height(20.dp))

                    // ─── Info Grid (2 x 2) ────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        JobInfoCard(
                            label  = "Mức lương",
                            value  = AppConstants.formatSalary(job.salary),
                            valueColor = AppColors.AccentBlue,
                            modifier = Modifier.weight(1f)
                        )
                        JobInfoCard(
                            label  = "Địa điểm",
                            value  = AppConstants.formatLocation(job.location),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        JobInfoCard(
                            label  = "Cấp độ",
                            value  = job.level,
                            modifier = Modifier.weight(1f)
                        )
                        JobInfoCard(
                            label  = "Số lượng",
                            value  = "${job.quantity} vị trí",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // ─── Skills ───────────────────────────────────
                    if (!job.skills.isNullOrEmpty()) {
                        Spacer(Modifier.height(24.dp))
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                "Kỹ năng yêu cầu",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = AppColors.TextPrimary
                            )
                            Spacer(Modifier.height(10.dp))
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                job.skills.forEach { skill ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(AppColors.AccentBlueLight)
                                            .border(0.5.dp, AppColors.AccentBlueMid, RoundedCornerShape(20.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            skill.name,
                                            color = AppColors.AccentBlue,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ─── Description ─────────────────────────────
                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = AppColors.Border
                    )
                    Spacer(Modifier.height(20.dp))

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            "Mô tả công việc",
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
                                        setLineSpacing(6f, 1.2f)
                                    }
                                },
                                update = { tv ->
                                    tv.text = HtmlCompat.fromHtml(
                                        job.description ?: "Không có mô tả chi tiết.",
                                        HtmlCompat.FROM_HTML_MODE_COMPACT
                                    )
                                }
                            )
                        }
                    }

                    // ─── CTA Button ───────────────────────────────
                    Spacer(Modifier.height(32.dp))
                    Box(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                        Button(
                            onClick = { /* TODO: Apply flow */ },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.AccentBlue
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text(
                                "Ứng tuyển ngay",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

// ─── Info Card component ─────────────────────────────────────────────────────
@Composable
fun JobInfoCard(
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
            color = valueColor,
            maxLines = 1
        )
    }
}

// ─── Hero logo fallback — shown when logo is null or fails to load ────────────
@Composable
private fun HeroLogoFallback(name: String?) {
    val initial = name?.firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = Modifier
            .size(66.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
            .background(AppColors.AccentBlueLight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.AccentBlue
        )
    }
}

