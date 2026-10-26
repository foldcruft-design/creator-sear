package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ProjectEntity
import com.example.data.local.UserEntity
import com.example.data.repository.AuthRepository
import com.example.data.repository.VideoProjectRepository
import com.example.domain.engine.TikTokOptimizerEngine
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.ProcessingStage
import com.example.domain.model.TikTokReadinessReport
import com.example.domain.model.VideoItem
import com.example.ui.components.AlgorithmTunerSheet
import com.example.ui.components.GlassButton
import com.example.ui.components.IceCyan
import com.example.ui.components.IceSilver
import com.example.ui.components.MutedSlate
import com.example.ui.components.ProcessingDialog
import com.example.ui.components.ResultExportSheet
import com.example.ui.components.VideoComparisonPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    // Current settings
    var enhanceSettings by remember { mutableStateOf(EnhanceSettings()) }

    // Default bundled sample video
    val defaultSampleVideo = remember {
        VideoItem(
            uri = Uri.parse("android.resource://${context.packageName}/${R.raw.sample_raw_clip}"),
            title = "Aesthetic Night Clip (Тестовый исходник)",
            durationMs = 4000L,
            resolution = "1080 × 1920",
            originalBitrateMbps = 2.0f,
            isSample = true,
            rawResId = R.raw.sample_raw_clip
        )
    }

    var selectedVideo by remember { mutableStateOf(defaultSampleVideo) }

    // Video Picker launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "Видео из галереи"
            selectedVideo = VideoItem(
                uri = uri,
                title = fileName,
                durationMs = 5000L,
                resolution = "1080 × 1920",
                originalBitrateMbps = 3.5f,
                isSample = false,
                rawResId = null
            )
            Toast.makeText(context, "Видео успешно загружено!", Toast.LENGTH_SHORT).show()
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
                        Color(0xFF07090E),
                        Color(0xFF0D1019),
                        Color(0xFF111420)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Sleek frosted Top Bar
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

            // Main Content Area based on Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (currentTab) {
                    0 -> {
                        // Optimizer Screen
                        OptimizerContent(
                            video = selectedVideo,
                            settings = enhanceSettings,
                            onSettingsChanged = { enhanceSettings = it },
                            onUploadClick = {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            },
                            onSelectDemo = { demoId ->
                                val (resId, title) = if (demoId == 1) {
                                    R.raw.sample_raw_clip to "Neon Night Aesthetic (Raw)"
                                } else {
                                    R.raw.sample_motion_clip to "Street Motion 60FPS (Raw)"
                                }
                                selectedVideo = VideoItem(
                                    uri = Uri.parse("android.resource://${context.packageName}/$resId"),
                                    title = title,
                                    durationMs = 4000L,
                                    resolution = "1080 × 1920",
                                    originalBitrateMbps = 2.5f,
                                    isSample = true,
                                    rawResId = resId
                                )
                            },
                            onEnhanceClick = {
                                // Start processing animation and computation
                                showProcessingDialog = true
                                processingProgress = 0.05f
                                processingStage = ProcessingStage.ANALYZING

                                scope.launch {
                                    // Pipeline step 1: Analysis
                                    delay(600)
                                    processingProgress = 0.25f
                                    processingStage = ProcessingStage.SHARPENING

                                    // Pipeline step 2: Unsharp Mask
                                    delay(750)
                                    processingProgress = 0.50f
                                    processingStage = ProcessingStage.DYNAMIC_RANGE

                                    // Pipeline step 3: Dynamic range & shadow lifting
                                    delay(650)
                                    processingProgress = 0.75f
                                    processingStage = ProcessingStage.COLOR_CALIBRATION

                                    // Pipeline step 4: Rec.709
                                    delay(650)
                                    processingProgress = 0.92f
                                    processingStage = ProcessingStage.ENCODING

                                    // Pipeline step 5: Sweet-spot Encoding & Save
                                    delay(600)
                                    val report = TikTokOptimizerEngine.analyzeTikTokCompatibility(selectedVideo, enhanceSettings)
                                    latestReport = report

                                    // Save project to Room DB
                                    currentUser?.id?.let { uid ->
                                        projectRepository.saveProject(uid, selectedVideo, enhanceSettings, report)
                                        authRepository.onVideoEnhanced(14.5f)
                                    }

                                    processingProgress = 1.0f
                                    processingStage = ProcessingStage.COMPLETED
                                    delay(300)
                                    showProcessingDialog = false
                                    showResultSheet = true
                                }
                            }
                        )
                    }
                    1 -> {
                        // Projects History
                        ProjectsScreen(
                            projects = projects,
                            onSelectProject = { proj ->
                                selectedVideo = VideoItem(
                                    uri = Uri.parse(proj.originalUri),
                                    title = proj.title,
                                    resolution = proj.resolution,
                                    originalBitrateMbps = proj.originalBitrateMbps
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
                        // Profile Screen view
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

            // Bottom Navigation Bar
            BottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    }

    // Auth Dialog
    if (showAuthDialog) {
        AuthDialog(
            authRepository = authRepository,
            onSuccess = { showAuthDialog = false },
            onDismiss = { showAuthDialog = false }
        )
    }

    // Profile Dialog
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

    // Processing Dialog
    if (showProcessingDialog) {
        ProcessingDialog(
            currentStage = processingStage,
            progress = processingProgress,
            onDismissRequest = { /* Modal */ }
        )
    }

    // Result Sheet
    if (showResultSheet && latestReport != null) {
        ResultExportSheet(
            report = latestReport!!,
            settings = enhanceSettings,
            onSaveToGallery = {
                scope.launch {
                    val uri = projectRepository.exportVideoToGallery(selectedVideo, enhanceSettings)
                    if (uri != null) {
                        Toast.makeText(context, "Видео успешно сохранено в Галерею (Movies/TokEnhance)!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Экспорт завершен!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onShareToTikTok = {
                scope.launch {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "video/*"
                            putExtra(Intent.EXTRA_STREAM, selectedVideo.uri)
                            putExtra(Intent.EXTRA_TEXT, "Enhanced with TokEnhance Ultra Quality #tiktokquality #hdvideo")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Поделиться в TikTok"))
                    } catch (e: Exception) {
                        Toast.makeText(context, "Отправка в TikTok...", Toast.LENGTH_SHORT).show()
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
        // App brand & logo
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x2238BDF8))
                .border(1.dp, Color(0x6638BDF8), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = IceCyan,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "TokEnhance",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x2A38BDF8))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ULTRA",
                        color = IceCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = "Anti-Compression Engine",
                color = MutedSlate,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // User profile pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x18FFFFFF))
                .border(1.dp, Color(0x28FFFFFF), RoundedCornerShape(20.dp))
                .clickable(onClick = onUserClick)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0284C7), Color(0xFF38BDF8))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (user?.username ?: "C").take(1).uppercase(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = user?.username?.take(12) ?: "Регистрация",
                    color = IceSilver,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun OptimizerContent(
    video: VideoItem,
    settings: EnhanceSettings,
    onSettingsChanged: (EnhanceSettings) -> Unit,
    onUploadClick: () -> Unit,
    onSelectDemo: (Int) -> Unit,
    onEnhanceClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Upload Card & Quick Demo Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Upload button
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x28FFFFFF), Color(0x10FFFFFF))
                        )
                    )
                    .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(18.dp))
                    .clickable(onClick = onUploadClick)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Загрузить видео",
                        tint = IceCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Загрузить видео",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Из галереи (MP4/MOV)",
                            color = MutedSlate,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Test Demo 1 button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x14FFFFFF))
                    .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(18.dp))
                    .clickable { onSelectDemo(1) }
                    .padding(horizontal = 10.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = IceSilver,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Тест: Ночь",
                            color = IceSilver,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Neon 1080p",
                            color = MutedSlate,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // Test Demo 2 button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x14FFFFFF))
                    .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(18.dp))
                    .clickable { onSelectDemo(2) }
                    .padding(horizontal = 10.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = IceSilver,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Тест: Движ",
                            color = IceSilver,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Street 60FPS",
                            color = MutedSlate,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive Before / After Video Player
        VideoComparisonPlayer(
            videoItem = video,
            settings = settings,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Current video stats bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x10FFFFFF))
                .border(1.dp, Color(0x1CFFFFFF), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VideoFile,
                    contentDescription = null,
                    tint = IceCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = video.title,
                    color = IceSilver,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "9:16 Vertical",
                    color = MutedSlate,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Big Primary Action: Enhance for TikTok
        GlassButton(
            text = "УЛУЧШИТЬ ДЛЯ TIKTOK (ULTRA HD)",
            icon = Icons.Default.AutoAwesome,
            isPrimary = true,
            modifier = Modifier.fillMaxWidth(),
            onClick = onEnhanceClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Algorithm Tuner Sheet with presets & sliders
        AlgorithmTunerSheet(
            settings = settings,
            onSettingsChanged = onSettingsChanged,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
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
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF0284C7), Color(0xFF38BDF8))
                    )
                )
                .border(2.dp, Color(0x8038BDF8), CircleShape),
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
            color = MutedSlate,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Pro perks card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x2238BDF8), Color(0x0C38BDF8))
                    )
                )
                .border(1.dp, Color(0x4438BDF8), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = IceCyan,
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
                    color = IceSilver.copy(alpha = 0.85f),
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
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xDD0C0F17))
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(Color(0x35FFFFFF), Color(0x10FFFFFF))),
                    RoundedCornerShape(24.dp)
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
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0x2838BDF8) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) IceCyan else MutedSlate,
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
