package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppStrings
import com.example.domain.model.ProcessingStage
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextSecondary

@Composable
fun ProcessingDialog(
    currentStage: ProcessingStage,
    progress: Float,
    strings: AppStrings = AppStrings(AppLanguage.RU),
    onDismissRequest: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glass_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Dialog(
        onDismissRequest = { /* Modal */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xE614161C), Color(0xF208090C))
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(Color(0x60FFFFFF), Color(0x14FFFFFF))),
                    RoundedCornerShape(30.dp)
                )
                .padding(26.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // High-tech frosted glass aperture spinner
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .rotate(rotation)
                            .border(
                                2.dp,
                                Brush.sweepGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0x20FFFFFF),
                                        Color(0x80FFFFFF),
                                        Color.White
                                    )
                                ),
                                CircleShape
                            )
                    )

                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = strings.stageTitle(currentStage),
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = strings.stageDetail(currentStage),
                    color = GlassTextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Progress Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = strings.stepOfTotal(currentStage.step.coerceAtMost(8)),
                            color = GlassTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color.White,
                        trackColor = Color(0x18FFFFFF)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Steps list
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProcessingStage.values().filter { it != ProcessingStage.COMPLETED }.forEach { stage ->
                        val isDone = stage.step < currentStage.step
                        val isCurrent = stage.step == currentStage.step

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCurrent) Color(0x20FFFFFF) else Color.Transparent)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            } else if (isCurrent) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    color = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(15.dp)
                                        .border(1.dp, Color(0x30FFFFFF), CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = strings.stageTitle(stage),
                                color = if (isCurrent) Color.White else if (isDone) GlassTextSecondary else GlassTextMuted.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
