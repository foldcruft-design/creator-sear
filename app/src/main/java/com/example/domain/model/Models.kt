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
        description = "Адаптивная резкость без белых ореолов и защита битрейта под алгоритмы TikTok 1080p 60fps.",
        defaultSharpness = 48,
        defaultShadowLift = 10,
        defaultAntiBanding = true,
        defaultVibrance = 12,
        defaultBitrateMbps = 14.5f
    ),
    NIGHT_AESTHETIC(
        title = "Ночной Aesthetic",
        description = "Шумоподавление и подъём теней (+18%) для темных рилсов, устранение бандинга в градиентах.",
        defaultSharpness = 40,
        defaultShadowLift = 18,
        defaultAntiBanding = true,
        defaultVibrance = 15,
        defaultBitrateMbps = 15.0f
    ),
    VLOG_PORTRAIT(
        title = "Портрет & Влог",
        description = "Естественная текстура кожи, микро-детали глаз и волос без искусственного перешарпа.",
        defaultSharpness = 38,
        defaultShadowLift = 6,
        defaultAntiBanding = false,
        defaultVibrance = 8,
        defaultBitrateMbps = 13.5f
    ),
    HIGH_MOTION(
        title = "Динамика 60 FPS",
        description = "Четкость контуров при быстром движении и танцах, стабильный битрейт без смазывания.",
        defaultSharpness = 52,
        defaultShadowLift = 8,
        defaultAntiBanding = true,
        defaultVibrance = 10,
        defaultBitrateMbps = 15.5f
    ),
    CUSTOM(
        title = "Ручной режим",
        description = "Точная калибровка резкости, кривой теней и битрейта под конкретное видео.",
        defaultSharpness = 48,
        defaultShadowLift = 10,
        defaultAntiBanding = true,
        defaultVibrance = 12,
        defaultBitrateMbps = 14.5f
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

    fun isProOnly(res: String): Boolean = res != RES_1080P
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

    fun isProOnly(fps: Int): Boolean = fps == FPS_120
}

data class EnhanceSettings(
    val preset: PresetType = PresetType.TIKTOK_ULTRA,
    val sharpness: Int = 48,
    val shadowLift: Int = 10,
    val antiBanding: Boolean = true,
    val sensorDenoise: Boolean = true,
    val rec709ColorMatrix: Boolean = true,
    val vfrToCfrStabilizer: Boolean = true,
    val autoSaveToGallery: Boolean = false,
    val vibrance: Int = 12,
    val targetBitrateMbps: Float = 14.5f,
    val targetFps: Int = 60,
    val targetResolution: String = VideoResolutionPresets.RES_1080P,
    val motionInterpolation120Hz: Boolean = true,
    val deepMultiPass: Boolean = true,
    val haloSuppression: Boolean = true
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
    MOTION_SMOOTHING(3, "Интерполяция кадров 120 Hz", "Выравнивание межкадрового тайминга и устранение микрофризов..."),
    SHARPENING(4, "Адаптивный Unsharp-Masking", "Субпиксельное усиление текстур без белых контурных ореолов..."),
    DYNAMIC_RANGE(5, "Реконструкция теней (0-15 IRE)", "Подъём тёмных тонов и дизеринг градиентов без бандинга..."),
    COLOR_CALIBRATION(6, "Калибровка Studio Rec.709", "Внедрение цветовых матриц BT.709 и нормализация гаммы..."),
    ENCODING(7, "Сборка мастер-потока Sweet-Spot", "Запись MP4 с выравниванием fast-start и защитой битрейта..."),
    VALIDATION(8, "Верификация 4K/120Hz для TikTok", "Финальная проверка битовой синхронизации и сжатия..."),
    COMPLETED(9, "Готово!", "Видео профессионально оптимизировано и готово к сохранению.")
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

