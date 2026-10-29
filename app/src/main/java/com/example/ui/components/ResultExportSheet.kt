package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppStrings
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.TikTokReadinessReport
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextSecondary

@Composable
fun ResultExportSheet(
    report: TikTokReadinessReport,
    settings: EnhanceSettings,
    savedPath: String? = null,
    strings: AppStrings = AppStrings(AppLanguage.RU),
    onSaveToGallery: () -> Unit,
    onShareToTikTok: () -> Unit,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xF014161C), Color(0xF808090C))
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(Color(0x60FFFFFF), Color(0x14FFFFFF))),
                    RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 580.dp)
                    .verticalScroll(scrollState)
            ) {
                // Frosted Verified Emblem
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0x28FFFFFF))
                        .border(1.5.dp, Color(0x80FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = strings.resultTitle,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = strings.resultSubtitle,
                    color = GlassTextMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Score card - compact and unwrapped
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0x18FFFFFF))
                        .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(18.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.protectionIndexLabel,
                                color = GlassTextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${report.score}% Anti-Compression",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x30FFFFFF))
                                .border(1.dp, Color(0x50FFFFFF), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = strings.passthroughBadge,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Core diagnostic parameters (Resolution, Framerate, Bitrate, Standard)
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SpecRow(
                        label = strings.labelTargetRes,
                        value = "${settings.targetResolution} (${com.example.domain.model.VideoResolutionPresets.labelFor(settings.targetResolution)})"
                    )
                    SpecRow(
                        label = strings.labelFrameRate,
                        value = if (settings.targetFps == 120) "120 FPS · 120 Hz Ultra Motion" else "${settings.targetFps} FPS Monotonic CFR"
                    )
                    SpecRow(label = strings.labelBitrate, value = "${settings.targetBitrateMbps} Mbps Sweet-Spot")
                    SpecRow(
                        label = strings.labelKeyframes,
                        value = if (settings.vfrToCfrStabilizer) "VFR → CFR · Rec.709 Master" else "Rec.709 Direct"
                    )
                }

                if (savedPath != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1EFFFFFF))
                            .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${strings.savedInLabel} $savedPath",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Download Button (Primary)
                GlassButton(
                    text = strings.downloadToGalleryBtn,
                    icon = Icons.Rounded.Download,
                    isPrimary = true,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSaveToGallery
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Share Button
                GlassButton(
                    text = strings.shareVideoBtn,
                    icon = Icons.Rounded.Share,
                    isPrimary = false,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onShareToTikTok
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = strings.closeBtn,
                        color = GlassTextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x10FFFFFF))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = GlassTextMuted,
            fontSize = 11.sp,
            maxLines = 1,
            modifier = Modifier.weight(1f, fill = false)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false
        )
    }
}
