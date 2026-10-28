package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderGlow
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassButtonBg
import com.example.ui.theme.GlassButtonBgPressed
import com.example.ui.theme.GlassButtonBorder
import com.example.ui.theme.GlassObsidian
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    onClick: (() -> Unit)? = null,
    borderBrush: Brush = Brush.verticalGradient(
        colors = listOf(Color(0x40FFFFFF), Color(0x0EFFFFFF))
    ),
    backgroundBrush: Brush = Brush.verticalGradient(
        colors = listOf(Color(0x1EFFFFFF), Color(0x0AFFFFFF))
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
                    colors = listOf(Color(0x16FFFFFF), Color(0x06FFFFFF))
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(listOf(Color(0x28FFFFFF), Color(0x06FFFFFF)))
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

    // Smooth spring animation on button click/press
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "btn_press_scale"
    )

    val bgBrush = if (isPrimary) {
        Brush.verticalGradient(
            colors = if (isPressed) {
                listOf(Color(0x45FFFFFF), Color(0x25FFFFFF))
            } else {
                listOf(Color(0x2CFFFFFF), Color(0x14FFFFFF))
            }
        )
    } else {
        Brush.verticalGradient(
            colors = if (isPressed) {
                listOf(Color(0x30FFFFFF), Color(0x16FFFFFF))
            } else {
                listOf(Color(0x18FFFFFF), Color(0x08FFFFFF))
            }
        )
    }

    val borderStroke = if (isPrimary) {
        BorderStroke(1.dp, Brush.verticalGradient(listOf(Color(0x60FFFFFF), Color(0x1CFFFFFF))))
    } else {
        BorderStroke(1.dp, Brush.verticalGradient(listOf(Color(0x30FFFFFF), Color(0x0CFFFFFF))))
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color(0x0AFFFFFF)
        ),
        border = if (enabled) borderStroke else BorderStroke(1.dp, Color(0x10FFFFFF)),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 15.dp),
        modifier = modifier
            .scale(scale)
            .heightIn(min = 52.dp)
            .background(if (enabled) bgBrush else Brush.linearGradient(listOf(Color(0x0EFFFFFF), Color(0x05FFFFFF))), shape)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) GlassTextPrimary else GlassTextMuted.copy(alpha = 0.5f),
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = if (enabled) GlassTextPrimary else GlassTextMuted.copy(alpha = 0.5f),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.3.sp
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
    val bg = if (active) Color(0x22FFFFFF) else Color(0x0DFFFFFF)
    val borderCol = if (active) Color(0x45FFFFFF) else Color(0x18FFFFFF)
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
                fontWeight = FontWeight.Medium
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
            .background(Color(0x12FFFFFF))
            .border(BorderStroke(1.dp, Color(0x22FFFFFF)), RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = GlassTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = valueText,
                    color = GlassTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xD0FFFFFF),
                    inactiveTrackColor = Color(0x22FFFFFF)
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
            .heightIn(min = 52.dp)
            .clip(shape)
            .background(Color(0x14FFFFFF))
            .border(BorderStroke(1.dp, Color(0x2AFFFFFF)), shape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
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
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = GlassTextMuted,
                        fontSize = 14.sp
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
 * Noticeable glowing verified checkmark badge that indicates a registered creator account.
 */
@Composable
fun VerifiedBadge(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    iconSize: Dp = 10.dp,
    showGlow: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF00E5FF), Color(0xFF0077FE))
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    if (showGlow) Color(0xD0FFFFFF) else Color(0x60FFFFFF)
                ),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Verified Account",
            tint = Color(0xFF04070D),
            modifier = Modifier.size(iconSize)
        )
    }
}

