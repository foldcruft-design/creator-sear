package com.example

import android.net.Uri
import com.example.domain.engine.TikTokOptimizerEngine
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.PresetType
import com.example.domain.model.VideoItem
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
    assertTrue("TikTok score should be high for 1080p target", report.score >= 90)
    assertTrue("Direct stream should be verified", report.directStreamVerified)
    assertEquals(60, report.fpsTarget)
    assertTrue("Quality boost should be positive", report.estimatedQualityBoostPercent > 0)
  }

  @Test
  fun testPresetTypeValues() {
    val presets = PresetType.values()
    assertTrue(presets.isNotEmpty())
    val ultra = PresetType.TIKTOK_ULTRA
    assertEquals(48, ultra.defaultSharpness)
    assertEquals(10, ultra.defaultShadowLift)
  }
}
