package com.example.domain.model

import android.graphics.Bitmap
import android.net.Uri

enum class PresetType(
    val title: String,
    val titleEn: String,
    val description: String,
    val descriptionEn: String,
    val defaultSharpness: Int,
    val defaultMicroContrast: Int,
    val defaultShadowLift: Int,
    val defaultHighlightRollOff: Int,
    val defaultDeblock: Int,
    val defaultAntiBanding: Boolean,
    val defaultVibrance: Int,
    val defaultBitrateMbps: Float
) {
    TIKTOK_ULTRA(
        title = "TikTok Crystal Bypass",
        titleEn = "TikTok Crystal Bypass",
        description = "Эталонная 9-точечная CAS резкость, микроконтраст текстур и полный обход пережатия ByteVC1.",
        descriptionEn = "Reference 9-tap CAS sharpness, micro-contrast clarity, and full ByteVC1 re-encode bypass.",
        defaultSharpness = 54,
        defaultMicroContrast = 28,
        defaultShadowLift = 11,
        defaultHighlightRollOff = 20,
        defaultDeblock = 35,
        defaultAntiBanding = true,
        defaultVibrance = 12,
        defaultBitrateMbps = 14.2f
    ),
    HIGH_MOTION(
        title = "Динамика 120 Hz Pro",
        titleEn = "120 Hz Motion Reference",
        description = "Максимальная чёткость контуров в быстром движении, субкадровая плавность 120 FPS и GOP 1 сек.",
        descriptionEn = "Maximum motion edge definition, 120 FPS sub-frame cadence, and 1s closed GOP.",
        defaultSharpness = 60,
        defaultMicroContrast = 32,
        defaultShadowLift = 10,
        defaultHighlightRollOff = 22,
        defaultDeblock = 42,
        defaultAntiBanding = true,
        defaultVibrance = 11,
        defaultBitrateMbps = 16.5f
    ),
    NIGHT_AESTHETIC(
        title = "Ночной Obsidian HDR",
        titleEn = "Night Obsidian HDR",
        description = "Глубокое подавление хрома-шума, деблокинг теней (+18% IRE) и 10-bit TPDF защита градиентов.",
        descriptionEn = "Deep chroma denoise, shadow toe recovery (+18% IRE), and 10-bit TPDF gradient protection.",
        defaultSharpness = 46,
        defaultMicroContrast = 24,
        defaultShadowLift = 18,
        defaultHighlightRollOff = 26,
        defaultDeblock = 50,
        defaultAntiBanding = true,
        defaultVibrance = 15,
        defaultBitrateMbps = 14.8f
    ),
    VLOG_PORTRAIT(
        title = "Портрет & Детали кожи",
        titleEn = "Portrait & Skin Clarity",
        description = "Деликатный микроконтраст глаз и волос с защитой оттенков кожи от перешарпа и засветов.",
        descriptionEn = "Delicate eye & hair micro-clarity with skin-tone protection and soft highlight roll-off.",
        defaultSharpness = 44,
        defaultMicroContrast = 22,
        defaultShadowLift = 9,
        defaultHighlightRollOff = 28,
        defaultDeblock = 30,
        defaultAntiBanding = true,
        defaultVibrance = 9,
        defaultBitrateMbps = 13.8f
    ),
    CUSTOM(
        title = "Ручная студия",
        titleEn = "Custom Studio",
        description = "Индивидуальная калибровка 32-bit GPU шейдера, кривых яркости, GOP и битрейта.",
        descriptionEn = "Manual calibration of the 32-bit GPU shader, luma curves, GOP cadence, and bitrate.",
        defaultSharpness = 54,
        defaultMicroContrast = 26,
        defaultShadowLift = 11,
        defaultHighlightRollOff = 20,
        defaultDeblock = 35,
        defaultAntiBanding = true,
        defaultVibrance = 12,
        defaultBitrateMbps = 14.2f
    );

    fun displayTitle(isRu: Boolean): String = if (isRu) title else titleEn
    fun displayDescription(isRu: Boolean): String = if (isRu) description else descriptionEn
}

