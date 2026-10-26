package com.example.domain.model

import android.net.Uri

enum class PresetType(
    val title: String,
    val description: String,
    val iconName: String,
    val defaultSharpness: Int,
    val defaultShadowLift: Int,
    val defaultAntiBanding: Boolean,
    val defaultVibrance: Int,
    val defaultBitrateMbps: Float
) {
    TIKTOK_ULTRA(
        title = "TikTok Ultra Bypass",
        description = "Золотой стандарт: адаптивная микрорезкость и защита теней от пережатия TikTok",
        iconName = "bolt",
        defaultSharpness = 75,
        defaultShadowLift = 12,
        defaultAntiBanding = true,
        defaultVibrance = 15,
        defaultBitrateMbps = 14.5f
    ),
    NIGHT_AESTHETIC(
        title = "Ночной / Темный Aesthetic",
        description = "Защита глубоких теней от пикселизации, устранение ступенчатых градиентов (бандинга)",
        iconName = "moon",
        defaultSharpness = 65,
        defaultShadowLift = 26,
        defaultAntiBanding = true,
        defaultVibrance = 20,
        defaultBitrateMbps = 15.0f
    ),
    VLOG_PORTRAIT(
        title = "Влог & Talking Head",
        description = "Премиальная текстура кожи, естественная резкость взгляда и глаз без перешарпа",
        iconName = "face",
        defaultSharpness = 82,
        defaultShadowLift = 8,
        defaultAntiBanding = false,
        defaultVibrance = 10,
        defaultBitrateMbps = 13.5f
    ),
    HIGH_MOTION(
        title = "Динамика & Танцы 60 FPS",
        description = "Фиксация контуров в движении, плавный битрейт без смазанных артефактов",
        iconName = "speed",
        defaultSharpness = 70,
        defaultShadowLift = 10,
        defaultAntiBanding = true,
        defaultVibrance = 14,
        defaultBitrateMbps = 15.8f
    ),
    CUSTOM(
        title = "Ручная настройка PRO",
        description = "Индивидуальный контроль резкости, кривой динамического диапазона и кодека",
        iconName = "tune",
        defaultSharpness = 75,
        defaultShadowLift = 12,
        defaultAntiBanding = true,
        defaultVibrance = 15,
        defaultBitrateMbps = 14.5f
    )
}

data class EnhanceSettings(
    val preset: PresetType = PresetType.TIKTOK_ULTRA,
    val sharpness: Int = PresetType.TIKTOK_ULTRA.defaultSharpness,
    val shadowLift: Int = PresetType.TIKTOK_ULTRA.defaultShadowLift,
    val antiBanding: Boolean = PresetType.TIKTOK_ULTRA.defaultAntiBanding,
    val vibrance: Int = PresetType.TIKTOK_ULTRA.defaultVibrance,
    val targetBitrateMbps: Float = PresetType.TIKTOK_ULTRA.defaultBitrateMbps,
    val targetFps: Int = 60,
    val targetResolution: String = "1080 × 1920"
)

data class VideoItem(
    val uri: Uri,
    val title: String,
    val durationMs: Long = 4000L,
    val resolution: String = "1080 × 1920",
    val originalBitrateMbps: Float = 3.2f,
    val isSample: Boolean = false,
    val rawResId: Int? = null
)

enum class ProcessingStage(val step: Int, val title: String, val detail: String) {
    ANALYZING(1, "Анализ видеопотока", "Проверка контейнера, фреймрейта и цветовой субдискретизации..."),
    SHARPENING(2, "Нейро-резкость & Unsharp Mask", "Усиление высокочастотных контуров для компенсации фильтров сжатия..."),
    DYNAMIC_RANGE(3, "Защита теней от пережатия", "Поднятие 0-15 IRE уровней теней и устранение 8-битного бандинга..."),
    COLOR_CALIBRATION(4, "Калибровка Rec.709 & OLED", "Оптимизация контраста под экраны смартфонов без клиппинга..."),
    ENCODING(5, "Кодирование TikTok Sweet-Spot", "H.264 High Profile, GOP=30, битрейт 14.5 Mbps для прямого прохождения..."),
    COMPLETED(6, "Готово к публикации!", "Качество защищено от пережатия алгоритмами TikTok.")
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
    val estimatedQualityBoostPercent: Int
)
