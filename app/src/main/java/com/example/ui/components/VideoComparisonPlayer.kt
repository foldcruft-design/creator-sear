package com.example.ui.components

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.VideoItem
import kotlin.math.roundToInt

@Composable
fun VideoComparisonPlayer(
    videoItem: VideoItem,
    settings: EnhanceSettings,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(true) }
    var splitFraction by remember { mutableFloatStateOf(0.5f) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var showControls by remember { mutableStateOf(true) }

    DisposableEffect(videoItem.uri) {
        val player = MediaPlayer().apply {
            isLooping = true
            try {
                if (videoItem.rawResId != null) {
                    val afd = context.resources.openRawResourceFd(videoItem.rawResId)
                    if (afd != null) {
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                    } else {
                        setDataSource(context, videoItem.uri)
                    }
                } else {
                    setDataSource(context, videoItem.uri)
                }
                setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
                prepareAsync()
                setOnPreparedListener { mp ->
                    mp.start()
                    isPlaying = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mediaPlayer = player

        onDispose {
            try {
                player.stop()
                player.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer = null
        }
    }

    LaunchedEffect(isMuted) {
        mediaPlayer?.let { mp ->
            try {
                val vol = if (isMuted) 0f else 1f
                mp.setVolume(vol, vol)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(isPlaying) {
        mediaPlayer?.let { mp ->
            try {
                if (isPlaying && !mp.isPlaying) mp.start()
                else if (!isPlaying && mp.isPlaying) mp.pause()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF06070A))
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color(0x35FFFFFF), Color(0x10FFFFFF))),
                RoundedCornerShape(24.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        showControls = !showControls
                    },
                    onDoubleTap = {
                        isPlaying = !isPlaying
                    }
                )
            }
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // SurfaceView holding the real media playback
        AndroidView(
            factory = { ctx ->
                SurfaceView(ctx).apply {
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            mediaPlayer?.setDisplay(holder)
                        }

                        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

                        override fun surfaceDestroyed(holder: SurfaceHolder) {
                            mediaPlayer?.setDisplay(null)
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay 1: Simulated compression degradation on the Left (TikTok low-bitrate artifacts)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = splitFraction)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x33000000), // Shadow compression
                            Color(0x1A000000),
                            Color(0x28000000)
                        )
                    )
                )
        )

        // Overlay 2: Enhanced Ultra clarity & Rec.709 vibrancy on the Right (TokEnhance Ultra)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .fillMaxWidth(fraction = 1f - splitFraction)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x1238BDF8), // Subtle vibrant Rec.709 glow
                            Color(0x05FFFFFF),
                            Color(0x1538BDF8)
                        )
                    )
                )
        )

        val density = LocalDensity.current
        val dividerThicknessPx = with(density) { 1.dp.toPx() }
        val handleRadiusPx = with(density) { 18.dp.toPx() }

        // Split Divider line with interactive touch drag
        val dividerOffsetX = (splitFraction * widthPx) - dividerThicknessPx
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .offset { IntOffset(dividerOffsetX.roundToInt(), 0) }
                .width(2.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x8038BDF8),
                            Color.White,
                            Color(0x8038BDF8)
                        )
                    )
                )
        )

        // Central Drag Handle on the divider
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset((dividerOffsetX - handleRadiusPx).roundToInt(), 0) }
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xDD0F172A))
                .border(1.5.dp, Color(0xFF38BDF8), CircleShape)
                .pointerInput(widthPx) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newFraction = (splitFraction + (dragAmount.x / widthPx)).coerceIn(0.05f, 0.95f)
                        splitFraction = newFraction
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Compare,
                contentDescription = "Сравнить До/После",
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(20.dp)
            )
        }

        // Header comparison badges
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left badge: ДО (Обычный TikTok)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xB3000000))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ДО • TikTok сжатие",
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Right badge: ПОСЛЕ (TokEnhance Ultra)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xCC0369A1))
                    .border(1.dp, Color(0x8038BDF8), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ПОСЛЕ • Ultra 1080p",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Play/Pause & Audio Mute controls on bottom
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xB3090B12))
                    .border(1.dp, Color(0x28FFFFFF), RoundedCornerShape(18.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Пауза" else "Воспроизведение",
                        tint = IceCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { isMuted = !isMuted },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        contentDescription = if (isMuted) "Включить звук" else "Выключить звук",
                        tint = IceSilver,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${(splitFraction * 100).roundToInt()}% До / ${(100 - (splitFraction * 100)).roundToInt()}% После",
                    color = MutedSlate,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
