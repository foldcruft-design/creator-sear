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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.RestartAlt
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.TikTokOptimizerEngine
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppStrings
import com.example.domain.model.EnhanceSettings
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
    isPro: Boolean = true,
    onLanguageChange: (AppLanguage) -> Unit,
    onSettingsChange: (EnhanceSettings) -> Unit,
    onActivatePro: ((Boolean) -> Unit)? = null,
    onResetDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var cacheSizeText by remember { mutableStateOf(TikTokOptimizerEngine.getCacheSizeFormatted(context)) }
    var showProDialog by remember { mutableStateOf(false) }

    if (showProDialog) {
        com.example.ui.components.ProSubscriptionDialog(
            isPro = isPro,
            strings = strings,
            onActivatePro = { active ->
                onActivatePro?.invoke(active)
                showProDialog = false
                Toast.makeText(context, strings.proActivatedToast, Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showProDialog = false }
        )
    }

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
                verticalArrangement = Arrangement.spacedBy(18.dp)
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

                // PRO Studio VIP Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x2EFFFFFF), Color(0x12FFFFFF))
                            )
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(Color(0x70FFFFFF), Color(0x20FFFFFF))
                            ),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x25FFFFFF))
                                        .border(1.dp, Color(0x60FFFFFF), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = strings.proStudioTitle,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isPro) strings.proActiveStatus else "4K UHD · 120 FPS · Optical Flow",
                                        color = GlassTextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            com.example.ui.components.ProBadge(
                                text = if (isPro) "VIP" else "PRO",
                                modifier = Modifier
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        GlassButton(
                            text = if (isPro) strings.manageProBtn else strings.activateProBtn,
                            isPrimary = true,
                            onClick = { showProDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 1. Language Selection Glass Card
                SettingsSectionCard(
                    sectionLabel = strings.sectionLanguage,
                    icon = Icons.Default.Language
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
                            .padding(4.dp),
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
                    icon = Icons.Default.Tune
                ) {
                    // Output Resolution Selector (1080p / 2K / 4K)
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
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x10FFFFFF))
                            .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GlassSegmentedOption(
                            label = "1080p FHD",
                            selected = settings.targetResolution == com.example.domain.model.VideoResolutionPresets.RES_1080P,
                            onClick = {
                                val autoBitrate = if (settings.targetFps == 120) 22.0f else if (settings.targetFps == 60) 15.5f else 12.0f
                                onSettingsChange(
                                    settings.copy(
                                        targetResolution = com.example.domain.model.VideoResolutionPresets.RES_1080P,
                                        targetBitrateMbps = autoBitrate
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        GlassSegmentedOption(
                            label = "2K 1440p",
                            badge = "PRO",
                            selected = settings.targetResolution == com.example.domain.model.VideoResolutionPresets.RES_1440P,
                            onClick = {
                                if (!isPro) {
                                    showProDialog = true
                                } else {
                                    val autoBitrate = if (settings.targetFps == 120) 42.0f else if (settings.targetFps == 60) 32.0f else 24.0f
                                    onSettingsChange(
                                        settings.copy(
                                            targetResolution = com.example.domain.model.VideoResolutionPresets.RES_1440P,
                                            targetBitrateMbps = autoBitrate
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        GlassSegmentedOption(
                            label = "4K UHD",
                            badge = "VIP",
                            selected = settings.targetResolution == com.example.domain.model.VideoResolutionPresets.RES_4K,
                            onClick = {
                                if (!isPro) {
                                    showProDialog = true
                                } else {
                                    val autoBitrate = if (settings.targetFps == 120) 75.0f else if (settings.targetFps == 60) 65.0f else 48.0f
                                    onSettingsChange(
                                        settings.copy(
                                            targetResolution = com.example.domain.model.VideoResolutionPresets.RES_4K,
                                            targetBitrateMbps = autoBitrate
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target Frame Rate Selector (30 FPS / 60 FPS / 120 FPS)
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
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x10FFFFFF))
                            .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GlassSegmentedOption(
                            label = "30 FPS",
                            selected = settings.targetFps == 30,
                            onClick = { onSettingsChange(settings.copy(targetFps = 30)) },
                            modifier = Modifier.weight(1f)
                        )
                        GlassSegmentedOption(
                            label = "60 FPS",
                            selected = settings.targetFps == 60,
                            onClick = { onSettingsChange(settings.copy(targetFps = 60)) },
                            modifier = Modifier.weight(1f)
                        )
                        GlassSegmentedOption(
                            label = "120 FPS",
                            badge = "120Hz",
                            selected = settings.targetFps == 120,
                            onClick = {
                                if (!isPro) {
                                    showProDialog = true
                                } else {
                                    onSettingsChange(settings.copy(targetFps = 120))
                                }
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
                    icon = Icons.Rounded.AutoFixHigh
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassToggleRow(
                            title = strings.preFilterTitle,
                            subtitle = strings.preFilterDesc,
                            checked = settings.vfrToCfrStabilizer,
                            onCheckedChange = {
                                onSettingsChange(settings.copy(vfrToCfrStabilizer = it))
                            }
                        )
                        GlassToggleRow(
                            title = "Оптический поток & Анализ движения (120 Hz)",
                            subtitle = "Глубокий межкадровый анализ для устранения стробоскопического эффекта и плавности 120 Гц.",
                            checked = settings.motionInterpolation120Hz,
                            onCheckedChange = {
                                onSettingsChange(settings.copy(motionInterpolation120Hz = it))
                            }
                        )
                        GlassToggleRow(
                            title = "Субпиксельное подавление ореолов (Halo Suppression)",
                            subtitle = "Усиливает микротекстуры без белых контурных линий вокруг объектов.",
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
                    icon = Icons.Default.Storage
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x10FFFFFF))
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
                        icon = Icons.Rounded.DeleteSweep,
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
                        icon = Icons.Rounded.RestartAlt,
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
                    letterSpacing = 0.8.sp
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
    badge: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0x35FFFFFF) else Color.Transparent)
            .border(
                1.dp,
                if (selected) Color(0x70FFFFFF) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                color = if (selected) Color.White else GlassTextMuted,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
            if (badge != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (selected) Color(0x40FFFFFF) else Color(0x18FFFFFF))
                        .border(0.5.dp, Color(0x60FFFFFF), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
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
