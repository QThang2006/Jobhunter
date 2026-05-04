package com.example.dacs4.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.dacs4.core.utils.AppConstants
import com.example.dacs4.data.model.response.CompanyResponse
import com.example.dacs4.ui.theme.AppColors

@Composable
fun CompanyCard(
    company: CompanyResponse,
    onClick: () -> Unit
) {
    // ✅ FIX: collectIsPressedAsState() auto-resets when finger lifts
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardBg by animateColorAsState(
        targetValue = if (isPressed) AppColors.BgAccentLight else AppColors.BgPrimary,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "companyCardBg"
    )

    Box(
        modifier = Modifier
            .width(148.dp)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(0.5.dp, AppColors.Border, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // ─── Logo with text fallback ───────────────────────
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AppColors.BgSurface)
                    .border(0.5.dp, AppColors.Border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val logoUrl = if (company.logo != null)
                    "${AppConstants.IMAGE_BASE_URL}${company.logo}"
                else null

                if (logoUrl != null) {
                    SubcomposeAsyncImage(
                        model = logoUrl,
                        contentDescription = "${company.name} Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(48.dp).clip(CircleShape)
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
                            else -> CompanyInitial(company.name)
                        }
                    }
                } else {
                    CompanyInitial(company.name)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = company.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = AppColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            if (!company.address.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = company.address,
                    fontSize = 10.sp,
                    color = AppColors.TextHint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CompanyInitial(name: String) {
    val initial = name.firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(AppColors.AccentBlueLight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.AccentBlue
        )
    }
}
