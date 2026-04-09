package com.example.dacs4.ui.screens.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs4.data.model.Gender
import com.example.dacs4.ui.components.*
import com.example.dacs4.ui.theme.*

// ═══════════════════════════════════════════════════════════════════
//  EDIT PROFILE SCREEN — Phase 3
//  Sections: Avatar · Thông tin · Mục tiêu · Kỹ năng · Mạng xã hội
// ═══════════════════════════════════════════════════════════════════

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val form    by viewModel.editForm.collectAsState()

    // Init form fields from loaded profile
    LaunchedEffect(uiState.profile) {
        if (uiState.profile != null) viewModel.initEditForm()
    }

    // Navigate back on save success
    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            viewModel.resetUpdateSuccess()
            onBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JHColors.Background)
    ) {
        // Ambient
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(JHColors.AccentSecondary.copy(alpha = 0.1f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.15f), radius = 300f
                ),
                radius = 300f, center = Offset(size.width * 0.9f, size.height * 0.15f)
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top Bar ───────────────────────────────────────────
            EditTopBar(
                isSaving = uiState.isUpdating,
                canSave = form.isValid,
                onBack = onBack,
                onSave = { viewModel.saveProfile() }
            )

            // ── Form Content ──────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = JHSpacing.screen.dp)
                    .padding(bottom = 40.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // ── Avatar ────────────────────────────────────────
                AvatarEditSection(name = form.name)

                Spacer(modifier = Modifier.height(24.dp))

                // ── Basic Info ────────────────────────────────────
                EditSectionHeader("Thông tin cá nhân")
                Spacer(modifier = Modifier.height(12.dp))

                GlassTextField(
                    value = form.name, onValueChange = { viewModel.updateField { copy(name = it) } },
                    label = "Họ và tên *",
                    leadingIcon = { Icon(Icons.Outlined.Person, null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                GlassTextField(
                    value = form.phone, onValueChange = { viewModel.updateField { copy(phone = it) } },
                    label = "Số điện thoại",
                    leadingIcon = { Icon(Icons.Outlined.Phone, null, modifier = Modifier.size(18.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                GlassTextField(
                    value = form.address, onValueChange = { viewModel.updateField { copy(address = it) } },
                    label = "Địa chỉ",
                    leadingIcon = { Icon(Icons.Outlined.LocationOn, null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlassTextField(
                        value = form.age, onValueChange = { viewModel.updateField { copy(age = it) } },
                        label = "Tuổi",
                        leadingIcon = { Icon(Icons.Outlined.Cake, null, modifier = Modifier.size(18.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    GenderSelector(
                        selected = form.gender,
                        onSelect = { viewModel.updateField { copy(gender = it) } },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Bio ───────────────────────────────────────────
                EditSectionHeader("Giới thiệu bản thân")
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(JHRadius.lg.dp))
                        .background(JHColors.SurfaceElevated.copy(alpha = 0.6f))
                        .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.lg.dp))
                        .padding(14.dp)
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = form.bio,
                        onValueChange = { if (it.length <= 500) viewModel.updateField { copy(bio = it) } },
                        textStyle = JHTypography.BodyM.copy(color = JHColors.TextPrimary),
                        cursorBrush = SolidColor(JHColors.AccentPrimary),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                        decorationBox = { inner ->
                            if (form.bio.isEmpty()) Text("Mô tả ngắn về bản thân, kinh nghiệm và mục tiêu...", style = JHTypography.BodyM, color = JHColors.TextMuted)
                            inner()
                        }
                    )
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.End) {
                    Text("${form.bio.length}/500", style = JHTypography.LabelS, color = JHColors.TextMuted)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Career Goals ──────────────────────────────────
                EditSectionHeader("Mục tiêu nghề nghiệp")
                Spacer(modifier = Modifier.height(12.dp))

                GlassTextField(
                    value = form.desiredPosition,
                    onValueChange = { viewModel.updateField { copy(desiredPosition = it) } },
                    label = "Vị trí mong muốn",
                    leadingIcon = { Icon(Icons.Outlined.Work, null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlassTextField(
                        value = form.yearsOfExperience,
                        onValueChange = { viewModel.updateField { copy(yearsOfExperience = it) } },
                        label = "Năm kinh nghiệm",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    GlassTextField(
                        value = form.desiredSalary,
                        onValueChange = { viewModel.updateField { copy(desiredSalary = it) } },
                        label = "Lương mong muốn ($)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Looking for job toggle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(JHRadius.lg.dp))
                        .background(JHColors.SurfaceMid)
                        .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.lg.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Search, null, tint = JHColors.StatusSuccess, modifier = Modifier.size(18.dp))
                            Column {
                                Text("Đang tìm việc", style = JHTypography.BodyM, color = JHColors.TextPrimary)
                                Text("Hiển thị hồ sơ với nhà tuyển dụng", style = JHTypography.BodyS, color = JHColors.TextMuted)
                            }
                        }
                        Switch(
                            checked = form.isLookingForJob,
                            onCheckedChange = { viewModel.updateField { copy(isLookingForJob = it) } },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = JHColors.StatusSuccess,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = JHColors.SurfaceElevated
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Skills ────────────────────────────────────────
                EditSectionHeader("Kỹ năng")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Nhập các kỹ năng cách nhau bằng dấu phẩy", style = JHTypography.BodyS, color = JHColors.TextMuted)
                Spacer(modifier = Modifier.height(10.dp))

                GlassTextField(
                    value = form.skillsRaw,
                    onValueChange = { viewModel.updateField { copy(skillsRaw = it) } },
                    label = "Kotlin, Jetpack Compose, MVVM...",
                    leadingIcon = { Icon(Icons.Outlined.Code, null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Live skill preview
                if (form.skillsRaw.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    val chips = form.skillsRaw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        chips.forEach { TechBadge(text = it) }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Social Links ──────────────────────────────────
                EditSectionHeader("Liên kết mạng xã hội")
                Spacer(modifier = Modifier.height(12.dp))

                GlassTextField(
                    value = form.linkedIn,
                    onValueChange = { viewModel.updateField { copy(linkedIn = it) } },
                    label = "LinkedIn URL",
                    leadingIcon = { Icon(Icons.Outlined.Link, null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                GlassTextField(
                    value = form.github,
                    onValueChange = { viewModel.updateField { copy(github = it) } },
                    label = "GitHub URL",
                    leadingIcon = { Icon(Icons.Outlined.Code, null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                GlassTextField(
                    value = form.website,
                    onValueChange = { viewModel.updateField { copy(website = it) } },
                    label = "Website cá nhân",
                    leadingIcon = { Icon(Icons.Outlined.Language, null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Error
                if (!uiState.error.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(JHRadius.lg.dp))
                            .background(JHColors.StatusError.copy(alpha = 0.1f))
                            .border(1.dp, JHColors.StatusError.copy(alpha = 0.3f), RoundedCornerShape(JHRadius.lg.dp))
                            .padding(12.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.ErrorOutline, null, tint = JHColors.StatusError, modifier = Modifier.size(16.dp))
                            Text(uiState.error!!, style = JHTypography.BodyS, color = JHColors.StatusError)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                GradientButton(
                    text = "LƯU HỒ SƠ",
                    onClick = { viewModel.saveProfile() },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = uiState.isUpdating,
                    enabled = form.isValid
                )
            }
        }
    }
}

@Composable
private fun EditTopBar(isSaving: Boolean, canSave: Boolean, onBack: () -> Unit, onSave: () -> Unit) {
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
        Text("Chỉnh sửa hồ sơ", style = JHTypography.HeadingM, color = JHColors.TextPrimary)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(JHRadius.lg.dp))
                .background(if (canSave && !isSaving) JHColors.AccentPrimary else JHColors.SurfaceElevated)
                .clickable(enabled = canSave && !isSaving, onClick = onSave)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSaving) {
                DotLoader(modifier = Modifier.height(16.dp), color = Color.White)
            } else {
                Text("Lưu", style = JHTypography.LabelM, color = if (canSave) Color.White else JHColors.TextMuted)
            }
        }
    }
    GradientDivider()
}

@Composable
private fun AvatarEditSection(name: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box {
                Box(
                    modifier = Modifier.size(88.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(JHColors.AccentPrimary, JHColors.AccentSecondary)))
                        .border(2.dp, JHColors.BorderMid, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(name.take(1).uppercase().ifEmpty { "?" }, style = JHTypography.DisplayL, color = Color.White)
                }
                Box(
                    modifier = Modifier.size(28.dp).align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(JHColors.AccentPrimary)
                        .border(2.dp, JHColors.Background, CircleShape)
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.CameraAlt, null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
            Text("Chạm để đổi ảnh đại diện", style = JHTypography.BodyS, color = JHColors.AccentPrimary)
        }
    }
}

@Composable
private fun EditSectionHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.width(3.dp).height(18.dp).clip(RoundedCornerShape(JHRadius.full.dp))
            .background(Brush.verticalGradient(listOf(JHColors.AccentPrimary, JHColors.AccentSecondary))))
        Text(title, style = JHTypography.HeadingM, color = JHColors.TextPrimary)
    }
}

@Composable
private fun GenderSelector(selected: Gender, onSelect: (Gender) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .background(JHColors.SurfaceElevated.copy(alpha = 0.6f))
            .border(1.dp, JHColors.BorderSubtle, RoundedCornerShape(JHRadius.lg.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Column {
            Text("Giới tính", style = JHTypography.BodyS, color = JHColors.TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Gender.values().forEach { g ->
                    val isSel = selected == g
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(JHRadius.sm.dp))
                            .background(if (isSel) JHColors.AccentPrimary else Color.Transparent)
                            .border(1.dp, if (isSel) JHColors.AccentPrimary else JHColors.BorderMid, RoundedCornerShape(JHRadius.sm.dp))
                            .clickable { onSelect(g) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(g.displayName, style = JHTypography.LabelS, color = if (isSel) Color.White else JHColors.TextSecondary)
                    }
                }
            }
        }
    }
}
