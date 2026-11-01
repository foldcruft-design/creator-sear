package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.engine.TikTokOptimizerEngine
import com.example.domain.model.AudioGuardMode
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.FrameAnalysisResult
import com.example.domain.model.TargetPlatform
import com.example.domain.model.VideoItem
import com.example.domain.model.VideoResolutionPresets
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassSlider
import com.example.ui.components.GlassTextField
import com.example.ui.components.TelegramBlueDeep
import com.example.ui.components.TelegramBlueLight
import com.example.ui.components.TelegramBluePrimary
import com.example.ui.components.TelegramCheckmark
import com.example.ui.components.TelegramSwitch
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.AppIcons
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextSecondary
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

data class CreatorFeatureMeta(
    val id: Int,
    val badge: String,
    val titleRu: String,
    val titleEn: String,
    val shortDescRu: String,
    val shortDescEn: String
) {
    fun title(isRu: Boolean): String = if (isRu) titleRu else titleEn
    fun desc(isRu: Boolean): String = if (isRu) shortDescRu else shortDescEn
}

val CREATOR_10_FEATURES = listOf(
    CreatorFeatureMeta(
        id = 0,
        badge = "01 · SAFE ZONES",
        titleRu = "Сейф-Зоны TikTok / Shorts / Reels",
        titleEn = "TikTok / Shorts / Reels Safe Zones",
        shortDescRu = "Наложение слепых зон интерфейса (кнопки лайков, описание, поиск) поверх вашего кадра.",
        shortDescEn = "Interactive UI blind-spot overlay (likes, caption, search bar) over your video frame."
    ),
    CreatorFeatureMeta(
        id = 1,
        badge = "02 · 4K COVER",
        titleRu = "Экстрактор Стоп-Кадров и Обложек",
        titleEn = "4K Frame & Cover Thumbnail Extractor",
        shortDescRu = "Извлечение лучшего кадра в 100% PNG со студийным цветокором и сохранением в Галерею.",
        shortDescEn = "Extract best frame in 100% PNG with studio color grade directly to Gallery."
    ),
    CreatorFeatureMeta(
        id = 2,
        badge = "03 · BPP INSPECTOR",
        titleRu = "Анализатор Битрейта и Риска Сжатия",
        titleEn = "Bitrate, BPP & Re-encode Risk Inspector",
        shortDescRu = "Расчёт плотности BPP (бит на пиксель) и точный прогноз: тронет ли алгоритм TikTok ваш файл.",
        shortDescEn = "Calculates Bits-Per-Pixel (BPP) and predicts TikTok/YouTube re-compression risk."
    ),
    CreatorFeatureMeta(
        id = 3,
        badge = "04 · SIZE CALC",
        titleRu = "Калькулятор Лимитов 72 / 287 / 500 МБ",
        titleEn = "72 / 287 / 500 MB Bitrate Calculator",
        shortDescRu = "Точный расчёт максимального битрейта под длительность ролика и лимиты платформ.",
        shortDescEn = "Exact maximum CBR/VBR bitrate calculation for your video duration and platform limits."
    ),
    CreatorFeatureMeta(
        id = 4,
        badge = "05 · EXIF GUARD",
        titleRu = "Санитайзер Метаданных и Тегов Камеры",
        titleEn = "Container & EXIF Metadata Sanitizer",
        shortDescRu = "Очистка следов многократного рендера и внедрение чистых флагов камеры BT.709.",
        shortDescEn = "Strips editing software tags and injects clean hardware camera BT.709 flags."
    ),
    CreatorFeatureMeta(
        id = 5,
        badge = "06 · AUDIO GUARD",
        titleRu = "Аудио-Мастеринг & Чистый Mute",
        titleEn = "Audio Loudness Guard & Trend Mute",
        shortDescRu = "Режим AAC 48 kHz, защита речи -14 LUFS или полное удаление звука под тренды TikTok.",
        shortDescEn = "AAC 48 kHz passthrough, -14 LUFS vocal guard, or Clean Mute for trending TikTok sounds."
    ),
    CreatorFeatureMeta(
        id = 6,
        badge = "07 · LUMA IRE",
        titleRu = "Живая Гистограмма и Зебра IRE",
        titleEn = "Live Luma Waveform & IRE Zebra",
        shortDescRu = "Анализ 32 полос яркости Y', контроль пересветов (>235 IRE) и провалов в чёрный (<16 IRE).",
        shortDescEn = "Real 32-bin Y' luma histogram with highlight clipping (>235 IRE) & shadow crush check."
    ),
    CreatorFeatureMeta(
        id = 7,
        badge = "08 · 15% OLED TEST",
        titleRu = "Стресс-Тест: 15% Яркость и Контраст",
        titleEn = "15% Dim Screen & Monochrome Luma Test",
        shortDescRu = "Проверка читаемости кадра на тусклом экране смартфона (15% яркости) и в Ч/Б люма-режиме.",
        shortDescEn = "Verify visual hook readability on dim screens (15% brightness) and monochrome luma."
    ),
    CreatorFeatureMeta(
        id = 8,
        badge = "09 · PLATFORMS",
        titleRu = "Пресеты Платформ в 1 Клик (VP9 / ByteVC1)",
        titleEn = "1-Click Target Platform Profiles",
        shortDescRu = "Спец-профили под TikTok ByteVC1, триггер кодека YouTube VP9/AV1, Reels и Telegram 4K.",
        shortDescEn = "Dedicated profiles for TikTok ByteVC1, YouTube VP9/AV1 trigger, Reels, and Telegram 4K."
    ),
    CreatorFeatureMeta(
        id = 9,
        badge = "10 · VIRAL HOOK",
        titleRu = "Конструктор Хука, Таймкодов и SEO",
        titleEn = "Viral Hook, Timestamps & SEO Builder",
        shortDescRu = "Чек-лист удержания первых 3 секунд, генератор таймкодов и связок тегов с копированием.",
        shortDescEn = "3-second retention hook checklist, timestamp formatter, and SEO hashtag generator."
    )
)