/**
 * Creator Feature #9: Target Social Platform Profiles (1-Click Ingestion Bypass)
 */
enum class TargetPlatform(
    val shortBadge: String,
    val titleRu: String,
    val titleEn: String,
    val descRu: String,
    val descEn: String,
    val recommendedRes: String,
    val recommendedFps: Int,
    val recommendedBitrateMbps: Float,
    val maxFileLimitMb: Int,
    val gopSec: Int
) {
    TIKTOK_BYTEVC1(
        shortBadge = "TikTok Bypass",
        titleRu = "TikTok ByteVC1 Sweet-Spot",
        titleEn = "TikTok ByteVC1 Sweet-Spot",
        descRu = "Удерживает поток строго ниже порога принудительного пережатия TikTok (14.2 Mbps, GOP 1 сек, BT.709).",
        descEn = "Keeps stream strictly below TikTok forced downsampling threshold (14.2 Mbps, 1s GOP, BT.709).",
        recommendedRes = "1080 × 1920",
        recommendedFps = 60,
        recommendedBitrateMbps = 14.2f,
        maxFileLimitMb = 287,
        gopSec = 1
    ),
    YOUTUBE_SHORTS_VP9(
        shortBadge = "YouTube VP9/AV1",
        titleRu = "YouTube Shorts VP9 / AV1 Trigger",
        titleEn = "YouTube Shorts VP9 / AV1 Trigger",
        descRu = "Активирует премиальный кодек VP9/AV1 на серверах YouTube через 2K/4K 60 FPS и высокий BPP.",
        descEn = "Triggers YouTube's premium VP9/AV1 server pipeline via 2K/4K 60 FPS high-BPP delivery.",
        recommendedRes = "1440 × 2560",
        recommendedFps = 60,
        recommendedBitrateMbps = 21.5f,
        maxFileLimitMb = 500,
        gopSec = 1
    ),
    INSTAGRAM_REELS_HDR(
        shortBadge = "Reels Crisp",
        titleRu = "Instagram Reels Crisp SDR",
        titleEn = "Instagram Reels Crisp SDR",
        descRu = "Мягкий Soft-Knee ролл-офф светов и защита от перешарпа алгоритмов Instagram Reels.",
        descEn = "Soft-Knee highlight roll-off and anti-halo protection tailored for Instagram Reels.",
        recommendedRes = "1080 × 1920",
        recommendedFps = 60,
        recommendedBitrateMbps = 15.4f,
        maxFileLimitMb = 250,
        gopSec = 1
    ),
    TELEGRAM_4K_STREAM(
        shortBadge = "Telegram 4K",
        titleRu = "Telegram 4K Instant Play",
        titleEn = "Telegram 4K Instant Play",
        descRu = "Fast-Start moov в начале файла и максимальная детализация для мгновенного воспроизведения в ТГ.",
        descEn = "Fast-Start moov atom placement and maximum detail for instant Telegram streaming.",
        recommendedRes = "2160 × 3840",
        recommendedFps = 60,
        recommendedBitrateMbps = 24.5f,
        maxFileLimitMb = 2000,
        gopSec = 2
    );

    fun displayTitle(isRu: Boolean): String = if (isRu) titleRu else titleEn
    fun displayDesc(isRu: Boolean): String = if (isRu) descRu else descEn
}

/**
 * Creator Feature #6: Audio Mastering & Copyright / Trend-Sound Guard
 */
