package com.example.dacs4.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
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
import com.example.dacs4.ui.components.*
import com.example.dacs4.ui.theme.*

// ═══════════════════════════════════════════════════════════════════
//  JOB DETAIL SCREEN — Flagship Redesign
//  Immersive header + Rich content sections + Floating Apply CTA
// ═══════════════════════════════════════════════════════════════════

@Composable
fun JobDetailScreen(
    jobId: String,
    onBack: () -> Unit,
    onApply: () -> Unit
) {
    // Resolve job data (in real app: from ViewModel)
    val job = remember(jobId) {
        sampleJobs.find { it.id == jobId } ?: sampleJobs.first()
    }

    var isSaved by remember { mutableStateOf(false) }
    var hasApplied by remember { mutableStateOf(false) }
    var showApplySuccess by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Header parallax
    val headerAlpha by remember {
        derivedStateOf {
            (1f - scrollState.value / 600f).coerceIn(0f, 1f)
        }
    }
    val headerOffset by remember {
        derivedStateOf { scrollState.value * 0.4f }
    }

    // Ambient animation
    val infiniteTransition = rememberInfiniteTransition(label = "detail_ambient")
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ambient_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JHColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // ── Hero Header ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                // Background with company color gradient
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-headerOffset).dp)
                        .alpha(headerAlpha)
                ) {
                    drawDetailHero(job.logoColor, ambientPulse)
                }

                // Back button + actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = JHSpacing.screen.dp)
                        .padding(top = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.3f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.ArrowBack, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }

                    // Share + Save
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton(icon = Icons.Outlined.Share, onClick = {})
                        ActionButton(
                            icon = if (isSaved) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                            tint = if (isSaved) JHColors.AccentGold else Color.White,
                            onClick = { isSaved = !isSaved }
                        )
                    }
                }

                // Company logo + name in hero
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = JHSpacing.screen.dp)
                        .padding(bottom = 24.dp)
                        .alpha(headerAlpha)
                ) {
                    CompanyLogo(letter = job.logoLetter, color = job.logoColor, size = 56)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = job.title,
                        style = JHTypography.DisplayM,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = job.company,
                        style = JHTypography.BodyL,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // ── Content ───────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(JHColors.Background)
                    .padding(top = 24.dp)
            ) {
                // ── Quick Info Cards ──────────────────────────────
                QuickInfoSection(job = job)

                Spacer(modifier = Modifier.height(24.dp))
                GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                Spacer(modifier = Modifier.height(24.dp))

                // ── Tags ──────────────────────────────────────────
                SectionHeader("Kỹ năng yêu cầu")
                Spacer(modifier = Modifier.height(12.dp))
                TechTagsSection(tags = job.tags + listOf("Git", "Agile", "CI/CD", "Unit Testing"))

                Spacer(modifier = Modifier.height(24.dp))
                GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                Spacer(modifier = Modifier.height(24.dp))

                // ── Job Description ───────────────────────────────
                SectionHeader("Mô tả công việc")
                Spacer(modifier = Modifier.height(12.dp))
                JobDescriptionSection()

                Spacer(modifier = Modifier.height(24.dp))
                GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                Spacer(modifier = Modifier.height(24.dp))

                // ── Requirements ─────────────────────────────────
                SectionHeader("Yêu cầu")
                Spacer(modifier = Modifier.height(12.dp))
                RequirementsSection()

                Spacer(modifier = Modifier.height(24.dp))
                GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                Spacer(modifier = Modifier.height(24.dp))

                // ── Benefits ──────────────────────────────────────
                SectionHeader("Quyền lợi")
                Spacer(modifier = Modifier.height(12.dp))
                BenefitsSection()

                Spacer(modifier = Modifier.height(24.dp))
                GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                Spacer(modifier = Modifier.height(24.dp))

                // ── Company Info ──────────────────────────────────
                SectionHeader("Về công ty")
                Spacer(modifier = Modifier.height(12.dp))
                CompanyInfoSection(job = job)

                // Bottom spacing for FAB
                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        // ── Floating Apply Button ─────────────────────────────────
        FloatingApplyButton(
            hasApplied = hasApplied,
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = {
                if (!hasApplied) {
                    hasApplied = true
                    showApplySuccess = true
                }
            }
        )

        // ── Apply Success Toast ───────────────────────────────────
        if (showApplySuccess) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                showApplySuccess = false
            }
            ApplySuccessToast(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  Section Components
// ──────────────────────────────────────────────────────────────────

@Composable
private fun QuickInfoSection(job: JobItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        InfoCard(
            icon = Icons.Outlined.LocationOn,
            label = "Địa điểm",
            value = job.location,
            modifier = Modifier.weight(1f)
        )
        InfoCard(
            icon = Icons.Outlined.AttachMoney,
            label = "Mức lương",
            value = job.salary,
            accentColor = JHColors.AccentGold,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        InfoCard(
            icon = Icons.Outlined.Work,
            label = "Hình thức",
            value = if (job.isRemote) "Remote / Hybrid" else "Full-time",
            accentColor = JHColors.AccentTertiary,
            modifier = Modifier.weight(1f)
        )
        InfoCard(
            icon = Icons.Outlined.Schedule,
            label = "Đăng tuyển",
            value = job.postedAt,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    accentColor: Color = JHColors.AccentPrimary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.lg.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(14.dp))
                Text(label, style = JHTypography.LabelS, color = JHColors.TextMuted)
            }
            Text(
                value,
                style = JHTypography.BodyM,
                color = JHColors.TextPrimary,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = JHTypography.HeadingL,
        color = JHColors.TextPrimary,
        modifier = Modifier.padding(horizontal = JHSpacing.screen.dp)
    )
}

@Composable
private fun TechTagsSection(tags: List<String>) {
    // Flow layout (wrapping)
    val rows = tags.chunked(3)
    Column(
        modifier = Modifier.padding(horizontal = JHSpacing.screen.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { rowTags ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowTags.forEach { tag ->
                    TechBadge(text = tag)
                }
            }
        }
    }
}

@Composable
private fun JobDescriptionSection() {
    val description = listOf(
        "Phát triển và duy trì ứng dụng Android sử dụng Kotlin và Jetpack Compose.",
        "Thiết kế kiến trúc MVVM/MVI theo chuẩn Clean Architecture.",
        "Tích hợp REST API và GraphQL với Retrofit, Ktor.",
        "Đảm bảo hiệu suất ứng dụng và viết Unit Tests, UI Tests.",
        "Tham gia Agile/Scrum với chu kỳ 2 tuần."
    )

    Column(
        modifier = Modifier.padding(horizontal = JHSpacing.screen.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        description.forEachIndexed { index, item ->
            BulletItem(text = item, index = index)
        }
    }
}

@Composable
private fun RequirementsSection() {
    val requirements = listOf(
        "3+ năm kinh nghiệm phát triển Android Native",
        "Thành thạo Kotlin, hiểu sâu Coroutines & Flow",
        "Kinh nghiệm Jetpack Compose là lợi thế",
        "Hiểu biết về RESTful APIs và JSON",
        "Tiếng Anh đọc hiểu tài liệu kỹ thuật"
    )

    Column(
        modifier = Modifier.padding(horizontal = JHSpacing.screen.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        requirements.forEach { req ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(JHColors.AccentPrimary)
                )
                Text(req, style = JHTypography.BodyM, color = JHColors.TextSecondary)
            }
        }
    }
}

@Composable
private fun BenefitsSection() {
    val benefits = listOf(
        Pair("💰", "Lương tháng 13, thưởng KPI hấp dẫn"),
        Pair("🏥", "Bảo hiểm sức khỏe Premium cho bản thân & gia đình"),
        Pair("📚", "Budget học tập \$500/năm"),
        Pair("🏖️", "15 ngày phép năm + 5 ngày phép sinh nhật"),
        Pair("🎮", "Team building, happy hour hàng tuần")
    )

    Column(
        modifier = Modifier.padding(horizontal = JHSpacing.screen.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        benefits.forEach { (emoji, text) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(JHRadius.md.dp))
                    .background(JHColors.SurfaceMid)
                    .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.md.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(emoji, style = JHTypography.HeadingM)
                Text(text, style = JHTypography.BodyM, color = JHColors.TextSecondary)
            }
        }
    }
}

