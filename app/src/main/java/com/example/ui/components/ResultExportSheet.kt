package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.TikTokReadinessReport

@Composable
fun ResultExportSheet(
    report: TikTokReadinessReport,
    settings: EnhanceSettings,
    onSaveToGallery: () -> Unit,
    onShareToTikTok: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF131724), Color(0xFF090B10))
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(Color(0x6638BDF8), Color(0x15FFFFFF))),
                    RoundedCornerShape(28.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Success badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0x2210B981))
                        .border(1.dp, Color(0xFF10B981), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Видео готово для TikTok!",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Алгоритмы защиты от сжатия успешно применены",
                    color = MutedSlate,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Score card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0x18FFFFFF))
                        .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ИНДЕКС ЗАЩИТЫ TIKTOK",
                                color = MutedSlate,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${report.score}% Anti-Compression",
                                color = IceCyan,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x2A38BDF8))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "DIRECT STREAM",
                                color = IceCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Diagnostics grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SpecRow(label = "Разрешение", value = settings.targetResolution)
                    SpecRow(label = "Частота кадров", value = "${settings.targetFps} FPS Smooth")
                    SpecRow(label = "Целевой битрейт", value = "${settings.targetBitrateMbps} Mbps (Sweet-Spot)")
                    SpecRow(label = "Unsharp Mask", value = "+${settings.sharpness}% Резкость")
                    SpecRow(label = "Защита теней", value = "+${settings.shadowLift}% Динамический диапазон")
                    SpecRow(label = "Anti-Banding", value = if (settings.antiBanding) "Активен (Dithered)" else "Выкл")
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Action buttons
                GlassButton(
                    text = "Скачать в Галерею",
                    icon = Icons.Default.Download,
                    isPrimary = true,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSaveToGallery
                )

                Spacer(modifier = Modifier.height(10.dp))

                GlassButton(
                    text = "Поделиться / Открыть в TikTok",
                    icon = Icons.Default.Share,
                    isPrimary = false,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onShareToTikTok
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Закрыть",
                        color = MutedSlate,
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
            .background(Color(0x0AFFFFFF))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = MutedSlate, fontSize = 12.sp)
        Text(text = value, color = IceSilver, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