enum class AudioGuardMode(
    val badge: String,
    val titleRu: String,
    val titleEn: String,
    val descRu: String,
    val descEn: String
) {
    STUDIO_AAC_320(
        badge = "AAC 48kHz",
        titleRu = "Студийный оригинал (Direct AAC Pass-Through)",
        titleEn = "Studio Original (Direct AAC Pass-Through)",
        descRu = "Прямое копирование аудиодорожки 48 kHz без повторного перекодирования и потерь высоких частот.",
        descEn = "Bit-exact 48 kHz audio stream copy without re-encoding or treble loss."
    ),
    LOUDNESS_14_LUFS(
        badge = "-14 LUFS",
        titleRu = "Защита речи & Луднесс -14 LUFS (TikTok/Shorts)",
        titleEn = "Speech Clarity & -14 LUFS Loudness Guard",
        descRu = "Оптимальный баланс голоса, чтобы лимитер TikTok и YouTube Shorts не глушил дорожку при публикации.",
        descEn = "Calibrated vocal headroom so TikTok & Shorts limiters never duck your audio track."
    ),
    MUTE_FOR_TREND_SOUND(
        badge = "CLEAN MUTE",
        titleRu = "Чистый Mute (Под трендовый звук TikTok)",
        titleEn = "Clean Mute (For Trending TikTok Audio)",
        descRu = "Полностью удаляет аудиодорожку из MP4, экономя 100% битрейта под видео и наложение звука в самом TikTok.",
        descEn = "Strips the audio track completely so 100% of container budget goes to video and trending sounds."
    );

    fun displayTitle(isRu: Boolean): String = if (isRu) titleRu else titleEn
    fun displayDesc(isRu: Boolean): String = if (isRu) descRu else descEn
}

object VideoResolutionPresets {
    const val RES_1080P = "1080 × 1920"
    const val RES_1440P = "1440 × 2560"
    const val RES_4K = "2160 × 3840"

    val ALL = listOf(RES_1080P, RES_1440P, RES_4K)

    fun labelFor(res: String): String = when {
        res.contains("2160") || res.contains("4K", ignoreCase = true) -> "4K UHD"
        res.contains("1440") || res.contains("2K", ignoreCase = true) -> "2K QHD"
        res.contains("720") -> "720p HD"
        else -> "1080p FHD"
    }

    fun dimensionsFor(res: String): Pair<Int, Int> = when {
        res.contains("2160") || res.contains("4K", ignoreCase = true) -> Pair(2160, 3840)
        res.contains("1440") || res.contains("2K", ignoreCase = true) -> Pair(1440, 2560)
        res.contains("720") -> Pair(720, 1280)
        else -> Pair(1080, 1920)
    }

    /**
     * Calibrated TikTok ByteVC1 / H.264 High-Profile Anti-Reencode Sweet-Spot Bitrate (in Mbps).
     */
    fun optimalSweetSpotBitrate(res: String, fps: Int): Float = when {
        res.contains("2160") || res.contains("4K", ignoreCase = true) -> when {
            fps >= 120 -> 28.5f
            fps >= 60 -> 24.5f
            else -> 19.5f
        }
        res.contains("1440") || res.contains("2K", ignoreCase = true) -> when {
            fps >= 120 -> 22.0f
            fps >= 60 -> 18.5f
            else -> 15.0f
        }
        else -> when {
            fps >= 120 -> 16.5f
            fps >= 60 -> 14.2f
            else -> 11.8f
        }
    }
}

object VideoFpsPresets {
    const val FPS_30 = 30
    const val FPS_60 = 60
    const val FPS_120 = 120

    val ALL = listOf(FPS_30, FPS_60, FPS_120)

    fun labelFor(fps: Int): String = when (fps) {
        120 -> "120 FPS (120 Hz)"
        60 -> "60 FPS Smooth"
        else -> "30 FPS Cinema"
    }
}

data class EnhanceSettings(
    val preset: PresetType = PresetType.TIKTOK_ULTRA,
    val targetPlatform: TargetPlatform = TargetPlatform.TIKTOK_BYTEVC1,
    val audioGuardMode: AudioGuardMode = AudioGuardMode.STUDIO_AAC_320,
    val cleanMetadataGuard: Boolean = true,
    val sharpness: Int = 54,
    val microContrast: Int = 26,
    val shadowLift: Int = 11,
    val highlightRollOff: Int = 20,
    val deblockStrength: Int = 35,
    val antiBanding: Boolean = true,
    val sensorDenoise: Boolean = true,
    val rec709ColorMatrix: Boolean = true,
    val vfrToCfrStabilizer: Boolean = true,
    val autoSaveToGallery: Boolean = false,
    val vibrance: Int = 12,
    val targetBitrateMbps: Float = 14.2f,
    val targetFps: Int = 60,
    val targetResolution: String = VideoResolutionPresets.RES_1080P,
    val keyframeIntervalSec: Int = 1,
    val motionInterpolation120Hz: Boolean = true,
    val deepMultiPass: Boolean = true,
    val haloSuppression: Boolean = true,
    val tiktokAntiCompressionMatrix: Boolean = true
) {
    val is4k: Boolean get() = targetResolution == VideoResolutionPresets.RES_4K
    val is1440p: Boolean get() = targetResolution == VideoResolutionPresets.RES_1440P
    val is120Fps: Boolean get() = targetFps == 120
}

