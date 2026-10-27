package com.example.domain.model

enum class AppLanguage(val code: String, val displayName: String) {
    RU("ru", "Русский"),
    EN("en", "English");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().find { it.code.equals(code, ignoreCase = true) } ?: RU
        }
    }
}

data class FAQItemText(
    val question: String,
    val answer: String
)

class AppStrings(val lang: AppLanguage) {
    val isRu = lang == AppLanguage.RU

    // Top Bar
    val appTitle = "TokEnhance"
    val accountGuestLabel = if (isRu) "Аккаунт" else "Account"

    // Bottom Navigation Tabs
    val tabOptimize = if (isRu) "Оптимизация" else "Optimize"
    val tabProjects = if (isRu) "Проекты" else "Projects"
    val tabSettings = if (isRu) "Настройки" else "Settings"
    val tabProfile = if (isRu) "Профиль" else "Profile"

    // Home / Optimizer Screen
    val heroTitle = if (isRu) "Оптимизация видео" else "Optimize a video"
    val heroSubtitle = if (isRu) {
        "MP4 и MOV · любой кодек — прямая калибровка потока, защита битрейта и чёткости от сжатия TikTok и Reels."
    } else {
        "MP4 & MOV · any codec — on-device stream calibration, bitrate sweet-spot, and anti-compression protection for TikTok & Reels."
    }

    val preFilterTitle = if (isRu) "Стабилизация VFR и битрейта" else "VFR & Bitrate Stabilizer"
    val preFilterBadge = if (isRu) "АКТИВНЫЙ МОДУЛЬ" else "CORE ENGINE"
    val preFilterDesc = if (isRu) {
        "Выравнивает плавающий фреймрейт (VFR → CFR), очищает лишние метаданные и защищает видео от лагов и мыла в TikTok."
    } else {
        "Normalizes Variable Frame Rate (VFR → CFR), strips bloated metadata, and prevents TikTok playback stutter & blur."
    }

    val dropVideoHere = if (isRu) "Загрузите видео сюда" else "Drop your video here"
    val clickToBrowse = if (isRu) "нажмите для выбора · MP4, MOV, WebM" else "or tap to browse · MP4, MOV, WebM"
    val tapToChangeVideo = if (isRu) "Нажмите, чтобы выбрать другое видео" else "Tap to choose a different video"
    val loadDemoVideoLink = if (isRu) "Загрузить тестовое демо-видео" else "Load sample demo video for testing"
    val videoSelectedToast = if (isRu) "Видео загружено и проанализировано" else "Video loaded and analyzed"

    // 5 Spec Pills
    val specSize = if (isRu) "РАЗМЕР" else "SIZE"
    val specResolution = if (isRu) "РАЗРЕШЕНИЕ" else "RESOLUTION"
    val specDuration = if (isRu) "ДЛИНА" else "DURATION"
    val specCodec = if (isRu) "КОДЕК" else "CODEC"
    val specHealth = if (isRu) "КАЧЕСТВО" else "HEALTH"

    // Action & Download Buttons on Main Card
    val optimizeButtonText = if (isRu) "Улучшить и оптимизировать видео" else "Optimize & Patch Video"
    val engineCaption = if (isRu) {
        "Аппаратная обработка потока · Rec.709 · Без потери качества"
    } else {
        "Hardware stream remuxing · Rec.709 · Zero quality loss"
    }

    val readyToDownloadTitle = if (isRu) "Видео оптимизировано" else "Video Optimized & Ready"
    val downloadToGalleryBtn = if (isRu) "Скачать видео в Галерею" else "Download Video to Gallery"
    val shareVideoBtn = if (isRu) "Поделиться" else "Share"
    val openReportBtn = if (isRu) "Отчёт" else "Report"
    val savedInLabel = if (isRu) "Сохранено в:" else "Saved to:"
    val savedToastPrefix = if (isRu) "Видео сохранено в Галерею:" else "Video saved to Gallery:"
    val exportDoneToast = if (isRu) "Экспорт успешно завершён!" else "Export completed!"

    // Active Algorithm Summary Pill on Main Screen
    val activeProfileLabel = if (isRu) "Активные параметры:" else "Active pipeline:"
    val customizeInSettings = if (isRu) "Настроить" else "Configure"

