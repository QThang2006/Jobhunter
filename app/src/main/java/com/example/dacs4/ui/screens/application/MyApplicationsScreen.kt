package com.example.dacs4.ui.screens.application

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs4.data.model.*
import com.example.dacs4.ui.components.*
import com.example.dacs4.ui.theme.*

// ═══════════════════════════════════════════════════════════════════
//  MY APPLICATIONS SCREEN — Phase 2
//  Danh sách đơn ứng tuyển với filter tabs + animated status cards
// ═══════════════════════════════════════════════════════════════════

@Composable
fun MyApplicationsScreen(
    onBack: () -> Unit,
    onApplicationClick: (String) -> Unit,
    viewModel: ApplicationViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMyApplications()
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
                    center = Offset(size.width * 0.8f, 200f), radius = 350f
                ),
                radius = 350f, center = Offset(size.width * 0.8f, 200f)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ── Top Bar ───────────────────────────────────────────
            item {
                ApplicationsTopBar(onBack = onBack)
            }

            // ── Summary Stats ─────────────────────────────────────
            item {
                ApplicationStatsSummary(applications = state.applications)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ── Filter Tabs ───────────────────────────────────────
            item {
                ApplicationFilterTabs(
                    selected = state.filter,
                    counts = buildCountMap(state.applications),
                    onSelect = { viewModel.setFilter(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Section label ─────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = JHSpacing.screen.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${state.filteredApplications.size} đơn ứng tuyển",
                        style = JHTypography.HeadingM,
                        color = JHColors.TextPrimary
                    )
                    if (state.isRefreshing) {
                        DotLoader(modifier = Modifier.height(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Content ───────────────────────────────────────────
            when {
                state.isLoading -> {
                    items(4) {
                        ApplicationCardSkeleton(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp, vertical = 6.dp))
                    }
                }
                !state.error.isNullOrEmpty() -> {
                    item {
                        ErrorState(
                            message = state.error!!,
                            onRetry = { viewModel.loadMyApplications() }
                        )
                    }
                }
                state.filteredApplications.isEmpty() -> {
                    item { EmptyApplicationsState(filter = state.filter) }
                }
                else -> {
                    itemsIndexed(state.filteredApplications) { index, app ->
                        ApplicationCard(
                            application = app,
                            index = index,
                            onClick = { onApplicationClick(app.id) }
                        )
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
private fun ApplicationsTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(JHColors.Background.copy(alpha = 0.95f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = JHSpacing.screen.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                        .background(JHColors.SurfaceElevated)
                        .border(1.dp, JHColors.BorderSubtle, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.ArrowBack, null, tint = JHColors.TextSecondary, modifier = Modifier.size(18.dp))
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Đơn ứng tuyển của tôi", style = JHTypography.HeadingM, color = JHColors.TextPrimary)
                Text("Theo dõi trạng thái ứng tuyển", style = JHTypography.BodyS, color = JHColors.TextSecondary)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.FilterList, null, tint = JHColors.TextSecondary, modifier = Modifier.size(20.dp))
            }
        }
        GradientDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

// ──────────────────────────────────────────────────────────────────
//  STATS SUMMARY
// ──────────────────────────────────────────────────────────────────

@Composable
private fun ApplicationStatsSummary(applications: List<ApplicationResponse>) {
    val total    = applications.size
    val approved = applications.count { it.status == ApplicationStatus.APPROVED }
    val pending  = applications.count { it.status == ApplicationStatus.PENDING || it.status == ApplicationStatus.REVIEWING }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard("Tổng cộng", "$total", JHColors.AccentPrimary, modifier = Modifier.weight(1f))
        StatCard("Đang xử lý", "$pending", JHColors.StatusWarning, modifier = Modifier.weight(1f))
        StatCard("Chấp nhận", "$approved", JHColors.StatusSuccess, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(JHRadius.lg.dp))
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(value, style = JHTypography.DisplayM, color = color)
            Text(label, style = JHTypography.BodyS, color = JHColors.TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  FILTER TABS
// ──────────────────────────────────────────────────────────────────

@Composable
private fun ApplicationFilterTabs(
    selected: ApplicationStatus?,
    counts: Map<ApplicationStatus?, Int>,
    onSelect: (ApplicationStatus?) -> Unit
) {
    data class FilterItem(val status: ApplicationStatus?, val label: String)
    val filters = listOf(
        FilterItem(null, "Tất cả"),
        FilterItem(ApplicationStatus.PENDING, "Chờ duyệt"),
        FilterItem(ApplicationStatus.REVIEWING, "Đang xét"),
        FilterItem(ApplicationStatus.APPROVED, "Chấp nhận"),
        FilterItem(ApplicationStatus.REJECTED, "Từ chối")
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = JHSpacing.screen.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            val isSelected = filter.status == selected
            val count = counts[filter.status] ?: 0
            val filterColor = when (filter.status) {
                ApplicationStatus.PENDING   -> JHColors.StatusWarning
                ApplicationStatus.REVIEWING -> JHColors.AccentPrimary
                ApplicationStatus.APPROVED  -> JHColors.StatusSuccess
                ApplicationStatus.REJECTED  -> JHColors.StatusError
                null -> JHColors.AccentPrimary
            }
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) filterColor else JHColors.SurfaceElevated,
                tween(200), label = "filter_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else JHColors.TextSecondary,
                tween(200), label = "filter_text"
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
                    .clickable { onSelect(filter.status) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(filter.label, style = JHTypography.LabelM, color = textColor)
                    if (count > 0) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White.copy(alpha = 0.25f) else JHColors.SurfaceDeep),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$count", style = JHTypography.LabelS, color = if (isSelected) Color.White else JHColors.TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  APPLICATION CARD
// ──────────────────────────────────────────────────────────────────

@Composable
fun ApplicationCard(
    application: ApplicationResponse,
    index: Int,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "app_card_scale"
    )

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 70L)
        isVisible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(350),
        label = "app_card_alpha"
    )
    val offset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 16f,
        animationSpec = tween(350, easing = FastOutSlowInEasing),
        label = "app_card_offset"
    )

    val statusColor = Color(android.graphics.Color.parseColor(application.status.colorHex))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp, vertical = 6.dp)
            .alpha(alpha)
            .offset(y = offset.dp)
            .scale(scale)
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // ── Header ────────────────────────────────────────────
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
                    // Company logo letter
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(statusColor.copy(alpha = 0.12f))
                            .border(1.dp, statusColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            application.companyName.firstOrNull()?.toString() ?: "?",
                            style = JHTypography.HeadingM,
                            color = statusColor
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            application.jobName,
                            style = JHTypography.HeadingM,
                            color = JHColors.TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            application.companyName,
                            style = JHTypography.BodyS,
                            color = JHColors.TextSecondary
                        )
                    }
                }
                StatusPill(status = application.status)
            }

            GradientDivider()

            // ── Status Timeline ───────────────────────────────────
            ApplicationMiniTimeline(status = application.status)

            // ── Footer ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Outlined.Schedule, null, tint = JHColors.TextMuted, modifier = Modifier.size(12.dp))
                    Text(
                        formatDate(application.createdAt),
                        style = JHTypography.BodyS,
                        color = JHColors.TextMuted
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Chi tiết", style = JHTypography.LabelS, color = JHColors.AccentPrimary)
                    Icon(Icons.Outlined.ChevronRight, null, tint = JHColors.AccentPrimary, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun StatusPill(status: ApplicationStatus) {
    val color = Color(android.graphics.Color.parseColor(status.colorHex))
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(JHRadius.full.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(JHRadius.full.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            if (status == ApplicationStatus.PENDING || status == ApplicationStatus.REVIEWING) {
                val infiniteTransition = rememberInfiniteTransition(label = "status_pulse")
                val dotAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.4f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
                    label = "dot_alpha"
                )
                Box(modifier = Modifier.size(5.dp).alpha(dotAlpha).clip(CircleShape).background(color))
            }
            Text(status.displayName, style = JHTypography.LabelS, color = color)
        }
    }
}

@Composable
private fun ApplicationMiniTimeline(status: ApplicationStatus) {
    val steps = listOf(
        ApplicationStatus.PENDING,
        ApplicationStatus.REVIEWING,
        ApplicationStatus.APPROVED
    )
    val currentIdx = when (status) {
        ApplicationStatus.PENDING   -> 0
        ApplicationStatus.REVIEWING -> 1
        ApplicationStatus.APPROVED  -> 2
        ApplicationStatus.REJECTED  -> -1 // special
    }

    if (status == ApplicationStatus.REJECTED) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(20.dp).clip(CircleShape)
                    .background(JHColors.StatusError.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Close, null, tint = JHColors.StatusError, modifier = Modifier.size(12.dp))
            }
            Text("Không phù hợp với vị trí này", style = JHTypography.BodyS, color = JHColors.StatusError)
        }
        return
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val isDone    = index < currentIdx
            val isCurrent = index == currentIdx
            val color = Color(android.graphics.Color.parseColor(step.colorHex))

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isDone || isCurrent -> color
                            else -> JHColors.SurfaceElevated
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(11.dp))
                } else if (isCurrent) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                }
            }

            if (index < steps.lastIndex) {
                val lineProgress by animateFloatAsState(
                    targetValue = if (isDone) 1f else 0f,
                    animationSpec = tween(600, easing = FastOutSlowInEasing),
                    label = "line_$index"
                )
                Box(
                    modifier = Modifier.weight(1f).height(2.dp)
                        .clip(RoundedCornerShape(JHRadius.full.dp))
                        .background(JHColors.SurfaceElevated)
                ) {
                    Box(
                        modifier = Modifier.fillMaxHeight().fillMaxWidth(lineProgress)
                            .background(color)
                    )
                }
            }
        }
    }

    // Step labels
    Row(modifier = Modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            val isDone    = index < currentIdx
            val isCurrent = index == currentIdx
            val color = Color(android.graphics.Color.parseColor(step.colorHex))
            val textAlign = when (index) {
                0 -> TextAlign.Start
                steps.lastIndex -> TextAlign.End
                else -> TextAlign.Center
            }
            Text(
                step.displayName,
                style = JHTypography.LabelS,
                color = if (isDone || isCurrent) color else JHColors.TextMuted,
                textAlign = textAlign,
                modifier = Modifier.weight(if (index == 0 || index == steps.lastIndex) 0.5f else 1f)
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  SKELETON & EMPTY STATES
// ──────────────────────────────────────────────────────────────────

@Composable
private fun ApplicationCardSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                ShimmerBox(modifier = Modifier.size(44.dp), cornerRadius = 12.dp)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                    ShimmerBox(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp))
                    ShimmerBox(modifier = Modifier.fillMaxWidth(0.4f).height(12.dp))
                }
                ShimmerBox(modifier = Modifier.width(72.dp).height(24.dp), cornerRadius = 99.dp)
            }
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(1.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ShimmerBox(modifier = Modifier.width(100.dp).height(12.dp))
                ShimmerBox(modifier = Modifier.width(60.dp).height(12.dp))
            }
        }
    }
}

