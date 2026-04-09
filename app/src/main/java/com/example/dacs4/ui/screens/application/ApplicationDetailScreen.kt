package com.example.dacs4.ui.screens.application

import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs4.data.model.*
import com.example.dacs4.ui.components.*
import com.example.dacs4.ui.theme.*

// ═══════════════════════════════════════════════════════════════════
//  APPLICATION DETAIL SCREEN — Phase 2
//  Chi tiết một đơn ứng tuyển: timeline đầy đủ + thông tin CV + rút đơn
// ═══════════════════════════════════════════════════════════════════

@Composable
fun ApplicationDetailScreen(
    applicationId: String,
    onBack: () -> Unit,
    onWithdrawSuccess: () -> Unit,
    viewModel: ApplicationViewModel = hiltViewModel()
) {
    val state by viewModel.detailState.collectAsState()
    var showWithdrawDialog by remember { mutableStateOf(false) }

    LaunchedEffect(applicationId) {
        viewModel.loadApplicationDetail(applicationId)
    }

    LaunchedEffect(state.isWithdrawn) {
        if (state.isWithdrawn) {
            onWithdrawSuccess()
        }
    }

    // Withdraw confirmation dialog
    if (showWithdrawDialog) {
        WithdrawConfirmDialog(
            onConfirm = {
                showWithdrawDialog = false
                viewModel.withdrawApplication(applicationId, onSuccess = {})
            },
            onDismiss = { showWithdrawDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JHColors.Background)
    ) {
        when {
            state.isLoading -> DetailLoadingState()

            !state.error.isNullOrEmpty() -> DetailErrorState(
                message = state.error!!,
                onBack = onBack,
                onRetry = { viewModel.loadApplicationDetail(applicationId) }
            )

            state.application != null -> {
                val app = state.application!!
                val statusColor = Color(android.graphics.Color.parseColor(app.status.colorHex))

                Column(modifier = Modifier.fillMaxSize()) {
                    // ── Hero Header ───────────────────────────────
                    ApplicationDetailHero(
                        application = app,
                        statusColor = statusColor,
                        onBack = onBack
                    )

                    // ── Scrollable body ───────────────────────────
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                            .background(JHColors.Background)
                            .padding(top = 24.dp)
                    ) {
                        // Status Timeline
                        DetailSection("Trạng thái") {
                            FullStatusTimeline(status = app.status)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                        Spacer(modifier = Modifier.height(20.dp))

                        // Job Info
                        DetailSection("Thông tin vị trí") {
                            JobInfoPanel(application = app)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                        Spacer(modifier = Modifier.height(20.dp))

                        // CV Section
                        DetailSection("CV đã nộp") {
                            CvPanel(cvUrl = app.url)
                        }

                        // Cover Letter
                        if (!app.coverLetter.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                            Spacer(modifier = Modifier.height(20.dp))
                            DetailSection("Thư giới thiệu") {
                                CoverLetterPanel(text = app.coverLetter)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        GradientDivider(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp))
                        Spacer(modifier = Modifier.height(20.dp))

                        // Timeline log
                        DetailSection("Lịch sử cập nhật") {
                            ApplicationUpdateLog(application = app)
                        }

                        // Bottom actions
                        if (app.status == ApplicationStatus.PENDING || app.status == ApplicationStatus.REVIEWING) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(modifier = Modifier.padding(horizontal = JHSpacing.screen.dp).fillMaxWidth()) {
                                WithdrawButton(
                                    isLoading = state.isWithdrawing,
                                    onClick = { showWithdrawDialog = true }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
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
private fun ApplicationDetailHero(
    application: ApplicationResponse,
    statusColor: Color,
    onBack: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "detail_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "hero_pulse"
    )

    Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D1117), Color(0xFF080B14))
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(statusColor.copy(alpha = 0.25f * pulse), Color.Transparent),
                    center = Offset(size.width * 0.7f, size.height * 0.4f), radius = 300f
                ),
                radius = 300f, center = Offset(size.width * 0.7f, size.height * 0.4f)
            )
            // Grid
            val gridA = 0.035f; val sp = 50.dp.toPx()
            var x = 0f; while (x < size.width) { drawLine(Color.White.copy(alpha = gridA), Offset(x, 0f), Offset(x, size.height)); x += sp }
            var y = 0f; while (y < size.height) { drawLine(Color.White.copy(alpha = gridA), Offset(0f, y), Offset(size.width, y)); y += sp }
            drawRect(brush = Brush.verticalGradient(colors = listOf(Color.Transparent, Color(0xFF080B14)), startY = size.height * 0.55f, endY = size.height))
        }

        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(start = 8.dp, top = 44.dp)
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.ArrowBack, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        // Status badge top-right
        Box(
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 16.dp, top = 52.dp)
        ) {
            StatusPill(status = application.status)
        }

        // Content
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp).padding(bottom = 20.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        application.companyName.firstOrNull()?.toString() ?: "?",
                        style = JHTypography.HeadingL, color = statusColor
                    )
                }
                Column {
                    Text(
                        application.jobName,
                        style = JHTypography.DisplayM,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(application.companyName, style = JHTypography.BodyM, color = Color.White.copy(alpha = 0.7f))
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  FULL STATUS TIMELINE
// ──────────────────────────────────────────────────────────────────

@Composable
private fun FullStatusTimeline(status: ApplicationStatus) {
    val timelineSteps = listOf(
        TimelineStep(
            status = ApplicationStatus.PENDING,
            icon = Icons.Outlined.Send,
            title = "Đã nộp đơn",
            subtitle = "Đơn của bạn đã được gửi thành công"
        ),
        TimelineStep(
            status = ApplicationStatus.REVIEWING,
            icon = Icons.Outlined.ManageSearch,
            title = "Đang xem xét",
            subtitle = "Nhà tuyển dụng đang đánh giá hồ sơ"
        ),
        TimelineStep(
            status = ApplicationStatus.APPROVED,
            icon = Icons.Outlined.CheckCircle,
            title = "Chấp nhận",
            subtitle = "Chúc mừng! Bạn đã qua vòng hồ sơ"
        )
    )

    val currentOrdinal = when (status) {
        ApplicationStatus.PENDING   -> 0
        ApplicationStatus.REVIEWING -> 1
        ApplicationStatus.APPROVED  -> 2
        ApplicationStatus.REJECTED  -> -1
    }

    if (status == ApplicationStatus.REJECTED) {
        // Rejected special display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = JHSpacing.screen.dp)
                .clip(RoundedCornerShape(JHRadius.xl.dp))
                .background(JHColors.StatusError.copy(alpha = 0.08f))
                .border(1.dp, JHColors.StatusError.copy(alpha = 0.25f), RoundedCornerShape(JHRadius.xl.dp))
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape)
                        .background(JHColors.StatusError.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Cancel, null, tint = JHColors.StatusError, modifier = Modifier.size(24.dp))
                }
                Column {
                    Text("Không phù hợp", style = JHTypography.HeadingM, color = JHColors.StatusError)
                    Text(
                        "Cảm ơn bạn đã quan tâm. Hãy tiếp tục ứng tuyển các vị trí khác!",
                        style = JHTypography.BodyS, color = JHColors.TextSecondary
                    )
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier.padding(horizontal = JHSpacing.screen.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        timelineSteps.forEachIndexed { index, step ->
            val isDone    = index < currentOrdinal
            val isCurrent = index == currentOrdinal
            val isPending = index > currentOrdinal
            val stepColor = Color(android.graphics.Color.parseColor(step.status.colorHex))

            var isVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(index * 150L)
                isVisible = true
            }
            val animAlpha by animateFloatAsState(
                targetValue = if (isVisible) 1f else 0f,
                animationSpec = tween(400),
                label = "tl_alpha_$index"
            )

            Row(
                modifier = Modifier.alpha(animAlpha),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Left column: icon + connector line ────────────
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isDone    -> stepColor.copy(alpha = 0.2f)
                                    isCurrent -> stepColor.copy(alpha = 0.15f)
                                    else      -> JHColors.SurfaceElevated
                                }
                            )
                            .border(
                                width = if (isCurrent) 1.5.dp else 1.dp,
                                color = when {
                                    isDone    -> stepColor.copy(alpha = 0.5f)
                                    isCurrent -> stepColor
                                    else      -> JHColors.BorderSubtle
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Icon(Icons.Outlined.Check, null, tint = stepColor, modifier = Modifier.size(18.dp))
                        } else {
                            Icon(
                                step.icon, null,
                                tint = if (isCurrent) stepColor else JHColors.TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Connector line
                    if (index < timelineSteps.lastIndex) {
                        val lineProgress by animateFloatAsState(
                            targetValue = if (isDone) 1f else 0f,
                            animationSpec = tween(600, easing = FastOutSlowInEasing),
                            label = "tl_line_$index"
                        )
                        Box(
                            modifier = Modifier.width(2.dp).height(52.dp)
                                .clip(RoundedCornerShape(JHRadius.full.dp))
                                .background(JHColors.SurfaceElevated)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().fillMaxHeight(lineProgress)
                                    .background(stepColor.copy(alpha = 0.6f))
                            )
                        }
                    }
                }

                // ── Right column: content ─────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp, bottom = if (index < timelineSteps.lastIndex) 0.dp else 0.dp)
                ) {
                    Text(
                        step.title,
                        style = JHTypography.HeadingM,
                        color = when {
                            isPending -> JHColors.TextMuted
                            isCurrent -> stepColor
                            else      -> JHColors.TextPrimary
                        }
                    )
                    Text(
                        step.subtitle,
                        style = JHTypography.BodyS,
                        color = if (isPending) JHColors.TextMuted else JHColors.TextSecondary
                    )
                    if (isCurrent) {
                        Spacer(modifier = Modifier.height(6.dp))
                        StatusBadge(label = "TRẠNG THÁI HIỆN TẠI", color = stepColor)
                    }
                    Spacer(modifier = Modifier.height(if (index < timelineSteps.lastIndex) 12.dp else 0.dp))
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  DETAIL PANELS
// ──────────────────────────────────────────────────────────────────

@Composable
private fun DetailSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            title,
            style = JHTypography.HeadingL,
            color = JHColors.TextPrimary,
            modifier = Modifier.padding(horizontal = JHSpacing.screen.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun JobInfoPanel(application: ApplicationResponse) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp)
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(
                Triple(Icons.Outlined.Work, "Vị trí", application.jobName),
                Triple(Icons.Outlined.Business, "Công ty", application.companyName),
                Triple(Icons.Outlined.LocationOn, "Địa điểm", application.job?.location ?: "—"),
                Triple(Icons.Outlined.AttachMoney, "Mức lương", application.job?.salary?.let { "$$it/tháng" } ?: "Thương lượng")
            ).forEach { (icon, label, value) ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                            .background(JHColors.SurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = JHColors.AccentPrimary, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text(label, style = JHTypography.LabelS, color = JHColors.TextMuted)
                        Text(value, style = JHTypography.BodyM, color = JHColors.TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun CvPanel(cvUrl: String) {
    val fileName = cvUrl.substringAfterLast("/")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp)
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                    .background(JHColors.AccentTertiary.copy(alpha = 0.12f))
                    .border(1.dp, JHColors.AccentTertiary.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.PictureAsPdf, null, tint = JHColors.AccentTertiary, modifier = Modifier.size(24.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    fileName.ifEmpty { "cv_file.pdf" },
                    style = JHTypography.BodyM.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                    color = JHColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("PDF Document", style = JHTypography.BodyS, color = JHColors.TextSecondary)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(JHRadius.lg.dp))
                    .border(1.dp, JHColors.AccentPrimary.copy(alpha = 0.4f), RoundedCornerShape(JHRadius.lg.dp))
                    .clickable { }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.OpenInNew, null, tint = JHColors.AccentPrimary, modifier = Modifier.size(14.dp))
                    Text("Xem", style = JHTypography.LabelS, color = JHColors.AccentPrimary)
                }
            }
        }
    }
}

@Composable
private fun CoverLetterPanel(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp)
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Description, null, tint = JHColors.AccentGold, modifier = Modifier.size(16.dp))
                Text("Thư giới thiệu", style = JHTypography.LabelM, color = JHColors.AccentGold)
            }
            Text(
                text,
                style = JHTypography.BodyM,
                color = JHColors.TextSecondary,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun ApplicationUpdateLog(application: ApplicationResponse) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp)
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LogEntry(
                icon = Icons.Outlined.Send,
                title = "Đã nộp đơn",
                date = formatDate(application.createdAt),
                color = JHColors.AccentPrimary
            )
            if (application.updatedAt != application.createdAt) {
                GradientDivider()
                val updateDesc = when (application.status) {
                    ApplicationStatus.REVIEWING -> "Nhà tuyển dụng đã xem hồ sơ"
                    ApplicationStatus.APPROVED  -> "Đơn được chấp nhận"
                    ApplicationStatus.REJECTED  -> "Đơn không phù hợp"
                    else -> "Cập nhật trạng thái"
                }
                LogEntry(
                    icon = Icons.Outlined.Update,
                    title = updateDesc,
                    date = formatDate(application.updatedAt),
                    color = Color(android.graphics.Color.parseColor(application.status.colorHex))
                )
            }
        }
    }
}

