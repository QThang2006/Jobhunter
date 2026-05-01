package com.example.dacs4.ui.screens.job

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dacs4.core.utils.AppConstants
import com.example.dacs4.ui.components.JobCard
import com.example.dacs4.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobListScreen(
    onBackClick: () -> Unit,
    onJobClick: (String) -> Unit,
    viewModel: JobListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchTextField by remember { mutableStateOf(TextFieldValue(uiState.searchQuery)) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var tempLocation by remember { mutableStateOf(uiState.locationFilter) }
    var tempSkills   by remember { mutableStateOf(uiState.skillsFilter) }
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
                        "Danh sách việc làm",
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
                actions = {
                    // Filter icon with badge if filter active
                    val hasFilter = uiState.locationFilter.isNotBlank() || uiState.skillsFilter.isNotBlank()
                    BadgedBox(
                        badge = {
                            if (hasFilter) Badge(containerColor = AppColors.AccentBlue)
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        IconButton(onClick = {
                            tempLocation = uiState.locationFilter
                            tempSkills   = uiState.skillsFilter
                            showBottomSheet = true
                        }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Bộ lọc",
                                tint = AppColors.TextPrimary
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
                        Text("Tên công việc...", color = AppColors.TextHint, fontSize = 14.sp)
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { viewModel.performSearch() }),
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
                    modifier = Modifier
                        .height(56.dp)
                        .width(56.dp),
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

            // ─── Active filter chips ───────────────────────────
            if (uiState.locationFilter.isNotBlank() || uiState.skillsFilter.isNotBlank()) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiState.locationFilter.isNotBlank()) {
                        ActiveFilterChip(
                            label = AppConstants.LOCATION_LIST
                                .find { it.first == uiState.locationFilter }?.second
                                ?: uiState.locationFilter,
                            onRemove = { viewModel.applyFilters("", uiState.skillsFilter) }
                        )
                    }
                    if (uiState.skillsFilter.isNotBlank()) {
                        ActiveFilterChip(
                            label = uiState.skillsFilter,
                            onRemove = { viewModel.applyFilters(uiState.locationFilter, "") }
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = AppColors.Border)

            // ─── List ─────────────────────────────────────────
            when {
                uiState.isLoading && uiState.jobs.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = AppColors.AccentBlue,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                !uiState.isLoading && uiState.error != null && uiState.jobs.isEmpty() -> {
                    EmptyState(message = "Lỗi: ${uiState.error}")
                }
                !uiState.isLoading && uiState.jobs.isEmpty() -> {
                    EmptyState(message = "Rất tiếc! Không có việc làm nào phù hợp.")
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
                    ) {
                        items(uiState.jobs, key = { it.id }) { job ->
                            JobCard(job = job, onClick = { onJobClick(job.id) })
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
                        } else if (!uiState.hasMoreData && uiState.jobs.isNotEmpty()) {
                            item {
                                Box(
                                    Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Đã hiển thị tất cả kết quả",
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

    // ─── Filter Bottom Sheet ──────────────────────────────────
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = AppColors.BgPrimary,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AppColors.Border)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 36.dp)
            ) {
                Text(
                    "Bộ lọc nâng cao",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AppColors.TextPrimary
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    thickness = 0.5.dp,
                    color = AppColors.Border
                )

                Text(
                    "Địa điểm",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                var locationExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = locationExpanded,
                    onExpandedChange = { locationExpanded = !locationExpanded }
                ) {
                    OutlinedTextField(
                        value = if (tempLocation.isEmpty()) "Tất cả địa điểm"
                        else AppConstants.LOCATION_LIST.find { it.first == tempLocation }?.second ?: tempLocation,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = AppColors.AccentBlue,
                            unfocusedBorderColor = AppColors.Border,
                            focusedTextColor     = AppColors.TextPrimary,
                            unfocusedTextColor   = AppColors.TextSecondary,
                            focusedContainerColor   = AppColors.BgPrimary,
                            unfocusedContainerColor = AppColors.BgSurface,
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = locationExpanded,
                        onDismissRequest = { locationExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tất cả", color = AppColors.TextPrimary) },
                            onClick = { tempLocation = ""; locationExpanded = false }
                        )
                        AppConstants.LOCATION_LIST.forEach { (code, name) ->
                            DropdownMenuItem(
                                text = { Text(name, color = AppColors.TextPrimary) },
                                onClick = { tempLocation = code; locationExpanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Kỹ năng",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                OutlinedTextField(
                    value = tempSkills,
                    onValueChange = { tempSkills = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ví dụ: Java, React, Spring...", color = AppColors.TextHint) },
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

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.applyFilters(location = tempLocation, skills = tempSkills)
                        showBottomSheet = false
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentBlue),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text(
                        "Áp dụng bộ lọc",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ─── Helper composables ──────────────────────────────────────────────────────
@Composable
private fun ActiveFilterChip(label: String, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AppColors.AccentBlueLight)
            .border(0.5.dp, AppColors.AccentBlueMid, RoundedCornerShape(20.dp))
            .clickable { onRemove() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = "$label ✕",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.AccentBlue
        )
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, fontSize = 14.sp, color = AppColors.TextSecondary)
        }
    }
}
