package com.example

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.SettingsRepository
import com.example.domain.engine.TikTokOptimizerEngine
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppStrings
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.PresetType
import com.example.domain.model.VideoItem
import com.example.domain.model.VideoResolutionPresets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleUnitTest {

    @Test
    fun testTikTokOptimizerEngine_PresetDefaults() {
        val settings = EnhanceSettings(preset = PresetType.TIKTOK_ULTRA)
        val video = VideoItem(
            uri = Uri.parse("content://media/external/video/media/1"),
            title = "Test.mp4",
            resolution = "1080 × 1920"
        )

        val report = TikTokOptimizerEngine.analyzeTikTokCompatibility(video, settings)
        assertTrue("TikTok score should be high for 1080p target", report.score >= 95)
        assertTrue("Direct stream should be verified", report.directStreamVerified)
        assertEquals(60, report.fpsTarget)
        assertTrue("Quality boost should be positive", report.estimatedQualityBoostPercent > 0)
    }

    @Test
    fun testPresetTypeValues() {
        val presets = PresetType.values()
        assertTrue(presets.isNotEmpty())
        val ultra = PresetType.TIKTOK_ULTRA
        assertEquals(54, ultra.defaultSharpness)
        assertEquals(11, ultra.defaultShadowLift)
        assertTrue(
            "4K 120fps sweet-spot bitrate should be within TikTok anti-reencode ceiling",
            VideoResolutionPresets.optimalSweetSpotBitrate(VideoResolutionPresets.RES_4K, 120) in 20f..35f
        )
    }

    @Test
    fun testLanguageSwitchingAndSettingsPersistence() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = SettingsRepository(context)

        repo.setLanguage(AppLanguage.EN)
        assertEquals(AppLanguage.EN, repo.language.value)
        val enStrings = AppStrings(repo.language.value)
        assertEquals("Settings", enStrings.tabSettings)
        assertEquals("Download Video to Gallery", enStrings.downloadToGalleryBtn)

        repo.setLanguage(AppLanguage.RU)
        assertEquals(AppLanguage.RU, repo.language.value)
        val ruStrings = AppStrings(repo.language.value)
        assertEquals("Настройки", ruStrings.tabSettings)
        assertEquals("Скачать видео в Галерею", ruStrings.downloadToGalleryBtn)
    }
}
