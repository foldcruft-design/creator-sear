package com.example.data.repository

import android.content.Context
import com.example.domain.model.AppLanguage
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.PresetType
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
        return EnhanceSettings(
            preset = PresetType.TIKTOK_ULTRA,
            sharpness = prefs.getInt("sharpness", 48),
            shadowLift = prefs.getInt("shadow_lift", 10),
            antiBanding = prefs.getBoolean("anti_banding", true),
            sensorDenoise = prefs.getBoolean("sensor_denoise", true),
            rec709ColorMatrix = prefs.getBoolean("rec709_color", true),
            vfrToCfrStabilizer = prefs.getBoolean("vfr_stabilizer", true),
            autoSaveToGallery = prefs.getBoolean("auto_save_gallery", false),
            targetBitrateMbps = prefs.getFloat("target_bitrate", 14.5f),
            targetFps = prefs.getInt("target_fps", 60),
            targetResolution = prefs.getString("target_resolution", "1080 × 1920") ?: "1080 × 1920"
        )
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        prefs.edit().putString("app_language", lang.code).apply()
    }

    fun updateSettings(newSettings: EnhanceSettings) {
        _settings.value = newSettings
        prefs.edit()
            .putInt("sharpness", newSettings.sharpness)
            .putInt("shadow_lift", newSettings.shadowLift)
            .putBoolean("anti_banding", newSettings.antiBanding)
            .putBoolean("sensor_denoise", newSettings.sensorDenoise)
            .putBoolean("rec709_color", newSettings.rec709ColorMatrix)
            .putBoolean("vfr_stabilizer", newSettings.vfrToCfrStabilizer)
            .putBoolean("auto_save_gallery", newSettings.autoSaveToGallery)
            .putFloat("target_bitrate", newSettings.targetBitrateMbps)
            .putInt("target_fps", newSettings.targetFps)
            .putString("target_resolution", newSettings.targetResolution)
            .apply()
    }

    fun resetToDefaults() {
        val defaults = EnhanceSettings()
        updateSettings(defaults)
    }
}
