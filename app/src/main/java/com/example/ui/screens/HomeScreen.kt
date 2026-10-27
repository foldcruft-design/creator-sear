package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.UserEntity
import com.example.data.repository.AuthRepository
import com.example.data.repository.SettingsRepository
import com.example.data.repository.VideoProjectRepository
import com.example.domain.engine.TikTokOptimizerEngine
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppStrings
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.FAQItemText
import com.example.domain.model.ProcessingStage
import com.example.domain.model.TikTokReadinessReport
import com.example.domain.model.VideoItem
import com.example.ui.components.GlassButton
import com.example.ui.components.ProcessingDialog
import com.example.ui.components.ResultExportSheet
import com.example.ui.theme.GlassObsidian
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun HomeScreen(
    authRepository: AuthRepository,
    projectRepository: VideoProjectRepository,
    settingsRepository: SettingsRepository? = null
) {
    val context = LocalContext.current
    val activeSettingsRepo = remember(settingsRepository) {
        settingsRepository ?: SettingsRepository(context)
    }
    val scope = rememberCoroutineScope()
    val currentUser by authRepository.currentUser.collectAsState()
    val currentLanguage by activeSettingsRepo.language.collectAsState()
    val enhanceSettings by activeSettingsRepo.settings.collectAsState()

    val strings = remember(currentLanguage) { AppStrings(currentLanguage) }

    // Nav tabs: 0 = Optimizer, 1 = Projects, 2 = Settings, 3 = Profile
    var currentTab by remember { mutableIntStateOf(0) }

    BackHandler(enabled = currentTab != 0) {
        currentTab = 0
    }

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

    var selectedVideo by remember { mutableStateOf<VideoItem?>(null) }

    // Video Picker launcher with real MediaMetadataRetriever inspection
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val fallbackName = uri.lastPathSegment?.substringAfterLast('/') ?: "custom_video.mp4"
                val analyzedVideo = TikTokOptimizerEngine.extractVideoMetadata(
                    context = context,
                    uri = uri,
                    rawResId = null,
                    fallbackTitle = fallbackName
                )
                selectedVideo = analyzedVideo
                enhancedFile = null
                savedGalleryPath = null
                Toast.makeText(context, strings.videoSelectedToast, Toast.LENGTH_SHORT).show()
            }
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
            // Sleek Top Bar with compact language toggle and profile button
            TopBar(
                user = currentUser,
                strings = strings,
                currentLanguage = currentLanguage,
                onLanguageToggle = {
                    val next = if (currentLanguage == AppLanguage.RU) AppLanguage.EN else AppLanguage.RU
                    activeSettingsRepo.setLanguage(next)
                },
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
                            strings = strings,
                            selectedVideo = selectedVideo,
                            settings = enhanceSettings,
                            enhancedFile = enhancedFile,
                            savedGalleryPath = savedGalleryPath,
                            onVfrToggle = { enabled ->
                                activeSettingsRepo.updateSettings(
                                    enhanceSettings.copy(vfrToCfrStabilizer = enabled)
                                )
                            },
                            onOpenSettingsTab = { currentTab = 2 },
                            onUploadClick = {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            },
                            onSelectDemoVideo = {
                                scope.launch {
                                    val demoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.sample_raw_clip}")
                                    val analyzed = TikTokOptimizerEngine.extractVideoMetadata(
                                        context = context,
                                        uri = demoUri,
                                        rawResId = R.raw.sample_raw_clip,
                                        fallbackTitle = "sample_raw_clip.mp4"
                                    )
                                    selectedVideo = analyzed
                                    enhancedFile = null
                                    savedGalleryPath = null
                                }
                            },
                            onPatchClick = {
                                scope.launch {
                                    val videoToProcess = selectedVideo ?: run {
                                        val demoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.sample_raw_clip}")
                                        TikTokOptimizerEngine.extractVideoMetadata(
                                            context = context,
                                            uri = demoUri,
                                            rawResId = R.raw.sample_raw_clip,
                                            fallbackTitle = "sample_raw_clip.mp4"
                                        ).also { selectedVideo = it }
                                    }

                                    showProcessingDialog = true
                                    processingProgress = 0.08f
                                    processingStage = ProcessingStage.ANALYZING

                                    val outFile = TikTokOptimizerEngine.processAndExportVideo(
                                        context = context,
                                        video = videoToProcess,
                                        settings = enhanceSettings,
                                        onStageProgress = { stage, prog ->
                                            processingStage = stage
                                            processingProgress = prog
                                        }
                                    )
                                    enhancedFile = outFile

                                    val report = TikTokOptimizerEngine.analyzeTikTokCompatibility(
                                        video = videoToProcess,
                                        settings = enhanceSettings
                                    )
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

                                    if (enhanceSettings.autoSaveToGallery) {
                                        val (uri, path) = projectRepository.exportVideoToGallery(videoToProcess, outFile)
                                        if (uri != null) {
                                            savedGalleryPath = path ?: "Movies/TokEnhance"
                                        }
                                    }

                                    delay(220)
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
                                        Toast.makeText(
                                            context,
                                            "${strings.savedToastPrefix} $savedGalleryPath",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        Toast.makeText(context, strings.exportDoneToast, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onShareVideo = {
                                val currentVid = selectedVideo ?: return@OptimizerZilemContent
                                try {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "video/*"
                                        putExtra(Intent.EXTRA_STREAM, currentVid.uri)
                                        putExtra(Intent.EXTRA_TEXT, "Optimized with TokEnhance (${enhanceSettings.targetResolution} ${enhanceSettings.targetFps}fps)")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, strings.shareVideoBtn))
                                } catch (_: Exception) {
                                }
                            }
                        )
                    }
                    1 -> {
                        ProjectsScreen(
                            projects = projects,
                            strings = strings,
                            onSelectProject = { proj ->
                                selectedVideo = VideoItem(
                                    uri = Uri.parse(proj.originalUri),
                                    title = proj.title,
                                    resolution = proj.resolution,
                                    originalBitrateMbps = proj.originalBitrateMbps,
                                    fps = proj.fps,
                                    localFilePath = proj.enhancedUri
                                )
                                enhancedFile = proj.enhancedUri.takeIf { it.isNotBlank() }?.let { File(it) }
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
                        SettingsScreen(
                            strings = strings,
                            currentLanguage = currentLanguage,
                            settings = enhanceSettings,
                            onLanguageChange = { lang ->
                                activeSettingsRepo.setLanguage(lang)
                            },
                            onSettingsChange = { updated ->
                                activeSettingsRepo.updateSettings(updated)
                            },
                            onResetDefaults = {
                                activeSettingsRepo.resetToDefaults()
                            }
                        )
                    }
                    3 -> {
                        currentUser?.let { user ->
                            ProfileScreenContent(
                                user = user,
                                strings = strings,
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

            // Sleek Floating Bottom Bar with 4 Tabs
            BottomNavigationBar(
                strings = strings,
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    }

    if (showAuthDialog) {
        AuthDialog(
            authRepository = authRepository,
            strings = strings,
            onSuccess = { showAuthDialog = false },
            onDismiss = { showAuthDialog = false }
        )
    }

    if (showProfileDialog && currentUser != null) {
        ProfileDialog(
            user = currentUser!!,
            authRepository = authRepository,
            strings = strings,
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
            strings = strings,
            onDismissRequest = { /* Modal */ }
        )
    }

    if (showResultSheet && latestReport != null && selectedVideo != null) {
        ResultExportSheet(
            report = latestReport!!,
            settings = enhanceSettings,
            savedPath = savedGalleryPath,
            strings = strings,
            onSaveToGallery = {
                scope.launch {
                    val (uri, path) = projectRepository.exportVideoToGallery(selectedVideo!!, enhancedFile)
                    if (uri != null) {
                        savedGalleryPath = path ?: "Movies/TokEnhance"
                        Toast.makeText(
                            context,
                            "${strings.savedToastPrefix} $savedGalleryPath",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(context, strings.exportDoneToast, Toast.LENGTH_SHORT).show()
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
                        context.startActivity(Intent.createChooser(shareIntent, strings.shareVideoBtn))
                    } catch (_: Exception) {
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
    strings: AppStrings,
    currentLanguage: AppLanguage,
    onLanguageToggle: () -> Unit,
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
                text = strings.appTitle,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        // Quick Language Switch Pill (RU / EN)
        Box(
            modifier = Modifier
                .height(34.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(Color(0x16FFFFFF))
                .border(1.dp, Color(0x2EFFFFFF), RoundedCornerShape(17.dp))
                .clickable(onClick = onLanguageToggle)
                .padding(horizontal = 10.dp)
                .testTag("topbar_language_toggle"),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = currentLanguage.code.uppercase(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Compact, single-line, perfectly scaled Profile Pill
        Box(
            modifier = Modifier
                .height(34.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(Color(0x18FFFFFF))
                .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(17.dp))
                .clickable(onClick = onUserClick)
                .padding(horizontal = 12.dp)
                .testTag("topbar_profile_button"),
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
                    strings.accountGuestLabel
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
    strings: AppStrings,
    selectedVideo: VideoItem?,
    settings: EnhanceSettings,
    enhancedFile: File?,
    savedGalleryPath: String?,
    onVfrToggle: (Boolean) -> Unit,
    onOpenSettingsTab: () -> Unit,
    onUploadClick: () -> Unit,
    onSelectDemoVideo: () -> Unit,
    onPatchClick: () -> Unit,
    onDownloadToGallery: () -> Unit,
    onShareVideo: () -> Unit
) {
    val scrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val isWideScreen = maxWidth > 600.dp
        val horizontalPadding = if (isWideScreen) 32.dp else 16.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = horizontalPadding, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Hero Title
                Text(
                    text = strings.heroTitle,
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = strings.heroSubtitle,
                    color = GlassTextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // The Central Glass Optimizer Card (Clean, no Ultra 1/2/3 clutter, no paid badges)
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
                            Brush.verticalGradient(listOf(Color(0x38FFFFFF), Color(0x0CFFFFFF))),
                            RoundedCornerShape(26.dp)
                        )
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Active Pipeline Summary Row (Quick jump to Settings tab)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x12FFFFFF))
                                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                                .clickable(onClick = onOpenSettingsTab)
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${settings.targetResolution} · ${settings.targetFps} FPS · ${settings.targetBitrateMbps} Mbps · +${settings.sharpness}%",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.customizeInSettings,
                                color = GlassTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // VFR & Bitrate Stabilizer Switch Row (Free Core Engine, no paid badges)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0x0DFFFFFF))
                                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = strings.preFilterTitle,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0x20FFFFFF))
                                            .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = strings.preFilterBadge,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = strings.preFilterDesc,
                                    color = GlassTextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = settings.vfrToCfrStabilizer,
                                onCheckedChange = onVfrToggle,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0x80FFFFFF),
                                    uncheckedThumbColor = GlassTextMuted,
                                    uncheckedTrackColor = Color(0x18FFFFFF)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

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
                                .padding(vertical = 34.dp, horizontal = 20.dp)
                                .testTag("drop_video_zone"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (selectedVideo != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
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
                                        text = strings.tapToChangeVideo,
                                        color = GlassTextMuted,
                                        fontSize = 12.sp
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(58.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x16FFFFFF))
                                            .border(1.dp, Color(0x35FFFFFF), CircleShape),
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
                                        text = strings.dropVideoHere,
                                        color = Color.White,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = strings.clickToBrowse,
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
                                    text = "${strings.loadDemoVideoLink} →",
                                    color = GlassTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Live Metadata 5 Specs Row (SIZE, RESOLUTION, DURATION, CODEC, HEALTH)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SpecPill(
                                label = strings.specSize,
                                value = selectedVideo?.formattedSize ?: "—",
                                modifier = Modifier.weight(1f)
                            )
                            SpecPill(
                                label = strings.specResolution,
                                value = selectedVideo?.resolution?.replace(" ", "") ?: "—",
                                modifier = Modifier.weight(1f)
                            )
                            SpecPill(
                                label = strings.specDuration,
                                value = selectedVideo?.formattedDuration ?: "—",
                                modifier = Modifier.weight(1f)
                            )
                            SpecPill(
                                label = strings.specCodec,
                                value = selectedVideo?.codec ?: "—",
                                modifier = Modifier.weight(1f)
                            )
                            SpecPill(
                                label = strings.specHealth,
                                value = if (selectedVideo != null) "${selectedVideo.healthPercent}%" else "—",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Primary Optimize Button with vector icon (no emojis)
                        GlassButton(
                            text = strings.optimizeButtonText,
                            icon = Icons.Rounded.AutoFixHigh,
                            isPrimary = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("optimize_video_button"),
                            onClick = onPatchClick
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = strings.engineCaption,
                            color = GlassTextMuted,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Bottom Download Panel with proper vector Download Icon
                        AnimatedVisibility(visible = enhancedFile != null) {
                            Column {
                                Spacer(modifier = Modifier.height(16.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color(0x24FFFFFF), Color(0x10FFFFFF))
                                            )
                                        )
                                        .border(
                                            1.dp,
                                            Brush.verticalGradient(listOf(Color(0x50FFFFFF), Color(0x18FFFFFF))),
                                            RoundedCornerShape(20.dp)
                                        )
                                        .padding(14.dp)
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0x28FFFFFF))
                                                    .border(1.dp, Color(0x60FFFFFF), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = strings.readyToDownloadTitle,
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${settings.targetResolution} · ${settings.targetFps} FPS · Rec.709",
                                                    color = GlassTextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            GlassButton(
                                                text = strings.downloadToGalleryBtn,
                                                icon = Icons.Rounded.Download,
                                                isPrimary = true,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("download_video_button"),
                                                onClick = onDownloadToGallery
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .size(52.dp)
                                                    .clip(RoundedCornerShape(18.dp))
                                                    .background(Color(0x1AFFFFFF))
                                                    .border(1.dp, Color(0x38FFFFFF), RoundedCornerShape(18.dp))
                                                    .clickable(onClick = onShareVideo),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Share,
                                                    contentDescription = strings.shareVideoBtn,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        if (savedGalleryPath != null) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "${strings.savedInLabel} $savedGalleryPath",
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
                    }
                }

                Spacer(modifier = Modifier.height(42.dp))

                // FAQ / Questions Answered Section
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x18FFFFFF))
                        .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = strings.helpBadge,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = strings.faqHeader,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Expandable Accordions
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    strings.faqList.forEach { item ->
                        FAQAccordion(item = item)
                    }
                }

                Spacer(modifier = Modifier.height(42.dp))

                // Sleek Footer
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
                            Text(text = "v4.0", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TokEnhance Engine",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = strings.footerSubtitle,
                        color = GlassTextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(text = strings.footerTag1, color = GlassTextSecondary, fontSize = 11.sp)
                        Text(text = strings.footerTag2, color = GlassTextSecondary, fontSize = 11.sp)
                        Text(text = strings.footerTag3, color = GlassTextSecondary, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
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
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
private fun FAQAccordion(item: FAQItemText) {
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
    strings: AppStrings,
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

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x1EFFFFFF))
                .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text = strings.freeCreatorBadge,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Engine Active Card (Free, no subscription walls)
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
                        text = strings.profilePassTitle,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = strings.profilePassDesc,
                    color = GlassTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GlassButton(
            text = if (user.passwordHash.isEmpty()) strings.registerAccountBtn else strings.switchAccountBtn,
            icon = Icons.Default.Person,
            isPrimary = user.passwordHash.isEmpty(),
            modifier = Modifier.fillMaxWidth(),
            onClick = onAuthAction
        )
    }
}

@Composable
private fun BottomNavigationBar(
    strings: AppStrings,
    currentTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp)
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
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabItem(
                title = strings.tabOptimize,
                icon = Icons.Default.AutoAwesome,
                isSelected = currentTab == 0,
                testTag = "nav_tab_optimize",
                onClick = { onTabSelected(0) }
            )

            NavTabItem(
                title = strings.tabProjects,
                icon = Icons.Default.History,
                isSelected = currentTab == 1,
                testTag = "nav_tab_projects",
                onClick = { onTabSelected(1) }
            )

            NavTabItem(
                title = strings.tabSettings,
                icon = Icons.Default.Tune,
                isSelected = currentTab == 2,
                testTag = "nav_tab_settings",
                onClick = { onTabSelected(2) }
            )

            NavTabItem(
                title = strings.tabProfile,
                icon = Icons.Default.Person,
                isSelected = currentTab == 3,
                testTag = "nav_tab_profile",
                onClick = { onTabSelected(3) }
            )
        }
    }
}

@Composable
private fun NavTabItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
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
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag(testTag),
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
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}