    // FAQ Section
    val helpBadge = if (isRu) "Помощь" else "Help"
    val faqHeader = if (isRu) "ЧАСТЫЕ ВОПРОСЫ" else "QUESTIONS, ANSWERED."
    val footerSubtitle = if (isRu) "Профессиональные алгоритмы качества видео." else "Professional video quality tools for creators."
    val footerTag1 = if (isRu) "Гайд 60 FPS" else "60fps upload guide"
    val footerTag2 = if (isRu) "Защита от мыла" else "Anti-blur matrix"
    val footerTag3 = if (isRu) "Rec.709 Поток" else "Direct stream ready"

    val faqList: List<FAQItemText> = if (isRu) {
        listOf(
            FAQItemText(
                question = "Как именно алгоритмы защищают видео от сжатия TikTok?",
                answer = "Движок пересобирает MP4-контейнер на аппаратном уровне: выравнивает временные метки кадров (VFR → строгий CFR 60/30 FPS), очищает мусорные атомы метаданных, прописывает цветовой стандарт Rec.709 и калибрует битрейт в диапазон Sweet-Spot (12–16 Mbps). Серверы TikTok пропускают такой поток без агрессивного пережатия."
            ),
            FAQItemText(
                question = "Какие форматы видео поддерживаются?",
                answer = "Поддерживаются все видеофайлы MP4, MOV и WebM (H.264 / HEVC), снятые на камеру смартфона или экспортированные из CapCut, Premiere Pro, After Effects и DaVinci Resolve."
            ),
            FAQItemText(
                question = "Как скачать обработанное видео?",
                answer = "Сразу после оптимизации в нижней части карточки появляется стеклянная панель с кнопкой «Скачать видео в Галерею». Видео мгновенно сохраняется в папку Movies/TokEnhance на вашем устройстве."
            ),
            FAQItemText(
                question = "Почему моё видео лагает в TikTok и как это исправить?",
                answer = "Причина лагов — плавающая частота кадров (Variable Frame Rate) после записи экрана или монтажа, а также слишком высокий пиковый битрейт (>25 Mbps). Модуль стабилизации VFR выравнивает каждый кадр по точной сетке таймингов."
            ),
            FAQItemText(
                question = "Где настроить резкость, битрейт и FPS?",
                answer = "Перейдите во вкладку «Настройки» в нижней панели навигации. Там доступны ползунки адаптивной резкости (без белых ореолов), защиты глубоких теней IRE, целевого битрейта, FPS и цветовой матрицы."
            )
        )
    } else {
        listOf(
            FAQItemText(
                question = "What does the optimizer engine actually do?",
                answer = "It performs hardware-level MP4 stream remuxing: aligns presentation timestamps from Variable Frame Rate (VFR) to strict Constant Frame Rate (CFR), strips bloated metadata atoms, injects Rec.709 color signaling, and targets the 12–16 Mbps sweet-spot so TikTok servers avoid aggressive downsampling."
            ),
            FAQItemText(
                question = "What file formats are supported?",
                answer = "All standard MP4, MOV, and WebM (H.264 / HEVC) video files recorded on smartphones or exported from CapCut, Premiere Pro, After Effects, or DaVinci Resolve."
            ),
            FAQItemText(
                question = "How do I download the optimized video?",
                answer = "Right after optimization completes, a dedicated 'Download Video to Gallery' button appears at the bottom of the main card and in the report sheet, saving directly to your phone's Movies/TokEnhance folder."
            ),
            FAQItemText(
                question = "My video lags on TikTok — how does VFR stabilization fix it?",
                answer = "When videos have inconsistent frame intervals (VFR) or extreme bitrate spikes (>25 Mbps), TikTok drops frames. Our VFR stabilizer aligns every frame timestamp to a uniform CFR grid with synchronized AAC audio."
            ),
            FAQItemText(
                question = "Where can I customize sharpness, bitrate, and FPS?",
                answer = "Open the 'Settings' tab in the bottom bar to fine-tune Halo-Free Sharpness, Shadow IRE Protection, Target Bitrate Sweet-Spot, Frame Rate, and Auto-Save preferences."
            )
        )
    }

    // Settings Screen
    val settingsTitle = if (isRu) "Настройки" else "Settings"
    val settingsSubtitle = if (isRu) {
        "Персонализация интерфейса, языка и алгоритмов обработки видеопотока"
    } else {
        "Customize language, interface behavior, and video stream optimization algorithms"
    }

    val sectionLanguage = if (isRu) "ЯЗЫК ИНТЕРФЕЙСА" else "INTERFACE LANGUAGE"
    val languageLabel = if (isRu) "Выберите язык приложения" else "Select application language"

