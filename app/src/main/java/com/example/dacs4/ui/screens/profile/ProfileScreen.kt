package com.example.dacs4.ui.screens.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.dacs4.ui.theme.*

// ═══════════════════════════════════════════════════════════════════
//  PROFILE SCREEN — Phase 3
//  Avatar hero · Stats · Skills · CV Library · Social · Settings
// ═══════════════════════════════════════════════════════════════════

@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onMyApplications: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadProfile() }

    val scrollState = rememberScrollState()

    // Header parallax opacity
    val headerAlpha by remember {
        derivedStateOf { (1f - scrollState.value / 400f).coerceIn(0f, 1f) }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "profile_ambient")
    val ambientAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(25000, easing = LinearEasing)),
        label = "ambient_angle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JHColors.Background)
    ) {
        when {
            state.isLoading -> ProfileSkeleton()
            state.profile != null -> {
                val profile = state.profile!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // ── Hero Header ───────────────────────────────
                    ProfileHeroHeader(
                        profile = profile,
                        ambientAngle = ambientAngle,
                        headerAlpha = headerAlpha,
                        onEdit = onEditProfile
                    )

                    // ── Stats row ─────────────────────────────────
                    ProfileStatsRow(
                        yearsExp = profile.yearsOfExperience,
                        skillCount = profile.skills.size,
                        isOpen = profile.isLookingForJob
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // ── Bio ───────────────────────────────────────
                    if (profile.bio.isNotBlank()) {
                        ProfileSection(title = "Giới thiệu") {
                            Text(
                                profile.bio,
                                style = JHTypography.BodyM,
                                color = JHColors.TextSecondary,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    // ── Skills ────────────────────────────────────
                    if (profile.skills.isNotEmpty()) {
                        ProfileSection(title = "Kỹ năng") {
                            SkillsFlowRow(skills = profile.skills)
                        }
                    }

                    // ── Desired Job ───────────────────────────────
                    ProfileSection(title = "Mục tiêu nghề nghiệp") {
                        CareerGoalCard(profile = profile)
                    }

                    // ── CV Library ────────────────────────────────
                    ProfileSection(title = "Thư viện CV") {
                        CvLibrarySection()
                    }

                    // ── Social Links ──────────────────────────────
                    if (profile.linkedIn.isNotBlank() || profile.github.isNotBlank()) {
                        ProfileSection(title = "Liên kết") {
                            SocialLinksSection(profile = profile)
                        }
                    }

                    // ── Settings ──────────────────────────────────
                    ProfileSection(title = "Tài khoản") {
                        SettingsSection(
                            onMyApplications = onMyApplications,
                            onLogout = onLogout
                        )
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            !state.error.isNullOrEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(Icons.Outlined.WifiOff, null, tint = JHColors.StatusError, modifier = Modifier.size(48.dp))
                        Text(state.error!!, style = JHTypography.BodyM, color = JHColors.TextSecondary)
                        GradientButton("Thử lại", onClick = { viewModel.loadProfile() }, modifier = Modifier.fillMaxWidth(0.5f))
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  HERO HEADER
// ──────────────────────────────────────────────────────────────────

@Composable
private fun ProfileHeroHeader(
    profile: UserProfile,
    ambientAngle: Float,
    headerAlpha: Float,
    onEdit: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        // Background canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawProfileHero(ambientAngle, headerAlpha)
        }

        // Top bar actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand mark
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(JHColors.GradientButton)),
                    contentAlignment = Alignment.Center
                ) { Text("JH", style = JHTypography.LabelS, color = Color.White) }
                Text("Hồ sơ", style = JHTypography.HeadingM, color = Color.White)
            }
            // Edit button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(JHRadius.lg.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(JHRadius.lg.dp))
                    .clickable(onClick = onEdit)
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Edit, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Text("Chỉnh sửa", style = JHTypography.LabelM, color = Color.White)
                }
            }
        }

        // Avatar + name
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Box {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(JHColors.AccentPrimary, JHColors.AccentSecondary)
                            )
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        profile.name.take(1).uppercase(),
                        style = JHTypography.DisplayM,
                        color = Color.White
                    )
                }
                // Online dot
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(JHColors.StatusSuccess)
                        .border(2.dp, JHColors.Background, CircleShape)
                )
            }
            Column {
                Text(profile.name, style = JHTypography.HeadingL, color = Color.White)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(profile.email, style = JHTypography.BodyS, color = Color.White.copy(alpha = 0.7f))
                }
            }
        }

        // Role badge top-right of avatar area
        if (profile.role != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 28.dp)
                    .clip(RoundedCornerShape(JHRadius.full.dp))
                    .background(JHColors.AccentPrimary.copy(alpha = 0.2f))
                    .border(1.dp, JHColors.AccentPrimary.copy(alpha = 0.4f), RoundedCornerShape(JHRadius.full.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(profile.role.name, style = JHTypography.LabelM, color = JHColors.AccentPrimary)
            }
        }
    }
}

