package com.example.dacs4.ui.screens.company

import androidx.compose.animation.core.*
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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs4.data.model.*
import com.example.dacs4.ui.components.*
import com.example.dacs4.ui.theme.*

// ═══════════════════════════════════════════════════════════════════
//  COMPANY LIST SCREEN — Phase 3
//  Featured row · Industry filters · Animated company cards
// ═══════════════════════════════════════════════════════════════════

@Composable
fun CompanyListScreen(
    onBack: () -> Unit,
    onCompanyClick: (String) -> Unit,
    viewModel: CompanyViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsState()
    var localQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadCompanies() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JHColors.Background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(JHColors.AccentPrimary.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.85f, 180f), radius = 380f
                ),
                radius = 380f, center = Offset(size.width * 0.85f, 180f)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // ── Top Bar ───────────────────────────────────────────
            item {
                CompanyListTopBar(onBack = onBack)
            }

            // ── Hero ──────────────────────────────────────────────
            item {
                CompanyHero(total = state.companies.size)
            }

            // ── Search ────────────────────────────────────────────
            item {
                CompanySearchBar(
                    query = localQuery,
                    onQueryChange = {
                        localQuery = it
                        viewModel.onSearchChange(it)
                    },
                    modifier = Modifier.padding(horizontal = JHSpacing.screen.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // ── Industry Filters ──────────────────────────────────
            item {
                IndustryFilterRow(
                    selected = state.selectedIndustry,
                    onSelect = { viewModel.setIndustry(it) }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // ── Featured ──────────────────────────────────────────
            if (localQuery.isEmpty() && state.selectedIndustry == "Tất cả" && state.featuredCompanies.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = JHSpacing.screen.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Nổi bật ⭐", style = JHTypography.HeadingM, color = JHColors.TextPrimary)
                        Text("${state.featuredCompanies.size} công ty", style = JHTypography.BodyS, color = JHColors.TextMuted)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = JHSpacing.screen.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.featuredCompanies) { company ->
                            FeaturedCompanyCard(company = company, onClick = { onCompanyClick(company.id) })
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // ── All companies ─────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = JHSpacing.screen.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${state.filtered.size} công ty${if (state.selectedIndustry != "Tất cả") " · ${state.selectedIndustry}" else ""}",
                        style = JHTypography.HeadingM, color = JHColors.TextPrimary
                    )
                    if (state.isRefreshing) DotLoader(modifier = Modifier.height(20.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            when {
                state.isLoading -> {
                    items(5) {
                        ShimmerBox(modifier = Modifier.fillMaxWidth().height(110.dp)
                            .padding(horizontal = JHSpacing.screen.dp, vertical = 5.dp))
                    }
                }
                !state.error.isNullOrEmpty() -> {
                    item {
                        CompanyErrorState(message = state.error!!, onRetry = { viewModel.loadCompanies() })
                    }
                }
                state.filtered.isEmpty() -> {
                    item { CompanyEmptyState() }
                }
                else -> {
                    itemsIndexed(state.filtered) { index, company ->
                        CompanyCard(company = company, index = index, onClick = { onCompanyClick(company.id) })
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  TOP BAR
// ──────────────────────────────────────────────────────────────────

@Composable
private fun CompanyListTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(JHColors.Background.copy(alpha = 0.95f))
            .padding(horizontal = JHSpacing.screen.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape)
                .background(JHColors.SurfaceElevated)
                .border(1.dp, JHColors.BorderSubtle, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.ArrowBack, null, tint = JHColors.TextSecondary, modifier = Modifier.size(18.dp))
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Khám phá công ty", style = JHTypography.HeadingM, color = JHColors.TextPrimary)
            Text("Tìm môi trường phù hợp", style = JHTypography.BodyS, color = JHColors.TextSecondary)
        }
        Box(modifier = Modifier.size(36.dp))
    }
    GradientDivider()
}

// ──────────────────────────────────────────────────────────────────
//  HERO
// ──────────────────────────────────────────────────────────────────

@Composable
private fun CompanyHero(total: Int) {
    Column(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp, vertical = 20.dp)) {
        StatusBadge(label = "2,000+ CÔNG TY", color = JHColors.AccentPrimary)
        Spacer(modifier = Modifier.height(10.dp))
        Text("Khám phá\ncông ty hàng đầu", style = JHTypography.DisplayL, color = JHColors.TextPrimary, lineHeight = 44.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Tìm hiểu văn hoá, công nghệ và phúc lợi của các công ty IT", style = JHTypography.BodyM, color = JHColors.TextSecondary)
    }
}

// ──────────────────────────────────────────────────────────────────
//  SEARCH
// ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanySearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceElevated)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
    ) {
        TextField(
            value = query, onValueChange = onQueryChange,
            placeholder = { Text("Tìm công ty, ngành nghề...", style = JHTypography.BodyM, color = JHColors.TextMuted) },
            leadingIcon = { Icon(Icons.Outlined.Search, null, tint = JHColors.TextMuted, modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                if (query.isNotEmpty()) IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Outlined.Close, null, tint = JHColors.TextMuted, modifier = Modifier.size(18.dp))
                }
            },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = JHColors.TextPrimary, unfocusedTextColor = JHColors.TextPrimary,
                cursorColor = JHColors.AccentPrimary
            ),
            textStyle = JHTypography.BodyM
        )
    }
}

// ──────────────────────────────────────────────────────────────────
//  INDUSTRY FILTERS
// ──────────────────────────────────────────────────────────────────

@Composable
private fun IndustryFilterRow(selected: String, onSelect: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = JHSpacing.screen.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(industryList) { industry ->
            val isSel = industry == selected
            val bg by animateColorAsState(if (isSel) JHColors.AccentPrimary else JHColors.SurfaceElevated, tween(200), label = "ind_bg")
            val tc by animateColorAsState(if (isSel) Color.White else JHColors.TextSecondary, tween(200), label = "ind_tc")
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(JHRadius.full.dp))
                    .background(bg)
                    .border(1.dp, if (isSel) Color.Transparent else JHColors.BorderSubtle, RoundedCornerShape(JHRadius.full.dp))
                    .clickable { onSelect(industry) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) { Text(industry, style = JHTypography.LabelM, color = tc) }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  FEATURED COMPANY CARD (horizontal scroll)
// ──────────────────────────────────────────────────────────────────

@Composable
private fun FeaturedCompanyCard(company: Company, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "feat_scale")

    val logoColor = companyLogoColor(company.id)

    Box(
        modifier = Modifier
            .width(200.dp)
            .scale(scale)
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                CompanyLogoBox(letter = company.name.first().toString(), color = logoColor, size = 44)
                if (company.isVerified) {
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape)
                            .background(JHColors.StatusSuccess.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Outlined.Verified, null, tint = JHColors.StatusSuccess, modifier = Modifier.size(14.dp)) }
                }
            }
            Text(company.name, style = JHTypography.HeadingM, color = JHColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(company.industry, style = JHTypography.BodyS, color = JHColors.TextSecondary)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                RatingChip(rating = company.rating)
                Text("${company.totalJobs} jobs", style = JHTypography.LabelS, color = JHColors.AccentPrimary)
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  COMPANY CARD (list)
// ──────────────────────────────────────────────────────────────────

@Composable
fun CompanyCard(company: Company, index: Int, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "co_scale_$index")

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { kotlinx.coroutines.delay(index * 60L); isVisible = true }
    val alpha by animateFloatAsState(if (isVisible) 1f else 0f, tween(350), label = "co_alpha_$index")
    val offset by animateFloatAsState(if (isVisible) 0f else 16f, tween(350, easing = FastOutSlowInEasing), label = "co_off_$index")

    val logoColor = companyLogoColor(company.id)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp, vertical = 5.dp)
            .alpha(alpha).offset(y = offset.dp).scale(scale)
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                CompanyLogoBox(letter = company.name.first().toString(), color = logoColor, size = 48)
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(company.name, style = JHTypography.HeadingM, color = JHColors.TextPrimary, modifier = Modifier.weight(1f, false))
                        if (company.isVerified) Icon(Icons.Outlined.Verified, null, tint = JHColors.StatusSuccess, modifier = Modifier.size(14.dp))
                    }
                    Text(company.industry, style = JHTypography.BodyS, color = JHColors.TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, null, tint = JHColors.TextMuted, modifier = Modifier.size(12.dp))
                        Text(company.address, style = JHTypography.BodyS, color = JHColors.TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    RatingChip(rating = company.rating)
                    Text("${company.totalJobs} jobs", style = JHTypography.LabelS, color = JHColors.AccentPrimary)
                }
            }

            GradientDivider()

            // Scale + tech row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.People, null, tint = JHColors.TextMuted, modifier = Modifier.size(12.dp))
                    Text(company.scale.range, style = JHTypography.BodyS, color = JHColors.TextMuted)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    company.techStack.take(3).forEach { tech -> TechBadge(text = tech) }
                    if (company.techStack.size > 3) {
                        TechBadge(text = "+${company.techStack.size - 3}")
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  HELPERS
// ──────────────────────────────────────────────────────────────────

@Composable
fun CompanyLogoBox(letter: String, color: Color, size: Int) {
    Box(
        modifier = Modifier.size(size.dp).clip(RoundedCornerShape((size * 0.28f).dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape((size * 0.28f).dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(letter, style = if (size >= 44) JHTypography.HeadingL else JHTypography.HeadingM, color = color)
    }
}

@Composable
fun RatingChip(rating: Float) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(JHRadius.sm.dp))
            .background(JHColors.AccentGold.copy(alpha = 0.12f))
            .border(1.dp, JHColors.AccentGold.copy(alpha = 0.3f), RoundedCornerShape(JHRadius.sm.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text("⭐ ${"%.1f".format(rating)}", style = JHTypography.LabelS, color = JHColors.AccentGold)
    }
}

fun companyLogoColor(id: String): Color {
    val colors = listOf(
        Color(0xFF6366F1), Color(0xFF06B6D4), Color(0xFFF59E0B),
        Color(0xFF10B981), Color(0xFFEF4444), Color(0xFF8B5CF6),
        Color(0xFFEC4899), Color(0xFF0EA5E9)
    )
    return colors[(id.hashCode().absoluteValue) % colors.size]
}

private val Int.absoluteValue get() = if (this < 0) -this else this

@Composable
private fun CompanyEmptyState() {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(Icons.Outlined.BusinessCenter, null, tint = JHColors.TextMuted, modifier = Modifier.size(48.dp))
        Text("Không tìm thấy công ty", style = JHTypography.HeadingM, color = JHColors.TextSecondary)
        Text("Thử tìm kiếm với từ khóa khác", style = JHTypography.BodyM, color = JHColors.TextMuted)
    }
}

@Composable
private fun CompanyErrorState(message: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(Icons.Outlined.WifiOff, null, tint = JHColors.StatusError, modifier = Modifier.size(40.dp))
        Text(message, style = JHTypography.BodyM, color = JHColors.TextSecondary, textAlign = TextAlign.Center)
        GhostButton("Thử lại", onClick = onRetry, modifier = Modifier.fillMaxWidth(0.5f),
            borderColor = JHColors.StatusError, textColor = JHColors.StatusError)
    }
}
