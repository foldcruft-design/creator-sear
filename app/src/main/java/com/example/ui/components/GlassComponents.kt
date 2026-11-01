package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    onClick: (() -> Unit)? = null,
    borderBrush: Brush = Brush.verticalGradient(
        colors = listOf(Color(0x2CFFFFFF), Color(0x09FFFFFF))
    ),
    backgroundBrush: Brush = Brush.verticalGradient(
        colors = listOf(Color(0xD90B0E14), Color(0xE607090D))
    ),
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val clickableModifier = if (onClick != null) {
        val interactionSource = remember { MutableInteractionSource() }
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundBrush)
            .border(BorderStroke(1.dp, borderBrush), shape)
            .then(clickableModifier),
        content = content
    )
}

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 18.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xCC0D1017), Color(0xD9080A0F))
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(listOf(Color(0x22FFFFFF), Color(0x06FFFFFF)))
                ),
                shape
            ),
        content = content
    )
}

@Composable
fun GlassButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isPrimary: Boolean = true,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "btn_press_scale"
    )

    val bgBrush = if (isPrimary) {
        Brush.verticalGradient(
            colors = if (isPressed) {
                listOf(Color(0x30FFFFFF), Color(0x18FFFFFF))
            } else {
                listOf(Color(0x20FFFFFF), Color(0x0DFFFFFF))
            }
        )
    } else {
        Brush.verticalGradient(
            colors = if (isPressed) {
                listOf(Color(0x1EFFFFFF), Color(0x0EFFFFFF))
            } else {
                listOf(Color(0xCC0D1017), Color(0xD9080A0E))
            }
        )
    }

    val borderStroke = if (isPrimary) {
        BorderStroke(1.dp, Brush.verticalGradient(listOf(Color(0x52FFFFFF), Color(0x16FFFFFF))))
    } else {
        BorderStroke(1.dp, Brush.verticalGradient(listOf(Color(0x28FFFFFF), Color(0x0AFFFFFF))))
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color(0x08FFFFFF)
        ),
        border = if (enabled) borderStroke else BorderStroke(1.dp, Color(0x0CFFFFFF)),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 14.dp),
        modifier = modifier
            .scale(scale)
            .height(50.dp)
            .background(
                if (enabled) bgBrush else Brush.linearGradient(listOf(Color(0x0AFFFFFF), Color(0x04FFFFFF))),
                shape
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) GlassTextPrimary else GlassTextMuted.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (enabled) GlassTextPrimary else GlassTextMuted.copy(alpha = 0.5f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
fun GlassBadge(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    active: Boolean = true
) {
    val bg = if (active) Color(0x1AFFFFFF) else Color(0xCC0B0E14)
    val borderCol = if (active) Color(0x38FFFFFF) else Color(0x16FFFFFF)
    val textColor = if (active) GlassTextPrimary else GlassTextSecondary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(BorderStroke(1.dp, borderCol), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
fun GlassSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    title: String,
    valueText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xD90B0E15), Color(0xE607090D))
                )
            )
            .border(BorderStroke(1.dp, Color(0x1EFFFFFF)), RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = GlassTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x14FFFFFF))
                        .border(1.dp, Color(0x28FFFFFF), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = valueText,
                        color = GlassTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xB8FFFFFF),
                    inactiveTrackColor = Color(0x18FFFFFF)
                )
            )
        }
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(shape)
            .background(Color(0xD90A0D13))
            .border(BorderStroke(1.dp, Color(0x24FFFFFF)), shape)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = GlassTextSecondary,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = GlassTextMuted,
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = GlassTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    visualTransformation = visualTransformation,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    cursorBrush = SolidColor(Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailingIcon()
            }
        }
    }
}

/**
 * Telegram-style Scalloped Seal Verified Badge in smoked silver-white aesthetic.
 */
