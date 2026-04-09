package com.example.dacs4.ui.screens.company

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs4.data.model.*
import com.example.dacs4.ui.components.*
import com.example.dacs4.ui.screens.home.CompanyLogo
import com.example.dacs4.ui.screens.home.JobCard
import com.example.dacs4.ui.screens.home.sampleJobs
import com.example.dacs4.ui.theme.*

// ═══════════════════════════════════════════════════════════════════
//  COMPANY DETAIL SCREEN — Phase 3
//  Hero · Thống kê · Về chúng tôi · Phúc lợi · Tech Stack · Jobs
// ═══════════════════════════════════════════════════════════════════

@Composable
fun CompanyDetailScreen(
    companyId: String,
    onBack: () -> Unit,
    onJobClick: (String) -> Unit,
    viewModel: CompanyViewModel = hiltViewModel()
) {
    val state by viewModel.detailState.collectAsState()

    LaunchedEffect(companyId) { viewModel.loadCompanyDetail(companyId) }

    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "co_detail_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "co_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JHColors.Background)
    ) {
        when {
            state.isLoading -> CompanyDetailSkeleton()
            !state.error.isNullOrEmpty() -> CompanyDetailError(
                message = state.error!!,
                onBack = onBack,
                onRetry = { viewModel.loadCompanyDetail(companyId) }
            )
            state.company != null -> {
                val company = state.company!!
                val logoColor = companyLogoColor(company.id)
                // Jobs from this company (mock: filter by industry)
                val companyJobs = sampleJobs.take(3)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // ── Hero ──────────────────────────────────────
                    CompanyDetailHero(
                        company = company,
                        logoColor = logoColor,
                        pulse = pulse,
                        isSaved = state.isSaved,
                        onBack = onBack,
                        onToggleSave = { viewModel.toggleSave() }
                    )

                    // ── Body ──────────────────────────────────────
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                            .background(JHColors.Background)
                            .padding(top = 24.dp)
                    ) {
                        // Stats
                        CompanyStatsRow(company = company)

                        Spacer(modifier = Modifier.height(20.dp))
                        GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                        Spacer(modifier = Modifier.height(20.dp))

                        // About
                        CoDetailSection("Về công ty") {
                            Text(
                                company.description,
                                style = JHTypography.BodyM,
                                color = JHColors.TextSecondary,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CoInfoGrid(company = company)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                        Spacer(modifier = Modifier.height(20.dp))

                        // Tech Stack
                        if (company.techStack.isNotEmpty()) {
                            CoDetailSection("Công nghệ sử dụng") {
                                TechStackSection(stack = company.techStack)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // Benefits
                        if (company.benefits.isNotEmpty()) {
                            CoDetailSection("Phúc lợi") {
                                BenefitsGrid(benefits = company.benefits)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // Jobs from company
                        CoDetailSection("Vị trí đang tuyển (${companyJobs.size})") {
                            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                                companyJobs.forEachIndexed { index, job ->
                                    JobCard(job = job, index = index, onClick = { onJobClick(job.id) })
                                }
                            }
                        }

                        // Rating section
                        Spacer(modifier = Modifier.height(20.dp))
                        GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                        Spacer(modifier = Modifier.height(20.dp))

                        CoDetailSection("Đánh giá") {
                            CompanyRatingSection(company = company)
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }

                // Floating "Xem việc làm" button
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, JHColors.Background)
                            )
                        )
                        .padding(horizontal = JHSpacing.screen.dp, vertical = 20.dp)
                ) {
                    GradientButton(
                        text = "XEM ${company.totalJobs} VỊ TRÍ TUYỂN DỤNG",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  HERO
// ──────────────────────────────────────────────────────────────────

@Composable
private fun CompanyDetailHero(
    company: Company,
    logoColor: Color,
    pulse: Float,
    isSaved: Boolean,
    onBack: () -> Unit,
    onToggleSave: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCompanyHero(logoColor, pulse)
        }

        // Back + Save
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(38.dp).clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                .clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.ArrowBack, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(38.dp).clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    .clickable {}, contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Share, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Box(modifier = Modifier.size(38.dp).clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    .clickable(onClick = onToggleSave), contentAlignment = Alignment.Center) {
                    Icon(
                        if (isSaved) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                        null,
                        tint = if (isSaved) JHColors.AccentGold else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Company identity
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                CompanyLogoBox(letter = company.name.first().toString(), color = logoColor, size = 52)
                if (company.isVerified) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(JHRadius.full.dp))
                        .background(JHColors.StatusSuccess.copy(alpha = 0.2f))
                        .border(1.dp, JHColors.StatusSuccess.copy(alpha = 0.4f), RoundedCornerShape(JHRadius.full.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Verified, null, tint = JHColors.StatusSuccess, modifier = Modifier.size(10.dp))
                            Text("Đã xác minh", style = JHTypography.LabelS, color = JHColors.StatusSuccess)
                        }
                    }
                }
            }
            Text(company.name, style = JHTypography.DisplayM, color = Color.White)
            Text(company.industry, style = JHTypography.BodyM, color = Color.White.copy(alpha = 0.75f))
        }
    }
}

private fun DrawScope.drawCompanyHero(accentColor: Color, pulse: Float) {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF0D1117), Color(0xFF080B14))))
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(accentColor.copy(alpha = 0.28f * pulse), accentColor.copy(alpha = 0.08f), Color.Transparent),
            center = Offset(size.width * 0.72f, size.height * 0.35f), radius = 340f
        ),
        radius = 340f, center = Offset(size.width * 0.72f, size.height * 0.35f)
    )
    val g = 50.dp.toPx(); var x = 0f
    while (x < size.width) { drawLine(Color.White.copy(alpha = 0.025f), Offset(x,0f), Offset(x, size.height)); x+=g }
    var y = 0f
    while (y < size.height) { drawLine(Color.White.copy(alpha = 0.025f), Offset(0f,y), Offset(size.width,y)); y+=g }
    drawRect(brush = Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF080B14)), startY = size.height*0.5f, endY = size.height))
}

