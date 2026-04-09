package com.example.dacs4.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.dacs4.ui.components.*
import com.example.dacs4.ui.theme.*

// ═══════════════════════════════════════════════════════════════════
//  JOB LIST SCREEN (HOME) — Flagship Redesign
//  Sticky search header + Filter chips + Animated job cards
// ═══════════════════════════════════════════════════════════════════

data class JobItem(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val salary: String,
    val tags: List<String>,
    val postedAt: String,
    val isHot: Boolean = false,
    val isRemote: Boolean = false,
    val logoLetter: String,
    val logoColor: Color
)



val sampleJobs = listOf(
    JobItem(
        id = "1", title = "Senior Android Developer", company = "VNG Corporation",
        location = "TP. Hồ Chí Minh", salary = "$2,500 – $4,000",
        tags = listOf("Kotlin", "Jetpack Compose", "MVVM"),
        postedAt = "2 giờ trước", isHot = true, isRemote = false,
        logoLetter = "V", logoColor = Color(0xFF6366F1)
    ),
    JobItem(
        id = "2", title = "Full-Stack Engineer", company = "Tiki",
        location = "Hà Nội", salary = "$1,800 – $3,200",
        tags = listOf("React", "Node.js", "PostgreSQL"),
        postedAt = "5 giờ trước", isHot = true, isRemote = true,
        logoLetter = "T", logoColor = Color(0xFF06B6D4)
    ),
    JobItem(
        id = "3", title = "iOS Developer", company = "Momo",
        location = "Remote", salary = "$2,000 – $3,500",
        tags = listOf("Swift", "SwiftUI", "CoreData"),
        postedAt = "1 ngày trước", isHot = false, isRemote = true,
        logoLetter = "M", logoColor = Color(0xFFF59E0B)
    ),
    JobItem(
        id = "4", title = "Backend Developer (Java)", company = "FPT Software",
        location = "Đà Nẵng", salary = "$1,500 – $2,800",
        tags = listOf("Spring Boot", "Microservices", "Docker"),
        postedAt = "2 ngày trước", isHot = false, isRemote = false,
        logoLetter = "F", logoColor = Color(0xFF10B981)
    ),
    JobItem(
        id = "5", title = "DevOps Engineer", company = "Shopee Vietnam",
        location = "TP. Hồ Chí Minh", salary = "$2,200 – $3,800",
        tags = listOf("Kubernetes", "AWS", "Terraform"),
        postedAt = "3 ngày trước", isHot = true, isRemote = false,
        logoLetter = "S", logoColor = Color(0xFFEF4444)
    ),
    JobItem(
        id = "6", title = "Machine Learning Engineer", company = "Zalo AI",
        location = "Remote", salary = "$3,000 – $5,500",
        tags = listOf("Python", "PyTorch", "MLOps"),
        postedAt = "4 ngày trước", isHot = true, isRemote = true,
        logoLetter = "Z", logoColor = Color(0xFF8B5CF6)
    )
)

