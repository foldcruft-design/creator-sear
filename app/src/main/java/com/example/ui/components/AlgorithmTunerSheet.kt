package com.example.ui.components

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
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
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
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0x1CFFFFFF), Color(0x0AFFFFFF))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color(0x35FFFFFF), Color(0x0CFFFFFF))),
                RoundedCornerShape(26.dp)
            )
            .padding(22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
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
            text = "Адаптивное сохранение микро-текстур, динамический диапазон теней и битрейт 1080p 60fps.",
            color = GlassTextMuted,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Preset selector carousel
        Text(
            text = "РЕЖИМ ОБРАБОТКИ",
            color = GlassTextMuted,
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
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Color(0x35FFFFFF) else Color(0x10FFFFFF))
                        .border(
                            1.dp,
                            if (isSelected) Color(0x90FFFFFF) else Color(0x1EFFFFFF),
                            RoundedCornerShape(16.dp)
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
                            tint = if (isSelected) Color.White else GlassTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = preset.title,
                            color = if (isSelected) Color.White else GlassTextSecondary,
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
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x10FFFFFF))
                .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = settings.preset.description,
                color = GlassTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Sliders
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
            valueRange = 10f..80f,
            title = "Адаптивная резкость (без ореолов)",
            valueText = "${settings.sharpness}%"
        )

        Spacer(modifier = Modifier.height(12.dp))

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
            valueRange = 0f..30f,
            title = "Защита теней от блочности (+IRE)",
            valueText = "+${settings.shadowLift}%"
        )

        Spacer(modifier = Modifier.height(12.dp))

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
            valueRange = 10f..18f,
            title = "TikTok Bitrate Sweet-Spot",
            valueText = "${settings.targetBitrateMbps} Mbps"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Anti-Banding toggle row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0x12FFFFFF))
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Anti-Banding (Устранение полос градиентов)",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Микро-дизеринг для плавных градиентов в небе и темноте",
                    color = GlassTextMuted,
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
                    checkedTrackColor = Color(0x80FFFFFF),
                    uncheckedThumbColor = GlassTextMuted,
                    uncheckedTrackColor = Color(0x20FFFFFF)
                )
            )
        }
    }
}