// ──────────────────────────────────────────────────────────────────
//  STATS ROW
// ──────────────────────────────────────────────────────────────────

@Composable
private fun CompanyStatsRow(company: Company) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = JHSpacing.screen.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CoStatBox("${company.totalJobs}+", "Việc làm", JHColors.AccentPrimary, Modifier.weight(1f))
        CoStatBox(company.scale.displayName, "Quy mô", JHColors.AccentTertiary, Modifier.weight(1f))
        CoStatBox("${"%.1f".format(company.rating)}⭐", "${company.reviewCount} đánh giá", JHColors.AccentGold, Modifier.weight(1f))
    }
}

@Composable
private fun CoStatBox(value: String, label: String, color: Color, modifier: Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(JHRadius.lg.dp))
        .background(color.copy(alpha = 0.08f))
        .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(JHRadius.lg.dp)).padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = JHTypography.HeadingL, color = color)
            Text(label, style = JHTypography.BodyS, color = JHColors.TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  SECTION WRAPPER
// ──────────────────────────────────────────────────────────────────

@Composable
private fun CoDetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp)) {
        Text(title, style = JHTypography.HeadingL, color = JHColors.TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

// ──────────────────────────────────────────────────────────────────
//  CO INFO GRID
// ──────────────────────────────────────────────────────────────────

@Composable
private fun CoInfoGrid(company: Company) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CoInfoRow(Icons.Outlined.LocationOn, "Địa chỉ", company.address, JHColors.AccentTertiary)
            if (company.foundedYear != null) CoInfoRow(Icons.Outlined.CalendarMonth, "Thành lập", "${company.foundedYear}", JHColors.AccentPrimary)
            CoInfoRow(Icons.Outlined.People, "Quy mô", company.scale.range, JHColors.AccentSecondary)
            if (company.website.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {}) {
                    Box(modifier = Modifier.size(30.dp).clip(RoundedCornerShape(8.dp))
                        .background(JHColors.AccentGold.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Language, null, tint = JHColors.AccentGold, modifier = Modifier.size(15.dp))
                    }
                    Column {
                        Text("Website", style = JHTypography.LabelS, color = JHColors.TextMuted)
                        Text(company.website, style = JHTypography.BodyM, color = JHColors.AccentPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
private fun CoInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(30.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(15.dp))
        }
        Column {
            Text(label, style = JHTypography.LabelS, color = JHColors.TextMuted)
            Text(value, style = JHTypography.BodyM, color = JHColors.TextPrimary)
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  TECH STACK
// ──────────────────────────────────────────────────────────────────

@Composable
private fun TechStackSection(stack: List<String>) {
    val rows = stack.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { tech -> TechBadge(text = tech) }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  BENEFITS
// ──────────────────────────────────────────────────────────────────

@Composable
private fun BenefitsGrid(benefits: List<String>) {
    val icons = listOf("💰", "🏥", "📚", "🏖️", "🎮", "🚀", "🌍", "💪")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        benefits.forEachIndexed { index, benefit ->
            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(JHRadius.lg.dp))
                    .background(JHColors.SurfaceMid)
                    .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.lg.dp))
                    .padding(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(icons.getOrElse(index) { "✨" }, style = JHTypography.HeadingM)
                    Text(benefit, style = JHTypography.BodyM, color = JHColors.TextSecondary)
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  RATING SECTION
// ──────────────────────────────────────────────────────────────────

@Composable
private fun CompanyRatingSection(company: Company) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${"%.1f".format(company.rating)}", style = JHTypography.DisplayXL, color = JHColors.AccentGold)
                    Text("/ 5.0", style = JHTypography.BodyS, color = JHColors.TextMuted)
                    Text("${company.reviewCount} đánh giá", style = JHTypography.BodyS, color = JHColors.TextMuted)
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(5 to 0.68f, 4 to 0.18f, 3 to 0.08f, 2 to 0.04f, 1 to 0.02f).forEach { (star, fraction) ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("$star★", style = JHTypography.LabelS, color = JHColors.TextMuted, modifier = Modifier.width(24.dp))
                            Box(modifier = Modifier.weight(1f).height(6.dp)
                                .clip(RoundedCornerShape(JHRadius.full.dp)).background(JHColors.SurfaceElevated)) {
                                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(fraction)
                                    .background(JHColors.AccentGold).clip(RoundedCornerShape(JHRadius.full.dp)))
                            }
                            Text("${(fraction * 100).toInt()}%", style = JHTypography.LabelS, color = JHColors.TextMuted, modifier = Modifier.width(32.dp))
                        }
                    }
                }
            }
            GradientDivider()
            Text("Dựa trên đánh giá từ nhân viên hiện tại và cựu nhân viên.", style = JHTypography.BodyS, color = JHColors.TextMuted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  LOADING & ERROR STATES
// ──────────────────────────────────────────────────────────────────

@Composable
private fun CompanyDetailSkeleton() {
    Column(modifier = Modifier.fillMaxSize()) {
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(240.dp), cornerRadius = 0.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { repeat(3) { ShimmerBox(Modifier.weight(1f).height(70.dp)) } }
            ShimmerBox(Modifier.fillMaxWidth(0.5f).height(20.dp))
            ShimmerBox(Modifier.fillMaxWidth().height(90.dp))
            ShimmerBox(Modifier.fillMaxWidth(0.35f).height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { repeat(4) { ShimmerBox(Modifier.width(70.dp).height(28.dp)) } }
        }
    }
}

@Composable
private fun CompanyDetailError(message: String, onBack: () -> Unit, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Outlined.ErrorOutline, null, tint = JHColors.StatusError, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, style = JHTypography.BodyM, color = JHColors.TextSecondary, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GhostButton("Quay lại", onClick = onBack, modifier = Modifier.weight(1f))
            GradientButton("Thử lại", onClick = onRetry, modifier = Modifier.weight(1f))
        }
    }
}
