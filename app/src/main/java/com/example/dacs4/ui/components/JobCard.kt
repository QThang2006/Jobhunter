package com.example.dacs4.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.dacs4.core.utils.AppConstants
import com.example.dacs4.data.model.response.JobResponse
import com.example.dacs4.ui.theme.AppColors

@Composable
fun JobCard(
    job: JobResponse,
    onClick: () -> Unit
) {
    // ✅ FIX: Use interactionSource so "pressed" auto-resets when finger lifts
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardBg by animateColorAsState(
        targetValue = if (isPressed) AppColors.BgAccentLight else AppColors.BgPrimary,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "cardBg"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(0.5.dp, AppColors.Border, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null // disable ripple, we handle it ourselves
            ) { onClick() }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // ─── Company Logo with text fallback ──────────────────
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.BgSurface)
                    .border(0.5.dp, AppColors.Border, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                val logoUrl = if (job.company?.logo != null)
                    "${AppConstants.IMAGE_BASE_URL}${job.company.logo}"
                else null

                if (logoUrl != null) {
                    SubcomposeAsyncImage(
                        model = logoUrl,
                        contentDescription = "Company Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
                            else -> LogoFallback(name = job.company?.name)
                        }
                    }
                } else {
                    LogoFallback(name = job.company?.name)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // ─── Job Info ─────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {

                // Job title
                Text(
                    text = job.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Company name
                Text(
                    text = job.company?.name ?: "Công ty chưa cập nhật",
                    fontSize = 12.sp,
                    color = AppColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tags row — ✅ FIX: location uses formatLocation()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoChip(
                        text = AppConstants.formatLocation(job.location),
                        textColor = AppColors.TextSecondary,
                        bgColor = AppColors.BgSurface
                    )
                    InfoChip(
                        text = AppConstants.formatSalary(job.salary),
                        textColor = AppColors.AccentBlue,
                        bgColor = AppColors.AccentBlueLight
                    )
                }

                // Skills row
                if (!job.skills.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        job.skills.take(3).forEach { skill ->
                            Text(
                                text = skill.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.TextSecondary,
                                modifier = Modifier
                                    .background(AppColors.BgSurface, RoundedCornerShape(20.dp))
                                    .border(0.5.dp, AppColors.Border, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/** ✅ NEW: Text-based fallback when logo is null or fails to load */
@Composable
private fun LogoFallback(name: String?) {
    val initial = name?.firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.AccentBlueLight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.AccentBlue
        )
    }
}

@Composable
fun InfoChip(text: String, textColor: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
