package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppStrings
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextSecondary

@Composable
fun ProSubscriptionDialog(
    isPro: Boolean,
    strings: AppStrings = AppStrings(AppLanguage.RU),
    onActivatePro: (Boolean) -> Unit,
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
                .clip(RoundedCornerShape(30.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xF514161C), Color(0xFC08090C))
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(Color(0x80FFFFFF), Color(0x20FFFFFF), Color(0x40FFFFFF))
                    ),
                    RoundedCornerShape(30.dp)
                )
                .padding(horizontal = 22.dp, vertical = 22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 640.dp)
                    .verticalScroll(scrollState)
            ) {
                // Top close icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x20FFFFFF))
                            .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = strings.proStudioBadge,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Glowing crown / diamond emblem
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0x40FFFFFF), Color(0x15FFFFFF))
                            )
                        )
                        .border(1.5.dp, Color(0x90FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = strings.proModalTitle,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = strings.proModalSubtitle,
                    color = GlassTextMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Feature items in frosted cards
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProFeatureRow(
                        icon = Icons.Default.Hd,
                        title = strings.proFeature4k,
                        description = strings.proFeature4kDesc,
                        tag = "4K 2160p"
                    )
                    ProFeatureRow(
                        icon = Icons.Default.Speed,
                        title = strings.proFeature120Fps,
                        description = strings.proFeature120FpsDesc,
                        tag = "120 ГЦ"
                    )
                    ProFeatureRow(
                        icon = Icons.Default.AutoAwesome,
                        title = strings.proFeatureFlow,
                        description = strings.proFeatureFlowDesc,
                        tag = "OPTICAL"
                    )
                    ProFeatureRow(
                        icon = Icons.Default.Tune,
                        title = strings.proFeatureBitrate,
                        description = strings.proFeatureBitrateDesc,
                        tag = "85 Mbps"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Price / Status pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x18FFFFFF))
                        .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isPro) "Статус подписки" else "Тариф Creator VIP",
                                color = GlassTextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = if (isRu(strings)) "Бессрочный VIP доступ" else "Lifetime VIP Access",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isPro) Color(0x30FFFFFF) else Color(0x20FFFFFF))
                                .border(1.dp, Color(0x60FFFFFF), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (isPro) "АКТИВНА" else "FREE VIP",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Activation Button
                GlassButton(
                    text = if (isPro) strings.proActiveStatus else strings.activateProBtn,
                    icon = if (isPro) Icons.Default.Check else Icons.Default.Diamond,
                    isPrimary = true,
                    onClick = {
                        onActivatePro(!isPro)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("activate_pro_button")
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private fun isRu(strings: AppStrings): Boolean = strings.isRu

@Composable
private fun ProFeatureRow(
    icon: ImageVector,
    title: String,
    description: String,
    tag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x10FFFFFF))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x20FFFFFF))
                    .border(1.dp, Color(0x40FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    color = GlassTextMuted,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x18FFFFFF))
                    .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = tag,
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
