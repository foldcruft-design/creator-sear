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
import com.example.domain.model.PresetType
import com.example.domain.model.VideoResolutionPresets
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassSlider
import com.example.ui.theme.AppIcons
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

                // 1. Studio Algorithm Presets Card (Obsidian Smoked Glass)
                SettingsSectionCard(
                    sectionLabel = if (strings.isRu) "СТУДИЙНЫЕ ПРЕСЕТЫ АЛГОРИТМА" else "STUDIO ALGORITHM PRESETS",
                    icon = AppIcons.AutoAwesome
                ) {
                    Text(
                        text = if (strings.isRu) {
                            "Выберите калибровку 6-ступенчатого 32-bit шейдера под тип исходного видео:"
                        } else {
                            "Select a 6-stage 32-bit shader calibration profile for your source footage:"
                        },
                        color = GlassTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PresetType.values().forEach { preset ->
                            val isSelected = settings.preset == preset
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) {
                                            Brush.verticalGradient(
                                                listOf(Color(0xE61A202E), Color(0xF010141F))
                                            )
                                        } else {
                                            Brush.verticalGradient(
                                                listOf(Color(0xCC090C12), Color(0xE606080C))
                                            )
                                        }
                                    )
                                    .border(
                                        width = if (isSelected) 1.2.dp else 1.dp,
                                        brush = if (isSelected) {
                                            Brush.verticalGradient(listOf(Color(0x90FFFFFF), Color(0x30FFFFFF)))
                                        } else {
                                            Brush.verticalGradient(listOf(Color(0x22FFFFFF), Color(0x0AFFFFFF)))
                                        },
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        if (preset == PresetType.CUSTOM) {
                                            onSettingsChange(settings.copy(preset = PresetType.CUSTOM))
                                        } else {
                                            val targetFps = if (preset == PresetType.HIGH_MOTION) 120 else settings.targetFps
                                            val autoBitrate = VideoResolutionPresets.optimalSweetSpotBitrate(
                                                settings.targetResolution,
                                                targetFps
                                            )
                                            onSettingsChange(
                                                settings.copy(
                                                    preset = preset,
                                                    targetFps = targetFps,
                                                    sharpness = preset.defaultSharpness,
                                                    microContrast = preset.defaultMicroContrast,
                                                    shadowLift = preset.defaultShadowLift,
                                                    highlightRollOff = preset.defaultHighlightRollOff,
                                                    deblockStrength = preset.defaultDeblock,
                                                    vibrance = preset.defaultVibrance,
                                                    targetBitrateMbps = autoBitrate,
                                                    motionInterpolation120Hz = true,
                                                    tiktokAntiCompressionMatrix = true,
                                                    haloSuppression = true
                                                )
                                            )
                                        }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                                    .testTag("preset_card_${preset.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = preset.displayTitle(strings.isRu),
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            val badgeLabel = when (preset) {
                                                PresetType.TIKTOK_ULTRA -> "BYPASS"
                                                PresetType.HIGH_MOTION -> "120 HZ"
                                                PresetType.NIGHT_AESTHETIC -> "10-BIT"
                                                PresetType.VLOG_PORTRAIT -> "SKIN"
                                                PresetType.CUSTOM -> "MANUAL"
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(
                                                        if (isSelected) Color(0x2EFFFFFF) else Color(0x14FFFFFF)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (isSelected) Color(0x65FFFFFF) else Color(0x24FFFFFF),
                                                        RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = badgeLabel,
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = preset.displayDescription(strings.isRu),
                                            color = if (isSelected) GlassTextSecondary else GlassTextMuted,
                                            fontSize = 11.sp,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Video Algorithm Calibration Glass Card (6-Stage 32-Bit GPU Pipeline)
                SettingsSectionCard(
                    sectionLabel = strings.sectionEngineParams,
                    icon = AppIcons.Tune
                ) {
                    // Output Resolution Selector (1080p / 2K / 4K) — Strictly Equal Smoked Glass Buttons
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
                            .background(Color(0xD907090D))
                            .border(1.dp, Color(0x24FFFFFF), RoundedCornerShape(16.dp))
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

                    // Target Frame Rate Selector (30 FPS / 60 FPS / 120 FPS) — Strictly Equal Smoked Glass Buttons
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
                            .background(Color(0xD907090D))
                            .border(1.dp, Color(0x24FFFFFF), RoundedCornerShape(16.dp))
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

                    // Closed-GOP Keyframe Interval Selector (1s vs 2s) — Uniform Equal Buttons
                    Text(
                        text = if (strings.isRu) "Интервал ключевых I-кадров (Closed-GOP)" else "Closed-GOP Keyframe Cadence",
                        color = GlassTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xD907090D))
                            .border(1.dp, Color(0x24FFFFFF), RoundedCornerShape(16.dp))
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GlassSegmentedOption(
                            label = if (strings.isRu) "GOP 1 сек (Max Crisp)" else "GOP 1 sec (Max Crisp)",
                            selected = settings.keyframeIntervalSec == 1,
                            onClick = {
                                onSettingsChange(settings.copy(keyframeIntervalSec = 1))
                            },
                            modifier = Modifier.weight(1f)
                        )
                        GlassSegmentedOption(
                            label = if (strings.isRu) "GOP 2 сек (Sweet-Spot)" else "GOP 2 sec (Sweet-Spot)",
                            selected = settings.keyframeIntervalSec == 2,
                            onClick = {
                                onSettingsChange(settings.copy(keyframeIntervalSec = 2))
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Sharpness Slider (9-Tap CAS)
                    GlassSlider(
                        value = settings.sharpness.toFloat(),
                        onValueChange = {
                            onSettingsChange(
                                settings.copy(
                                    preset = PresetType.CUSTOM,
                                    sharpness = it.roundToInt()
                                )
                            )
                        },
                        valueRange = 15f..90f,
                        title = strings.sharpnessLabel,
                        valueText = "${settings.sharpness}%"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Mid-Frequency Micro-Contrast Slider (Luma Clarity)
                    GlassSlider(
                        value = settings.microContrast.toFloat(),
                        onValueChange = {
                            onSettingsChange(
                                settings.copy(
                                    preset = PresetType.CUSTOM,
                                    microContrast = it.roundToInt()
                                )
                            )
                        },
                        valueRange = 0f..60f,
                        title = if (strings.isRu) "Микроконтраст текстур (Luma Clarity)" else "Texture Micro-Contrast (Luma Clarity)",
                        valueText = "+${settings.microContrast}%"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Adaptive Source Pre-Deblocking Slider
                    GlassSlider(
                        value = settings.deblockStrength.toFloat(),
                        onValueChange = {
                            onSettingsChange(
                                settings.copy(
                                    preset = PresetType.CUSTOM,
                                    deblockStrength = it.roundToInt()
                                )
                            )
                        },
                        valueRange = 0f..80f,
                        title = if (strings.isRu) "Пре-деблокинг квадратов исходника" else "Adaptive Source Macroblock Deblocking",
                        valueText = "${settings.deblockStrength}%"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Shadow IRE Lift Slider (16–235 IRE Toe Protection)
                    GlassSlider(
                        value = settings.shadowLift.toFloat(),
                        onValueChange = {
                            onSettingsChange(
                                settings.copy(
                                    preset = PresetType.CUSTOM,
                                    shadowLift = it.roundToInt()
                                )
                            )
                        },
                        valueRange = 0f..35f,
                        title = strings.shadowLiftLabel,
                        valueText = "+${settings.shadowLift}%"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5. Highlight Soft-Knee Roll-Off Slider
                    GlassSlider(
                        value = settings.highlightRollOff.toFloat(),
                        onValueChange = {
                            onSettingsChange(
                                settings.copy(
                                    preset = PresetType.CUSTOM,
                                    highlightRollOff = it.roundToInt()
                                )
                            )
                        },
                        valueRange = 0f..50f,
                        title = if (strings.isRu) "Защита светов от выгорания (Soft-Knee)" else "Highlight Soft-Knee Roll-Off",
                        valueText = "${settings.highlightRollOff}%"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 6. Skin-Safe Rec.709 Perceptual Vibrance Slider
                    GlassSlider(
                        value = settings.vibrance.toFloat(),
                        onValueChange = {
                            onSettingsChange(
                                settings.copy(
                                    preset = PresetType.CUSTOM,
                                    vibrance = it.roundToInt()
                                )
                            )
                        },
                        valueRange = 0f..40f,
                        title = if (strings.isRu) "Перцептивная сочность (Skin-Safe Rec.709)" else "Skin-Safe Rec.709 Vibrance",
                        valueText = "+${settings.vibrance}%"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 7. Bitrate Sweet-Spot Slider + Auto Calibrate Button
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

                    Spacer(modifier = Modifier.height(10.dp))

                    val optimalSweetSpot = VideoResolutionPresets.optimalSweetSpotBitrate(
                        settings.targetResolution,
                        settings.targetFps
                    )
                    GlassButton(
                        text = if (strings.isRu) {
                            "Выставить эталон TikTok Bypass (${String.format(Locale.US, "%.1f", optimalSweetSpot)} Mbps)"
                        } else {
                            "Apply TikTok Bypass Sweet-Spot (${String.format(Locale.US, "%.1f", optimalSweetSpot)} Mbps)"
                        },
                        icon = AppIcons.Bolt,
                        isPrimary = false,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onSettingsChange(settings.copy(targetBitrateMbps = optimalSweetSpot))
                        }
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
                                "32-bit 9-Tap CAS предкомпенсация DCT-частот, CABAC High-Profile и удержание битрейта ниже порога пережатия TikTok."
                            } else {
                                "32-bit 9-Tap CAS DCT frequency pre-emphasis, High-Profile CABAC, and peak bitrate guard against TikTok re-encoding."
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

                // 4. Language Selection Glass Card
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
                            .background(Color(0xD907090D))
                            .border(1.dp, Color(0x24FFFFFF), RoundedCornerShape(16.dp))
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

                // 5. Storage & Reset Glass Card
                SettingsSectionCard(
                    sectionLabel = strings.sectionStorage,
                    icon = AppIcons.Storage
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xD907090D))
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
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
                    colors = listOf(Color(0xD90B0E15), Color(0xEB06080C))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color(0x36FFFFFF), Color(0x0EFFFFFF))),
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
                        .background(Color(0xE6121722))
                        .border(1.dp, Color(0x38FFFFFF), RoundedCornerShape(8.dp)),
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
                    Brush.verticalGradient(listOf(Color(0xF01C2230), Color(0xF5111520)))
                } else {
                    Brush.verticalGradient(listOf(Color(0x990A0D14), Color(0xB307090E)))
                }
            )
            .border(
                1.dp,
                if (selected) Color(0x88FFFFFF) else Color(0x18FFFFFF),
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
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xD90A0D14), Color(0xE607090D))
                )
            )
            .border(
                1.dp,
                if (checked) Color(0x36FFFFFF) else Color(0x1AFFFFFF),
                RoundedCornerShape(16.dp)
            )
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
                checkedTrackColor = Color(0xFF232B3D),
                checkedBorderColor = Color(0x80FFFFFF),
                uncheckedThumbColor = GlassTextMuted,
                uncheckedTrackColor = Color(0xFF0A0D13),
                uncheckedBorderColor = Color(0x28FFFFFF)
            )
        )
    }
}