data class VideoItem(
    val uri: Uri,
    val title: String,
    val durationMs: Long = 4000L,
    val resolution: String = "1080 × 1920",
    val originalBitrateMbps: Float = 4.2f,
    val fileSizeMb: Float = 8.4f,
    val codec: String = "H.264",
    val fps: Int = 60,
    val healthPercent: Int = 99,
    val isSample: Boolean = false,
    val rawResId: Int? = null,
    val localFilePath: String? = null
) {
    val formattedSize: String
        get() = String.format(java.util.Locale.US, "%.1f MB", fileSizeMb)

    val formattedDuration: String
        get() {
            val totalSeconds = (durationMs / 1000L).coerceAtLeast(1L)
            val mins = totalSeconds / 60
            val secs = totalSeconds % 60
            return String.format(java.util.Locale.US, "%02d:%02d", mins, secs)
        }
}

/**
 * Real Frame & Luma Waveform Analysis Result for Creator Studio Tools
 */
data class FrameAnalysisResult(
    val previewBitmap: Bitmap?,
    val gradedBitmap: Bitmap?,
    val lumaHistogram32: List<Float>,
    val averageIre: Int,
    val shadowCrushPercent: Float,
    val highlightClipPercent: Float,
    val dynamicRangeStops: Float,
    val bitsPerPixel: Float,
    val compressionRiskPercent: Int,
    val timestampMs: Long
)

enum class ProcessingStage(val step: Int, val title: String, val detail: String) {
    ANALYZING(1, "Скан GOP & Деблокинг исходника", "Очистка блочных артефактов, анализ IDR-кадров и джиттера..."),
    MOTION_CADENCE(2, "Оптический поток & Сетка 120 Гц", "Расчёт векторов смещения и выравнивание каденции VFR → CFR..."),
    MOTION_SMOOTHING(3, "Субкадровая интерполяция 120 Hz", "Временной FBO-морфинг и субпиксельное сглаживание движения..."),
    SHARPENING(4, "9-Tap CAS & Микроконтраст (32-bit)", "Филигранное усиление текстур и предкомпенсация частот без ореолов..."),
    DYNAMIC_RANGE(5, "Кривые 16-235 IRE & TPDF Дизеринг", "Защита глубоких теней, мягкий клиппинг светов и 10-bit дизеринг..."),
    COLOR_CALIBRATION(6, "Калибровка Studio Rec.709", "Внедрение матриц BT.709 и перцептивный баланс оттенков кожи..."),
    ENCODING(7, "Сборка High-Profile CABAC потока", "Аппаратное кодирование Sweet-Spot с защитой от пережатия TikTok..."),
    VALIDATION(8, "Верификация Anti-Compression 4K/120Hz", "Финальная проверка обхода агрессивного транскодера соцсетей..."),
    COMPLETED(9, "Готово!", "Видео филигранно оптимизировано и защищено от сжатия TikTok.")
}

data class TikTokReadinessReport(
    val score: Int,
    val resolutionMatch: Boolean,
    val fpsTarget: Int,
    val optimalBitrateMbps: Float,
    val antiBandingActive: Boolean,
    val unsharpBoostPercent: Int,
    val shadowProtectionPercent: Int,
    val directStreamVerified: Boolean,
    val estimatedQualityBoostPercent: Int,
    val packetsRemuxed: Int = 240,
    val keyframesAligned: Int = 12
)