@Composable
fun VerifiedBadge(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    iconSize: Dp = 10.dp,
    showGlow: Boolean = true
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h / 2f
        val outerR = w * 0.48f
        val innerR = w * 0.39f
        val points = 16

        val sealPath = Path()
        for (i in 0 until points) {
            val angle = (i * 2.0 * Math.PI / points) - Math.PI / 2.0
            val r = if (i % 2 == 0) outerR else innerR
            val px = cx + (r * cos(angle)).toFloat()
            val py = cy + (r * sin(angle)).toFloat()
            if (i == 0) {
                sealPath.moveTo(px, py)
            } else {
                sealPath.lineTo(px, py)
            }
        }
        sealPath.close()

        drawPath(
            path = sealPath,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFFFFFF), Color(0xFFD0D6E2)),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )
        )

        if (showGlow) {
            drawPath(
                path = sealPath,
                color = Color(0xAAFFFFFF),
                style = Stroke(width = (w * 0.04f).coerceAtLeast(0.8f), join = StrokeJoin.Round)
            )
        }

        val checkPath = Path().apply {
            moveTo(w * 0.29f, h * 0.52f)
            lineTo(w * 0.44f, h * 0.67f)
            lineTo(w * 0.72f, h * 0.36f)
        }
        drawPath(
            path = checkPath,
            color = Color(0xFF080A0F),
            style = Stroke(
                width = (w * 0.12f).coerceAtLeast(1.6f),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

/**
 * Animated Telegram-style Circular Checkmark in Deep Obsidian / Silver-White contour palette.
 */
@Composable
fun TelegramCheckmark(
    checked: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    onClick: (() -> Unit)? = null
) {
    val progress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "tg_check_progress"
    )

    val strokeProgress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "tg_check_stroke"
    )

    val clickMod = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else Modifier

    Canvas(
        modifier = modifier
            .size(size)
            .scale(0.92f + 0.08f * progress)
            .then(clickMod)
    ) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w / 2f, h / 2f)
        val radius = (w / 2f) * 0.88f

        drawCircle(
            color = Color(0xCC0A0D14),
            radius = radius,
            center = center
        )
        drawCircle(
            color = if (checked) Color(0x90FFFFFF) else Color(0x30FFFFFF),
            radius = radius,
            center = center,
            style = Stroke(width = (w * 0.07f).coerceAtLeast(1.2f))
        )

        if (progress > 0.01f) {
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFF5F7FA), Color(0xFFD2D8E4)),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                ),
                radius = radius * progress.coerceIn(0f, 1f),
                center = center
            )

            val fullCheckPath = Path().apply {
                moveTo(w * 0.28f, h * 0.52f)
                lineTo(w * 0.44f, h * 0.68f)
                lineTo(w * 0.74f, h * 0.35f)
            }
            val partialPath = Path()
            val pathMeasure = PathMeasure()
            pathMeasure.setPath(fullCheckPath, false)
            pathMeasure.getSegment(
                0f,
                pathMeasure.length * strokeProgress.coerceIn(0f, 1f),
                partialPath,
                true
            )

            drawPath(
                path = partialPath,
                color = Color(0xFF080A0F),
                style = Stroke(
                    width = (w * 0.115f).coerceAtLeast(1.8f),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

/**
 * Telegram-style Animated Toggle Switch with smoked-glass dark track, silver-gray contour,
 * and an animated checkmark drawn inside the thumb when enabled.
 */
@Composable
fun TelegramSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbFraction by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tg_switch_thumb"
    )

    val trackColorTop by animateColorAsState(
        targetValue = if (checked) Color(0xFF222938) else Color(0xFF0A0D13),
        animationSpec = tween(180),
        label = "tg_track_top"
    )
    val trackColorBottom by animateColorAsState(
        targetValue = if (checked) Color(0xFF161B26) else Color(0xFF07090E),
        animationSpec = tween(180),
        label = "tg_track_bottom"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) Color(0x88FFFFFF) else Color(0x28FFFFFF),
        animationSpec = tween(180),
        label = "tg_border"
    )

    val switchWidth = 50.dp
    val switchHeight = 28.dp
    val thumbSize = 22.dp

    Box(
        modifier = modifier
            .size(width = switchWidth, height = switchHeight)
            .clip(CircleShape)
            .background(Brush.verticalGradient(listOf(trackColorTop, trackColorBottom)))
            .border(1.dp, borderColor, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onCheckedChange(!checked)
            }
            .padding(horizontal = 3.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val maxOffsetPx = 22f
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (maxOffsetPx * thumbFraction * density).roundToInt(),
                        y = 0
                    )
                }
                .size(thumbSize)
                .clip(CircleShape)
                .background(if (checked) Color.White else Color(0xFF8C94A6))
                .border(
                    width = 1.dp,
                    color = if (checked) Color.White else Color(0x44FFFFFF),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(12.dp)) {
                val w = size.width
                val h = size.height
                if (thumbFraction > 0.2f) {
                    val checkAlpha = ((thumbFraction - 0.2f) / 0.8f).coerceIn(0f, 1f)
                    val path = Path().apply {
                        moveTo(w * 0.18f, h * 0.52f)
                        lineTo(w * 0.42f, h * 0.76f)
                        lineTo(w * 0.84f, h * 0.26f)
                    }
                    val partial = Path()
                    val pm = PathMeasure()
                    pm.setPath(path, false)
                    pm.getSegment(0f, pm.length * checkAlpha, partial, true)

                    drawPath(
                        path = partial,
                        color = Color(0xFF0A0D14).copy(alpha = checkAlpha),
                        style = Stroke(
                            width = (w * 0.18f).coerceAtLeast(1.8f),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}