private fun DrawScope.drawProfileHero(angle: Float, alpha: Float) {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF0D1117), Color(0xFF080B14))))
    // Rotating accent orb
    val cx = size.width * 0.7f
    val cy = size.height * 0.4f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF6366F1).copy(alpha = 0.3f * alpha), Color.Transparent),
            center = Offset(cx, cy), radius = 320f
        ),
        radius = 320f, center = Offset(cx, cy)
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF06B6D4).copy(alpha = 0.15f * alpha), Color.Transparent),
            center = Offset(size.width * 0.15f, size.height * 0.8f), radius = 200f
        ),
        radius = 200f, center = Offset(size.width * 0.15f, size.height * 0.8f)
    )
    // Grid
    val g = 50.dp.toPx(); var x = 0f
    while (x < size.width) { drawLine(Color.White.copy(alpha = 0.025f), Offset(x,0f), Offset(x,size.height)); x+=g }
    var y = 0f
    while (y < size.height) { drawLine(Color.White.copy(alpha = 0.025f), Offset(0f,y), Offset(size.width,y)); y+=g }
    // Bottom fade
    drawRect(brush = Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF080B14)), startY = size.height * 0.5f, endY = size.height))
}

// ──────────────────────────────────────────────────────────────────
//  STATS ROW
// ──────────────────────────────────────────────────────────────────

@Composable
private fun ProfileStatsRow(yearsExp: Int, skillCount: Int, isOpen: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ProfileStatBox("${yearsExp}+", "Năm kinh nghiệm", JHColors.AccentPrimary, Modifier.weight(1f))
        ProfileStatBox("$skillCount", "Kỹ năng", JHColors.AccentTertiary, Modifier.weight(1f))
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(JHRadius.lg.dp))
                .background(
                    if (isOpen) JHColors.StatusSuccess.copy(alpha = 0.08f)
                    else JHColors.SurfaceMid
                )
                .border(
                    1.dp,
                    if (isOpen) JHColors.StatusSuccess.copy(alpha = 0.3f) else JHColors.BorderSubtle,
                    RoundedCornerShape(JHRadius.lg.dp)
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (isOpen) "Đang\ntìm việc" else "Không\ntìm việc",
                    style = JHTypography.LabelS,
                    color = if (isOpen) JHColors.StatusSuccess else JHColors.TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ProfileStatBox(value: String, label: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(JHRadius.lg.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = JHTypography.DisplayM, color = color)
            Text(label, style = JHTypography.BodyS, color = JHColors.TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  REUSABLE SECTION WRAPPER
// ──────────────────────────────────────────────────────────────────

@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 20.dp)) {
        Text(title, style = JHTypography.HeadingL, color = JHColors.TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
    GradientDivider(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
    Spacer(modifier = Modifier.height(16.dp))
}

// ──────────────────────────────────────────────────────────────────
//  SKILLS FLOW
// ──────────────────────────────────────────────────────────────────

@Composable
private fun SkillsFlowRow(skills: List<String>) {
    val rows = skills.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { skill -> TechBadge(text = skill) }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  CAREER GOAL CARD
// ──────────────────────────────────────────────────────────────────

@Composable
private fun CareerGoalCard(profile: UserProfile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoRow(Icons.Outlined.Work, "Vị trí mong muốn", profile.desiredPosition.ifBlank { "Chưa cập nhật" }, JHColors.AccentPrimary)
            if (profile.desiredSalary != null) {
                InfoRow(Icons.Outlined.AttachMoney, "Mức lương mong muốn", "$${profile.desiredSalary}/tháng", JHColors.AccentGold)
            }
            if (profile.address.isNotBlank()) {
                InfoRow(Icons.Outlined.LocationOn, "Địa điểm", profile.address, JHColors.AccentTertiary)
            }
            if (profile.phone.isNotBlank()) {
                InfoRow(Icons.Outlined.Phone, "Điện thoại", profile.phone, JHColors.TextSecondary)
            }
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, iconColor: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = iconColor, modifier = Modifier.size(16.dp)) }
        Column {
            Text(label, style = JHTypography.LabelS, color = JHColors.TextMuted)
            Text(value, style = JHTypography.BodyM, color = JHColors.TextPrimary)
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  CV LIBRARY
// ──────────────────────────────────────────────────────────────────

@Composable
private fun CvLibrarySection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        mockSavedCvs.forEach { cv ->
            CvLibraryCard(cv = cv)
        }
        // Upload new CV button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(JHRadius.lg.dp))
                .background(Color.Transparent)
                .drawWithContent {
                    drawContent()
                    drawRoundRect(
                        color = JHColors.AccentPrimary.copy(alpha = 0.4f),
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
                        ),
                        cornerRadius = CornerRadius(JHRadius.lg.dp.toPx())
                    )
                }
                .clickable { }
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Add, null, tint = JHColors.AccentPrimary, modifier = Modifier.size(18.dp))
                Text("Tải CV mới lên", style = JHTypography.LabelM, color = JHColors.AccentPrimary)
            }
        }}}

