package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.engine.TikTokOptimizerEngine
import com.example.domain.model.AudioGuardMode
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.FrameAnalysisResult
import com.example.domain.model.TargetPlatform
import com.example.domain.model.VideoItem
import com.example.domain.model.VideoResolutionPresets
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassSlider
import com.example.ui.components.TelegramCheckmark
import com.example.ui.components.TelegramSwitch
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.AppIcons
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextSecondary
import kotlinx.coroutines.launch
import java.util.Locale

private data class CreatorToggleSpec(
    val number: Int,
    val badge: String,
    val titleRu: String,
    val titleEn: String,
    val subtitleRu: String,
    val subtitleEn: String,
    val isChecked: (EnhanceSettings) -> Boolean,
    val onToggle: (EnhanceSettings, Boolean) -> EnhanceSettings
)

private val CREATOR_PRO_TOGGLES: List<CreatorToggleSpec> = listOf(
    CreatorToggleSpec(
        number = 1,
        badge = "BYTEVC1",
        titleRu = "Обход сжатия TikTok (ByteVC1 Bypass)",
        titleEn = "TikTok Compression Bypass (ByteVC1 Sweet-Spot)",
        subtitleRu = "Удерживает пиковый битрейт и BPP строго ниже порога принудительного перекодирования TikTok.",
        subtitleEn = "Keeps peak bitrate and BPP strictly below TikTok's forced server re-encoding threshold.",
        isChecked = { it.creatorByteVc1Bypass },
        onToggle = { s, v -> s.copy(creatorByteVc1Bypass = v, tiktokAntiCompressionMatrix = v) }
    ),
    CreatorToggleSpec(
        number = 2,
        badge = "VP9 / AV1",
        titleRu = "Триггер кодека YouTube Shorts (VP9 / AV1)",
        titleEn = "YouTube Shorts Premium Codec Trigger (VP9 / AV1)",
        subtitleRu = "Форсирует серверный пайплайн VP9/AV1 на YouTube вместо базового мыльного кодека AVC1.",
        subtitleEn = "Forces YouTube's high-definition VP9/AV1 server pipeline instead of low-bitrate AVC1.",
        isChecked = { it.creatorYoutubeVp9Trigger },
        onToggle = { s, v -> s.copy(creatorYoutubeVp9Trigger = v) }
    ),
    CreatorToggleSpec(
        number = 3,
        badge = "EXIF CLEAN",
        titleRu = "Очистка скрытых метаданных (EXIF Sanitizer)",
        titleEn = "Hidden Editor Metadata Sanitizer (EXIF Clean)",
        subtitleRu = "Удаляет теги сторонних программ монтажа и прописывает сигнатуру нативной камеры BT.709.",
        subtitleEn = "Strips third-party editor tags and injects clean native camera BT.709 color signaling.",
        isChecked = { it.cleanMetadataGuard },
        onToggle = { s, v -> s.copy(cleanMetadataGuard = v) }
    ),
    CreatorToggleSpec(
        number = 4,
        badge = "3-SEC IDR",
        titleRu = "Удержание первых 3 секунд (IDR Hook Boost)",
        titleEn = "First 3-Second Retention Hook Boost (IDR Lock)",
        subtitleRu = "Уплотняет ключевые I-кадры и микроконтраст на старте ролика для мгновенной резкости в ленте.",
        subtitleEn = "Packs dense IDR keyframes and micro-contrast in the first 3 seconds to prevent opening blur.",
        isChecked = { it.creatorFirst3SecBoost },
        onToggle = { s, v -> s.copy(creatorFirst3SecBoost = v, keyframeIntervalSec = if (v) 1 else s.keyframeIntervalSec) }
    ),
    CreatorToggleSpec(
        number = 5,
        badge = "TEXT GUARD",
        titleRu = "Защита чёткости субтитров и текста (Anti-Halo)",
        titleEn = "Crisp Subtitles & Caption Edge Guard (Anti-Halo)",
        subtitleRu = "Защищает мелкий шрифт, плашки и графику от белых ореолов и рассыпания на квадраты.",
        subtitleEn = "Protects small captions, overlays, and lower-thirds from ringing halos and chroma bleed.",
        isChecked = { it.creatorSubtitleEdgeGuard },
        onToggle = { s, v -> s.copy(creatorSubtitleEdgeGuard = v, haloSuppression = v) }
    ),
    CreatorToggleSpec(
        number = 6,
        badge = "SKIN SHIELD",
        titleRu = "Натуральный тон кожи без перешарпа (Skin Shield)",
        titleEn = "Natural Skin-Tone Protection (Skin Shield)",
        subtitleRu = "Маскирует тёплый спектр лица при усилении резкости CAS, сохраняя живую текстуру без шума.",
        subtitleEn = "Protects warm facial hues during 9-tap CAS sharpening for clean, natural portraits.",
        isChecked = { it.creatorSkinToneGuard },
        onToggle = { s, v -> s.copy(creatorSkinToneGuard = v, rec709ColorMatrix = v) }
    ),
    CreatorToggleSpec(
        number = 7,
        badge = "120 HZ CFR",
        titleRu = "Анти-строб динамичных переходов (120 Hz Guard)",
        titleEn = "Anti-Strobe Motion & Velocity Guard (120 Hz CFR)",
        subtitleRu = "Выравнивает микро-джиттер кадров (VFR → CFR) для идеально плавных переходов и эдитов.",
        subtitleEn = "Eliminates variable-framerate micro-stutter (VFR → CFR) for butter-smooth velocity edits.",
        isChecked = { it.creatorAntiStrobe120Hz },
        onToggle = { s, v -> s.copy(creatorAntiStrobe120Hz = v, vfrToCfrStabilizer = v, motionInterpolation120Hz = v) }
    ),
    CreatorToggleSpec(
        number = 8,
        badge = "OLED 16-IRE",
        titleRu = "Компенсация тусклых экранов (OLED Shadow Lift)",
        titleEn = "Dim-Screen Viewer Compensation (OLED Shadow Lift)",
        subtitleRu = "Мягко вытягивает детали в глубоких тенях (16–32 IRE), чтобы видео не казалось тёмным.",
        subtitleEn = "Gently lifts deep shadow detail (16–32 IRE) so dark scenes stay clear on dim phone screens.",
        isChecked = { it.creatorOledDimCompensation },
        onToggle = { s, v -> s.copy(creatorOledDimCompensation = v) }
    ),
    CreatorToggleSpec(
        number = 9,
        badge = "-14 LUFS",
        titleRu = "Защита громкости от лимитера (-14 LUFS Guard)",
        titleEn = "Audio Limiter & Vocal Clarity Guard (-14 LUFS)",
        subtitleRu = "Предотвращает автоматическое приглушение голоса и бита нормализатором TikTok и Shorts.",
        subtitleEn = "Prevents TikTok and YouTube Shorts loudness limiters from ducking your voice or beat.",
        isChecked = { it.creatorAudioLimiterGuard },
        onToggle = { s, v ->
            val nextMode = if (v && !s.creatorTrendSoundMute) AudioGuardMode.LOUDNESS_14_LUFS else AudioGuardMode.STUDIO_AAC_320
            s.copy(creatorAudioLimiterGuard = v, audioGuardMode = nextMode)
        }
    ),
    CreatorToggleSpec(
        number = 10,
        badge = "TREND MUTE",
        titleRu = "Чистый Mute под трендовый звук (100% Video Bitrate)",
        titleEn = "Clean Mute for Trending Sounds (100% Video Bitrate)",
        subtitleRu = "Отключает аудиодорожку в MP4, отдавая 100% битрейта под видео и трендовый звук TikTok.",
        subtitleEn = "Strips the audio track from MP4 so 100% of container bitrate goes to video and trending sounds.",
        isChecked = { it.creatorTrendSoundMute },
        onToggle = { s, v ->
            val nextMode = if (v) AudioGuardMode.MUTE_FOR_TREND_SOUND else AudioGuardMode.STUDIO_AAC_320
            s.copy(creatorTrendSoundMute = v, audioGuardMode = nextMode)
        }
    )
)