@Composable
private fun CompanyInfoSection(job: JobItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp)
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompanyLogo(letter = job.logoLetter, color = job.logoColor, size = 52)
                Column {
                    Text(job.company, style = JHTypography.HeadingM, color = JHColors.TextPrimary)
                    StatusBadge(label = "VERIFIED ✓", color = JHColors.StatusSuccess)
                }
            }
            GradientDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CompanyStat("500+", "Nhân viên")
                CompanyStat("2012", "Thành lập")
                CompanyStat("4.5★", "Đánh giá")
            }
            Text(
                "Công ty công nghệ hàng đầu Việt Nam với hơn 500 nhân viên, " +
                        "chuyên cung cấp giải pháp phần mềm cho các doanh nghiệp toàn cầu. " +
                        "Môi trường làm việc năng động, sáng tạo và nhiều cơ hội phát triển.",
                style = JHTypography.BodyM,
                color = JHColors.TextSecondary,
                lineHeight = 22.sp
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {}
            ) {
                Text("Xem trang công ty", style = JHTypography.LabelM, color = JHColors.AccentPrimary)
                Icon(Icons.Outlined.OpenInNew, null, tint = JHColors.AccentPrimary, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun CompanyStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = JHTypography.HeadingM, color = JHColors.AccentPrimary)
        Text(label, style = JHTypography.BodyS, color = JHColors.TextMuted)
    }
}

