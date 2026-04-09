package com.example.dacs4.ui.screens.application

import androidx.compose.animation.core.*
import androidx.compose.animation.*
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
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs4.data.model.*
import com.example.dacs4.ui.components.*
import com.example.dacs4.ui.theme.*

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri

// ═══════════════════════════════════════════════════════════════════
//  APPLY JOB SCREEN — Phase 2
//  3-step multi-form: Chọn CV → Cover Letter → Xác nhận
//  Flagship Neo-Brutalism × Glassmorphism
// ═══════════════════════════════════════════════════════════════════

@Composable
fun ApplyJobScreen(
    jobId: String,
    jobName: String,
    companyName: String,
    onBack: () -> Unit,
    onSuccess: (applicationId: String) -> Unit,
    viewModel: ApplicationViewModel = hiltViewModel()
) {
    val formState by viewModel.applyForm.collectAsState()

    // ── FILE PICKER (THÊM MỚI) ────────────────────────────────────
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = it.lastPathSegment ?: "cv.pdf"
            val fileSize = 0L
            viewModel.selectFileFromDevice(it.toString(), fileName, fileSize)
        }
    }

    // Init form on enter
    LaunchedEffect(jobId) {
        viewModel.initApplyForm(jobId, jobName, companyName)
    }

    // Navigate on success
    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            onSuccess(formState.submittedApplicationId ?: "")
        }
    }

    // Ambient background pulse
    val infiniteTransition = rememberInfiniteTransition(label = "apply_ambient")
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "ambient_rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JHColors.Background)
    ) {
        // ── Background ────────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(JHColors.AccentPrimary.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.2f),
                    radius = 380f
                ),
                radius = 380f, center = Offset(size.width * 0.15f, size.height * 0.2f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(JHColors.AccentTertiary.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.8f),
                    radius = 300f
                ),
                radius = 300f, center = Offset(size.width * 0.85f, size.height * 0.8f)
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top Bar ───────────────────────────────────────────
            ApplyTopBar(
                step = formState.currentStep,
                jobName = jobName,
                onBack = {
                    when (formState.currentStep) {
                        ApplyStep.SELECT_CV -> onBack()
                        else -> viewModel.prevStep()
                    }
                }
            )

            // ── Step Progress ─────────────────────────────────────
            ApplyStepProgress(currentStep = formState.currentStep)

            // ── Step Content ──────────────────────────────────────
            AnimatedContent(
                targetState = formState.currentStep,
                transitionSpec = {
                    val forward = targetState.ordinal > initialState.ordinal
                    if (forward) {
                        slideInHorizontally { it } + fadeIn(tween(250)) togetherWith
                                slideOutHorizontally { -it / 2 } + fadeOut(tween(250))
                    } else {
                        slideInHorizontally { -it } + fadeIn(tween(250)) togetherWith
                                slideOutHorizontally { it / 2 } + fadeOut(tween(250))
                    }
                },
                modifier = Modifier.weight(1f),
                label = "apply_step_content"
            ) { step ->
                when (step) {
                    ApplyStep.SELECT_CV -> StepSelectCv(
                        formState = formState,
                        onSelectSaved = { viewModel.selectSavedCv(it) },
                        onSelectFromDevice = {
                            // ── SỬA: GỌI FILE PICKER THAY VÌ MOCK ──
                            filePickerLauncher.launch("application/pdf")
                        },
                        onNext = { viewModel.nextStep() }
                    )
                    ApplyStep.COVER_LETTER -> StepCoverLetter(
                        text = formState.coverLetter,
                        charCount = formState.coverLetterCharCount,
                        onTextChange = { viewModel.updateCoverLetter(it) },
                        onNext = { viewModel.nextStep() },
                        onSkip = { viewModel.nextStep() }
                    )
                    ApplyStep.CONFIRM -> StepConfirm(
                        formState = formState,
                        jobName = jobName,
                        companyName = companyName,
                        isSubmitting = formState.isSubmitting,
                        error = formState.submitError,
                        onEdit = { viewModel.goToStep(it) },
                        onSubmit = {
                            viewModel.submitApplication(jobId, jobName, companyName)
                        }
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  TOP BAR
// ──────────────────────────────────────────────────────────────────

@Composable
private fun ApplyTopBar(step: ApplyStep, jobName: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(JHColors.SurfaceElevated)
                    .border(1.dp, JHColors.BorderSubtle, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = JHColors.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Nộp đơn ứng tuyển", style = JHTypography.LabelM, color = JHColors.TextSecondary)
            Text(
                jobName,
                style = JHTypography.BodyM.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                color = JHColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Placeholder để giữ layout cân bằng
        Box(modifier = Modifier.size(36.dp))
    }
}

// ──────────────────────────────────────────────────────────────────
//  STEP PROGRESS BAR — 3 steps
// ──────────────────────────────────────────────────────────────────

@Composable
private fun ApplyStepProgress(currentStep: ApplyStep) {
    val steps = listOf("Chọn CV", "Giới thiệu", "Xác nhận")
    val currentIdx = currentStep.ordinal

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = JHSpacing.screen.dp)
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            steps.forEachIndexed { index, label ->
                val isDone    = index < currentIdx
                val isCurrent = index == currentIdx

                // Step connector line (bên trái, trừ item đầu)
                if (index > 0) {
                    val lineProgress by animateFloatAsState(
                        targetValue = if (isDone) 1f else 0f,
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                        label = "line_$index"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .align(Alignment.Top)
                            .padding(top = 15.dp)
                            .clip(RoundedCornerShape(JHRadius.full.dp))
                            .background(JHColors.SurfaceElevated)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(lineProgress)
                                .background(
                                    brush = Brush.horizontalGradient(JHColors.GradientButton)
                                )
                        )
                    }
                }

                // Step circle + label
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(if (index == 0 || index == steps.lastIndex) 0.6f else 0.4f)
                ) {
                    val circleColor by animateColorAsState(
                        targetValue = when {
                            isDone    -> JHColors.AccentPrimary
                            isCurrent -> JHColors.AccentPrimary
                            else      -> JHColors.SurfaceElevated
                        },
                        animationSpec = tween(300),
                        label = "circle_color_$index"
                    )
                    val circleScale by animateFloatAsState(
                        targetValue = if (isCurrent) 1.15f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "circle_scale_$index"
                    )

                    Box(
                        modifier = Modifier
                            .scale(circleScale)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(circleColor)
                            .border(
                                width = if (isCurrent) 2.dp else 0.dp,
                                color = if (isCurrent) JHColors.AccentPrimary.copy(alpha = 0.5f) else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Icon(
                                Icons.Outlined.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                style = JHTypography.LabelS,
                                color = if (isCurrent) Color.White else JHColors.TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        style = JHTypography.BodyS,
                        color = if (isCurrent || isDone) JHColors.AccentPrimary else JHColors.TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  STEP 1: CHỌN CV
// ──────────────────────────────────────────────────────────────────

@Composable
private fun StepSelectCv(
    formState: ApplyFormState,
    onSelectSaved: (SavedCv) -> Unit,
    onSelectFromDevice: (String) -> Unit,
    onNext: () -> Unit
) {
    val canProceed = formState.selectedCvSource !is CvSource.None

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = JHSpacing.screen.dp)
    ) {
        Text("Chọn CV của bạn", style = JHTypography.DisplayM, color = JHColors.TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Chọn CV từ thư viện hoặc tải lên từ thiết bị",
            style = JHTypography.BodyM, color = JHColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Upload từ máy ────────────────────────────────────────
        val isDeviceSelected = formState.selectedCvSource is CvSource.FromDevice
        UploadFromDeviceCard(
            isSelected = isDeviceSelected,
            selectedFile = formState.selectedCvSource as? CvSource.FromDevice,
            onClick = { onSelectFromDevice("mock://device/my_cv.pdf") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Divider
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GradientDivider(modifier = Modifier.weight(1f))
            Text("HOẶC CHỌN TỪ THƯ VIỆN", style = JHTypography.LabelS, color = JHColors.TextMuted)
            GradientDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Saved CVs ─────────────────────────────────────────────
        if (formState.isLoadingSavedCvs) {
            repeat(3) {
                ShimmerBox(modifier = Modifier.fillMaxWidth().height(72.dp).padding(bottom = 10.dp))
            }
        } else {
            formState.savedCvs.forEachIndexed { index, cv ->
                var isVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(index * 80L)
                    isVisible = true
                }
                val alpha by animateFloatAsState(
                    targetValue = if (isVisible) 1f else 0f,
                    animationSpec = tween(300),
                    label = "cv_alpha_$index"
                )

                SavedCvCard(
                    cv = cv,
                    isSelected = (formState.selectedCvSource as? CvSource.FromSaved)?.cv?.id == cv.id,
                    onClick = { onSelectSaved(cv) },
                    modifier = Modifier.alpha(alpha)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GradientButton(
            text = "TIẾP THEO → THÊM GIỚI THIỆU",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = canProceed
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun UploadFromDeviceCard(
    isSelected: Boolean,
    selectedFile: CvSource.FromDevice?,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "upload_scale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) JHColors.AccentPrimary else JHColors.BorderSubtle,
        animationSpec = tween(200), label = "upload_border"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(
                if (isSelected)
                    JHColors.AccentPrimary.copy(alpha = 0.08f)
                else JHColors.SurfaceMid
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(JHRadius.xl.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(16.dp)
    ) {
        if (selectedFile != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(JHColors.AccentPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.PictureAsPdf, null,
                        tint = JHColors.AccentPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(selectedFile.fileName, style = JHTypography.BodyM, color = JHColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Đã tải lên từ thiết bị", style = JHTypography.BodyS, color = JHColors.StatusSuccess)
                }
                Icon(Icons.Outlined.CheckCircle, null, tint = JHColors.AccentPrimary, modifier = Modifier.size(20.dp))
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(JHColors.SurfaceElevated)
                        .border(1.dp, JHColors.BorderMid, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Upload, null, tint = JHColors.TextSecondary, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text("Tải CV từ thiết bị", style = JHTypography.BodyM.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold), color = JHColors.TextPrimary)
                    Text("PDF, DOCX — tối đa 5MB", style = JHTypography.BodyS, color = JHColors.TextMuted)
                }
            }
        }
    }
}

@Composable
private fun SavedCvCard(
    cv: SavedCv,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cv_card_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .background(
                if (isSelected) JHColors.AccentPrimary.copy(alpha = 0.08f)
                else JHColors.SurfaceMid
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) JHColors.AccentPrimary else JHColors.BorderSubtle,
                shape = RoundedCornerShape(JHRadius.lg.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // File icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) JHColors.AccentPrimary.copy(alpha = 0.2f)
                        else JHColors.SurfaceElevated
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Description, null,
                    tint = if (isSelected) JHColors.AccentPrimary else JHColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        cv.name,
                        style = JHTypography.BodyM.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                        color = if (isSelected) JHColors.AccentPrimary else JHColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (cv.isDefault) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(JHRadius.full.dp))
                                .background(JHColors.AccentGold.copy(alpha = 0.15f))
                                .border(1.dp, JHColors.AccentGold.copy(alpha = 0.4f), RoundedCornerShape(JHRadius.full.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Mặc định", style = JHTypography.LabelS, color = JHColors.AccentGold)
                        }
                    }
                }
                Text("Tải lên ${cv.uploadedAt}", style = JHTypography.BodyS, color = JHColors.TextMuted)
            }

            if (isSelected) {
                Icon(Icons.Outlined.CheckCircle, null, tint = JHColors.AccentPrimary, modifier = Modifier.size(20.dp))
            } else {
                Box(
                    modifier = Modifier.size(20.dp).clip(CircleShape)
                        .border(1.5.dp, JHColors.BorderMid, CircleShape)
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  STEP 2: COVER LETTER
// ──────────────────────────────────────────────────────────────────

@Composable
private fun StepCoverLetter(
    text: String,
    charCount: Int,
    onTextChange: (String) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val maxChars = 1000

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = JHSpacing.screen.dp)
    ) {
        Text("Thư giới thiệu", style = JHTypography.DisplayM, color = JHColors.TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Viết vài dòng về bản thân và lý do bạn phù hợp với vị trí này",
            style = JHTypography.BodyM, color = JHColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Writing tips
        WritingTipsCard()

        Spacer(modifier = Modifier.height(16.dp))

        // Text area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(JHRadius.xl.dp))
                .background(JHColors.SurfaceMid)
                .border(
                    width = 1.dp,
                    color = if (text.isNotEmpty()) JHColors.AccentPrimary.copy(alpha = 0.4f)
                            else JHColors.BorderSubtle,
                    shape = RoundedCornerShape(JHRadius.xl.dp)
                )
                .padding(16.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = { if (it.length <= maxChars) onTextChange(it) },
                textStyle = JHTypography.BodyM.copy(color = JHColors.TextPrimary),
                cursorBrush = SolidColor(JHColors.AccentPrimary),
                modifier = Modifier.fillMaxSize(),
                decorationBox = { inner ->
                    if (text.isEmpty()) {
                        Text(
                            "Ví dụ: Tôi có 3 năm kinh nghiệm phát triển Android với Kotlin và Jetpack Compose. " +
                            "Tôi đặc biệt ấn tượng với văn hoá engineering tại ${"\n\n"}...",
                            style = JHTypography.BodyM,
                            color = JHColors.TextMuted
                        )
                    }
                    inner()
                }
            )
        }

        // Character count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            val countColor = when {
                charCount > maxChars * 0.9f -> JHColors.StatusWarning
                charCount == maxChars       -> JHColors.StatusError
                else                        -> JHColors.TextMuted
            }
            Text(
                "$charCount / $maxChars",
                style = JHTypography.LabelS, color = countColor
            )
        }

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GhostButton(
                text = "Bỏ qua",
                onClick = onSkip,
                modifier = Modifier.weight(0.4f)
            )
            GradientButton(
                text = "TIẾP THEO →",
                onClick = onNext,
                modifier = Modifier.weight(0.6f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun WritingTipsCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .background(JHColors.AccentPrimary.copy(alpha = 0.06f))
            .border(1.dp, JHColors.AccentPrimary.copy(alpha = 0.2f), RoundedCornerShape(JHRadius.lg.dp))
            .padding(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Outlined.Lightbulb, null, tint = JHColors.AccentGold, modifier = Modifier.size(16.dp).padding(top = 2.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Gợi ý viết thư hiệu quả", style = JHTypography.LabelM, color = JHColors.AccentGold)
                listOf(
                    "Nêu rõ kinh nghiệm liên quan trực tiếp đến vị trí",
                    "Thể hiện sự hiểu biết về sản phẩm của công ty",
                    "Giữ ngắn gọn, súc tích (200–400 từ là lý tưởng)"
                ).forEach { tip ->
                    Text("• $tip", style = JHTypography.BodyS, color = JHColors.TextSecondary)
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  STEP 3: XÁC NHẬN
// ──────────────────────────────────────────────────────────────────

@Composable
private fun StepConfirm(
    formState: ApplyFormState,
    jobName: String,
    companyName: String,
    isSubmitting: Boolean,
    error: String?,
    onEdit: (ApplyStep) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = JHSpacing.screen.dp)
    ) {
        Text("Xác nhận đơn ứng tuyển", style = JHTypography.DisplayM, color = JHColors.TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Kiểm tra thông tin trước khi gửi", style = JHTypography.BodyM, color = JHColors.TextSecondary)

        Spacer(modifier = Modifier.height(24.dp))

        // Job info card
        ConfirmSection(
            title = "Vị trí ứng tuyển",
            onEdit = null
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                        .background(JHColors.AccentPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Work, null, tint = JHColors.AccentPrimary, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(jobName, style = JHTypography.HeadingM, color = JHColors.TextPrimary)
                    Text(companyName, style = JHTypography.BodyS, color = JHColors.TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // CV info card
        ConfirmSection(
            title = "CV đính kèm",
            onEdit = { onEdit(ApplyStep.SELECT_CV) }
        ) {
            val cvName = when (val src = formState.selectedCvSource) {
                is CvSource.FromSaved  -> src.cv.name
                is CvSource.FromDevice -> src.fileName
                is CvSource.None       -> "Chưa chọn"
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                        .background(JHColors.AccentTertiary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Description, null, tint = JHColors.AccentTertiary, modifier = Modifier.size(20.dp))
                }
                Text(cvName, style = JHTypography.BodyM, color = JHColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cover letter card
        ConfirmSection(
            title = "Thư giới thiệu",
            onEdit = { onEdit(ApplyStep.COVER_LETTER) }
        ) {
            if (formState.coverLetter.isEmpty()) {
                Text("Không có (bỏ qua)", style = JHTypography.BodyM, color = JHColors.TextMuted)
            } else {
                Text(
                    formState.coverLetter,
                    style = JHTypography.BodyM,
                    color = JHColors.TextSecondary,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${formState.coverLetterCharCount} ký tự",
                    style = JHTypography.LabelS,
                    color = JHColors.TextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy notice
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(JHRadius.lg.dp))
                .background(JHColors.SurfaceElevated)
                .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.lg.dp))
                .padding(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Outlined.Info, null, tint = JHColors.TextMuted, modifier = Modifier.size(14.dp).padding(top = 1.dp))
                Text(
                    "Thông tin CV của bạn sẽ được gửi trực tiếp đến nhà tuyển dụng. JobHunter không chịu trách nhiệm về quyết định tuyển dụng.",
                    style = JHTypography.BodyS, color = JHColors.TextMuted
                )
            }
        }

        // Error
        if (!error.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(JHRadius.lg.dp))
                    .background(JHColors.StatusError.copy(alpha = 0.1f))
                    .border(1.dp, JHColors.StatusError.copy(alpha = 0.3f), RoundedCornerShape(JHRadius.lg.dp))
                    .padding(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.ErrorOutline, null, tint = JHColors.StatusError, modifier = Modifier.size(16.dp))
                    Text(error, style = JHTypography.BodyS, color = JHColors.StatusError)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit button
        GradientButton(
            text = "GỬI ĐƠN ỨNG TUYỂN",
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            isLoading = isSubmitting,
            gradientColors = listOf(JHColors.AccentPrimary, JHColors.AccentSecondary)
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ConfirmSection(
    title: String,
    onEdit: (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(JHRadius.xl.dp))
            .background(JHColors.SurfaceMid)
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.xl.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = JHTypography.LabelM, color = JHColors.TextMuted)
                if (onEdit != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onEdit)
                    ) {
                        Icon(Icons.Outlined.Edit, null, tint = JHColors.AccentPrimary, modifier = Modifier.size(13.dp))
                        Text("Sửa", style = JHTypography.LabelS, color = JHColors.AccentPrimary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

// BasicTextField import helper
@Composable
private fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: androidx.compose.ui.text.TextStyle,
    cursorBrush: Brush,
    modifier: Modifier,
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        cursorBrush = cursorBrush,
        modifier = modifier,
        decorationBox = decorationBox
    )
}
