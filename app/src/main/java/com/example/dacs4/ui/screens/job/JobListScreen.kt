package com.example.dacs4.ui.screens.job

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Checkbox
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.graphics.vector.ImageVector
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
import kotlinx.coroutines.launch
import com.example.dacs4.core.utils.AppConstants
import com.example.dacs4.ui.components.JobCard
import com.example.dacs4.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JobListScreen(
    onBackClick: () -> Unit,
    onJobClick: (String) -> Unit,
    companyId: String? = null,
    viewModel: JobListViewModel = hiltViewModel()
) {
    LaunchedEffect(companyId) {
        if (companyId != null) {
            viewModel.setCompanyFilter(companyId)
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Temp states for "Other" sheet (which might still have an Apply button if it's too complex, 
    // but the request says Auto Apply, so we'll try to follow that)
    var tempSkills   by remember { mutableStateOf(uiState.skillsFilter) }
    var tempLevel    by remember { mutableStateOf(uiState.levelFilter) }
    var tempSalaryRange by remember { mutableStateOf(0f..50000000f) } 
    
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
        containerColor = AppColors.BgPrimary,
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "Tìm việc làm",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = AppColors.TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.BgPrimary)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterDropdownButton(
                        label = "Địa điểm", 
                        isActive = uiState.locationFilter.isNotEmpty(),
                        onClick = { viewModel.toggleLocationSheet(true) }
                    )
                    FilterDropdownButton(
                        label = "Lọc nâng cao",
                        isActive = uiState.levelFilter.isNotEmpty() || uiState.skillsFilter.isNotEmpty() || uiState.salaryMin != null,
                        icon = Icons.Default.FilterList,
                        onClick = { viewModel.toggleOtherSheet(true) }
                    )
                }
                
                if (uiState.locationFilter.isNotEmpty() || uiState.skillsFilter.isNotEmpty() || uiState.levelFilter.isNotEmpty() || uiState.salaryMin != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (uiState.locationFilter.isNotEmpty()) {
                            ActiveFilterChip(
                                label = AppConstants.LOCATION_LIST.find { it.first == uiState.locationFilter }?.second ?: uiState.locationFilter,
                                onRemove = { viewModel.updateLocation("") }
                            )
                        }
                        if (uiState.salaryMin != null) {
                            ActiveFilterChip(
                                label = "Lương > ${uiState.salaryMin?.let { formatSalary(it.toFloat()) } ?: ""}",
                                onRemove = { viewModel.updateSalary(null, null) }
                            )
                        }
                        if (uiState.levelFilter.isNotEmpty()) {
                            ActiveFilterChip(label = uiState.levelFilter, onRemove = { viewModel.updateLevel("") })
                        }
                        val skillList = uiState.skillsFilter.split(", ").filter { it.isNotBlank() }
                        if (skillList.isNotEmpty()) {
                            val displayLabel = if (skillList.size <= 2) {
                                skillList.joinToString(", ")
                            } else {
                                "${skillList.take(2).joinToString(", ")} +${skillList.size - 2}"
                            }
                            ActiveFilterChip(
                                label = displayLabel,
                                onRemove = { viewModel.updateSkills("") }
                            )
                        }
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = AppColors.Border)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                        contentPadding = PaddingValues(top = 8.dp, bottom = 110.dp)
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

    if (uiState.isLocationSheetVisible) {
        LocationSheet(
            selected = uiState.locationFilter,
            onSelect = { viewModel.updateLocation(it) },
            onDismiss = { viewModel.toggleLocationSheet(false) }
        )
    }

    if (uiState.isOtherSheetVisible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleOtherSheet(false) },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = null,
            shape = RoundedCornerShape(0.dp)
        ) {
            OtherDrawerContent(
                currentLevel = uiState.levelFilter,
                currentSalaryMin = uiState.salaryMin,
                currentSalaryMax = uiState.salaryMax,
                currentSkills = uiState.skillsFilter,
                availableSkills = uiState.availableSkills,
                onApply = { level, salaryMin, salaryMax, skills -> 
                    viewModel.applyFilters(level = level, salaryMin = salaryMin, salaryMax = salaryMax, skills = skills)
                    viewModel.toggleOtherSheet(false)
                },
                onClose = { viewModel.toggleOtherSheet(false) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OtherDrawerContent(
    currentLevel: String, 
    currentSalaryMin: Double?,
    currentSalaryMax: Double?,
    currentSkills: String, 
    availableSkills: List<com.example.dacs4.data.model.response.SkillResponse>,
    onApply: (String, Double?, Double?, String) -> Unit,
    onClose: () -> Unit
) {
    var tempLevel by remember { mutableStateOf(currentLevel) }
    var tempSalaryMin by remember { mutableStateOf(currentSalaryMin) }
    var tempSalaryMax by remember { mutableStateOf(currentSalaryMax) }
    var tempSkills by remember { mutableStateOf(currentSkills) }
    var skillSearch by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 16.dp) // Content padding
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Bộ lọc nâng cao", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AppColors.TextPrimary)
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = null, tint = AppColors.TextPrimary)
            }
        }
        
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            // 1. Level (Cấp bậc)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Cấp bậc", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = AppColors.TextPrimary)
            FlowRow(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("INTERN", "FRESHER", "JUNIOR", "MIDDLE", "SENIOR").forEach { lvl ->
                    LevelButton(lvl, tempLevel == lvl) { tempLevel = if (tempLevel == lvl) "" else lvl }
                }
            }

            // 2. Salary Range (Tiền) - Reordered to 2nd position
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mức lương", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = AppColors.TextPrimary)
                Text(
                    text = "${tempSalaryMin?.let { (it/1000000).toInt() } ?: 0}M - ${tempSalaryMax?.let { (it/1000000).toInt() } ?: 100}M",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.AccentBlue
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            RangeSlider(
                value = (tempSalaryMin?.toFloat() ?: 0f)..(tempSalaryMax?.toFloat() ?: 100000000f),
                onValueChange = { range ->
                    tempSalaryMin = range.start.toDouble()
                    tempSalaryMax = range.endInclusive.toDouble()
                },
                valueRange = 0f..100000000f,
                steps = 99, // 1M steps
                colors = SliderDefaults.colors(
                    activeTrackColor = AppColors.AccentBlue,
                    inactiveTrackColor = AppColors.AccentBlueLight,
                    thumbColor = AppColors.AccentBlue
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0M", fontSize = 11.sp, color = AppColors.TextHint)
                Text("100M+", fontSize = 11.sp, color = AppColors.TextHint)
            }

            // 3. Skills (Kỹ năng)
            Spacer(modifier = Modifier.height(32.dp))
            Text("Kỹ năng", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = AppColors.TextPrimary)
            OutlinedTextField(
                value = skillSearch,
                onValueChange = { skillSearch = it },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                placeholder = { Text("Tìm kỹ năng...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AppColors.BgSurface,
                    unfocusedContainerColor = AppColors.BgSurface
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            availableSkills.filter { it.name.contains(skillSearch, true) }.forEach { skill ->
                val isSelected = tempSkills.contains(skill.name)
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {
                        tempSkills = if (isSelected) tempSkills.split(", ").filter { it != skill.name }.joinToString(", ")
                                    else if (tempSkills.isEmpty()) skill.name else "$tempSkills, ${skill.name}"
                    }.padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected, 
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(checkedColor = AppColors.AccentBlue)
                    )
                    Text(
                        skill.name, 
                        modifier = Modifier.padding(start = 12.dp),
                        fontSize = 14.sp,
                        color = if (isSelected) AppColors.AccentBlue else AppColors.TextPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Footer Action
        Box(modifier = Modifier.padding(20.dp)) {
            Button(
                onClick = { onApply(tempLevel, tempSalaryMin, tempSalaryMax, tempSkills) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentBlue),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Áp dụng", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun FilterDropdownButton(
    label: String,
    isActive: Boolean,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    val containerColor = if (isActive) AppColors.AccentBlueLight else AppColors.BgSurface
    val contentColor = if (isActive) AppColors.AccentBlue else AppColors.TextPrimary
    val borderColor = if (isActive) AppColors.AccentBlue else AppColors.Border

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = contentColor)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                icon ?: Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSheet(selected: String, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss, 
        sheetState = sheetState,
        containerColor = AppColors.BgPrimary
    ) {
        Column(modifier = Modifier.padding(20.dp).padding(bottom = 20.dp)) {
            Text("Chọn địa điểm", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            val options = listOf("" to "Tất cả địa điểm", "HANOI" to "Hà Nội", "HOCHIMINH" to "TP. Hồ Chí Minh", "DANANG" to "Đà Nẵng")
            options.forEach { (code, name) ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect(code) }.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(name, color = if (selected == code) AppColors.AccentBlue else AppColors.TextPrimary)
                    RadioButton(selected = (selected == code), onClick = { onSelect(code) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalarySheet(selectedMin: Double?, onSelect: (Double?, Double?) -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = AppColors.BgPrimary) {
        Column(modifier = Modifier.padding(20.dp).padding(bottom = 20.dp)) {
            Text("Mức lương mong muốn", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            val presets = listOf(
                null to "Tất cả",
                0.0 to "Dưới 10 triệu",
                10000000.0 to "10M - 20M",
                20000000.0 to "20M - 40M",
                40000000.0 to "Trên 40 triệu"
            )
            presets.forEach { (min, label) ->
                val max = when(min) {
                    0.0 -> 10000000.0
                    10000000.0 -> 20000000.0
                    20000000.0 -> 40000000.0
                    else -> null
                }
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect(min, max) }.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label)
                    RadioButton(selected = (selectedMin == min), onClick = { onSelect(min, max) })
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
private fun LevelButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val containerColor by animateColorAsState(
        if (isSelected) AppColors.AccentBlue else AppColors.BgSurface,
        label = "color"
    )
    val contentColor by animateColorAsState(
        if (isSelected) Color.White else AppColors.TextSecondary,
        label = "text"
    )
    
    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .border(0.5.dp, if (isSelected) AppColors.AccentBlue else AppColors.Border, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = contentColor)
    }
}

private fun formatSalary(value: Float): String {
    return if (value >= 1000000) {
        "${(value / 1000000).toInt()}M"
    } else {
        "${(value / 1000).toInt()}k"
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
