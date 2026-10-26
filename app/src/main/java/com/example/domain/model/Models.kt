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
        defaultBitrateMbps = 14.2f
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
        title = "Ручной PRO",
        description = "Точная калибровка резкости, кривой теней и битрейта под конкретное видео.",
        defaultSharpness = 48,
        defaultShadowLift = 10,
        defaultAntiBanding = true,
        defaultVibrance = 12,
        defaultBitrateMbps = 14.2f
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
    val rawResId: Int? = null,
    val localFilePath: String? = null
)

enum class ProcessingStage(val step: Int, val title: String, val detail: String) {
    ANALYZING(1, "Анализ структуры потока", "Проверка цветовой субдискретизации, GOP-структуры и битрейта..."),
    SHARPENING(2, "Адаптивная резкость", "Усиление контуров без ореолов (Halo Suppression)..."),
    DYNAMIC_RANGE(3, "Калибровка теней & Градиентов", "Поднятие 0-15 IRE уровней и устранение бандинга..."),
    COLOR_CALIBRATION(4, "Калибровка Rec.709", "Оптимизация контраста под экраны смартфонов..."),
    ENCODING(5, "Сборка TikTok Sweet-Spot", "Запись оптимизированного MP4-контейнера..."),
    COMPLETED(6, "Готово!", "Видео оптимизировано и готово к загрузке в Галерею.")
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