val filterCategories = listOf("Tất cả", "Remote", "Hot 🔥", "Mới nhất", "Lương cao", "Startup")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobListScreen(
    onJobClick: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    var isSearchFocused by remember { mutableStateOf(false) }

    val filteredJobs = remember(searchQuery, selectedFilter) {
        sampleJobs.filter { job ->
            val matchSearch = searchQuery.isEmpty() ||
                    job.title.contains(searchQuery, ignoreCase = true) ||
                    job.company.contains(searchQuery, ignoreCase = true) ||
                    job.tags.any { it.contains(searchQuery, ignoreCase = true) }
            val matchFilter = when (selectedFilter) {
                "Remote"  -> job.isRemote
                "Hot 🔥"  -> job.isHot
                "Mới nhất" -> true
                "Lương cao" -> job.salary.contains("$3") || job.salary.contains("$4") || job.salary.contains("$5")
                "Startup"  -> false
                else -> true
            }
            matchSearch && matchFilter
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JHColors.Background)
    ) {
        // ── Ambient ───────────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(JHColors.AccentPrimary.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.8f, 200f),
                    radius = 400f
                ),
                radius = 400f,
                center = Offset(size.width * 0.8f, 200f)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ── Sticky Top Bar ────────────────────────────────────
            item {
                TopBar(onLogout = onLogout)
            }

            // ── Hero Section ──────────────────────────────────────
            item {
                HeroSection(jobCount = filteredJobs.size)
            }

            // ── Search Bar ────────────────────────────────────────
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(horizontal = JHSpacing.screen.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Filter Chips ──────────────────────────────────────
            item {
                FilterChipsRow(
                    filters = filterCategories,
                    selected = selectedFilter,
                    onSelect = { selectedFilter = it }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // ── Section Header ────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = JHSpacing.screen.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredJobs.size} vị trí phù hợp",
                        style = JHTypography.HeadingM,
                        color = JHColors.TextPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable {}
                    ) {
                        Icon(
                            Icons.Outlined.FilterAlt,
                            contentDescription = null,
                            tint = JHColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("Bộ lọc", style = JHTypography.BodyS, color = JHColors.TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Featured (Hot) jobs ───────────────────────────────
            if (selectedFilter == "Tất cả" && searchQuery.isEmpty()) {
                item {
                    FeaturedJobsSection(
                        jobs = sampleJobs.filter { it.isHot }.take(3),
                        onJobClick = onJobClick
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Tất cả việc làm",
                        style = JHTypography.HeadingM,
                        color = JHColors.TextPrimary,
                        modifier = Modifier.padding(horizontal = JHSpacing.screen.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // ── Job Cards List ────────────────────────────────────
            if (filteredJobs.isEmpty()) {
                item { EmptyState() }
            } else {
                itemsIndexed(filteredJobs) { index, job ->
                    JobCard(
                        job = job,
                        index = index,
                        onClick = { onJobClick(job.id) }
                    )
                }
            }
        }

        // ── Floating Action Button ────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(56.dp)
                .clip(RoundedCornerShape(JHRadius.full.dp))
                .background(brush = Brush.linearGradient(JHColors.GradientButton))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(JHRadius.full.dp))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun TopBar(onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(JHColors.Background.copy(alpha = 0.95f))
            .padding(horizontal = JHSpacing.screen.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brush = Brush.linearGradient(JHColors.GradientButton)),
                contentAlignment = Alignment.Center
            ) {
                Text("JH", style = JHTypography.LabelS, color = Color.White)
            }
            Text("JobHunter", style = JHTypography.HeadingM, color = JHColors.TextPrimary)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = JHColors.TextSecondary, modifier = Modifier.size(22.dp))
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.Outlined.Logout, contentDescription = null, tint = JHColors.TextSecondary, modifier = Modifier.size(22.dp))
            }
        }
    }
    GradientDivider()
}

@Composable
private fun HeroSection(jobCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp, vertical = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusBadge(label = "ĐANG TUYỂN DỤNG", color = JHColors.StatusSuccess)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tìm việc\nIT lý tưởng",
            style = JHTypography.DisplayL,
            color = JHColors.TextPrimary,
            lineHeight = 44.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Hàng ngàn cơ hội từ các công ty hàng đầu đang chờ bạn",
            style = JHTypography.BodyM,
            color = JHColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatsChip(value = "50K+", label = "Việc làm")
            StatsChip(value = "2K+", label = "Công ty")
            StatsChip(value = "98%", label = "Phù hợp")
        }
    }
}

@Composable
private fun StatsChip(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = JHTypography.HeadingL,
            color = JHColors.AccentPrimary
        )
        Text(text = label, style = JHTypography.BodyS, color = JHColors.TextMuted)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceElevated)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    "Tìm theo vị trí, công ty, kỹ năng...",
                    style = JHTypography.BodyM,
                    color = JHColors.TextMuted
                )
            },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = JHColors.TextMuted, modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = JHColors.TextMuted, modifier = Modifier.size(18.dp))
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = JHColors.TextPrimary,
                unfocusedTextColor = JHColors.TextPrimary,
                cursorColor = JHColors.AccentPrimary
            ),
            textStyle = JHTypography.BodyM
        )
    }
}

