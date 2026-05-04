package com.example.dacs4.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.dacs4.core.utils.AppConstants
import com.example.dacs4.data.model.response.CompanyResponse
import com.example.dacs4.ui.theme.AppColors
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyListScreen(
    onBackClick: () -> Unit,
    onCompanyClick: (String) -> Unit,
    viewModel: CompanyListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchTextField by remember { mutableStateOf(TextFieldValue(uiState.searchQuery)) }
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val total = info.totalItemsCount
            val last  = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            total > 0 && last >= total - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && uiState.hasMoreData && !uiState.isLoading && !uiState.isFetchingNextPage) {
            viewModel.fetchNextPage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Danh sách công ty",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = AppColors.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ─── Search Bar ───────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchTextField,
                    onValueChange = {
                        searchTextField = it
                        viewModel.onSearchQueryChanged(it.text)
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text("Tên công ty...", color = AppColors.TextHint, fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = AppColors.TextHint,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = AppColors.AccentBlue,
                        unfocusedBorderColor = AppColors.Border,
                        focusedTextColor     = AppColors.TextPrimary,
                        unfocusedTextColor   = AppColors.TextPrimary,
                        cursorColor          = AppColors.AccentBlue,
                        focusedContainerColor   = AppColors.BgPrimary,
                        unfocusedContainerColor = AppColors.BgSurface,
                    )
                )
                Button(
                    onClick = { viewModel.performSearch() },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentBlue),
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Tìm",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = AppColors.Border)

            // ─── List states ──────────────────────────────────
            when {
                uiState.isLoading && uiState.companies.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = AppColors.AccentBlue,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                !uiState.isLoading && uiState.error != null && uiState.companies.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Lỗi: ${uiState.error}", fontSize = 14.sp, color = AppColors.TextSecondary)
                    }
                }
                !uiState.isLoading && uiState.companies.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏢", fontSize = 40.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("Không tìm thấy công ty nào.", fontSize = 14.sp, color = AppColors.TextSecondary)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        items(uiState.companies, key = { it.id }) { company ->
                            CompanyRowItem(
                                company = company,
                                onClick = { onCompanyClick(company.id) }
                            )
                        }
                        if (uiState.isFetchingNextPage) {
                            item {
                                Box(
                                    Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = AppColors.AccentBlue,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        } else if (!uiState.hasMoreData && uiState.companies.isNotEmpty()) {
                            item {
                                Box(
                                    Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Đã hiển thị tất cả công ty",
                                        fontSize = 12.sp,
                                        color = AppColors.TextHint
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Row item (list-style, cleaner than card grid) ───────────────────────────
@Composable
private fun CompanyRowItem(company: CompanyResponse, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(AppColors.BgPrimary)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Logo with text fallback
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(AppColors.BgSurface)
                .border(0.5.dp, AppColors.Border, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val logoUrl = if (company.logo != null)
                "${AppConstants.IMAGE_BASE_URL}${company.logo}"
            else null
            if (logoUrl != null) {
                SubcomposeAsyncImage(
                    model = logoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(44.dp).clip(CircleShape)
                ) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
                        else -> CompanyInitialText(company.name)
                    }
                }
            } else {
                CompanyInitialText(company.name)
            }
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                company.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = AppColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!company.address.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    company.address,
                    fontSize = 12.sp,
                    color = AppColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Chevron hint
        Text("›", fontSize = 20.sp, color = AppColors.TextHint)
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 82.dp, end = 16.dp),
        thickness = 0.5.dp,
        color = AppColors.Border
    )
}

// ─── Text-based logo fallback ─────────────────────────────────────────────────
@Composable
private fun CompanyInitialText(name: String) {
    val initial = name.firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(AppColors.AccentBlueLight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.AccentBlue
        )
    }
}

