package com.example.data.repository

import android.content.Context
import com.example.domain.model.AppLanguage
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.PresetType
import com.example.domain.model.VideoResolutionPresets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("tokenhance_app_settings", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(loadLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<EnhanceSettings> = _settings.asStateFlow()

    private fun loadLanguage(): AppLanguage {
        val code = prefs.getString("app_language", AppLanguage.RU.code) ?: AppLanguage.RU.code
        return AppLanguage.fromCode(code)
    }

    private fun loadSettings(): EnhanceSettings {
        val presetName = prefs.getString("preset_type", PresetType.TIKTOK_ULTRA.name) ?: PresetType.TIKTOK_ULTRA.name
        val preset = PresetType.values().find { it.name == presetName } ?: PresetType.TIKTOK_ULTRA
        val res = prefs.getString("target_resolution", VideoResolutionPresets.RES_1080P)
            ?: VideoResolutionPresets.RES_1080P
        val fps = prefs.getInt("target_fps", 60)
        val defaultSweetSpot = VideoResolutionPresets.optimalSweetSpotBitrate(res, fps)

        return EnhanceSettings(
            preset = preset,
            sharpness = prefs.getInt("sharpness", preset.defaultSharpness),
            microContrast = prefs.getInt("micro_contrast", preset.defaultMicroContrast),
            shadowLift = prefs.getInt("shadow_lift", preset.defaultShadowLift),
            highlightRollOff = prefs.getInt("highlight_rolloff", preset.defaultHighlightRollOff),
            deblockStrength = prefs.getInt("deblock_strength", preset.defaultDeblock),
            antiBanding = prefs.getBoolean("anti_banding", true),
            sensorDenoise = prefs.getBoolean("sensor_denoise", true),
            rec709ColorMatrix = prefs.getBoolean("rec709_color", true),
            vfrToCfrStabilizer = prefs.getBoolean("vfr_stabilizer", true),
            autoSaveToGallery = prefs.getBoolean("auto_save_gallery", false),
            vibrance = prefs.getInt("vibrance", preset.defaultVibrance),
            targetBitrateMbps = prefs.getFloat("target_bitrate_v2", defaultSweetSpot),
            targetFps = fps,
            targetResolution = res,
            keyframeIntervalSec = prefs.getInt("keyframe_interval_sec", 1).coerceIn(1, 2),
            motionInterpolation120Hz = prefs.getBoolean("motion_interpolation_120hz", true),
            deepMultiPass = prefs.getBoolean("deep_multipass", true),
            haloSuppression = prefs.getBoolean("halo_suppression", true),
            tiktokAntiCompressionMatrix = prefs.getBoolean("tiktok_anti_compression_matrix", true)
        )
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        prefs.edit().putString("app_language", lang.code).apply()
    }

    fun updateSettings(newSettings: EnhanceSettings) {
        _settings.value = newSettings
        prefs.edit()
            .putString("preset_type", newSettings.preset.name)
            .putInt("sharpness", newSettings.sharpness)
            .putInt("micro_contrast", newSettings.microContrast)
            .putInt("shadow_lift", newSettings.shadowLift)
            .putInt("highlight_rolloff", newSettings.highlightRollOff)
            .putInt("deblock_strength", newSettings.deblockStrength)
            .putInt("vibrance", newSettings.vibrance)
            .putInt("keyframe_interval_sec", newSettings.keyframeIntervalSec)
            .putBoolean("anti_banding", newSettings.antiBanding)
            .putBoolean("sensor_denoise", newSettings.sensorDenoise)
            .putBoolean("rec709_color", newSettings.rec709ColorMatrix)
            .putBoolean("vfr_stabilizer", newSettings.vfrToCfrStabilizer)
            .putBoolean("auto_save_gallery", newSettings.autoSaveToGallery)
            .putFloat("target_bitrate_v2", newSettings.targetBitrateMbps)
            .putInt("target_fps", newSettings.targetFps)
            .putString("target_resolution", newSettings.targetResolution)
            .putBoolean("motion_interpolation_120hz", newSettings.motionInterpolation120Hz)
            .putBoolean("deep_multipass", newSettings.deepMultiPass)
            .putBoolean("halo_suppression", newSettings.haloSuppression)
            .putBoolean("tiktok_anti_compression_matrix", newSettings.tiktokAntiCompressionMatrix)
            .apply()
    }

    fun applyPreset(preset: PresetType) {
        val current = _settings.value
        val nextFps = if (preset == PresetType.HIGH_MOTION) 120 else current.targetFps
        val sweetSpot = VideoResolutionPresets.optimalSweetSpotBitrate(current.targetResolution, nextFps)
        updateSettings(
            current.copy(
                preset = preset,
                targetFps = nextFps,
                sharpness = preset.defaultSharpness,
                microContrast = preset.defaultMicroContrast,
                shadowLift = preset.defaultShadowLift,
                highlightRollOff = preset.defaultHighlightRollOff,
                deblockStrength = preset.defaultDeblock,
                vibrance = preset.defaultVibrance,
                antiBanding = preset.defaultAntiBanding,
                targetBitrateMbps = sweetSpot
            )
        )
    }

    fun setTargetResolutionAndFps(resolution: String, fps: Int) {
        val current = _settings.value
        val sweetSpotBitrate = VideoResolutionPresets.optimalSweetSpotBitrate(resolution, fps)
        updateSettings(
            current.copy(
                targetResolution = resolution,
                targetFps = fps,
                targetBitrateMbps = sweetSpotBitrate
            )
        )
    }

    fun resetToDefaults() {
        val defaults = EnhanceSettings()
        updateSettings(defaults)
    }
}
