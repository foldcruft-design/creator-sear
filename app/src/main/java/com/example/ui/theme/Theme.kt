package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GlassDarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = Color.White,
    primaryContainer = AccentCyanDark,
    onPrimaryContainer = Color.White,
    secondary = AccentSilver,
    onSecondary = DarkObsidian,
    background = DarkObsidian,
    onBackground = AccentSilver,
    surface = DarkSurface,
    onSurface = AccentSilver,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = AccentMuted,
    outline = AccentBorder
)

@Composable
fun TokEnhanceTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = GlassDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
