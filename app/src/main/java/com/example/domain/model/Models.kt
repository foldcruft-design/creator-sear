package com.example.domain.model

import android.net.Uri

enum class PresetType(
    val title: String,
    val description: String,
    val defaultSharpness: Int,
    val defaultShadowLift: Int,
    val defaultAntiBanding: Boolean,
    val defaultVibrance: Int,
    val defaultBitrateMbps: Float
) {
    TIKTOK_ULTRA(
        title = "TikTok Crystal Bypass",
        description = "Филигранная 9-точечная CAS резкость, предкомпенсация частот и защита от пережатия TikTok.",
        defaultSharpness = 54,
        defaultShadowLift = 11,
        defaultAntiBanding = true,
        defaultVibrance = 12,
        defaultBitrateMbps = 14.2f
    ),
    NIGHT_AESTHETIC(
        title = "Ночной Aesthetic",
        description = "Хрома-шумоподавление и подъём теней (+18%) для тёмных рилсов, 10-bit TPDF дизеринг градиентов.",
        defaultSharpness = 44,
        defaultShadowLift = 18,
        defaultAntiBanding = true,
        defaultVibrance = 15,
        defaultBitrateMbps = 14.8f
    ),
    VLOG_PORTRAIT(
        title = "Портрет & Влог",
        description = "Естественная текстура кожи, микро-детали глаз и волос без искусственного перешарпа.",
        defaultSharpness = 42,
        defaultShadowLift = 8,
        defaultAntiBanding = true,
        defaultVibrance = 9,
        defaultBitrateMbps = 13.8f
    ),
    HIGH_MOTION(
        title = "Динамика 120 FPS",
        description = "Чёткость контуров при быстром движении и танцах, межкадровая плавность без смазывания.",
        defaultSharpness = 58,
        defaultShadowLift = 10,
        defaultAntiBanding = true,
        defaultVibrance = 11,
        defaultBitrateMbps = 16.5f
    ),
    CUSTOM(
        title = "Ручной режим",
        description = "Точная калибровка резкости, кривой теней и битрейта под конкретное видео.",
        defaultSharpness = 54,
        defaultShadowLift = 11,
        defaultAntiBanding = true,
        defaultVibrance = 12,
        defaultBitrateMbps = 14.2f
    )
}

object VideoResolutionPresets {
    const val RES_1080P = "1080 × 1920"
    const val RES_1440P = "1440 × 2560"
    const val RES_4K = "2160 × 3840"

    val ALL = listOf(RES_1080P, RES_1440P, RES_4K)

    fun labelFor(res: String): String = when {
        res.contains("2160") || res.contains("4K", ignoreCase = true) -> "4K UHD"
        res.contains("1440") || res.contains("2K", ignoreCase = true) -> "2K QHD"
        else -> "1080p FHD"
    }

    fun dimensionsFor(res: String): Pair<Int, Int> = when {
        res.contains("2160") || res.contains("4K", ignoreCase = true) -> Pair(2160, 3840)
        res.contains("1440") || res.contains("2K", ignoreCase = true) -> Pair(1440, 2560)
        else -> Pair(1080, 1920)
    }

    /**
     * Calibrated TikTok ByteVC1 / H.264 High-Profile Anti-Reencode Sweet-Spot Bitrate (in Mbps).
     * Keeps peak stream bitrate strictly below TikTok's forced-downsampling threshold while
     * maximizing luma bits-per-pixel with CABAC + Chroma Denoise.
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
    val sharpness: Int = 54,
    val shadowLift: Int = 11,
    val antiBanding: Boolean = true,
    val sensorDenoise: Boolean = true,
    val rec709ColorMatrix: Boolean = true,
    val vfrToCfrStabilizer: Boolean = true,
    val autoSaveToGallery: Boolean = false,
    val vibrance: Int = 12,
    val targetBitrateMbps: Float = 14.2f,
    val targetFps: Int = 60,
    val targetResolution: String = VideoResolutionPresets.RES_1080P,
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

enum class ProcessingStage(val step: Int, val title: String, val detail: String) {
    ANALYZING(1, "Скан GOP & структуры потока", "Анализ макроблоков, IDR ключевых кадров и джиттера таймингов..."),
    MOTION_CADENCE(2, "Оптический поток & Анализ движения", "Расчёт векторов смещения и каденции развертки 120 Гц..."),
    MOTION_SMOOTHING(3, "Субкадровая интерполяция 120 Hz", "Временной FBO-морфинг и выравнивание межкадрового тайминга..."),
    SHARPENING(4, "9-точечный CAS & Предкомпенсация", "Филигранное усиление микротекстур (highp 32-bit) без ореолов..."),
    DYNAMIC_RANGE(5, "Защита теней (16-235 IRE) & TPDF", "Хрома-денойз, подъём тёмных тонов и 10-bit дизеринг градиентов..."),
    COLOR_CALIBRATION(6, "Калибровка Studio Rec.709", "Внедрение цветовых матриц BT.709 и перцептивный контраст..."),
    ENCODING(7, "Сборка High-Profile CABAC потока", "Кодирование с защитой от пережатия TikTok и fast-start атомом..."),
    VALIDATION(8, "Верификация Anti-Compression 4K/120Hz", "Финальная проверка обхода агрессивного транскодера TikTok..."),
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