    val sectionEngineParams = if (isRu) "КАЛИБРОВКА АЛГОРИТМОВ ВИДЕО" else "VIDEO ALGORITHM CALIBRATION"
    val sharpnessLabel = if (isRu) "Адаптивная резкость (Halo-Free)" else "Adaptive Sharpness (Halo-Free)"
    val shadowLiftLabel = if (isRu) "Защита глубоких теней (0–15 IRE)" else "Deep Shadow Lift (0–15 IRE)"
    val bitrateSweetSpotLabel = if (isRu) "Целевой битрейт (Sweet-Spot)" else "Target Bitrate (Sweet-Spot)"
    val targetFpsLabel = if (isRu) "Частота кадров (CFR сетка)" else "Target Frame Rate (CFR Grid)"
    val targetResolutionLabel = if (isRu) "Выходное разрешение" else "Output Resolution"

    val sectionStreamFilters = if (isRu) "МОДУЛИ ПОТОКА И АВТОМАТИЗАЦИЯ" else "STREAM FILTERS & AUTOMATION"
    val antiBandingTitle = if (isRu) "Anti-Banding матрица (10-bit Dither)" else "Anti-Banding Matrix (10-bit Dither)"
    val antiBandingSub = if (isRu) "Устраняет ступенчатые полосы на тёмных градиентах и небе" else "Eliminates color banding steps in dark gradients and skies"

    val denoiseTitle = if (isRu) "Подавление цифрового шума матрицы" else "Temporal Sensor Denoise"
    val denoiseSub = if (isRu) "Очищает цветовой шум до сжатия, экономя до 25% полезного битрейта" else "Cleans chroma sensor noise so bitrate is spent on real details"

    val rec709Title = if (isRu) "Калибровка цвета Rec.709 OLED" else "Rec.709 OLED Color Calibration"
    val rec709Sub = if (isRu) "Сохраняет сочность и контраст без выцветания после загрузки" else "Preserves contrast and prevents washed-out colors after upload"

    val autoSaveTitle = if (isRu) "Автосохранение в Галерею" else "Auto-Save to Gallery"
    val autoSaveSub = if (isRu) "Автоматически сохранять видео в Movies/TokEnhance после обработки" else "Automatically save optimized video to Movies/TokEnhance"

    val sectionStorage = if (isRu) "ПАМЯТЬ И СБРОС" else "STORAGE & RESET"
    val cacheUsedLabel = if (isRu) "Временный кэш обработанных видео" else "Processed video temporary cache"
    val clearCacheBtn = if (isRu) "Очистить кэш" else "Clear Cache"
    val cacheClearedToast = if (isRu) "Кэш временных файлов очищен" else "Temporary video cache cleared"
    val resetSettingsBtn = if (isRu) "Сбросить настройки по умолчанию" else "Reset Algorithm Settings to Default"
    val settingsResetToast = if (isRu) "Восстановлены оптимальные настройки" else "Default optimal settings restored"

    // Processing Dialog
    fun stageTitle(stage: ProcessingStage): String = when (stage) {
        ProcessingStage.ANALYZING -> if (isRu) "Анализ видеопотока и кадров" else "Analyzing Video Stream & Keyframes"
        ProcessingStage.SHARPENING -> if (isRu) "Адаптивная резкость и денойз" else "Halo-Free Edge & Sensor Calibration"
        ProcessingStage.DYNAMIC_RANGE -> if (isRu) "Калибровка теней 0-15 IRE" else "Shadow IRE & Anti-Banding Matrix"
        ProcessingStage.COLOR_CALIBRATION -> if (isRu) "Синхронизация VFR → CFR и Rec.709" else "VFR → CFR Timing & Rec.709 Tagging"
        ProcessingStage.ENCODING -> if (isRu) "Аппаратная сборка MP4 контейнера" else "Hardware MP4 Fast-Start Muxing"
        ProcessingStage.COMPLETED -> if (isRu) "Оптимизация завершена!" else "Optimization Complete!"
    }