@Composable
private fun FilterChipsRow(
    filters: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = JHSpacing.screen.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChipItem(
                label = filter,
                isSelected = filter == selected,
                onClick = { onSelect(filter) }
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) JHColors.AccentPrimary else JHColors.SurfaceElevated,
        animationSpec = tween(200),
        label = "chip_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else JHColors.TextSecondary,
        animationSpec = tween(200),
        label = "chip_text"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(JHRadius.full.dp))
            .background(bgColor)
            .border(
                1.dp,
                if (isSelected) Color.Transparent else JHColors.BorderSubtle,
                RoundedCornerShape(JHRadius.full.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label, style = JHTypography.LabelM, color = textColor)
    }
}

@Composable
private fun FeaturedJobsSection(
    jobs: List<JobItem>,
    onJobClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Nổi bật 🔥", style = JHTypography.HeadingM, color = JHColors.TextPrimary)
            Text("Xem tất cả →", style = JHTypography.BodyS, color = JHColors.AccentPrimary)
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(jobs) { job ->
                FeaturedJobCard(job = job, onClick = { onJobClick(job.id) })
            }
        }
    }
}

@Composable
private fun FeaturedJobCard(job: JobItem, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "featured_scale"
    )

    Box(
        modifier = Modifier
            .width(240.dp)
            .scale(scale)
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(JHColors.SurfaceElevated, JHColors.SurfaceMid)
                )
            )
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                CompanyLogo(letter = job.logoLetter, color = job.logoColor, size = 40)
                StatusBadge(label = "HOT", color = JHColors.AccentGold)
            }
            Text(job.title, style = JHTypography.HeadingM, color = JHColors.TextPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(job.company, style = JHTypography.BodyS, color = JHColors.TextSecondary)
            SalaryChip(salary = job.salary)
        }
    }
}

@Composable
fun JobCard(
    job: JobItem,
    index: Int,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale_$index"
    )

    // Staggered entrance animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 80L)
        isVisible = true
    }
    val cardAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400),
        label = "card_alpha_$index"
    )
    val cardOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 20f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "card_offset_$index"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp, vertical = 6.dp)
            .alpha(cardAlpha)
            .offset(y = cardOffset.dp)
            .scale(scale)
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    CompanyLogo(letter = job.logoLetter, color = job.logoColor, size = 44)
                    Column {
                        Text(
                            job.title,
                            style = JHTypography.HeadingM,
                            color = JHColors.TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(job.company, style = JHTypography.BodyS, color = JHColors.TextSecondary)
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (job.isHot) StatusBadge(label = "HOT", color = JHColors.AccentGold)
                    if (job.isRemote) StatusBadge(label = "REMOTE", color = JHColors.AccentTertiary)
                }
            }

            GradientDivider()

            // Info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.LocationOn, null, tint = JHColors.TextMuted, modifier = Modifier.size(14.dp))
                    Text(job.location, style = JHTypography.BodyS, color = JHColors.TextSecondary)
                }
                SalaryChip(salary = job.salary)
            }

            // Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                job.tags.forEach { tag ->
                    TechBadge(text = tag)
                }
            }

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Schedule, null, tint = JHColors.TextMuted, modifier = Modifier.size(12.dp))
                    Text(job.postedAt, style = JHTypography.BodyS, color = JHColors.TextMuted)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Xem chi tiết", style = JHTypography.LabelS, color = JHColors.AccentPrimary)
                    Icon(Icons.Outlined.ArrowForward, null, tint = JHColors.AccentPrimary, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun CompanyLogo(letter: String, color: Color, size: Int = 44) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size * 0.3f).dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape((size * 0.3f).dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            style = if (size >= 44) JHTypography.HeadingM else JHTypography.LabelM,
            color = color
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Outlined.SearchOff,
            contentDescription = null,
            tint = JHColors.TextMuted,
            modifier = Modifier.size(48.dp)
        )
        Text("Không tìm thấy việc làm phù hợp", style = JHTypography.HeadingM, color = JHColors.TextSecondary)
        Text("Thử tìm kiếm với từ khóa khác", style = JHTypography.BodyM, color = JHColors.TextMuted)
    }
}