@Composable
private fun BulletItem(text: String, index: Int) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 60L)
        isVisible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "bullet_$index"
    )

    Row(
        modifier = Modifier.alpha(alpha),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(4.dp)
                .clip(CircleShape)
                .background(JHColors.AccentPrimary)
        )
        Text(text, style = JHTypography.BodyM, color = JHColors.TextSecondary, lineHeight = 22.sp)
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.3f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun FloatingApplyButton(
    hasApplied: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "apply_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, JHColors.Background)
                )
            )
            .padding(horizontal = JHSpacing.screen.dp, vertical = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .scale(scale)
                .clip(RoundedCornerShape(JHRadius.lg.dp))
                .background(
                    brush = if (hasApplied) {
                        Brush.horizontalGradient(listOf(JHColors.StatusSuccess, JHColors.AccentGreen))
                    } else {
                        Brush.horizontalGradient(JHColors.GradientButton)
                    }
                )
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(JHRadius.lg.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !hasApplied,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (hasApplied) Icons.Outlined.CheckCircle else Icons.Outlined.Send,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (hasApplied) "ĐÃ ỨNG TUYỂN ✓" else "ỨNG TUYỂN NGAY",
                    style = JHTypography.LabelM,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ApplySuccessToast(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(300),
        label = "toast_alpha"
    )

    Box(
        modifier = modifier
            .alpha(alpha)
            .clip(RoundedCornerShape(JHRadius.full.dp))
            .background(JHColors.StatusSuccess)
            .border(1.dp, JHColors.StatusSuccess.copy(alpha = 0.5f), RoundedCornerShape(JHRadius.full.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.CheckCircle, null, tint = Color.White, modifier = Modifier.size(18.dp))
            Text(
                "Ứng tuyển thành công! CV đã được gửi.",
                style = JHTypography.BodyM,
                color = Color.White
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  Canvas Draw Helpers
// ──────────────────────────────────────────────────────────────────

private fun DrawScope.drawDetailHero(accentColor: Color, pulse: Float) {
    // Dark base
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0D1117),
                Color(0xFF080B14)
            )
        )
    )
    // Company color ambient
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.3f * pulse),
                accentColor.copy(alpha = 0.1f),
                Color.Transparent
            ),
            center = Offset(size.width * 0.7f, size.height * 0.3f),
            radius = 350f
        ),
        radius = 350f,
        center = Offset(size.width * 0.7f, size.height * 0.3f)
    )
    // Grid
    val gridAlpha = 0.04f
    val spacing = 50.dp.toPx()
    var x = 0f
    while (x < size.width) {
        drawLine(Color.White.copy(alpha = gridAlpha), Offset(x, 0f), Offset(x, size.height))
        x += spacing
    }
    var y = 0f
    while (y < size.height) {
        drawLine(Color.White.copy(alpha = gridAlpha), Offset(0f, y), Offset(size.width, y))
        y += spacing
    }
    // Bottom fade to background
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color(0xFF080B14)),
            startY = size.height * 0.5f,
            endY = size.height
        )
    )
}