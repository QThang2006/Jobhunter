package com.example.dacs4.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.*
import com.example.dacs4.ui.theme.*

// ═══════════════════════════════════════════════════════════════════
//  JOBHUNTER FLAGSHIP COMPONENT LIBRARY
//  Glassmorphism × Neo-Brutalism
// ═══════════════════════════════════════════════════════════════════

// ──────────────────────────────────────────────────────────────────
//  1. GLASS CARD COMPONENT
// ──────────────────────────────────────────────────────────────────
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    glowColor: Color = JHColors.GlowPrimary,
    borderColor: Color = JHColors.BorderSubtle,
    elevation: Float = 0f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x1AFFFFFF),
                        Color(0x0DFFFFFF)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
                shape = RoundedCornerShape(JHRadius.xl.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x40FFFFFF),
                        Color(0x10FFFFFF)
                    )
                ),
                shape = RoundedCornerShape(JHRadius.xl.dp)
            )
            .clip(RoundedCornerShape(JHRadius.xl.dp)),
        content = content
    )
}

// ──────────────────────────────────────────────────────────────────
//  2. GRADIENT BUTTON
// ──────────────────────────────────────────────────────────────────
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    gradientColors: List<Color> = JHColors.GradientButton,
    textStyle: androidx.compose.ui.text.TextStyle = JHTypography.LabelM
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "button_scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.5f,
        label = "button_alpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .alpha(alpha)
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .background(
                brush = Brush.horizontalGradient(gradientColors)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            )
            .padding(vertical = 16.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Text(
                    text = "Đang xử lý...",
                    style = textStyle,
                    color = Color.White
                )
            }
        } else {
            Text(
                text = text,
                style = textStyle,
                color = Color.White
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────
//  3. GHOST BUTTON (Outlined variant)
// ──────────────────────────────────────────────────────────────────
@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = JHColors.BorderMid,
    textColor: Color = JHColors.TextPrimary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "ghost_button_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(JHRadius.lg.dp)
            )
            .background(Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(vertical = 16.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = JHTypography.LabelM,
            color = textColor
        )
    }
}

// ──────────────────────────────────────────────────────────────────
//  4. TECH BADGE / TAG
// ──────────────────────────────────────────────────────────────────
@Composable
fun TechBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = JHColors.SurfaceElevated,
    textColor: Color = JHColors.TextAccent,
    borderColor: Color = JHColors.BorderAccent
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(JHRadius.sm.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(JHRadius.sm.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = JHTypography.LabelS,
            color = textColor
        )
    }
}

// ──────────────────────────────────────────────────────────────────
//  5. STATUS DOT INDICATOR
// ──────────────────────────────────────────────────────────────────
@Composable
fun StatusBadge(
    label: String,
    color: Color = JHColors.StatusSuccess,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "status_alpha"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(JHRadius.full.dp))
            .background(color.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.3f),
                shape = RoundedCornerShape(JHRadius.full.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = JHTypography.LabelS,
            color = color
        )
    }
}

// ──────────────────────────────────────────────────────────────────
//  6. GLASS TEXT FIELD
// ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation =
        androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions =
        androidx.compose.foundation.text.KeyboardOptions.Default,
    isError: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0.3f,
        label = "border_alpha"
    )
    val borderColor = if (isError) JHColors.StatusError
    else if (isFocused) JHColors.AccentPrimary
    else JHColors.BorderSubtle

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(JHRadius.lg.dp))
            .background(
                color = JHColors.SurfaceElevated.copy(alpha = 0.6f)
            )
            .border(
                width = 1.dp,
                color = borderColor.copy(alpha = borderAlpha),
                shape = RoundedCornerShape(JHRadius.lg.dp)
            )
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    style = JHTypography.BodyM,
                    color = if (isFocused) JHColors.AccentPrimary else JHColors.TextMuted
                )
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent,
                focusedTextColor = JHColors.TextPrimary,
                unfocusedTextColor = JHColors.TextPrimary,
                cursorColor = JHColors.AccentPrimary,
                focusedLeadingIconColor = JHColors.AccentPrimary,
                unfocusedLeadingIconColor = JHColors.TextMuted,
                focusedTrailingIconColor = JHColors.AccentPrimary,
                unfocusedTrailingIconColor = JHColors.TextMuted
            )
        )
    }
}

// ──────────────────────────────────────────────────────────────────
//  7. SALARY CHIP
// ──────────────────────────────────────────────────────────────────
@Composable
fun SalaryChip(
    salary: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(JHRadius.sm.dp))
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        JHColors.AccentGold.copy(alpha = 0.2f),
                        JHColors.AccentGold.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = JHColors.AccentGold.copy(alpha = 0.4f),
                shape = RoundedCornerShape(JHRadius.sm.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = salary,
            style = JHTypography.LabelM,
            color = JHColors.AccentGold
        )
    }
}

// ──────────────────────────────────────────────────────────────────
//  8. SHIMMER LOADING SKELETON
// ──────────────────────────────────────────────────────────────────
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = JHRadius.md.dp
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_x"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        JHColors.SurfaceElevated,
                        Color(0xFF243050),
                        JHColors.SurfaceElevated
                    ),
                    start = Offset(translateX, 0f),
                    end = Offset(translateX + 300f, 0f)
                )
            )
    )
}

// ──────────────────────────────────────────────────────────────────
//  9. DIVIDER with GRADIENT
// ──────────────────────────────────────────────────────────────────
@Composable
fun GradientDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        JHColors.BorderMid,
                        Color.Transparent
                    )
                )
            )
    )
}

// ──────────────────────────────────────────────────────────────────
//  10. ANIMATED DOT LOADER
// ──────────────────────────────────────────────────────────────────
@Composable
fun DotLoader(
    modifier: Modifier = Modifier,
    color: Color = JHColors.AccentPrimary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, delayMillis = index * 150),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_scale_$index"
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

// Helper extension for onFocusChanged
fun Modifier.onFocusChanged(onFocusChanged: (androidx.compose.ui.focus.FocusState) -> Unit): Modifier {
    return this.then(
        Modifier.composed {
            var lastFocusState by remember { mutableStateOf(false) }
            this.focusable()
        }
    )
}