@Composable
private fun LogEntry(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    date: String,
    color: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = JHTypography.BodyM, color = JHColors.TextPrimary)
            Text(date, style = JHTypography.BodyS, color = JHColors.TextMuted)
        }
    }
}

@Composable
private fun WithdrawButton(isLoading: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .background(JHColors.StatusError.copy(alpha = 0.08f))
            .border(1.dp, JHColors.StatusError.copy(alpha = 0.3f), RoundedCornerShape(JHRadius.lg.dp))
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            DotLoader(color = JHColors.StatusError)
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.DeleteOutline, null, tint = JHColors.StatusError, modifier = Modifier.size(18.dp))
                Text("Rút đơn ứng tuyển", style = JHTypography.LabelM, color = JHColors.StatusError)
            }
        }
    }
}

@Composable
private fun WithdrawConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .clip(RoundedCornerShape(JHRadius.xxl.dp))
                .background(JHColors.SurfaceDeep)
                .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xxl.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape)
                        .background(JHColors.StatusError.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.DeleteOutline, null, tint = JHColors.StatusError, modifier = Modifier.size(28.dp))
                }
                Text("Rút đơn ứng tuyển?", style = JHTypography.HeadingL, color = JHColors.TextPrimary, textAlign = TextAlign.Center)
                Text(
                    "Hành động này không thể hoàn tác. Đơn của bạn sẽ bị xoá khỏi danh sách của nhà tuyển dụng.",
                    style = JHTypography.BodyM,
                    color = JHColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
                GradientDivider()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GhostButton(
                        text = "Huỷ",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(JHRadius.lg.dp))
                            .background(JHColors.StatusError)
                            .clickable(onClick = onConfirm)
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Xác nhận rút", style = JHTypography.LabelM, color = Color.White)
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  LOADING & ERROR STATES
// ──────────────────────────────────────────────────────────────────

@Composable
private fun DetailLoadingState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(JHSpacing.screen.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(220.dp), cornerRadius = 0.dp)
        repeat(3) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(80.dp))
        }
    }
}

@Composable
private fun DetailErrorState(message: String, onBack: () -> Unit, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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

// ──────────────────────────────────────────────────────────────────
//  DATA
// ──────────────────────────────────────────────────────────────────

private data class TimelineStep(
    val status: ApplicationStatus,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val subtitle: String
)