@Composable
private fun CvLibraryCard(cv: SavedCv) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.lg.dp))
            .padding(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                    .background(JHColors.AccentTertiary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Outlined.Description, null, tint = JHColors.AccentTertiary, modifier = Modifier.size(20.dp)) }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(cv.name, style = JHTypography.BodyM.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                        color = JHColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, false))
                    if (cv.isDefault) {
                        Box(modifier = Modifier
                            .clip(RoundedCornerShape(JHRadius.full.dp))
                            .background(JHColors.AccentGold.copy(alpha = 0.15f))
                            .border(1.dp, JHColors.AccentGold.copy(alpha = 0.4f), RoundedCornerShape(JHRadius.full.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) { Text("Mặc định", style = JHTypography.LabelS, color = JHColors.AccentGold) }
                    }
                }
                Text("Tải lên ${cv.uploadedAt}", style = JHTypography.BodyS, color = JHColors.TextMuted)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.OpenInNew, null, tint = JHColors.AccentPrimary, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.MoreVert, null, tint = JHColors.TextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  SOCIAL LINKS
// ──────────────────────────────────────────────────────────────────

@Composable
private fun SocialLinksSection(profile: UserProfile) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (profile.linkedIn.isNotBlank()) {
            SocialLinkRow("LinkedIn", profile.linkedIn, Icons.Outlined.Link, Color(0xFF0A66C2))
        }
        if (profile.github.isNotBlank()) {
            SocialLinkRow("GitHub", profile.github, Icons.Outlined.Code, JHColors.TextSecondary)
        }
        if (profile.website.isNotBlank()) {
            SocialLinkRow("Website", profile.website, Icons.Outlined.Language, JHColors.AccentPrimary)
        }
    }
}

@Composable
private fun SocialLinkRow(label: String, url: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.lg.dp))
            .clickable {}
            .padding(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = JHTypography.LabelM, color = JHColors.TextPrimary)
                Text(url, style = JHTypography.BodyS, color = JHColors.TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = JHColors.TextMuted, modifier = Modifier.size(18.dp))
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  SETTINGS SECTION
// ──────────────────────────────────────────────────────────────────

@Composable
private fun SettingsSection(onMyApplications: () -> Unit, onLogout: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            Triple(Icons.Outlined.WorkHistory, "Lịch sử ứng tuyển", onMyApplications),
            Triple(Icons.Outlined.Notifications, "Thông báo", {}),
            Triple(Icons.Outlined.Security, "Bảo mật & Mật khẩu", {}),
            Triple(Icons.Outlined.HelpOutline, "Trợ giúp", {})
        ).forEach { (icon, label, onClick) ->
            SettingsRow(icon = icon, label = label, onClick = onClick)
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Logout
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(JHRadius.lg.dp))
                .background(JHColors.StatusError.copy(alpha = 0.08f))
                .border(1.dp, JHColors.StatusError.copy(alpha = 0.25f), RoundedCornerShape(JHRadius.lg.dp))
                .clickable(onClick = onLogout)
                .padding(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Logout, null, tint = JHColors.StatusError, modifier = Modifier.size(18.dp))
                Text("Đăng xuất", style = JHTypography.LabelM, color = JHColors.StatusError)
            }
        }
    }
}

@Composable
private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.lg.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(JHColors.SurfaceElevated), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = JHColors.TextSecondary, modifier = Modifier.size(16.dp))
            }
            Text(label, style = JHTypography.BodyM, color = JHColors.TextPrimary, modifier = Modifier.weight(1f))
            Icon(Icons.Outlined.ChevronRight, null, tint = JHColors.TextMuted, modifier = Modifier.size(18.dp))
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  SKELETON
// ──────────────────────────────────────────────────────────────────

@Composable
private fun ProfileSkeleton() {
    Column(modifier = Modifier.fillMaxSize()) {
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(260.dp), cornerRadius = 0.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(3) { ShimmerBox(modifier = Modifier.weight(1f).height(72.dp)) }
            }
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(20.dp).fillMaxWidth(0.4f))
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(80.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.35f).height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { ShimmerBox(modifier = Modifier.width(70.dp).height(28.dp)) }
            }
        }
    }
}