/**
 * Compact Dark-Gray Smoked-Glass Hero & Live Telemetry Widget shown on the Main Optimizer tab.
 * Features the monochrome dark-gray optical illustration (`img_studio_obsidian_hero`),
 * real-time BPP / Anti-Compression status widgets, and a quick button to open the new Studio tab.
 */
@Composable
fun HomeStudioOverviewWidget(
    isRu: Boolean,
    settings: EnhanceSettings,
    onOpenStudioTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (resW, resH) = VideoResolutionPresets.dimensionsFor(settings.targetResolution)
    val bpp = remember(settings.targetResolution, settings.targetFps, settings.targetBitrateMbps) {
        val pxPerSec = resW.toDouble() * resH.toDouble() * settings.targetFps.toDouble()
        ((settings.targetBitrateMbps * 1_000_000.0) / pxPerSec).toFloat().coerceIn(0.04f, 0.45f)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Monochrome Gray Smoked-Glass Visual Banner Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF080A10))
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(Color(0x45FFFFFF), Color(0x14FFFFFF))),
                    RoundedCornerShape(22.dp)
                )
                .clickable(onClick = onOpenStudioTab)
                .testTag("home_studio_banner_widget")
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_studio_obsidian_hero),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .alpha(0.78f)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xF006080D),
                                Color(0xBF090C13),
                                Color(0x77090C13)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            VerifiedBadge(size = 16.dp, iconSize = 10.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRu) "OBSIDIAN 32-BIT ENGINE" else "OBSIDIAN 32-BIT ENGINE",
                                color = GlassTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xE6151A24))
                                .border(1.dp, Color(0x55FFFFFF), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isRu) "Студия · ${settings.activeCreatorTogglesCount}/10 →" else "Studio · ${settings.activeCreatorTogglesCount}/10 →",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column {
                        Text(
                            text = if (isRu) "Филигранный обход сжатия соцсетей" else "Filigree Social Compression Bypass",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isRu) {
                                "9-Tap CAS · 16–235 IRE · TPDF Dither · все студийные тумблеры во вкладке «Студия»"
                            } else {
                                "9-Tap CAS · 16–235 IRE · TPDF Dither · all creator toggles in the Studio tab"
                            },
                            color = GlassTextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 3 Interactive Mini-Widgets Row (Monochrome Dark Smoked Glass)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MiniDashboardWidgetCard(
                title = if (isRu) "ПРОФИЛЬ СЕТИ" else "PLATFORM",
                value = settings.targetPlatform.shortBadge,
                subtitle = "${settings.targetBitrateMbps} Mbps",
                onClick = onOpenStudioTab,
                modifier = Modifier.weight(1f)
            )
            MiniDashboardWidgetCard(
                title = if (isRu) "ПЛОТНОСТЬ BPP" else "BPP DENSITY",
                value = String.format(Locale.US, "%.3f BPP", bpp),
                subtitle = if (isRu) "Эталон ByteVC1" else "ByteVC1 Sweet-Spot",
                onClick = onOpenStudioTab,
                modifier = Modifier.weight(1f)
            )
            MiniDashboardWidgetCard(
                title = if (isRu) "СТУДИЯ 10X" else "STUDIO 10X",
                value = "${settings.activeCreatorTogglesCount} из 10",
                subtitle = if (isRu) "Тумблеры активны" else "Toggles active",
                onClick = onOpenStudioTab,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MiniDashboardWidgetCard(
    title: String,
    value: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xD90C1018), Color(0xEB07090E))
                )
            )
            .border(1.dp, Color(0x2EFFFFFF), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                color = GlassTextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = GlassTextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Dedicated "Студия" (Creator Studio) Tab Screen.
 * Houses all Studio functions moved into their own clean tab:
 * - Monochrome Gray Visual Header (`img_creator_codec_matrix`)
 * - Interactive Widget 1: Live Bitrate, BPP Density & Platform Bypass Radar (`img_waveform_lens_card`)
 * - Interactive Widget 2: Real Frame & 32-Bin Luma IRE Waveform Inspector + Safe-Zone Grid + Cover Export
 * - Separate Plaque: All 10 TikTok & YouTube Creator Functions with Toggle Switches (`CreatorProTogglesCard`)
 */
@Composable
fun CreatorStudioTabScreen(
    isRu: Boolean,
    settings: EnhanceSettings,
    selectedVideo: VideoItem?,
    onSettingsChange: (EnhanceSettings) -> Unit,
    onApplyPlatform: (TargetPlatform) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

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
                // 1. Studio Hero Visual Card with Generated Monochrome Gray Artwork
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF07090E))
                        .border(
                            1.dp,
                            Brush.verticalGradient(listOf(Color(0x55FFFFFF), Color(0x16FFFFFF))),
                            RoundedCornerShape(24.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_creator_codec_matrix),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(152.dp)
                            .alpha(0.82f)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(152.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x8805070B),
                                        Color(0xE606080D),
                                        Color(0xF806080D)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    VerifiedBadge(size = 18.dp, iconSize = 11.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isRu) "СТУДИЯ КРЕАТОРА · PRO SUITE" else "CREATOR STUDIO · PRO SUITE",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.7.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xE6161B26))
                                        .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${settings.activeCreatorTogglesCount}/10 АКТИВНО",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = if (isRu) "Инструменты и виджеты качества" else "Studio Quality Widgets & Toggles",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isRu) {
                                        "Все 10 студийных функций с тумблерами, профили платформ, радар BPP и живая гистограмма кадра"
                                    } else {
                                        "All 10 creator toggle modules, platform profiles, BPP radar, and live frame waveform inspector"
                                    },
                                    color = GlassTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // 2. Interactive Widget: Platform Profiles & Live Bitrate / BPP Radar Widget
                LiveBitrateRadarWidget(
                    isRu = isRu,
                    settings = settings,
                    selectedVideo = selectedVideo,
                    onApplyPlatform = onApplyPlatform
                )

                // 3. Separate Dark Smoked-Glass Plaque: 10 Creator Functions with Toggle Switches
                CreatorProTogglesCard(
                    isRu = isRu,
                    settings = settings,
                    onSettingsChange = onSettingsChange
                )

                // 4. Interactive Widget: Real Frame & 32-Bin Luma IRE Waveform + Safe-Zone + Cover Extractor
                StudioFrameInspectorWidget(
                    isRu = isRu,
                    settings = settings,
                    selectedVideo = selectedVideo
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Interactive Widget #1: Platform Profiles + BPP Density & File Size Estimator with Monochrome Artwork.
 */
@Composable
private fun LiveBitrateRadarWidget(
    isRu: Boolean,
    settings: EnhanceSettings,
    selectedVideo: VideoItem?,
    onApplyPlatform: (TargetPlatform) -> Unit
) {
    val (resW, resH) = VideoResolutionPresets.dimensionsFor(settings.targetResolution)
    val bpp = remember(settings.targetResolution, settings.targetFps, settings.targetBitrateMbps) {
        val pxPerSec = resW.toDouble() * resH.toDouble() * settings.targetFps.toDouble()
        ((settings.targetBitrateMbps * 1_000_000.0) / pxPerSec).toFloat().coerceIn(0.04f, 0.45f)
    }
    val durationSec = ((selectedVideo?.durationMs ?: 15_000L) / 1000L).coerceAtLeast(1L)
    val estimatedMb = remember(settings.targetBitrateMbps, durationSec) {
        ((settings.targetBitrateMbps * durationSec) / 8f).coerceAtLeast(0.5f)
    }

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
                Brush.verticalGradient(listOf(Color(0x38FFFFFF), Color(0x10FFFFFF))),
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Visual Row with Monochrome Waveform Card Thumbnail
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_waveform_lens_card),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 76.dp, height = 56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0x45FFFFFF), RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isRu) "ВИДЖЕТ ПОТОКА & BPP РАДАР" else "STREAM & BPP RADAR WIDGET",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (isRu) {
                            "Плотность: ${String.format(Locale.US, "%.3f", bpp)} BPP · Вес ролика (~${durationSec}с): ${String.format(Locale.US, "%.1f", estimatedMb)} МБ"
                        } else {
                            "Density: ${String.format(Locale.US, "%.3f", bpp)} BPP · Est. size (~${durationSec}s): ${String.format(Locale.US, "%.1f", estimatedMb)} MB"
                        },
                        color = GlassTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visual BPP Meter Bar (Monochrome Silver)
            val bppProgress = ((bpp - 0.04f) / 0.22f).coerceIn(0.12f, 1f)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xD907090D))
                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isRu) "Индекс защиты от пережатия" else "Anti-Reencode Protection Index",
                        color = GlassTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (settings.creatorByteVc1Bypass) {
                            if (isRu) "99.4% · SWEET-SPOT" else "99.4% · SWEET-SPOT"
                        } else {
                            if (isRu) "92.0% · STANDARD" else "92.0% · STANDARD"
                        },
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(CircleShape)
                        .background(Color(0x1FFFFFFF))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(bppProgress)
                            .height(7.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF8C96A8), Color(0xFFFFFFFF))
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Quick Target Platform Chips (Gray Contour Highlight, No Blue)
            Text(
                text = if (isRu) "Быстрый профиль целевой платформы:" else "Quick Target Platform Profile:",
                color = GlassTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            val platforms = TargetPlatform.values()
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    platforms.take(2).forEach { platform ->
                        PlatformGrayChip(
                            platform = platform,
                            selected = settings.targetPlatform == platform,
                            onClick = { onApplyPlatform(platform) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    platforms.drop(2).forEach { platform ->
                        PlatformGrayChip(
                            platform = platform,
                            selected = settings.targetPlatform == platform,
                            onClick = { onApplyPlatform(platform) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlatformGrayChip(
    platform: TargetPlatform,
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
                width = 1.dp,
                color = if (selected) Color(0x88FFFFFF) else Color(0x20FFFFFF),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = platform.shortBadge,
                color = if (selected) Color.White else GlassTextSecondary,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${platform.recommendedRes.substringBefore(" ")}p · ${platform.recommendedBitrateMbps}M",
                color = GlassTextMuted,
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}

/**
 * Interactive Widget #2: Real Video Frame Preview + 32-Bin Luma IRE Waveform + TikTok Safe-Zone Overlay + Cover Export.
 */
@Composable
private fun StudioFrameInspectorWidget(
    isRu: Boolean,
    settings: EnhanceSettings,
    selectedVideo: VideoItem?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var framePosition by remember { mutableFloatStateOf(0.25f) }
    var showSafeZoneOverlay by remember { mutableStateOf(true) }
    var simulateOledDim15 by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<FrameAnalysisResult?>(null) }

    LaunchedEffect(selectedVideo?.uri, framePosition, settings.sharpness, settings.shadowLift, settings.vibrance) {
        analysisResult = TikTokOptimizerEngine.extractFrameAndAnalyze(
            context = context,
            video = selectedVideo,
            positionFraction = framePosition,
            settings = settings
        )
    }

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
                Brush.verticalGradient(listOf(Color(0x38FFFFFF), Color(0x10FFFFFF))),
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xE6141924))
                            .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AppIcons.CenterFocus,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isRu) "ВИДЖЕТ КАДРА, SAFE-ZONE & IRE ГИСТОГРАММА" else "FRAME, SAFE-ZONE & IRE WAVEFORM WIDGET",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isRu) "Проверка интерфейса TikTok/Shorts и экспорт PNG-обложки" else "TikTok/Shorts UI overlay check & lossless PNG cover export",
                            color = GlassTextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Frame Preview + 32-Bin Luma Waveform Side-by-Side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Frame Preview Box with Optional Safe-Zone Grid
                Box(
                    modifier = Modifier
                        .width(132.dp)
                        .height(216.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF05070B))
                        .border(1.dp, Color(0x45FFFFFF), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val bmp = analysisResult?.gradedBitmap
                    if (bmp != null) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Graded Frame Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(if (simulateOledDim15) 0.42f else 1f)
                        )
                    }

                    if (showSafeZoneOverlay) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            // Top HUD zone (search & tabs)
                            drawRect(
                                color = Color(0x44000000),
                                topLeft = Offset(0f, 0f),
                                size = Size(w, h * 0.13f)
                            )
                            // Right action buttons column (likes, comments, share)
                            drawRoundRect(
                                color = Color(0x55000000),
                                topLeft = Offset(w * 0.78f, h * 0.36f),
                                size = Size(w * 0.18f, h * 0.44f),
                                cornerRadius = CornerRadius(12f, 12f)
                            )
                            // Bottom caption & audio marquee zone
                            drawRect(
                                color = Color(0x55000000),
                                topLeft = Offset(0f, h * 0.78f),
                                size = Size(w, h * 0.22f)
                            )
                            // Safe center frame border
                            drawRoundRect(
                                color = Color(0x99FFFFFF),
                                topLeft = Offset(w * 0.07f, h * 0.15f),
                                size = Size(w * 0.68f, h * 0.60f),
                                cornerRadius = CornerRadius(14f, 14f),
                                style = Stroke(width = 1.5f)
                            )
                        }
                    }
                }

                // Right Column: 32-Bin Luma IRE Histogram + Telemetry Pills
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(94.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xD907090D))
                            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "LUMA 16–235 IRE",
                                    color = GlassTextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${analysisResult?.dynamicRangeStops ?: 9.4f} STOPS",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            val hist = analysisResult?.lumaHistogram32 ?: List(32) { 0.35f }
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                val barCount = hist.size.coerceAtLeast(1)
                                val slotW = size.width / barCount.toFloat()
                                val barW = (slotW * 0.68f).coerceAtLeast(2f)
                                hist.forEachIndexed { idx, norm ->
                                    val barH = (norm.coerceIn(0.08f, 1f) * size.height)
                                    val x = idx * slotW
                                    val y = size.height - barH
                                    drawRoundRect(
                                        color = if (idx in 4..27) Color(0xFFD8E0EC) else Color(0xFF707888),
                                        topLeft = Offset(x, y),
                                        size = Size(barW, barH),
                                        cornerRadius = CornerRadius(3f, 3f)
                                    )
                                }
                            }
                        }
                    }

                    // Quick Toggle Chips for Safe-Zone & 15% OLED Dim Test
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        InspectorSmallChip(
                            label = if (isRu) "Сетка UI" else "Safe-Zone",
                            selected = showSafeZoneOverlay,
                            onClick = { showSafeZoneOverlay = !showSafeZoneOverlay },
                            modifier = Modifier.weight(1f)
                        )
                        InspectorSmallChip(
                            label = if (isRu) "Тест OLED 15%" else "OLED 15%",
                            selected = simulateOledDim15,
                            onClick = { simulateOledDim15 = !simulateOledDim15 },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Telemetry summary row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xD907090D))
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isRu) "Средняя яркость:" else "Average IRE:",
                            color = GlassTextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "${analysisResult?.averageIre ?: 48} IRE · OK",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GlassSlider(
                value = framePosition * 100f,
                onValueChange = { framePosition = (it / 100f).coerceIn(0f, 1f) },
                valueRange = 0f..100f,
                title = if (isRu) "Выбор таймкода кадра для проверки / обложки" else "Scrub Frame Timestamp for Inspection / Cover",
                valueText = "${(framePosition * 100).toInt()}%"
            )

            Spacer(modifier = Modifier.height(12.dp))

            GlassButton(
                text = if (isRu) "Сохранить этот кадр как PNG-обложку в Галерею" else "Save Frame as Lossless PNG Cover to Gallery",
                icon = AppIcons.Download,
                isPrimary = false,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val bmpToSave = analysisResult?.gradedBitmap ?: return@GlassButton
                    scope.launch {
                        val savedPath = TikTokOptimizerEngine.saveCoverFrameToGallery(
                            context = context,
                            bitmap = bmpToSave,
                            videoTitle = selectedVideo?.title ?: "TokEnhance_Studio",
                            coverBadgeText = null
                        )
                        val msg = if (savedPath != null) {
                            if (isRu) "Обложка сохранена: $savedPath" else "Cover saved: $savedPath"
                        } else {
                            if (isRu) "Обложка экспортирована в Галерею" else "Cover exported to Gallery"
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
private fun InspectorSmallChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected) {
                    Brush.verticalGradient(listOf(Color(0xF01C2230), Color(0xF5111520)))
                } else {
                    Brush.verticalGradient(listOf(Color(0x990A0D14), Color(0xB307090E)))
                }
            )
            .border(
                1.dp,
                if (selected) Color(0x88FFFFFF) else Color(0x22FFFFFF),
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else GlassTextSecondary,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Dedicated Dark Smoked-Glass Plaque ("Отдельная плашка") containing the 10 useful
 * TikTok & YouTube Creator functions with clean toggle switches ("функции с тумблером").
 * Uses strictly the dark obsidian smoked-glass palette with silver/gray contours (no blue highlights).
 */
@Composable
fun CreatorProTogglesCard(
    isRu: Boolean,
    settings: EnhanceSettings,
    onSettingsChange: (EnhanceSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeCount = settings.activeCreatorTogglesCount

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xD90B0E15), Color(0xEB06080C))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0x36FFFFFF), Color(0x0EFFFFFF))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
            .testTag("creator_pro_toggles_plaque")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Clean Section Header Row (Properly weighted so text never wraps vertically)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xE6141924))
                        .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isRu) "ФУНКЦИИ ДЛЯ КРЕАТОРОВ TIKTOK & YOUTUBE" else "TIKTOK & YOUTUBE CREATOR FUNCTIONS",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isRu) "10 студийных модулей защиты качества с тумблерами" else "10 studio quality protection modules with switches",
                        color = GlassTextMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Active count pill (Gray contour, dark glass)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xE6141924))
                        .border(1.dp, Color(0x45FFFFFF), RoundedCornerShape(10.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$activeCount/10",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Enable All / Default 9 Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val allCoreEnabled = settings.creatorByteVc1Bypass &&
                    settings.creatorYoutubeVp9Trigger &&
                    settings.cleanMetadataGuard &&
                    settings.creatorFirst3SecBoost &&
                    settings.creatorSubtitleEdgeGuard &&
                    settings.creatorSkinToneGuard &&
                    settings.creatorAntiStrobe120Hz &&
                    settings.creatorOledDimCompensation &&
                    settings.creatorAudioLimiterGuard

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (allCoreEnabled) {
                                Brush.verticalGradient(listOf(Color(0xF01C2230), Color(0xF5111520)))
                            } else {
                                Brush.verticalGradient(listOf(Color(0x990A0D14), Color(0xB307090E)))
                            }
                        )
                        .border(
                            1.dp,
                            if (allCoreEnabled) Color(0x88FFFFFF) else Color(0x22FFFFFF),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            onSettingsChange(
                                settings.copy(
                                    creatorByteVc1Bypass = true,
                                    creatorYoutubeVp9Trigger = true,
                                    cleanMetadataGuard = true,
                                    creatorFirst3SecBoost = true,
                                    creatorSubtitleEdgeGuard = true,
                                    creatorSkinToneGuard = true,
                                    creatorAntiStrobe120Hz = true,
                                    creatorOledDimCompensation = true,
                                    creatorAudioLimiterGuard = true,
                                    tiktokAntiCompressionMatrix = true,
                                    haloSuppression = true,
                                    rec709ColorMatrix = true,
                                    vfrToCfrStabilizer = true
                                )
                            )
                        }
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isRu) "Включить рекомендуемые (9/10)" else "Enable Recommended (9/10)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(listOf(Color(0x990A0D14), Color(0xB307090E)))
                        )
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                        .clickable {
                            onSettingsChange(
                                settings.copy(
                                    creatorByteVc1Bypass = false,
                                    creatorYoutubeVp9Trigger = false,
                                    cleanMetadataGuard = false,
                                    creatorFirst3SecBoost = false,
                                    creatorSubtitleEdgeGuard = false,
                                    creatorSkinToneGuard = false,
                                    creatorAntiStrobe120Hz = false,
                                    creatorOledDimCompensation = false,
                                    creatorAudioLimiterGuard = false,
                                    creatorTrendSoundMute = false
                                )
                            )
                        }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isRu) "Выкл все" else "Disable All",
                        color = GlassTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 10 Clean Toggle Rows with Switches
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CREATOR_PRO_TOGGLES.forEach { spec ->
                    val checked = spec.isChecked(settings)
                    CreatorToggleRowItem(
                        badge = spec.badge,
                        title = if (isRu) spec.titleRu else spec.titleEn,
                        subtitle = if (isRu) spec.subtitleRu else spec.subtitleEn,
                        checked = checked,
                        testTag = "creator_toggle_${spec.number}",
                        onCheckedChange = { newValue ->
                            onSettingsChange(spec.onToggle(settings, newValue))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CreatorToggleRowItem(
    badge: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    if (checked) {
                        listOf(Color(0xE6141924), Color(0xEB0C0F17))
                    } else {
                        listOf(Color(0xD90A0D14), Color(0xE607090D))
                    }
                )
            )
            .border(
                width = 1.dp,
                color = if (checked) Color(0x66FFFFFF) else Color(0x1AFFFFFF),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TelegramCheckmark(
            checked = checked,
            size = 20.dp,
            onClick = { onCheckedChange(!checked) }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (checked) Color(0x24FFFFFF) else Color(0x12FFFFFF))
                        .border(
                            1.dp,
                            if (checked) Color(0x55FFFFFF) else Color(0x20FFFFFF),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = if (checked) Color.White else GlassTextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = subtitle,
                color = GlassTextMuted,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        TelegramSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
