package com.example.ui.components

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
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import java.io.File
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

    DisposableEffect(videoItem.uri, videoItem.localFilePath) {
        val player = MediaPlayer().apply {
            isLooping = true
            try {
                if (videoItem.localFilePath != null && File(videoItem.localFilePath).exists()) {
                    setDataSource(videoItem.localFilePath)
                } else if (videoItem.rawResId != null) {
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
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0xFF050608))
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color(0x40FFFFFF), Color(0x10FFFFFF))),
                RoundedCornerShape(26.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { showControls = !showControls },
                    onDoubleTap = { isPlaying = !isPlaying }
                )
            }
    ) {
        val widthPx = constraints.maxWidth.toFloat()

        // Real video rendering surface
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

        // Subtle dynamic contrast refinement on the Right (Optimized preview side)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .fillMaxWidth(fraction = 1f - splitFraction)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x08FFFFFF),
                            Color(0x02FFFFFF),
                            Color(0x08FFFFFF)
                        )
                    )
                )
        )

        val density = LocalDensity.current
        val dividerThicknessPx = with(density) { 1.5.dp.toPx() }
        val handleRadiusPx = with(density) { 18.dp.toPx() }

        // Draggable glass divider line
        val dividerOffsetX = (splitFraction * widthPx) - dividerThicknessPx
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .offset { IntOffset(dividerOffsetX.roundToInt(), 0) }
                .width(2.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x20FFFFFF),
                            Color.White,
                            Color(0x20FFFFFF)
                        )
                    )
                )
        )

        // Monochromatic frosted handle
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset((dividerOffsetX - handleRadiusPx).roundToInt(), 0) }
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xE6101217))
                .border(1.5.dp, Color(0x90FFFFFF), CircleShape)
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
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Pure Frosted Badges (Left = Исходник, Right = Crystal 1080p)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x99000000))
                    .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ДО • ИСХОДНИК",
                    color = GlassTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x99181A22))
                    .border(1.dp, Color(0x80FFFFFF), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ПОСЛЕ • 1080P 60FPS",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Play/Pause & Mute Bar in pure frosted glass
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
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xCC080A0E))
                    .border(1.dp, Color(0x2EFFFFFF), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Пауза" else "Воспроизведение",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { isMuted = !isMuted },
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        contentDescription = if (isMuted) "Включить звук" else "Выключить звук",
                        tint = GlassTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${(splitFraction * 100).roundToInt()}% До / ${(100 - (splitFraction * 100)).roundToInt()}% После",
                    color = GlassTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