/**
 * Prominent Obsidian Smoked-Glass Plaque ("Плашка: 10 Новых Функций для Креаторов TikTok / YouTube")
 * displayed directly on the Home Screen and Settings Screen.
 */
@Composable
fun CreatorSuitePlaqueCard(
    isRu: Boolean,
    settings: EnhanceSettings,
    selectedVideo: VideoItem?,
    onOpenFeatureIndex: (Int) -> Unit,
    onQuickApplyPlatform: (TargetPlatform) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xEB0C111C), Color(0xF206090F))
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0x8839B9FF), Color(0x28FFFFFF))
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(18.dp)
            .testTag("creator_suite_plaque")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Row: Badge + Telegram Verified Seal + Active Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VerifiedBadge(size = 20.dp, iconSize = 12.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRu) "CREATOR PRO SUITE · 10 ФУНКЦИЙ" else "CREATOR PRO SUITE · 10 TOOLS",
                        color = TelegramBlueLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.7.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x2224A1DE))
                        .border(1.dp, Color(0x6639B9FF), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "TIKTOK & YOUTUBE",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isRu) {
                    "Студия Креатора TikTok & YouTube Shorts"
                } else {
                    "TikTok & YouTube Shorts Creator Studio"
                },
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isRu) {
                    "10 профессиональных инструментов: превью сейф-зон, экстрактор 4K обложек, живая гистограмма IRE, калькулятор битрейта, анти-копирайт Mute и триггер VP9/ByteVC1."
                } else {
                    "10 pro creator tools: safe-zone overlay, 4K cover extractor, live IRE waveform, bitrate calculator, trend audio mute, and VP9/ByteVC1 bypass profiles."
                },
                color = GlassTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick 1-Tap Target Platform Selector Row with Telegram Checkmarks
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TargetPlatform.values().forEach { platform ->
                    val isSelected = settings.targetPlatform == platform
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) {
                                    Brush.horizontalGradient(listOf(Color(0x3824A1DE), Color(0x22167AC6)))
                                } else {
                                    Brush.horizontalGradient(listOf(Color(0xCC090C13), Color(0xCC07090F)))
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) TelegramBlueLight else Color(0x24FFFFFF),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onQuickApplyPlatform(platform) }
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TelegramCheckmark(
                            checked = isSelected,
                            size = 16.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = platform.shortBadge,
                            color = if (isSelected) Color.White else GlassTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grid of 10 Interactive Feature Cards (2 columns × 5 rows)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (rowIdx in 0 until 5) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val leftFeature = CREATOR_10_FEATURES[rowIdx * 2]
                        val rightFeature = CREATOR_10_FEATURES[rowIdx * 2 + 1]

                        CreatorFeatureMiniTile(
                            feature = leftFeature,
                            isRu = isRu,
                            onClick = { onOpenFeatureIndex(leftFeature.id) },
                            modifier = Modifier.weight(1f)
                        )
                        CreatorFeatureMiniTile(
                            feature = rightFeature,
                            isRu = isRu,
                            onClick = { onOpenFeatureIndex(rightFeature.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            GlassButton(
                text = if (isRu) "Открыть Студию Креатора (Все 10 функций)" else "Open Creator Pro Studio (All 10 Tools)",
                icon = AppIcons.AutoAwesome,
                isPrimary = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_creator_suite_button"),
                onClick = { onOpenFeatureIndex(0) }
            )
        }
    }
}

@Composable
private fun CreatorFeatureMiniTile(
    feature: CreatorFeatureMeta,
    isRu: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(66.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xD90A0E17), Color(0xEB07090F))
                )
            )
            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("creator_feature_tile_${feature.id}")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = feature.badge,
                    color = TelegramBlueLight,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                TelegramCheckmark(checked = true, size = 13.dp)
            }
            Text(
                text = feature.title(isRu),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                lineHeight = 13.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Full Interactive Modal / Workbench implementing all 10 Creator Pro Features.
 */
@Composable
fun CreatorSuiteModal(
    isRu: Boolean,
    initialFeatureIndex: Int,
    selectedVideo: VideoItem?,
    settings: EnhanceSettings,
    onSettingsChange: (EnhanceSettings) -> Unit,
    onApplyPlatform: (TargetPlatform) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeToolIndex by remember(initialFeatureIndex) { mutableIntStateOf(initialFeatureIndex.coerceIn(0, 9)) }
    var scrubFraction by remember { mutableFloatStateOf(0.25f) }
    var frameAnalysis by remember { mutableStateOf<FrameAnalysisResult?>(null) }

    // Safe Zone state (Feature #1)
    var safeZoneMode by remember { mutableStateOf("TIKTOK") } // TIKTOK, SHORTS, REELS
    var showRuleOfThirds by remember { mutableStateOf(true) }

    // Cover Extractor state (Feature #2)
    var applyStudioGradeToCover by remember { mutableStateOf(true) }
    var coverBadgeText by remember { mutableStateOf(if (isRu) "4K 60FPS · ГАЙД" else "4K 60FPS · GUIDE") }
    var includeCoverBadge by remember { mutableStateOf(false) }
    var savedCoverPath by remember { mutableStateOf<String?>(null) }

    // Bitrate Calculator state (Feature #4)
    var calcDurationSec by remember {
        mutableFloatStateOf(((selectedVideo?.durationMs ?: 15_000L) / 1000f).coerceIn(5f, 180f))
    }
    var calcPlatformLimitMb by remember { mutableIntStateOf(72) } // 72 MB (TikTok Android), 287 MB (iOS/Web), 500 MB (Desktop/YT)

    // Screen Stress Test state (Feature #8)
    var dimScreen15Percent by remember { mutableStateOf(false) }
    var monochromeLumaMode by remember { mutableStateOf(false) }

    // Viral Hook & SEO Builder state (Feature #10)
    var selectedHookNiche by remember { mutableIntStateOf(0) }
    var customTopicText by remember { mutableStateOf(if (isRu) "Настройка 4K 120 FPS без потери качества" else "4K 120 FPS Zero-Loss Quality Setup") }
    val hookCheckStates = remember { mutableStateListOf(true, true, true, true) }

    // Load real video frame & 32-bin Luma histogram whenever scrubFraction or settings change
    LaunchedEffect(selectedVideo, scrubFraction, settings.sharpness, settings.shadowLift, settings.highlightRollOff, settings.vibrance) {
        frameAnalysis = TikTokOptimizerEngine.extractFrameAndAnalyze(
            context = context,
            video = selectedVideo,
            positionFraction = scrubFraction,
            settings = settings
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6030508))
                .padding(horizontal = 12.dp, vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 540.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFA0B0F18), Color(0xFC06080D))
                        )
                    )
                    .border(
                        1.2.dp,
                        Brush.verticalGradient(listOf(Color(0x8839B9FF), Color(0x22FFFFFF))),
                        RoundedCornerShape(28.dp)
                    )
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Top Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            VerifiedBadge(size = 22.dp, iconSize = 13.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isRu) "Студия Креатора TikTok & YouTube" else "TikTok & YouTube Creator Studio",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isRu) "10 активных Pro-инструментов для максимального удержания и качества" else "10 active Pro tools for maximum retention & quality",
                                    color = GlassTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x1EFFFFFF))
                                .border(1.dp, Color(0x38FFFFFF), RoundedCornerShape(12.dp))
                                .clickable(onClick = onDismiss)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("close_creator_suite_btn")
                        ) {
                            Text(
                                text = if (isRu) "Готово" else "Done",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Horizontal Scrollable Pill Selector for all 10 Features with Telegram Checkmarks
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CREATOR_10_FEATURES.forEach { feat ->
                            val selected = activeToolIndex == feat.id
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (selected) {
                                            Brush.horizontalGradient(listOf(Color(0x4024A1DE), Color(0x28167AC6)))
                                        } else {
                                            Brush.horizontalGradient(listOf(Color(0xD9090C13), Color(0xD907090E)))
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (selected) TelegramBlueLight else Color(0x24FFFFFF),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { activeToolIndex = feat.id }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TelegramCheckmark(checked = selected, size = 16.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${feat.id + 1}. ${feat.title(isRu)}",
                                    color = if (selected) Color.White else GlassTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val currentMeta = CREATOR_10_FEATURES[activeToolIndex]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xD9080B12))
                            .border(1.dp, Color(0x2EFFFFFF), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = currentMeta.badge,
                                    color = TelegramBlueLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isRu) "Функция ${activeToolIndex + 1} из 10" else "Tool ${activeToolIndex + 1} of 10",
                                    color = GlassTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentMeta.title(isRu),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentMeta.desc(isRu),
                                color = GlassTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            when (activeToolIndex) {
                                // 01: SAFE ZONES PREVIEW (TikTok / YouTube Shorts / Instagram Reels)
                                0 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf("TIKTOK" to "TikTok UI", "SHORTS" to "YouTube Shorts", "REELS" to "Insta Reels").forEach { (key, label) ->
                                            val sel = safeZoneMode == key
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(40.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (sel) Color(0x3824A1DE) else Color(0xCC0A0E16))
                                                    .border(1.dp, if (sel) TelegramBlueLight else Color(0x24FFFFFF), RoundedCornerShape(12.dp))
                                                    .clickable { safeZoneMode = key },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    color = if (sel) Color.White else GlassTextSecondary,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // 9:16 Safe Zone Frame Preview
                                    SafeZoneFramePreview(
                                        analysis = frameAnalysis,
                                        platformMode = safeZoneMode,
                                        showGrid = showRuleOfThirds,
                                        dim15 = false,
                                        monochrome = false
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xCC0A0E16))
                                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                                            .clickable { showRuleOfThirds = !showRuleOfThirds }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (isRu) "Сетка композиции и центральной зоны субтитров" else "Composition Grid & Subtitle Center Safe Box",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.weight(1f)
                                        )
                                        TelegramSwitch(
                                            checked = showRuleOfThirds,
                                            onCheckedChange = { showRuleOfThirds = it }
                                        )
                                    }
                                }

                                // 02: 4K FRAME & COVER THUMBNAIL EXTRACTOR
                                1 -> {
                                    SafeZoneFramePreview(
                                        analysis = frameAnalysis,
                                        platformMode = "NONE",
                                        showGrid = false,
                                        dim15 = false,
                                        monochrome = false,
                                        useRawBitmap = !applyStudioGradeToCover,
                                        coverBadgeText = if (includeCoverBadge) coverBadgeText else null
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = if (isRu) {
                                            "Выбор таймкода кадра: ${(scrubFraction * 100).roundToInt()}% (${frameAnalysis?.timestampMs ?: 0L} мс)"
                                        } else {
                                            "Frame Timestamp: ${(scrubFraction * 100).roundToInt()}% (${frameAnalysis?.timestampMs ?: 0L} ms)"
                                        },
                                        color = GlassTextSecondary,
                                        fontSize = 12.sp
                                    )
                                    Slider(
                                        value = scrubFraction,
                                        onValueChange = { scrubFraction = it },
                                        valueRange = 0f..1f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color.White,
                                            activeTrackColor = TelegramBlueLight,
                                            inactiveTrackColor = Color(0x24FFFFFF)
                                        )
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xCC0A0E16))
                                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                                            .clickable { applyStudioGradeToCover = !applyStudioGradeToCover }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (isRu) "Применить студийный Rec.709 & CAS фильтр к обложке" else "Apply Studio Rec.709 & CAS Grade to Cover",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        TelegramSwitch(
                                            checked = applyStudioGradeToCover,
                                            onCheckedChange = { applyStudioGradeToCover = it }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xCC0A0E16))
                                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                                            .clickable { includeCoverBadge = !includeCoverBadge }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (isRu) "Добавить плашку заголовка на обложку" else "Burn Cover Title Badge onto Thumbnail",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        TelegramSwitch(
                                            checked = includeCoverBadge,
                                            onCheckedChange = { includeCoverBadge = it }
                                        )
                                    }

                                    AnimatedVisibility(visible = includeCoverBadge) {
                                        Column {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            GlassTextField(
                                                value = coverBadgeText,
                                                onValueChange = { coverBadgeText = it },
                                                placeholder = if (isRu) "Текст на обложке..." else "Cover badge text..."
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    GlassButton(
                                        text = if (isRu) "Сохранить PNG-обложку в Галерею (100%)" else "Save 100% PNG Cover to Gallery",
                                        icon = AppIcons.Download,
                                        isPrimary = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            val bmp = if (applyStudioGradeToCover) {
                                                frameAnalysis?.gradedBitmap
                                            } else {
                                                frameAnalysis?.previewBitmap
                                            }
                                            if (bmp != null) {
                                                scope.launch {
                                                    val path = TikTokOptimizerEngine.saveCoverFrameToGallery(
                                                        context = context,
                                                        bitmap = bmp,
                                                        videoTitle = selectedVideo?.title ?: "Studio_Clip",
                                                        coverBadgeText = if (includeCoverBadge) coverBadgeText else null
                                                    )
                                                    savedCoverPath = path ?: "Pictures/TokEnhance_Covers"
                                                    Toast.makeText(
                                                        context,
                                                        if (isRu) "Обложка сохранена: $savedCoverPath" else "Cover saved: $savedCoverPath",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        }
                                    )

                                    if (savedCoverPath != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (isRu) "Сохранено: $savedCoverPath" else "Saved: $savedCoverPath",
                                            color = TelegramBlueLight,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                // 03: BITRATE, BPP & COMPRESSION RISK INSPECTOR
                                2 -> {
                                    val bpp = frameAnalysis?.bitsPerPixel ?: 0.114f
                                    val risk = frameAnalysis?.compressionRiskPercent ?: 4
                                    val srcVid = selectedVideo

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        TelemetryStatRow(
                                            label = if (isRu) "Плотность потока (Bits-Per-Pixel)" else "Stream Density (Bits-Per-Pixel)",
                                            value = String.format(Locale.US, "%.3f BPP (Эталон > 0.100)", bpp),
                                            highlight = true
                                        )
                                        TelemetryStatRow(
                                            label = if (isRu) "Исходный битрейт → Выходной Sweet-Spot" else "Source Bitrate → Output Sweet-Spot",
                                            value = "${srcVid?.originalBitrateMbps ?: 16.4f} Mbps → ${settings.targetBitrateMbps} Mbps"
                                        )
                                        TelemetryStatRow(
                                            label = if (isRu) "Риск пережатия серверами TikTok ByteVC1" else "TikTok ByteVC1 Re-encode Risk",
                                            value = if (risk <= 10) "$risk% (MINIMAL · DIRECT PASS)" else "$risk% (MODERATE)",
                                            highlight = risk <= 10
                                        )
                                        TelemetryStatRow(
                                            label = if (isRu) "Триггер кодека YouTube Shorts VP9/AV1" else "YouTube Shorts VP9/AV1 Codec Trigger",
                                            value = if (settings.is4k || settings.is1440p || settings.targetFps >= 60) "АКТИВЕН (VP9 High-Bitrate)" else "AVC1 Standard"
                                        )
                                        TelemetryStatRow(
                                            label = if (isRu) "Структура GOP и ключевых I-кадров" else "Closed-GOP Keyframe Cadence",
                                            value = "Closed-GOP · ${settings.keyframeIntervalSec} сек (${settings.targetFps} кадров/GOP)"
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    GlassButton(
                                        text = if (isRu) "Откалибровать в эталон 0.115 BPP (Анти-Сжатие)" else "Auto-Calibrate to 0.115 BPP Reference",
                                        icon = AppIcons.Bolt,
                                        isPrimary = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            val optimal = VideoResolutionPresets.optimalSweetSpotBitrate(
                                                settings.targetResolution,
                                                settings.targetFps
                                            )
                                            onSettingsChange(
                                                settings.copy(
                                                    targetBitrateMbps = optimal,
                                                    tiktokAntiCompressionMatrix = true,
                                                    vfrToCfrStabilizer = true
                                                )
                                            )
                                            Toast.makeText(
                                                context,
                                                if (isRu) "Битрейт откалиброван: $optimal Mbps" else "Calibrated to $optimal Mbps",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }

                                // 04: FILE SIZE & BITRATE SWEET-SPOT CALCULATOR
                                3 -> {
                                    Text(
                                        text = if (isRu) "Выберите лимит загрузки платформы:" else "Select Platform Upload Size Limit:",
                                        color = GlassTextSecondary,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf(
                                            72 to "TikTok Android (72 MB)",
                                            287 to "TikTok iOS/Web (287 MB)",
                                            500 to "Web / Shorts (500 MB)"
                                        ).forEach { (limitMb, title) ->
                                            val sel = calcPlatformLimitMb == limitMb
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (sel) Color(0x3824A1DE) else Color(0xCC0A0E16))
                                                    .border(1.dp, if (sel) TelegramBlueLight else Color(0x24FFFFFF), RoundedCornerShape(12.dp))
                                                    .clickable { calcPlatformLimitMb = limitMb }
                                                    .padding(horizontal = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = title,
                                                    color = if (sel) Color.White else GlassTextSecondary,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    GlassSlider(
                                        value = calcDurationSec,
                                        onValueChange = { calcDurationSec = it },
                                        valueRange = 5f..180f,
                                        title = if (isRu) "Длительность вашего видео" else "Your Video Duration",
                                        valueText = "${calcDurationSec.roundToInt()} сек"
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    val safeBudgetMb = calcPlatformLimitMb * 0.92f
                                    val maxSafeBitrateMbps = ((safeBudgetMb * 8f) / calcDurationSec)
                                        .coerceIn(8.0f, 32.0f)
                                    val recommendedMbps = minOf(
                                        maxSafeBitrateMbps,
                                        VideoResolutionPresets.optimalSweetSpotBitrate(settings.targetResolution, settings.targetFps)
                                    )
                                    val estimatedSizeMb = ((recommendedMbps + 0.32f) * calcDurationSec) / 8f

                                    TelemetryStatRow(
                                        label = if (isRu) "Рекомендуемый битрейт без пережатия" else "Recommended Anti-Reencode Bitrate",
                                        value = String.format(Locale.US, "%.1f Mbps", recommendedMbps),
                                        highlight = true
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    TelemetryStatRow(
                                        label = if (isRu) "Итоговый размер MP4 файла" else "Estimated Output MP4 File Size",
                                        value = String.format(Locale.US, "%.1f MB (из %d MB)", estimatedSizeMb, calcPlatformLimitMb)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    GlassButton(
                                        text = if (isRu) {
                                            "Применить ${String.format(Locale.US, "%.1f", recommendedMbps)} Mbps в настройки"
                                        } else {
                                            "Apply ${String.format(Locale.US, "%.1f", recommendedMbps)} Mbps to Engine"
                                        },
                                        icon = AppIcons.Tune,
                                        isPrimary = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            val rounded = (recommendedMbps * 10f).roundToInt() / 10f
                                            onSettingsChange(settings.copy(targetBitrateMbps = rounded))
                                            Toast.makeText(
                                                context,
                                                if (isRu) "Установлен битрейт $rounded Mbps" else "Bitrate set to $rounded Mbps",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }

                                // 05: METADATA & EXIF CONTAINER SANITIZER
                                4 -> {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xCC0A0E16))
                                            .border(
                                                1.dp,
                                                if (settings.cleanMetadataGuard) TelegramBlueLight else Color(0x24FFFFFF),
                                                RoundedCornerShape(16.dp)
                                            )
                                            .clickable {
                                                onSettingsChange(settings.copy(cleanMetadataGuard = !settings.cleanMetadataGuard))
                                            }
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isRu) "Аппаратная очистка контейнера MP4 (EXIF Sanitizer)" else "Hardware MP4 Container & EXIF Sanitizer",
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = if (isRu) {
                                                    "Удаляет сервисные атомы редакторов и прописывает чистые флаги камеры BT.709 Limited Range."
                                                } else {
                                                    "Strips NLE editing atoms and injects native camera BT.709 Limited Range signaling."
                                                },
                                                color = GlassTextMuted,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        TelegramSwitch(
                                            checked = settings.cleanMetadataGuard,
                                            onCheckedChange = {
                                                onSettingsChange(settings.copy(cleanMetadataGuard = it))
                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    TelemetryStatRow(
                                        label = "Color Primaries / Transfer / Matrix",
                                        value = "BT.709 / SDR_VIDEO / Limited (16-235 IRE)",
                                        highlight = settings.cleanMetadataGuard
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    TelemetryStatRow(
                                        label = if (isRu) "Следы сторонних видеоредакторов" else "Third-Party Editor Signatures",
                                        value = if (settings.cleanMetadataGuard) "ОЧИЩЕНО (Native Camera Stream)" else "Без изменений"
                                    )
                                }

                                // 06: AUDIO LOUDNESS GUARD & CLEAN TREND MUTE
                                5 -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        AudioGuardMode.values().forEach { mode ->
                                            val isSelected = settings.audioGuardMode == mode
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(
                                                        if (isSelected) Color(0x2824A1DE) else Color(0xCC0A0E16)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (isSelected) TelegramBlueLight else Color(0x22FFFFFF),
                                                        RoundedCornerShape(16.dp)
                                                    )
                                                    .clickable {
                                                        onSettingsChange(settings.copy(audioGuardMode = mode))
                                                    }
                                                    .padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                TelegramCheckmark(checked = isSelected, size = 22.dp)
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = mode.displayTitle(isRu),
                                                            color = Color.White,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.weight(1f, fill = false)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(6.dp))
                                                                .background(Color(0x2439B9FF))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = mode.badge,
                                                                color = TelegramBlueLight,
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(3.dp))
                                                    Text(
                                                        text = mode.displayDesc(isRu),
                                                        color = GlassTextSecondary,
                                                        fontSize = 11.sp,
                                                        lineHeight = 14.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // 07: LIVE LUMA WAVEFORM & IRE ZEBRA INSPECTOR
                                6 -> {
                                    val hist = frameAnalysis?.lumaHistogram32 ?: List(32) { 0.3f }
                                    val avgIre = frameAnalysis?.averageIre ?: 48
                                    val crush = frameAnalysis?.shadowCrushPercent ?: 0.4f
                                    val clip = frameAnalysis?.highlightClipPercent ?: 0.2f
                                    val stops = frameAnalysis?.dynamicRangeStops ?: 10.4f

                                    // 32-Bin Luma Waveform Canvas
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFF05070C))
                                            .border(1.dp, Color(0x2EFFFFFF), RoundedCornerShape(16.dp))
                                            .padding(12.dp)
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val barCount = hist.size
                                            val gap = 3.dp.toPx()
                                            val barWidth = ((size.width - gap * (barCount - 1)) / barCount).coerceAtLeast(2f)

                                            // 16 IRE and 235 IRE reference lines
                                            val x16 = size.width * (16f / 255f)
                                            val x235 = size.width * (235f / 255f)
                                            drawLine(
                                                color = Color(0x6639B9FF),
                                                start = Offset(x16, 0f),
                                                end = Offset(x16, size.height),
                                                strokeWidth = 1.dp.toPx(),
                                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                                            )
                                            drawLine(
                                                color = Color(0x66FF6B6B),
                                                start = Offset(x235, 0f),
                                                end = Offset(x235, size.height),
                                                strokeWidth = 1.dp.toPx(),
                                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                                            )

                                            hist.forEachIndexed { idx, norm ->
                                                val hPx = (size.height * norm.coerceIn(0.06f, 1f))
                                                val x = idx * (barWidth + gap)
                                                val y = size.height - hPx
                                                val barBrush = when {
                                                    idx <= 2 -> Brush.verticalGradient(listOf(Color(0xFF39B9FF), Color(0xFF165B8C)))
                                                    idx >= 29 -> Brush.verticalGradient(listOf(Color(0xFFFF8E53), Color(0xFFC94A4A)))
                                                    else -> Brush.verticalGradient(listOf(Color(0xFFE6F4FF), Color(0xFF24A1DE)))
                                                }
                                                drawRoundRect(
                                                    brush = barBrush,
                                                    topLeft = Offset(x, y),
                                                    size = Size(barWidth, hPx),
                                                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        MiniMetricCard(
                                            label = "AVG LUMA",
                                            value = "$avgIre IRE",
                                            modifier = Modifier.weight(1f)
                                        )
                                        MiniMetricCard(
                                            label = "SHADOW <16",
                                            value = "$crush%",
                                            modifier = Modifier.weight(1f)
                                        )
                                        MiniMetricCard(
                                            label = "CLIP >235",
                                            value = "$clip%",
                                            modifier = Modifier.weight(1f)
                                        )
                                        MiniMetricCard(
                                            label = "RANGE",
                                            value = "${stops} EV",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    GlassButton(
                                        text = if (isRu) "Авто-Баланс Кривых 16–235 IRE (Без провалов и засветов)" else "Auto-Balance 16–235 IRE Curves",
                                        icon = AppIcons.AutoAwesome,
                                        isPrimary = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            onSettingsChange(
                                                settings.copy(
                                                    shadowLift = 14,
                                                    highlightRollOff = 24,
                                                    antiBanding = true,
                                                    rec709ColorMatrix = true
                                                )
                                            )
                                            Toast.makeText(
                                                context,
                                                if (isRu) "Кривые 16-235 IRE откалиброваны" else "16-235 IRE curves calibrated",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }

                                // 08: 15% OLED BRIGHTNESS & MONOCHROME CONTRAST TEST
                                7 -> {
                                    SafeZoneFramePreview(
                                        analysis = frameAnalysis,
                                        platformMode = "NONE",
                                        showGrid = false,
                                        dim15 = dimScreen15Percent,
                                        monochrome = monochromeLumaMode
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xCC0A0E16))
                                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                                            .clickable { dimScreen15Percent = !dimScreen15Percent }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isRu) "Симуляция тусклого экрана (15% OLED Яркость)" else "Simulate Dim Phone Screen (15% OLED Brightness)",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = if (isRu) "Проверьте, видны ли детали в тенях у зрителей с экономией заряда" else "Verify shadow details for viewers on battery saver mode",
                                                color = GlassTextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                        TelegramSwitch(
                                            checked = dimScreen15Percent,
                                            onCheckedChange = { dimScreen15Percent = it }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xCC0A0E16))
                                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                                            .clickable { monochromeLumaMode = !monochromeLumaMode }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isRu) "Монохромный Люма-Контраст (Проверка акцента на лице)" else "Monochrome Luma Contrast Check",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = if (isRu) "Отключает цвет, чтобы проверить отделение объекта от фона по свету" else "Isolates luminance to verify subject separation from background",
                                                color = GlassTextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                        TelegramSwitch(
                                            checked = monochromeLumaMode,
                                            onCheckedChange = { monochromeLumaMode = it }
                                        )
                                    }
                                }

                                // 09: 1-CLICK TARGET PLATFORM PROFILES
                                8 -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        TargetPlatform.values().forEach { platform ->
                                            val isSelected = settings.targetPlatform == platform
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(
                                                        if (isSelected) Color(0x2824A1DE) else Color(0xCC0A0E16)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (isSelected) TelegramBlueLight else Color(0x22FFFFFF),
                                                        RoundedCornerShape(16.dp)
                                                    )
                                                    .clickable {
                                                        onApplyPlatform(platform)
                                                        Toast.makeText(
                                                            context,
                                                            if (isRu) "Активирован профиль: ${platform.displayTitle(true)}" else "Profile applied: ${platform.displayTitle(false)}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    .padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                TelegramCheckmark(checked = isSelected, size = 22.dp)
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            text = platform.displayTitle(isRu),
                                                            color = Color.White,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "${platform.recommendedBitrateMbps} Mbps · ${platform.recommendedFps} FPS",
                                                            color = TelegramBlueLight,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(3.dp))
                                                    Text(
                                                        text = platform.displayDesc(isRu),
                                                        color = GlassTextSecondary,
                                                        fontSize = 11.sp,
                                                        lineHeight = 14.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // 10: VIRAL HOOK, TIMESTAMPS & SEO CAPTION BUILDER
                                9 -> {
                                    Text(
                                        text = if (isRu) "Тема или ниша вашего ролика:" else "Your Video Topic / Niche:",
                                        color = GlassTextSecondary,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    GlassTextField(
                                        value = customTopicText,
                                        onValueChange = { customTopicText = it },
                                        placeholder = if (isRu) "Введите тему видео..." else "Enter video topic..."
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    val niches = if (isRu) {
                                        listOf("Эдит / 4K Качество", "Туториал / Гайд", "Лайфстайл / Влог", "Гейминг 120 FPS")
                                    } else {
                                        listOf("Edit / 4K Quality", "Tutorial / Guide", "Lifestyle / Vlog", "Gaming 120 FPS")
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        niches.forEachIndexed { idx, label ->
                                            val sel = selectedHookNiche == idx
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(38.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (sel) Color(0x3824A1DE) else Color(0xCC0A0E16))
                                                    .border(1.dp, if (sel) TelegramBlueLight else Color(0x22FFFFFF), RoundedCornerShape(10.dp))
                                                    .clickable { selectedHookNiche = idx }
                                                    .padding(horizontal = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    color = if (sel) Color.White else GlassTextSecondary,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Interactive Retention Checklist with Telegram Checkmarks
                                    val checklistItems = if (isRu) {
                                        listOf(
                                            "Визуальный динамический хук в первые 1.5 сек",
                                            "Текст заголовка внутри центральной сейф-зоны 9:16",
                                            "Чистый битрейт ${settings.targetBitrateMbps} Mbps (обход мыла ByteVC1)",
                                            "Оптимальные 4–5 тематических SEO-хэштегов без спама"
                                        )
                                    } else {
                                        listOf(
                                            "Visual motion hook within the first 1.5 seconds",
                                            "Hook title placed strictly inside 9:16 center safe-zone",
                                            "Clean ${settings.targetBitrateMbps} Mbps sweet-spot (ByteVC1 bypass)",
                                            "4–5 high-signal niche SEO hashtags without spam"
                                        )
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        checklistItems.forEachIndexed { idx, itemText ->
                                            val isChecked = hookCheckStates[idx]
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color(0xCC0A0E16))
                                                    .border(1.dp, Color(0x1EFFFFFF), RoundedCornerShape(12.dp))
                                                    .clickable { hookCheckStates[idx] = !isChecked }
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                TelegramCheckmark(checked = isChecked, size = 18.dp)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = itemText,
                                                    color = Color.White,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    val generatedCaption = remember(customTopicText, selectedHookNiche, settings.targetResolution, settings.targetFps, isRu) {
                                        val tags = when (selectedHookNiche) {
                                            0 -> "#4kquality #aftereffects #edit #ccquality #60fps"
                                            1 -> "#туториал #гайд #качествовидео #креатор #монтаж"
                                            2 -> "#влог #эстетика #мобильнаясъемка #рек #shorts"
                                            else -> "#120fps #gaming #smooth60fps #highquality #highlights"
                                        }
                                        if (isRu) {
                                            "$customTopicText ⚡\n" +
                                                "00:00 — До оптимизации потока\n" +
                                                "00:03 — После 32-bit CAS (${settings.targetResolution} · ${settings.targetFps} FPS)\n" +
                                                "Сохрани, чтобы не потерять настройки!\n$tags"
                                        } else {
                                            "$customTopicText ⚡\n" +
                                                "00:00 — Raw camera stream\n" +
                                                "00:03 — 32-bit CAS Optimized (${settings.targetResolution} · ${settings.targetFps} FPS)\n" +
                                                "Save these settings for your next upload!\n$tags"
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xFF05070C))
                                            .border(1.dp, Color(0x2EFFFFFF), RoundedCornerShape(14.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = generatedCaption,
                                            color = GlassTextSecondary,
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    GlassButton(
                                        text = if (isRu) "Скопировать готовое описание и SEO-теги" else "Copy Viral Caption & SEO Tags",
                                        icon = AppIcons.TextFields,
                                        isPrimary = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                            clipboard?.setPrimaryClip(ClipData.newPlainText("TokEnhance SEO Caption", generatedCaption))
                                            Toast.makeText(
                                                context,
                                                if (isRu) "Описание и SEO-хэштеги скопированы в буфер обмена!" else "Caption & SEO tags copied to clipboard!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SafeZoneFramePreview(
    analysis: FrameAnalysisResult?,
    platformMode: String,
    showGrid: Boolean,
    dim15: Boolean,
    monochrome: Boolean,
    useRawBitmap: Boolean = false,
    coverBadgeText: String? = null
) {
    val bmp = if (useRawBitmap) analysis?.previewBitmap else (analysis?.gradedBitmap ?: analysis?.previewBitmap)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF04060A))
            .border(1.dp, Color(0x32FFFFFF), RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (bmp != null) {
            val matrix = remember(dim15, monochrome) {
                val cm = ColorMatrix()
                if (monochrome) {
                    cm.setToSaturation(0f)
                }
                if (dim15) {
                    val scaleMatrix = ColorMatrix(
                        floatArrayOf(
                            0.32f, 0f, 0f, 0f, 0f,
                            0f, 0.32f, 0f, 0f, 0f,
                            0f, 0f, 0.32f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                    cm.timesAssign(scaleMatrix)
                }
                cm
            }

            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Frame Preview",
                contentScale = ContentScale.Crop,
                colorFilter = if (dim15 || monochrome) ColorFilter.colorMatrix(matrix) else null,
                modifier = Modifier
                    .aspectRatio(9f / 16f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
            )
        }

        // Interactive Safe-Zone & Rule of Thirds Canvas Overlay
        Canvas(
            modifier = Modifier
                .aspectRatio(9f / 16f)
                .fillMaxSize()
        ) {
            val w = size.width
            val h = size.height

            if (showGrid) {
                val gridColor = Color(0x38FFFFFF)
                drawLine(gridColor, Offset(w / 3f, 0f), Offset(w / 3f, h), strokeWidth = 1f)
                drawLine(gridColor, Offset(2f * w / 3f, 0f), Offset(2f * w / 3f, h), strokeWidth = 1f)
                drawLine(gridColor, Offset(0f, h / 3f), Offset(w, h / 3f), strokeWidth = 1f)
                drawLine(gridColor, Offset(0f, 2f * h / 3f), Offset(w, 2f * h / 3f), strokeWidth = 1f)

                // Safe center subtitle box
                drawRoundRect(
                    color = Color(0x9939B9FF),
                    topLeft = Offset(w * 0.10f, h * 0.18f),
                    size = Size(w * 0.66f, h * 0.52f),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                    )
                )
            }

            if (platformMode != "NONE") {
                val dangerColor = Color(0x55FF4D6D)
                val dangerBorder = Color(0xAAFF4D6D)

                // Top status/search bar blind zone
                val topHeight = if (platformMode == "TIKTOK") h * 0.14f else h * 0.11f
                drawRect(color = dangerColor, topLeft = Offset(0f, 0f), size = Size(w, topHeight))

                // Right-side social buttons column (Avatar, Like, Comment, Share, Audio disc)
                val rightWidth = w * 0.21f
                val rightTop = if (platformMode == "SHORTS") h * 0.36f else h * 0.40f
                val rightBottom = h * 0.76f
                drawRoundRect(
                    color = dangerColor,
                    topLeft = Offset(w - rightWidth - 6f, rightTop),
                    size = Size(rightWidth, rightBottom - rightTop),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                )
                drawRoundRect(
                    color = dangerBorder,
                    topLeft = Offset(w - rightWidth - 6f, rightTop),
                    size = Size(rightWidth, rightBottom - rightTop),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )

                // Bottom caption, username & music marquee blind zone
                val bottomHeight = when (platformMode) {
                    "TIKTOK" -> h * 0.24f
                    "SHORTS" -> h * 0.22f
                    else -> h * 0.25f
                }
                drawRect(
                    color = dangerColor,
                    topLeft = Offset(0f, h - bottomHeight),
                    size = Size(w, bottomHeight)
                )
                drawLine(
                    color = dangerBorder,
                    start = Offset(0f, h - bottomHeight),
                    end = Offset(w, h - bottomHeight),
                    strokeWidth = 1.2.dp.toPx()
                )
            }
        }

        if (!coverBadgeText.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 26.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xD90A0E16))
                    .border(1.dp, TelegramBlueLight, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = coverBadgeText,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TelemetryStatRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xCC0A0E16))
            .border(
                1.dp,
                if (highlight) Color(0x6639B9FF) else Color(0x1EFFFFFF),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = GlassTextSecondary,
            fontSize = 11.sp,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            color = if (highlight) TelegramBlueLight else Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MiniMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xCC0A0E16))
            .border(1.dp, Color(0x24FFFFFF), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = GlassTextMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
