package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AudioGuardMode
import com.example.domain.model.EnhanceSettings
import com.example.ui.components.TelegramCheckmark
import com.example.ui.components.TelegramSwitch
import com.example.ui.theme.AppIcons
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextSecondary

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
