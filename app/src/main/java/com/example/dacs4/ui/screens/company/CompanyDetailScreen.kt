package com.example.dacs4.ui.screens.company

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
import com.example.dacs4.core.utils.AppConstants
import com.example.dacs4.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailScreen(
    companyId: String,
    onBack: () -> Unit,
    viewModel: CompanyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(companyId) { viewModel.fetchCompanyDetail(companyId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chi tiết công ty",
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
            is CompanyDetailUiState.Loading -> {
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
            is CompanyDetailUiState.Error -> {
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
            is CompanyDetailUiState.Success -> {
                val company = state.company
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
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
                                    model = if (company.logo != null)
                                        "${AppConstants.IMAGE_BASE_URL}${company.logo}"
                                    else null,
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
                            label = "Trạng thái",
                            value = "Đang hoạt động",
                            valueColor = AppColors.Success,
                            modifier = Modifier.weight(1f)
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

                    Spacer(Modifier.height(32.dp))
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
