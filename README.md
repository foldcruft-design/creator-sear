# TokEnhance (TikTok Video Quality Optimizer)

TokEnhance — Android-приложение в стиле тёмного глассморфизма (Dark Glassmorphism) для оптимизации и защиты качества вертикальных видео (Reels / Shorts / TikTok) от агрессивного пережатия алгоритмами платформ.

## 🚀 Сборка через GitHub Actions

В репозитории настроен автоматический CI/CD workflow: `.github/workflows/build.yml`.

### Что делает GitHub Actions:
1. Автоматически запускается при каждом `push` или `pull_request` в ветки `main` и `master`.
2. Поддерживает ручной запуск через вкладку **Actions** -> **Build Android APK** -> **Run workflow** (`workflow_dispatch`).
3. Разворачивает JDK 17 и Android SDK.
4. Прогоняет локальные юнит-тесты (`testDebugUnitTest`).
5. Компилирует готовый установочный файл APK (`assembleDebug`).
6. Публикует готовый файл в артефакты сборки (**Artifacts**) под именем `TokEnhance-debug-apk`.

### Как скачать скомпилированный APK из GitHub:
1. Откройте ваш репозиторий на GitHub.
2. Перейдите во вкладку **Actions**.
3. Нажмите на последний выполненный запуск workflow **Build Android APK**.
4. Прокрутите страницу вниз до блока **Artifacts**.
5. Нажмите на **TokEnhance-debug-apk**, чтобы скачать ZIP-архив с APK-файлом для установки на Android-устройство.

## 🛠 Локальная сборка
```bash
./gradlew assembleDebug
```
Скомпилированный APK будет находиться по пути:
`app/build/outputs/apk/debug/app-debug.apk`
