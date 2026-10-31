package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.example.ui.theme.AppIcons
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.TikTokOptimizerEngine
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppStrings
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.VideoResolutionPresets
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassSlider
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextSecondary
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    strings: AppStrings,
    currentLanguage: AppLanguage,
    settings: EnhanceSettings,
    onLanguageChange: (AppLanguage) -> Unit,
    onSettingsChange: (EnhanceSettings) -> Unit,
    onResetDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var cacheSizeText by remember { mutableStateOf(TikTokOptimizerEngine.getCacheSizeFormatted(context)) }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val isWideScreen = maxWidth > 600.dp
        val horizontalPadding = if (isWideScreen) 32.dp else 16.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = horizontalPadding, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = strings.settingsTitle,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.settingsSubtitle,
                        color = GlassTextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }

                // 1. Language Selection Glass Card
                SettingsSectionCard(
                    sectionLabel = strings.sectionLanguage,
                    icon = AppIcons.Language
                ) {
                    Text(
                        text = strings.languageLabel,
                        color = GlassTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x12FFFFFF))
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GlassSegmentedOption(
                            label = "Русский (RU)",
                            selected = currentLanguage == AppLanguage.RU,
                            onClick = { onLanguageChange(AppLanguage.RU) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("lang_ru_button")
                        )
                        GlassSegmentedOption(
                            label = "English (EN)",
                            selected = currentLanguage == AppLanguage.EN,
                            onClick = { onLanguageChange(AppLanguage.EN) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("lang_en_button")
                        )
                    }
                }

                // 2. Video Algorithm Calibration Glass Card
                SettingsSectionCard(
                    sectionLabel = strings.sectionEngineParams,
                    icon = AppIcons.Tune
                ) {
                    // Output Resolution Selector (1080p / 2K / 4K) — Uniform Free Glass Buttons
                    Text(
                        text = strings.targetResolutionLabel,
                        color = GlassTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x10FFFFFF))
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GlassSegmentedOption(
                            label = "1080p",
                            selected = settings.targetResolution == VideoResolutionPresets.RES_1080P,
                            onClick = {
                                val autoBitrate = VideoResolutionPresets.optimalSweetSpotBitrate(
                                    VideoResolutionPresets.RES_1080P,
                                    settings.targetFps
                                )
                                onSettingsChange(
                                    settings.copy(
                                        targetResolution = VideoResolutionPresets.RES_1080P,
                                        targetBitrateMbps = autoBitrate
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        GlassSegmentedOption(
                            label = "2K 1440p",
                            selected = settings.targetResolution == VideoResolutionPresets.RES_1440P,
                            onClick = {
                                val autoBitrate = VideoResolutionPresets.optimalSweetSpotBitrate(
                                    VideoResolutionPresets.RES_1440P,
                                    settings.targetFps
                                )
                                onSettingsChange(
                                    settings.copy(
                                        targetResolution = VideoResolutionPresets.RES_1440P,
                                        targetBitrateMbps = autoBitrate
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        GlassSegmentedOption(
                            label = "4K UHD",
                            selected = settings.targetResolution == VideoResolutionPresets.RES_4K,
                            onClick = {
                                val autoBitrate = VideoResolutionPresets.optimalSweetSpotBitrate(
                                    VideoResolutionPresets.RES_4K,
                                    settings.targetFps
                                )
                                onSettingsChange(
                                    settings.copy(
                                        targetResolution = VideoResolutionPresets.RES_4K,
                                        targetBitrateMbps = autoBitrate
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target Frame Rate Selector (30 FPS / 60 FPS / 120 FPS) — Uniform Free Glass Buttons
                    Text(
                        text = strings.targetFpsLabel,
                        color = GlassTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x10FFFFFF))
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GlassSegmentedOption(
                            label = "30 FPS",
                            selected = settings.targetFps == 30,
                            onClick = {
                                val autoBitrate = VideoResolutionPresets.optimalSweetSpotBitrate(
                                    settings.targetResolution,
                                    30
                                )
                                onSettingsChange(
                                    settings.copy(
                                        targetFps = 30,
                                        targetBitrateMbps = autoBitrate
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        GlassSegmentedOption(
                            label = "60 FPS",
                            selected = settings.targetFps == 60,
                            onClick = {
                                val autoBitrate = VideoResolutionPresets.optimalSweetSpotBitrate(
                                    settings.targetResolution,
                                    60
                                )
                                onSettingsChange(
                                    settings.copy(
                                        targetFps = 60,
                                        targetBitrateMbps = autoBitrate
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        GlassSegmentedOption(
                            label = "120 FPS",
                            selected = settings.targetFps == 120,
                            onClick = {
                                val autoBitrate = VideoResolutionPresets.optimalSweetSpotBitrate(
                                    settings.targetResolution,
                                    120
                                )
                                onSettingsChange(
                                    settings.copy(
                                        targetFps = 120,
                                        targetBitrateMbps = autoBitrate
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sharpness Slider
                    GlassSlider(
                        value = settings.sharpness.toFloat(),
                        onValueChange = {
                            onSettingsChange(settings.copy(sharpness = it.roundToInt()))
                        },
                        valueRange = 15f..85f,
                        title = strings.sharpnessLabel,
                        valueText = "${settings.sharpness}%"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Shadow IRE Lift Slider
                    GlassSlider(
                        value = settings.shadowLift.toFloat(),
                        onValueChange = {
                            onSettingsChange(settings.copy(shadowLift = it.roundToInt()))
                        },
                        valueRange = 0f..30f,
                        title = strings.shadowLiftLabel,
                        valueText = "+${settings.shadowLift}%"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bitrate Sweet-Spot Slider (up to 85 Mbps for 4K / 120fps)
                    val maxBitrate = if (settings.is4k) 85.0f else if (settings.is1440p) 55.0f else 28.0f
                    GlassSlider(
                        value = settings.targetBitrateMbps.coerceIn(8.0f, maxBitrate),
                        onValueChange = {
                            val rounded = (it * 10f).roundToInt() / 10f
                            onSettingsChange(settings.copy(targetBitrateMbps = rounded))
                        },
                        valueRange = 8.0f..maxBitrate,
                        title = strings.bitrateSweetSpotLabel,
                        valueText = String.format(Locale.US, "%.1f Mbps", settings.targetBitrateMbps)
                    )
                }

                // 3. Stream Modules & Automation Glass Card
                SettingsSectionCard(
                    sectionLabel = strings.sectionStreamFilters,
                    icon = AppIcons.AutoFixHigh
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassToggleRow(
                            title = if (strings.isRu) "Защита от пережатия TikTok (ByteVC1 Bypass)" else "TikTok Anti-Reencode Matrix (ByteVC1 Bypass)",
                            subtitle = if (strings.isRu) {
                                "32-bit 9-Tap CAS предкомпенсация частот, CABAC High-Profile и удержание битрейта ниже порога пережатия TikTok."
                            } else {
                                "32-bit 9-Tap CAS frequency pre-emphasis, High-Profile CABAC, and peak bitrate guard against TikTok re-encoding."
                            },
                            checked = settings.tiktokAntiCompressionMatrix,
                            onCheckedChange = {
                                onSettingsChange(settings.copy(tiktokAntiCompressionMatrix = it))
                            }
                        )
                        GlassToggleRow(
                            title = strings.preFilterTitle,
                            subtitle = strings.preFilterDesc,
                            checked = settings.vfrToCfrStabilizer,
                            onCheckedChange = {
                                onSettingsChange(settings.copy(vfrToCfrStabilizer = it))
                            }
                        )
                        GlassToggleRow(
                            title = if (strings.isRu) "Оптический поток & Развёртка 120 Гц" else "Optical Flow & 120 Hz Cadence",
                            subtitle = if (strings.isRu) {
                                "Межкадровая калибровка таймингов и генерация промежуточных кадров для честных 60 / 120 FPS."
                            } else {
                                "Inter-frame cadence calibration and frame synthesis for true 60 / 120 FPS output."
                            },
                            checked = settings.motionInterpolation120Hz,
                            onCheckedChange = {
                                onSettingsChange(settings.copy(motionInterpolation120Hz = it))
                            }
                        )
                        GlassToggleRow(
                            title = if (strings.isRu) "Подавление ореолов (Halo Suppression)" else "Subpixel Halo Suppression",
                            subtitle = if (strings.isRu) {
                                "Усиливает микротекстуры и контуры без белых полос вокруг объектов."
                            } else {
                                "Enhances micro-textures and edges without white ringing halos."
                            },
                            checked = settings.haloSuppression,
                            onCheckedChange = {
                                onSettingsChange(settings.copy(haloSuppression = it))
                            }
                        )
                        GlassToggleRow(
                            title = strings.antiBandingTitle,
                            subtitle = strings.antiBandingSub,
                            checked = settings.antiBanding,
                            onCheckedChange = {
                                onSettingsChange(settings.copy(antiBanding = it))
                            }
                        )
                        GlassToggleRow(
                            title = strings.denoiseTitle,
                            subtitle = strings.denoiseSub,
                            checked = settings.sensorDenoise,
                            onCheckedChange = {
                                onSettingsChange(settings.copy(sensorDenoise = it))
                            }
                        )
                        GlassToggleRow(
                            title = strings.rec709Title,
                            subtitle = strings.rec709Sub,
                            checked = settings.rec709ColorMatrix,
                            onCheckedChange = {
                                onSettingsChange(settings.copy(rec709ColorMatrix = it))
                            }
                        )
                        GlassToggleRow(
                            title = strings.autoSaveTitle,
                            subtitle = strings.autoSaveSub,
                            checked = settings.autoSaveToGallery,
                            onCheckedChange = {
                                onSettingsChange(settings.copy(autoSaveToGallery = it))
                            }
                        )
                    }
                }

                // 4. Storage & Reset Glass Card
                SettingsSectionCard(
                    sectionLabel = strings.sectionStorage,
                    icon = AppIcons.Storage
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x10FFFFFF))
                            .border(1.dp, Color(0x1EFFFFFF), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.cacheUsedLabel,
                            color = GlassTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = cacheSizeText,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassButton(
                        text = strings.clearCacheBtn,
                        icon = AppIcons.DeleteSweep,
                        isPrimary = false,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            TikTokOptimizerEngine.clearEnhancedCache(context)
                            cacheSizeText = TikTokOptimizerEngine.getCacheSizeFormatted(context)
                            Toast.makeText(context, strings.cacheClearedToast, Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    GlassButton(
                        text = strings.resetSettingsBtn,
                        icon = AppIcons.RestartAlt,
                        isPrimary = false,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onResetDefaults()
                            Toast.makeText(context, strings.settingsResetToast, Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    sectionLabel: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color(0x38FFFFFF), Color(0x0EFFFFFF))),
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x20FFFFFF))
                        .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = sectionLabel,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun GlassSegmentedOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) {
                    Brush.verticalGradient(listOf(Color(0x3DFFFFFF), Color(0x22FFFFFF)))
                } else {
                    Brush.verticalGradient(listOf(Color(0x08FFFFFF), Color(0x04FFFFFF)))
                }
            )
            .border(
                1.dp,
                if (selected) Color(0x75FFFFFF) else Color(0x16FFFFFF),
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else GlassTextMuted,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GlassToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x0EFFFFFF))
            .border(1.dp, Color(0x1CFFFFFF), RoundedCornerShape(16.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                color = GlassTextMuted,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0x80FFFFFF),
                uncheckedThumbColor = GlassTextMuted,
                uncheckedTrackColor = Color(0x18FFFFFF)
            )
        )
    }
}