@Composable
private fun EmptyApplicationsState(filter: ApplicationStatus?) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            if (filter == null) Icons.Outlined.WorkOff else Icons.Outlined.FilterAltOff,
            null,
            tint = JHColors.TextMuted,
            modifier = Modifier.size(48.dp)
        )
        Text(
            if (filter == null) "Bạn chưa nộp đơn nào" else "Không có đơn ở trạng thái này",
            style = JHTypography.HeadingM,
            color = JHColors.TextSecondary
        )
        Text(
            if (filter == null) "Bắt đầu ứng tuyển ngay hôm nay!" else "Thử chọn bộ lọc khác",
            style = JHTypography.BodyM,
            color = JHColors.TextMuted
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(Icons.Outlined.WifiOff, null, tint = JHColors.StatusError, modifier = Modifier.size(40.dp))
        Text(message, style = JHTypography.BodyM, color = JHColors.TextSecondary, textAlign = TextAlign.Center)
        GhostButton(text = "Thử lại", onClick = onRetry, modifier = Modifier.fillMaxWidth(0.5f), borderColor = JHColors.StatusError, textColor = JHColors.StatusError)
    }
}

// ──────────────────────────────────────────────────────────────────
//  HELPERS
// ──────────────────────────────────────────────────────────────────

private fun buildCountMap(apps: List<ApplicationResponse>): Map<ApplicationStatus?, Int> {
    val map = mutableMapOf<ApplicationStatus?, Int>()
    map[null] = apps.size
    ApplicationStatus.values().forEach { status ->
        map[status] = apps.count { it.status == status }
    }
    return map
}

fun formatDate(isoDate: String): String {
    return try {
        val parts = isoDate.split("T")[0].split("-")
        "${parts[2]}/${parts[1]}/${parts[0]}"
    } catch (e: Exception) { isoDate }
}
