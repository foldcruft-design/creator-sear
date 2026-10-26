package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.PresetType
import kotlin.math.roundToInt

@Composable
fun AlgorithmTunerSheet(
    settings: EnhanceSettings,
    onSettingsChanged: (EnhanceSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0x1EFFFFFF), Color(0x0CFFFFFF))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color(0x33FFFFFF), Color(0x10FFFFFF))),
                RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = IceCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Алгоритмы защиты от сжатия TikTok",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Специальная предварительная обработка компенсирует потери при конвертации серверами TikTok.",
            color = MutedSlate,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Preset selector carousel
        Text(
            text = "ПРЕСЕТ ОПТИМИЗАЦИИ",
            color = IceSilver.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(PresetType.values()) { preset ->
                val isSelected = settings.preset == preset
                val icon: ImageVector = when (preset) {
                    PresetType.TIKTOK_ULTRA -> Icons.Default.Bolt
                    PresetType.NIGHT_AESTHETIC -> Icons.Default.Nightlight
                    PresetType.VLOG_PORTRAIT -> Icons.Default.Face
                    PresetType.HIGH_MOTION -> Icons.Default.Speed
                    PresetType.CUSTOM -> Icons.Default.Tune
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Color(0x3338BDF8) else Color(0x12FFFFFF))
                        .border(
                            1.dp,
                            if (isSelected) Color(0xFF38BDF8) else Color(0x22FFFFFF),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            onSettingsChanged(
                                settings.copy(
                                    preset = preset,
                                    sharpness = preset.defaultSharpness,
                                    shadowLift = preset.defaultShadowLift,
                                    antiBanding = preset.defaultAntiBanding,
                                    vibrance = preset.defaultVibrance,
                                    targetBitrateMbps = preset.defaultBitrateMbps
                                )
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) IceCyan else MutedSlate,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = preset.title,
                            color = if (isSelected) Color.White else IceSilver,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset brief description
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x0EFFFFFF))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = settings.preset.description,
                color = IceSilver.copy(alpha = 0.85f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fine tuning sliders (Available on Custom or expanded)
        GlassSlider(
            value = settings.sharpness.toFloat(),
            onValueChange = {
                onSettingsChanged(
                    settings.copy(
                        sharpness = it.roundToInt(),
                        preset = PresetType.CUSTOM
                    )
                )
            },
            valueRange = 20f..100f,
            title = "Микро-резкость (Unsharp Mask)",
            valueText = "${settings.sharpness}%"
        )

        Spacer(modifier = Modifier.height(10.dp))

        GlassSlider(
            value = settings.shadowLift.toFloat(),
            onValueChange = {
                onSettingsChanged(
                    settings.copy(
                        shadowLift = it.roundToInt(),
                        preset = PresetType.CUSTOM
                    )
                )
            },
            valueRange = 0f..40f,
            title = "Защита теней от блочности (+IRE)",
            valueText = "+${settings.shadowLift}%"
        )

        Spacer(modifier = Modifier.height(10.dp))

        GlassSlider(
            value = settings.targetBitrateMbps,
            onValueChange = {
                onSettingsChanged(
                    settings.copy(
                        targetBitrateMbps = (it * 10f).roundToInt() / 10f,
                        preset = PresetType.CUSTOM
                    )
                )
            },
            valueRange = 8f..18f,
            title = "TikTok Bitrate Sweet-Spot",
            valueText = "${settings.targetBitrateMbps} Mbps"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Anti-Banding toggle row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x10FFFFFF))
                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Anti-Banding (Устранение ступенек цвета)",
                    color = IceSilver,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Инъекция микро-дизеринга для идеальных градиентов",
                    color = MutedSlate,
                    fontSize = 11.sp
                )
            }
            Switch(
                checked = settings.antiBanding,
                onCheckedChange = {
                    onSettingsChanged(
                        settings.copy(
                            antiBanding = it,
                            preset = PresetType.CUSTOM
                        )
                    )
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = IceCyan,
                    uncheckedThumbColor = MutedSlate,
                    uncheckedTrackColor = Color(0x20FFFFFF)
                )
            )
        }
    }
}