    fun stageDetail(stage: ProcessingStage): String = when (stage) {
        ProcessingStage.ANALYZING -> if (isRu) "Чтение разрешения, кодека, битрейта и яркости..." else "Inspecting resolution, codec, bitrate, and luma histogram..."
        ProcessingStage.SHARPENING -> if (isRu) "Оптимизация контуров без ореолов (Halo Suppression)..." else "Optimizing micro-contrast without white edge halos..."
        ProcessingStage.DYNAMIC_RANGE -> if (isRu) "Защита тёмных участков от распада на квадраты..." else "Protecting dark macroblocks from compression artifacts..."
        ProcessingStage.COLOR_CALIBRATION -> if (isRu) "Выравнивание таймкодов кадров и цветовой матрицы..." else "Normalizing frame presentation timestamps and BT.709..."
        ProcessingStage.ENCODING -> if (isRu) "Запись чистого видеопотока без лишних метаданных..." else "Writing clean fast-start MP4 stream..."
        ProcessingStage.COMPLETED -> if (isRu) "Видео готово к сохранению в Галерею." else "Video is ready to download to Gallery."
    }

    fun stepOfTotal(step: Int): String = if (isRu) "Этап $step из 5" else "Step $step of 5"

    // Result Sheet
    val resultTitle = if (isRu) "Видео успешно оптимизировано!" else "Video Successfully Optimized!"
    val resultSubtitle = if (isRu) "Поток откалиброван для загрузки без потери качества" else "Stream calibrated for lossless social media upload"
    val protectionIndexLabel = if (isRu) "ИНДЕКС ЗАЩИТЫ ПОТОКА" else "STREAM PROTECTION INDEX"
    val passthroughBadge = "DIRECT STREAM"
    val labelTargetRes = if (isRu) "Разрешение потока" else "Stream Resolution"
    val labelFrameRate = if (isRu) "Частота кадров" else "Frame Rate Grid"
    val labelBitrate = if (isRu) "Целевой битрейт" else "Target Bitrate"
    val labelSharpness = if (isRu) "Адаптивная резкость" else "Adaptive Sharpness"
    val labelShadows = if (isRu) "Защита теней (IRE)" else "Shadow Lift (IRE)"
    val labelKeyframes = if (isRu) "Синхронизация кадров" else "Frame Sync & Remux"
    val closeBtn = if (isRu) "Закрыть" else "Close"

    // Projects Screen
    val emptyProjectsTitle = if (isRu) "История проектов пуста" else "No Optimized Videos Yet"
    val emptyProjectsSub = if (isRu) {
        "Оптимизируйте ваше первое видео на главном экране, и оно сохранится здесь."
    } else {
        "Optimize your first video on the main tab and it will appear here."
    }
    val deleteLabel = if (isRu) "Удалить" else "Delete"

    // Profile Screen & Dialog
    val freeCreatorBadge = if (isRu) "Все функции открыты · Бесплатно" else "All Features Unlocked · Free"
    val profilePassTitle = if (isRu) "Движок защиты качества активен" else "Anti-Compression Engine Active"
    val profilePassDesc = if (isRu) {
        "Все алгоритмы стабилизации потока, Rec.709 калибровки и сохранения в Галерею доступны бесплатно и без ограничений."
    } else {
        "All stream stabilization, VFR-to-CFR normalization, Rec.709 calibration, and Gallery export features are 100% free and unlocked."
    }
    val statVideosProcessed = if (isRu) "Обработано видео" else "Videos Optimized"
    val statProtectionIndex = if (isRu) "Индекс защиты" else "Protection Score"
    val registerAccountBtn = if (isRu) "Создать профиль автора" else "Create Creator Profile"
    val switchAccountBtn = if (isRu) "Сменить аккаунт / Выйти" else "Switch Account / Sign Out"

    // Auth Dialog
    val signUpHeader = if (isRu) "Регистрация профиля" else "Create Creator Profile"
    val signInHeader = if (isRu) "Вход в аккаунт" else "Sign In to TokEnhance"
    val authSub = if (isRu) "Сохраняйте историю оптимизированных видео на устройстве" else "Save your optimized video history and custom presets"
    val tabSignUp = if (isRu) "Регистрация" else "Sign Up"
    val tabSignIn = if (isRu) "Вход" else "Sign In"
    val placeholderUsername = if (isRu) "Имя автора / Никнейм" else "Creator Name / Username"
    val placeholderEmail = "Email (creator@tok.io)"
    val placeholderPassword = if (isRu) "Пароль (от 4 символов)" else "Password (min 4 chars)"
    val submitSignUp = if (isRu) "Создать профиль" else "Create Account"
    val submitSignIn = if (isRu) "Войти" else "Sign In"
    val continueGuest = if (isRu) "Продолжить без регистрации" else "Continue as Guest"
}
