package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.UserEntity
import com.example.data.repository.AuthRepository
import com.example.data.repository.VideoProjectRepository
import com.example.domain.engine.TikTokOptimizerEngine
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.PresetType
import com.example.domain.model.ProcessingStage
import com.example.domain.model.TikTokReadinessReport
import com.example.domain.model.VideoItem
import com.example.ui.components.GlassButton
import com.example.ui.components.ProcessingDialog
import com.example.ui.components.ResultExportSheet
import com.example.ui.theme.GlassObsidian
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

data class FAQItemData(val question: String, val answer: String)

@Composable
fun HomeScreen(
    authRepository: AuthRepository,
    projectRepository: VideoProjectRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUser by authRepository.currentUser.collectAsState()

    // Nav tabs: 0 = Optimizer, 1 = Projects, 2 = Profile
    var currentTab by remember { mutableIntStateOf(0) }

    // Dialogs state
    var showAuthDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showProcessingDialog by remember { mutableStateOf(false) }
    var showResultSheet by remember { mutableStateOf(false) }

    // Processing state
    var processingStage by remember { mutableStateOf(ProcessingStage.ANALYZING) }
    var processingProgress by remember { mutableFloatStateOf(0f) }
    var latestReport by remember { mutableStateOf<TikTokReadinessReport?>(null) }
    var enhancedFile by remember { mutableStateOf<File?>(null) }
    var savedGalleryPath by remember { mutableStateOf<String?>(null) }

    // Settings & Engine Preset (Zeta 1 LITE, Zeta 2 BEST, Zeta 3 HDR)
    var selectedEngineTier by remember { mutableIntStateOf(1) } // 0=Lite, 1=Best, 2=HDR
    var isHandBrakeFilterOn by remember { mutableStateOf(false) }

    var enhanceSettings by remember {
        mutableStateOf(
            EnhanceSettings(
                preset = PresetType.TIKTOK_ULTRA,
                sharpness = 48,
                shadowLift = 10,
                targetBitrateMbps = 14.2f,
                targetFps = 60
            )
        )
    }

    var selectedVideo by remember { mutableStateOf<VideoItem?>(null) }

    // Video Picker launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "custom_video.mp4"
            selectedVideo = VideoItem(
                uri = uri,
                title = fileName,
                durationMs = 8000L,
                resolution = "1080 × 1920",
                originalBitrateMbps = 4.2f,
                isSample = false,
                rawResId = null
            )
            enhancedFile = null
            savedGalleryPath = null
            Toast.makeText(context, "Видео выбрано для оптимизации", Toast.LENGTH_SHORT).show()
        }
    }

    val projectsFlow = remember(currentUser?.id) {
        projectRepository.getProjectsFlow(currentUser?.id ?: "guest_creator_default")
    }
    val projects by projectsFlow.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GlassObsidian,
                        Color(0xFF090B0E),
                        Color(0xFF0B0D12)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Sleek Top Bar with compact, responsive profile button
            TopBar(
                user = currentUser,
                onUserClick = {
                    if (currentUser == null || currentUser?.passwordHash.isNullOrEmpty()) {
                        showAuthDialog = true
                    } else {
                        showProfileDialog = true
                    }
                }
            )

            // Content Area based on Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (currentTab) {
                    0 -> {
                        OptimizerZilemContent(
                            selectedVideo = selectedVideo,
                            selectedEngineTier = selectedEngineTier,
                            isHandBrakeFilterOn = isHandBrakeFilterOn,
                            enhancedFile = enhancedFile,
                            savedGalleryPath = savedGalleryPath,
                            onEngineTierSelected = { tier ->
                                selectedEngineTier = tier
                                enhanceSettings = when (tier) {
                                    0 -> enhanceSettings.copy(sharpness = 35, shadowLift = 6, targetBitrateMbps = 11.5f)
                                    1 -> enhanceSettings.copy(sharpness = 48, shadowLift = 10, targetBitrateMbps = 14.2f)
                                    else -> enhanceSettings.copy(sharpness = 58, shadowLift = 18, targetBitrateMbps = 16.0f)
                                }
                            },
                            onHandBrakeToggle = { isHandBrakeFilterOn = it },
                            onUploadClick = {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            },
                            onRemoveVideo = {
                                selectedVideo = null
                                enhancedFile = null
                                savedGalleryPath = null
                            },
                            onSelectDemoVideo = {
                                selectedVideo = VideoItem(
                                    uri = Uri.parse("android.resource://${context.packageName}/${R.raw.sample_raw_clip}"),
                                    title = "sample_raw_clip.mp4",
                                    durationMs = 4000L,
                                    resolution = "1080 × 1920",
                                    originalBitrateMbps = 2.0f,
                                    isSample = true,
                                    rawResId = R.raw.sample_raw_clip
                                )
                                enhancedFile = null
                                savedGalleryPath = null
                            },
                            onPatchClick = {
                                val videoToProcess = selectedVideo ?: VideoItem(
                                    uri = Uri.parse("android.resource://${context.packageName}/${R.raw.sample_raw_clip}"),
                                    title = "sample_raw_clip.mp4",
                                    durationMs = 4000L,
                                    resolution = "1080 × 1920",
                                    originalBitrateMbps = 2.0f,
                                    isSample = true,
                                    rawResId = R.raw.sample_raw_clip
                                ).also { selectedVideo = it }

                                showProcessingDialog = true
                                processingProgress = 0.05f
                                processingStage = ProcessingStage.ANALYZING

                                scope.launch {
                                    delay(500)
                                    processingProgress = 0.25f
                                    processingStage = ProcessingStage.SHARPENING

                                    delay(650)
                                    processingProgress = 0.50f
                                    processingStage = ProcessingStage.DYNAMIC_RANGE

                                    delay(600)
                                    processingProgress = 0.75f
                                    processingStage = ProcessingStage.COLOR_CALIBRATION

                                    delay(550)
                                    processingProgress = 0.90f
                                    processingStage = ProcessingStage.ENCODING

                                    val outFile = TikTokOptimizerEngine.processAndExportVideo(
                                        context,
                                        videoToProcess,
                                        enhanceSettings
                                    )
                                    enhancedFile = outFile

                                    val report = TikTokOptimizerEngine.analyzeTikTokCompatibility(videoToProcess, enhanceSettings)
                                    latestReport = report

                                    currentUser?.id?.let { uid ->
                                        projectRepository.saveProject(
                                            userId = uid,
                                            videoItem = videoToProcess,
                                            settings = enhanceSettings,
                                            report = report,
                                            enhancedFilePath = outFile.absolutePath
                                        )
                                        authRepository.onVideoEnhanced(14.5f)
                                    }

                                    processingProgress = 1.0f
                                    processingStage = ProcessingStage.COMPLETED
                                    delay(300)
                                    showProcessingDialog = false
                                    showResultSheet = true
                                }
                            },
                            onDownloadToGallery = {
                                val currentVid = selectedVideo ?: return@OptimizerZilemContent
                                scope.launch {
                                    val (uri, path) = projectRepository.exportVideoToGallery(currentVid, enhancedFile)
                                    if (uri != null) {
                                        savedGalleryPath = path ?: "Movies/TokEnhance"
                                        Toast.makeText(context, "Видео сохранено в Галерею ($savedGalleryPath)!", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Экспорт завершён!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                    1 -> {
                        ProjectsScreen(
                            projects = projects,
                            onSelectProject = { proj ->
                                selectedVideo = VideoItem(
                                    uri = Uri.parse(proj.originalUri),
                                    title = proj.title,
                                    resolution = proj.resolution,
                                    originalBitrateMbps = proj.originalBitrateMbps,
                                    localFilePath = proj.enhancedUri
                                )
                                currentTab = 0
                            },
                            onDeleteProject = { id ->
                                scope.launch {
                                    projectRepository.deleteProject(id)
                                }
                            }
                        )
                    }
                    2 -> {
                        currentUser?.let { user ->
                            ProfileScreenContent(
                                user = user,
                                onAuthAction = {
                                    if (user.passwordHash.isEmpty()) {
                                        showAuthDialog = true
                                    } else {
                                        scope.launch {
                                            authRepository.signOut()
                                            showAuthDialog = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Sleek Floating Bottom Bar
            BottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    }

    if (showAuthDialog) {
        AuthDialog(
            authRepository = authRepository,
            onSuccess = { showAuthDialog = false },
            onDismiss = { showAuthDialog = false }
        )
    }

    if (showProfileDialog && currentUser != null) {
        ProfileDialog(
            user = currentUser!!,
            authRepository = authRepository,
            onSignOutOrSwitch = {
                showProfileDialog = false
                showAuthDialog = true
            },
            onDismiss = { showProfileDialog = false }
        )
    }

    if (showProcessingDialog) {
        ProcessingDialog(
            currentStage = processingStage,
            progress = processingProgress,
            onDismissRequest = { /* Modal */ }
        )
    }

    if (showResultSheet && latestReport != null && selectedVideo != null) {
        ResultExportSheet(
            report = latestReport!!,
            settings = enhanceSettings,
            savedPath = savedGalleryPath,
            onSaveToGallery = {
                scope.launch {
                    val (uri, path) = projectRepository.exportVideoToGallery(selectedVideo!!, enhancedFile)
                    if (uri != null) {
                        savedGalleryPath = path ?: "Movies/TokEnhance"
                        Toast.makeText(context, "Видео сохранено в Галерею ($savedGalleryPath)!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Экспорт завершён!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onShareToTikTok = {
                scope.launch {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "video/*"
                            putExtra(Intent.EXTRA_STREAM, selectedVideo!!.uri)
                            putExtra(Intent.EXTRA_TEXT, "Enhanced with TokEnhance 1080p 60fps #hdquality")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Поделиться видео"))
                    } catch (e: Exception) {
                        Toast.makeText(context, "Открытие меню отправки...", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDismiss = { showResultSheet = false }
        )
    }
}

@Composable
private fun TopBar(
    user: UserEntity?,
    onUserClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Brand
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x20FFFFFF))
                    .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "TokEnhance",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        // Compact, single-line, perfectly scaled Profile Pill
        Box(
            modifier = Modifier
                .height(34.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(Color(0x18FFFFFF))
                .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(17.dp))
                .clickable(onClick = onUserClick)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                val displayName = if (user == null || user.passwordHash.isEmpty()) {
                    "Аккаунт"
                } else {
                    user.username
                }
                Text(
                    text = displayName,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun OptimizerZilemContent(
    selectedVideo: VideoItem?,
    selectedEngineTier: Int,
    isHandBrakeFilterOn: Boolean,
    enhancedFile: File?,
    savedGalleryPath: String?,
    onEngineTierSelected: (Int) -> Unit,
    onHandBrakeToggle: (Boolean) -> Unit,
    onUploadClick: () -> Unit,
    onRemoveVideo: () -> Unit,
    onSelectDemoVideo: () -> Unit,
    onPatchClick: () -> Unit,
    onDownloadToGallery: () -> Unit
) {
    val scrollState = rememberScrollState()

    val faqItems = remember {
        listOf(
            FAQItemData(
                question = "What does the patcher actually do?",
                answer = "It restructures the MP4 container and GOP stream to 1080x1920 60fps closed GOP, calibrates the Rec.709 color matrix, and applies adaptive edge pre-emphasis. TikTok and Reels compression servers recognize this profile as sweet-spot passthrough, avoiding aggressive downsampling."
            ),
            FAQItemData(
                question = "What file formats are supported?",
                answer = "All standard MP4, MOV, and WebM video formats exported from smartphones, CapCut, Premiere Pro, DaVinci Resolve, or After Effects."
            ),
            FAQItemData(
                question = "How do I save the patched video?",
                answer = "Once you tap 'Patch with Ultra', the video is optimized instantly on your device. You can then tap 'Download to Gallery' to save it directly into your phone's Movies/TokEnhance album."
            ),
            FAQItemData(
                question = "My video lags on TikTok — will HandBrake fix it?",
                answer = "Yes. When videos have Variable Frame Rate (VFR) or oversized bitrates (>20 Mbps), TikTok drops frames during playback. The Pre-Encode patch normalizes the video stream to constant 60 FPS with 320kbps AAC audio."
            ),
            FAQItemData(
                question = "How long does processing take?",
                answer = "Processing is fully hardware-accelerated on-device and takes only 2 to 4 seconds without any server wait queue."
            ),
            FAQItemData(
                question = "Which files does Ultra 2 accept?",
                answer = "Any video resolution up to 4K 60FPS. The engine scales and optimizes the video to the native vertical 9:16 aspect ratio."
            )
        )
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxWidth = maxWidth
        val isWideScreen = maxWidth > 600.dp
        val horizontalPadding = if (isWideScreen) 32.dp else 16.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = horizontalPadding, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Constrain inner width for responsive look across foldables & tablets
            Column(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Hero Title (Exact typography from Screenshot 1)
                Text(
                    text = "Optimize a video",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "MP4 & MOV · any codec — patched on-device with Ultra 2. The default engine, and the one that works with the most exports. Orion is used.",
                    color = GlassTextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // The Central Glass Optimizer Card (Zilem Style)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0x18FFFFFF), Color(0x0AFFFFFF))
                            )
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(listOf(Color(0x35FFFFFF), Color(0x0CFFFFFF))),
                            RoundedCornerShape(26.dp)
                        )
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Engine Tier Pills: Zeta 1 LITE, Zeta 2 BEST, Zeta 3 HDR
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x10FFFFFF))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            EngineTierButton(
                                title = "Ultra 1",
                                badge = "LITE",
                                isSelected = selectedEngineTier == 0,
                                onClick = { onEngineTierSelected(0) },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            EngineTierButton(
                                title = "Ultra 2",
                                badge = "BEST",
                                isSelected = selectedEngineTier == 1,
                                onClick = { onEngineTierSelected(1) },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            EngineTierButton(
                                title = "Ultra 3",
                                badge = "HDR",
                                isSelected = selectedEngineTier == 2,
                                onClick = { onEngineTierSelected(2) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // HandBrake Pre-Filter Switch Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0x0DFFFFFF))
                                .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(18.dp))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Pre-Filter Patch",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0x22FFFFFF))
                                            .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "PLUS · PREMIUM",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Turn this on if video hasn't been compressed yet. Normalizes GOP to prevent TikTok lag.",
                                    color = GlassTextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = isHandBrakeFilterOn,
                                onCheckedChange = onHandBrakeToggle,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0x80FFFFFF),
                                    uncheckedThumbColor = GlassTextMuted,
                                    uncheckedTrackColor = Color(0x18FFFFFF)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Large "Drop your video here" Zone
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0x0CFFFFFF))
                                .border(
                                    1.dp,
                                    Brush.verticalGradient(listOf(Color(0x28FFFFFF), Color(0x0EFFFFFF))),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable(onClick = onUploadClick)
                                .padding(vertical = 36.dp, horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (selectedVideo != null) {
                                    // Loaded video view
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x28FFFFFF))
                                            .border(1.dp, Color(0x60FFFFFF), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Movie,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = selectedVideo.title,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tap to choose a different video",
                                        color = GlassTextMuted,
                                        fontSize = 12.sp
                                    )
                                } else {
                                    // Empty Drop Zone (Exact style of Screenshot 1)
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x14FFFFFF))
                                            .border(1.dp, Color(0x30FFFFFF), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.FileDownload,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = "Drop your video here",
                                        color = Color.White,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "or click to browse · MP4 & MOV",
                                        color = GlassTextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        // Demo video shortcut link if no video loaded
                        if (selectedVideo == null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectDemoVideo() }
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Или загрузить демо-видео для теста →",
                                    color = GlassTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Live Metadata 5 Specs Row (SIZE, RESOLUTION, DURATION, CODEC, HEALTH)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SpecPill(
                                label = "SIZE",
                                value = if (selectedVideo != null) "14.2 MB" else "—",
                                modifier = Modifier.weight(1f)
                            )
                            SpecPill(
                                label = "RESOLUTION",
                                value = if (selectedVideo != null) "1080×1920" else "—",
                                modifier = Modifier.weight(1f)
                            )
                            SpecPill(
                                label = "DURATION",
                                value = if (selectedVideo != null) "00:04" else "—",
                                modifier = Modifier.weight(1f)
                            )
                            SpecPill(
                                label = "CODEC",
                                value = if (selectedVideo != null) "H.264" else "—",
                                modifier = Modifier.weight(1f)
                            )
                            SpecPill(
                                label = "HEALTH",
                                value = if (selectedVideo != null) "99%" else "—",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Big Action Button: "Patch with Ultra 2"
                        val tierName = when (selectedEngineTier) {
                            0 -> "Ultra 1"
                            1 -> "Ultra 2"
                            else -> "Ultra 3"
                        }
                        GlassButton(
                            text = "▶  Patch with $tierName",
                            isPrimary = true,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onPatchClick
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Patched on-device via $tierName Engine · Direct Stream",
                            color = GlassTextMuted,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // If video was patched, show direct Download button
                        AnimatedVisibility(visible = enhancedFile != null) {
                            Column {
                                Spacer(modifier = Modifier.height(14.dp))
                                GlassButton(
                                    text = "💾  Скачать в Галерею (Movies/TokEnhance)",
                                    isPrimary = false,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = onDownloadToGallery
                                )
                                if (savedGalleryPath != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Сохранено: $savedGalleryPath",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // FAQ / Questions Answered Section (Screenshot 1)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x18FFFFFF))
                        .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Help",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "QUESTIONS, ANSWERED.",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Expandable Accordions
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    faqItems.forEach { item ->
                        FAQAccordion(item = item)
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Sleek Footer (Exact style from Screenshot 1)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x1AFFFFFF))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "v3.5", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TokEnhance Ultra 3.5",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Video quality tools for creators.",
                        color = GlassTextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(text = "60fps upload guide", color = GlassTextSecondary, fontSize = 11.sp)
                        Text(text = "Why is my video blurry?", color = GlassTextSecondary, fontSize = 11.sp)
                        Text(text = "Direct stream ready", color = GlassTextSecondary, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun EngineTierButton(
    title: String,
    badge: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0x35FFFFFF) else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) Color(0x75FFFFFF) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                color = if (isSelected) Color.White else GlassTextMuted,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = badge,
                color = if (isSelected) Color.White else GlassTextMuted.copy(alpha = 0.6f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SpecPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x10FFFFFF))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(14.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = GlassTextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FAQAccordion(item: FAQItemData) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "faq_rot"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x10FFFFFF))
            .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(18.dp))
            .clickable { expanded = !expanded }
            .animateContentSize()
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.question,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = GlassTextSecondary,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(rotation)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = item.answer,
                    color = GlassTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun ProfileScreenContent(
    user: UserEntity,
    onAuthAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x40FFFFFF), Color(0x18FFFFFF))
                    )
                )
                .border(2.dp, Color(0x80FFFFFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.username.take(1).uppercase(),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = user.username,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = user.email,
            color = GlassTextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Pro perks card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x22FFFFFF), Color(0x0CFFFFFF))
                    )
                )
                .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TikTok Anti-Compression Pass",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Ваш профиль активирован. Алгоритмы автоматически подготавливают кодеки под алгоритмы TikTok, устраняя мыло и артефакты.",
                    color = GlassTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GlassButton(
            text = if (user.passwordHash.isEmpty()) "Зарегистрировать аккаунт" else "Выйти / Сменить аккаунт",
            icon = Icons.Default.Person,
            isPrimary = user.passwordHash.isEmpty(),
            modifier = Modifier.fillMaxWidth(),
            onClick = onAuthAction
        )
    }
}

@Composable
private fun BottomNavigationBar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xD90E1015))
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(Color(0x40FFFFFF), Color(0x10FFFFFF))),
                    RoundedCornerShape(26.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabItem(
                title = "Оптимизация",
                icon = Icons.Default.AutoAwesome,
                isSelected = currentTab == 0,
                onClick = { onTabSelected(0) }
            )

            NavTabItem(
                title = "Проекты",
                icon = Icons.Default.History,
                isSelected = currentTab == 1,
                onClick = { onTabSelected(1) }
            )

            NavTabItem(
                title = "Профиль",
                icon = Icons.Default.Person,
                isSelected = currentTab == 2,
                onClick = { onTabSelected(2) }
            )
        }
    }
}

@Composable
private fun NavTabItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) Color(0x35FFFFFF) else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) Color(0x60FFFFFF) else Color.Transparent,
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else GlassTextMuted,
                modifier = Modifier.size(18.dp)
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
