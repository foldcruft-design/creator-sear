package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Crisp Material 3 vector icons built directly with ImageVector.Builder.
 * Replaces the 44 MB androidx.compose.material:material-icons-extended DEX payload
 * so the final signed APK is ~4 MB and never suffers from download truncation or CRC errors.
 */
object AppIcons {

    val AutoAwesome: ImageVector by lazy {
        ImageVector.Builder(
            name = "AutoAwesome",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(19f, 9f)
            lineToRelative(1.25f, -2.75f)
            lineTo(23f, 5f)
            lineToRelative(-2.75f, -1.25f)
            lineTo(19f, 1f)
            lineToRelative(-1.25f, 2.75f)
            lineTo(15f, 5f)
            lineToRelative(2.75f, 1.25f)
            lineTo(19f, 9f)
            close()
            moveTo(11.5f, 9.5f)
            lineTo(9f, 4f)
            lineTo(6.5f, 9.5f)
            lineTo(1f, 12f)
            lineToRelative(5.5f, 2.5f)
            lineTo(9f, 20f)
            lineToRelative(2.5f, -5.5f)
            lineTo(17f, 12f)
            lineToRelative(-5.5f, -2.5f)
            close()
            moveTo(19f, 15f)
            lineToRelative(-1.25f, 2.75f)
            lineTo(15f, 19f)
            lineToRelative(2.75f, 1.25f)
            lineTo(19f, 23f)
            lineToRelative(1.25f, -2.75f)
            lineTo(23f, 19f)
            lineToRelative(-2.75f, -1.25f)
            lineTo(19f, 15f)
            close()
        }.build()
    }

    val AutoFixHigh: ImageVector get() = AutoAwesome

    val Bolt: ImageVector by lazy {
        ImageVector.Builder(
            name = "Bolt",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(11f, 21f)
            horizontalLineToRelative(-1f)
            lineToRelative(1f, -7f)
            horizontalLineTo(7.5f)
            curveToRelative(-0.58f, 0f, -0.57f, -0.32f, -0.38f, -0.66f)
            lineToRelative(0.1f, -0.16f)
            lineTo(12.92f, 3f)
            horizontalLineToRelative(1f)
            lineToRelative(-1f, 7f)
            horizontalLineToRelative(3.5f)
            curveToRelative(0.49f, 0f, 0.56f, 0.33f, 0.47f, 0.51f)
            lineToRelative(-0.07f, 0.15f)
            lineTo(11f, 21f)
            close()
        }.build()
    }

    val Tune: ImageVector by lazy {
        ImageVector.Builder(
            name = "Tune",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(3f, 17f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(-2f)
            horizontalLineTo(3f)
            close()
            moveTo(3f, 5f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(10f)
            verticalLineTo(5f)
            horizontalLineTo(3f)
            close()
            moveTo(13f, 21f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(8f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-8f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(6f)
            horizontalLineToRelative(2f)
            close()
            moveTo(7f, 9f)
            verticalLineToRelative(2f)
            horizontalLineTo(3f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(4f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(2f)
            verticalLineTo(9f)
            horizontalLineTo(7f)
            close()
            moveTo(21f, 13f)
            verticalLineToRelative(-2f)
            horizontalLineTo(11f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(10f)
            close()
            moveTo(15f, 9f)
            horizontalLineToRelative(2f)
            verticalLineTo(7f)
            horizontalLineToRelative(4f)
            verticalLineTo(5f)
            horizontalLineToRelative(-4f)
            verticalLineTo(3f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(6f)
            close()
        }.build()
    }

    val Download: ImageVector by lazy {
        ImageVector.Builder(
            name = "Download",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(19f, 9f)
            horizontalLineToRelative(-4f)
            verticalLineTo(3f)
            horizontalLineTo(9f)
            verticalLineToRelative(6f)
            horizontalLineTo(5f)
            lineToRelative(7f, 7f)
            lineToRelative(7f, -7f)
            close()
            moveTo(5f, 18f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(14f)
            verticalLineToRelative(-2f)
            horizontalLineTo(5f)
            close()
        }.build()
    }

    val FileDownload: ImageVector get() = Download

    val History: ImageVector by lazy {
        ImageVector.Builder(
            name = "History",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(13f, 3f)
            curveToRelative(-4.97f, 0f, -9f, 4.03f, -9f, 9f)
            horizontalLineTo(1f)
            lineToRelative(3.89f, 3.89f)
            lineToRelative(0.07f, 0.14f)
            lineTo(9f, 12f)
            horizontalLineTo(6f)
            curveToRelative(0f, -3.87f, 3.13f, -7f, 7f, -7f)
            reflectiveCurveToRelative(7f, 3.13f, 7f, 7f)
            reflectiveCurveToRelative(-3.13f, 7f, -7f, 7f)
            curveToRelative(-1.93f, 0f, -3.68f, -0.79f, -4.94f, -2.06f)
            lineToRelative(-1.42f, 1.42f)
            curveTo(8.27f, 19.99f, 10.51f, 21f, 13f, 21f)
            curveToRelative(4.97f, 0f, 9f, -4.03f, 9f, -9f)
            reflectiveCurveToRelative(-4.03f, -9f, -9f, -9f)
            close()
            moveTo(12f, 8f)
            verticalLineToRelative(5f)
            lineToRelative(4.28f, 2.54f)
            lineToRelative(0.72f, -1.21f)
            lineToRelative(-3.5f, -2.08f)
            verticalLineTo(8f)
            horizontalLineTo(12f)
            close()
        }.build()
    }

    val RestartAlt: ImageVector get() = History

    val Shield: ImageVector by lazy {
        ImageVector.Builder(
            name = "Shield",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(12f, 1f)
            lineTo(3f, 5f)
            verticalLineToRelative(6f)
            curveToRelative(0f, 5.55f, 3.84f, 10.74f, 9f, 12f)
            curveToRelative(5.16f, -1.26f, 9f, -6.45f, 9f, -12f)
            verticalLineTo(5f)
            lineToRelative(-9f, -4f)
            close()
        }.build()
    }

    val Language: ImageVector by lazy {
        ImageVector.Builder(
            name = "Language",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(11.99f, 2f)
            curveTo(6.47f, 2f, 2f, 6.48f, 2f, 12f)
            reflectiveCurveToRelative(4.47f, 10f, 9.99f, 10f)
            curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
            reflectiveCurveTo(17.52f, 2f, 11.99f, 2f)
            close()
            moveTo(18.92f, 8f)
            horizontalLineToRelative(-2.95f)
            curveToRelative(-0.32f, -1.25f, -0.78f, -2.45f, -1.38f, -3.56f)
            curveToRelative(1.84f, 0.63f, 3.37f, 1.91f, 4.33f, 3.56f)
            close()
            moveTo(12f, 4.04f)
            curveToRelative(0.83f, 1.2f, 1.48f, 2.53f, 1.91f, 3.96f)
            horizontalLineToRelative(-3.82f)
            curveToRelative(0.43f, -1.43f, 1.08f, -2.76f, 1.91f, -3.96f)
            close()
            moveTo(4.26f, 14f)
            curveTo(4.1f, 13.36f, 4f, 12.69f, 4f, 12f)
            reflectiveCurveToRelative(0.1f, -1.36f, 0.26f, -2f)
            horizontalLineToRelative(3.38f)
            curveToRelative(-0.08f, 0.66f, -0.14f, 1.32f, -0.14f, 2f)
            reflectiveCurveToRelative(0.06f, 1.34f, 0.14f, 2f)
            horizontalLineTo(4.26f)
            close()
            moveTo(5.08f, 16f)
            horizontalLineToRelative(2.95f)
            curveToRelative(0.32f, 1.25f, 0.78f, 2.45f, 1.38f, 3.56f)
            curveToRelative(-1.84f, -0.63f, -3.37f, -1.9f, -4.33f, -3.56f)
            close()
            moveTo(8.03f, 8f)
            horizontalLineTo(5.08f)
            curveToRelative(0.96f, -1.66f, 2.49f, -2.93f, 4.33f, -3.56f)
            curveTo(8.81f, 5.55f, 8.35f, 6.75f, 8.03f, 8f)
            close()
            moveTo(12f, 19.96f)
            curveToRelative(-0.83f, -1.2f, -1.48f, -2.53f, -1.91f, -3.96f)
            horizontalLineToRelative(3.82f)
            curveToRelative(-0.43f, 1.43f, -1.08f, 2.76f, -1.91f, 3.96f)
            close()
            moveTo(14.34f, 14f)
            horizontalLineTo(9.66f)
            curveToRelative(-0.09f, -0.66f, -0.16f, -1.32f, -0.16f, -2f)
            reflectiveCurveToRelative(0.07f, -1.35f, 0.16f, -2f)
            horizontalLineToRelative(4.68f)
            curveToRelative(0.09f, 0.65f, 0.16f, 1.32f, 0.16f, 2f)
            reflectiveCurveToRelative(-0.07f, 1.34f, -0.16f, 2f)
            close()
            moveTo(14.59f, 19.56f)
            curveToRelative(0.6f, -1.11f, 1.06f, -2.31f, 1.38f, -3.56f)
            horizontalLineToRelative(2.95f)
            curveToRelative(-0.96f, 1.65f, -2.49f, 2.93f, -4.33f, 3.56f)
            close()
            moveTo(16.36f, 14f)
            curveToRelative(0.08f, -0.66f, 0.14f, -1.32f, 0.14f, -2f)
            reflectiveCurveToRelative(-0.06f, -1.34f, -0.14f, -2f)
            horizontalLineToRelative(3.38f)
            curveToRelative(0.16f, 0.64f, 0.26f, 1.31f, 0.26f, 2f)
            reflectiveCurveToRelative(-0.1f, 1.36f, -0.26f, 2f)
            horizontalLineToRelative(-3.38f)
            close()
        }.build()
    }

    val Movie: ImageVector by lazy {
        ImageVector.Builder(
            name = "Movie",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(18f, 4f)
            lineToRelative(2f, 4f)
            horizontalLineToRelative(-3f)
            lineToRelative(-2f, -4f)
            horizontalLineToRelative(-2f)
            lineToRelative(2f, 4f)
            horizontalLineToRelative(-3f)
            lineToRelative(-2f, -4f)
            horizontalLineTo(8f)
            lineToRelative(2f, 4f)
            horizontalLineTo(7f)
            lineTo(5f, 4f)
            horizontalLineTo(4f)
            curveToRelative(-1.1f, 0f, -1.99f, 0.9f, -1.99f, 2f)
            lineTo(2f, 18f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(16f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineTo(4f)
            horizontalLineToRelative(-4f)
            close()
        }.build()
    }

    val VideoLibrary: ImageVector get() = Movie

    val ExpandMore: ImageVector by lazy {
        ImageVector.Builder(
            name = "ExpandMore",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(16.59f, 8.59f)
            lineTo(12f, 13.17f)
            lineTo(7.41f, 8.59f)
            lineTo(6f, 10f)
            lineToRelative(6f, 6f)
            lineToRelative(6f, -6f)
            close()
        }.build()
    }

    val AddAPhoto: ImageVector by lazy {
        ImageVector.Builder(
            name = "AddAPhoto",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(3f, 4f)
            verticalLineTo(1f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(3f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(2f)
            horizontalLineTo(5f)
            verticalLineToRelative(3f)
            horizontalLineTo(3f)
            verticalLineTo(6f)
            horizontalLineTo(0f)
            verticalLineTo(4f)
            horizontalLineToRelative(3f)
            close()
            moveTo(6f, 10f)
            verticalLineTo(7f)
            horizontalLineToRelative(3f)
            verticalLineTo(4f)
            horizontalLineToRelative(7f)
            lineToRelative(1.83f, 2f)
            horizontalLineTo(21f)
            curveToRelative(1.1f, 0f, 2f, 0.9f, 2f, 2f)
            verticalLineToRelative(12f)
            curveToRelative(0f, 1.1f, -0.9f, 2f, -2f, 2f)
            horizontalLineTo(5f)
            curveToRelative(-1.1f, 0f, -2f, -0.9f, -2f, -2f)
            verticalLineTo(10f)
            horizontalLineToRelative(3f)
            close()
            moveTo(13f, 19f)
            curveToRelative(2.76f, 0f, 5f, -2.24f, 5f, -5f)
            reflectiveCurveToRelative(-2.24f, -5f, -5f, -5f)
            reflectiveCurveToRelative(-5f, 2.24f, -5f, 5f)
            reflectiveCurveToRelative(2.24f, 5f, 5f, 5f)
            close()
        }.build()
    }

    val Visibility: ImageVector by lazy {
        ImageVector.Builder(
            name = "Visibility",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(12f, 4.5f)
            curveTo(7f, 4.5f, 2.73f, 7.61f, 1f, 12f)
            curveToRelative(1.73f, 4.39f, 6f, 7.5f, 11f, 7.5f)
            reflectiveCurveToRelative(9.27f, -3.11f, 11f, -7.5f)
            curveToRelative(-1.73f, -4.39f, -6f, -7.5f, -11f, -7.5f)
            close()
            moveTo(12f, 17f)
            curveToRelative(-2.76f, 0f, -5f, -2.24f, -5f, -5f)
            reflectiveCurveToRelative(2.24f, -5f, 5f, -5f)
            reflectiveCurveToRelative(5f, 2.24f, 5f, 5f)
            reflectiveCurveToRelative(-2.24f, 5f, -5f, 5f)
            close()
            moveTo(12f, 9f)
            curveToRelative(-1.66f, 0f, -3f, 1.34f, -3f, 3f)
            reflectiveCurveToRelative(1.34f, 3f, 3f, 3f)
            reflectiveCurveToRelative(3f, -1.34f, 3f, -3f)
            reflectiveCurveToRelative(-1.34f, -3f, -3f, -3f)
            close()
        }.build()
    }

    val VisibilityOff: ImageVector by lazy {
        ImageVector.Builder(
            name = "VisibilityOff",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(12f, 7f)
            curveToRelative(2.76f, 0f, 5f, 2.24f, 5f, 5f)
            curveToRelative(0f, 0.65f, -0.13f, 1.26f, -0.36f, 1.83f)
            lineToRelative(2.92f, 2.92f)
            curveToRelative(1.51f, -1.26f, 2.7f, -2.89f, 3.43f, -4.75f)
            curveToRelative(-1.73f, -4.39f, -6f, -7.5f, -11f, -7.5f)
            curveToRelative(-1.4f, 0f, -2.74f, 0.25f, -3.98f, 0.7f)
            lineToRelative(2.16f, 2.16f)
            curveTo(10.74f, 7.13f, 11.35f, 7f, 12f, 7f)
            close()
            moveTo(2f, 4.27f)
            lineToRelative(2.28f, 2.28f)
            lineToRelative(0.46f, 0.46f)
            curveTo(3.08f, 8.3f, 1.78f, 10.02f, 1f, 12f)
            curveToRelative(1.73f, 4.39f, 6f, 7.5f, 11f, 7.5f)
            curveToRelative(1.55f, 0f, 3.03f, -0.3f, 4.38f, -0.84f)
            lineToRelative(0.42f, 0.42f)
            lineTo(19.73f, 22f)
            lineTo(21f, 20.73f)
            lineTo(3.27f, 3f)
            lineTo(2f, 4.27f)
            close()
        }.build()
    }

    val Storage: ImageVector by lazy {
        ImageVector.Builder(
            name = "Storage",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(2f, 20f)
            horizontalLineToRelative(20f)
            verticalLineToRelative(-4f)
            horizontalLineTo(2f)
            verticalLineToRelative(4f)
            close()
            moveTo(4f, 17f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineTo(4f)
            verticalLineToRelative(-2f)
            close()
            moveTo(2f, 4f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(20f)
            verticalLineTo(4f)
            horizontalLineTo(2f)
            close()
            moveTo(6f, 7f)
            horizontalLineTo(4f)
            verticalLineTo(5f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            close()
            moveTo(2f, 14f)
            horizontalLineToRelative(20f)
            verticalLineToRelative(-4f)
            horizontalLineTo(2f)
            verticalLineToRelative(4f)
            close()
            moveTo(4f, 11f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineTo(4f)
            verticalLineToRelative(-2f)
            close()
        }.build()
    }

    val DeleteSweep: ImageVector by lazy {
        ImageVector.Builder(
            name = "DeleteSweep",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(15f, 16f)
            horizontalLineToRelative(4f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-4f)
            close()
            moveTo(15f, 8f)
            horizontalLineToRelative(7f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-7f)
            close()
            moveTo(15f, 12f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-6f)
            close()
            moveTo(3f, 18f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(6f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineTo(8f)
            horizontalLineTo(3f)
            verticalLineToRelative(10f)
            close()
            moveTo(14f, 5f)
            horizontalLineToRelative(-3f)
            lineToRelative(-1f, -1f)
            horizontalLineTo(6f)
            lineTo(5f, 5f)
            horizontalLineTo(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(12f)
            close()
        }.build()
    }

    val Logout: ImageVector by lazy {
        ImageVector.Builder(
            name = "Logout",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(17f, 7f)
            lineToRelative(-1.41f, 1.41f)
            lineTo(18.17f, 11f)
            horizontalLineTo(8f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(10.17f)
            lineToRelative(-2.58f, 2.58f)
            lineTo(17f, 17f)
            lineToRelative(5f, -5f)
            close()
            moveTo(4f, 5f)
            horizontalLineToRelative(8f)
            verticalLineTo(3f)
            horizontalLineTo(4f)
            curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(8f)
            verticalLineToRelative(-2f)
            horizontalLineTo(4f)
            verticalLineTo(5f)
            close()
        }.build()
    }

    val ContentCut: ImageVector by lazy {
        ImageVector.Builder(
            name = "ContentCut",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(9.64f, 7.64f)
            curveToRelative(0.23f, -0.5f, 0.36f, -1.05f, 0.36f, -1.64f)
            curveToRelative(0f, -2.21f, -1.79f, -4f, -4f, -4f)
            reflectiveCurveTo(2f, 3.79f, 2f, 6f)
            reflectiveCurveToRelative(1.79f, 4f, 4f, 4f)
            curveToRelative(0.59f, 0f, 1.14f, -0.13f, 1.64f, -0.36f)
            lineTo(10f, 12f)
            lineToRelative(-2.36f, 2.36f)
            curveTo(7.14f, 14.13f, 6.59f, 14f, 6f, 14f)
            curveToRelative(-2.21f, 0f, -4f, 1.79f, -4f, 4f)
            reflectiveCurveToRelative(1.79f, 4f, 4f, 4f)
            reflectiveCurveToRelative(4f, -1.79f, 4f, -4f)
            curveToRelative(0f, -0.59f, -0.13f, -1.14f, -0.36f, -1.64f)
            lineTo(12f, 14f)
            lineToRelative(7f, 7f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(-1f)
            lineTo(9.64f, 7.64f)
            close()
            moveTo(6f, 8f)
            curveToRelative(-1.1f, 0f, -2f, -0.89f, -2f, -2f)
            reflectiveCurveToRelative(0.9f, -2f, 2f, -2f)
            reflectiveCurveToRelative(2f, 0.89f, 2f, 2f)
            reflectiveCurveToRelative(-0.9f, 2f, -2f, 2f)
            close()
            moveTo(6f, 20f)
            curveToRelative(-1.1f, 0f, -2f, -0.89f, -2f, -2f)
            reflectiveCurveToRelative(0.9f, -2f, 2f, -2f)
            reflectiveCurveToRelative(2f, 0.89f, 2f, 2f)
            reflectiveCurveToRelative(-0.9f, 2f, -2f, 2f)
            close()
            moveTo(12f, 12.5f)
            curveToRelative(-0.28f, 0f, -0.5f, -0.22f, -0.5f, -0.5f)
            reflectiveCurveToRelative(0.22f, -0.5f, 0.5f, -0.5f)
            reflectiveCurveToRelative(0.5f, 0.22f, 0.5f, 0.5f)
            reflectiveCurveToRelative(-0.22f, 0.5f, -0.5f, 0.5f)
            close()
            moveTo(19f, 3f)
            lineToRelative(-6f, 6f)
            lineToRelative(2f, 2f)
            lineToRelative(7f, -7f)
            verticalLineTo(3f)
            horizontalLineToRelative(-3f)
            close()
        }.build()
    }

    val PlayArrow: ImageVector by lazy {
        ImageVector.Builder(
            name = "PlayArrow",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(8f, 5f)
            verticalLineToRelative(14f)
            lineToRelative(11f, -7f)
            close()
        }.build()
    }

    val Pause: ImageVector by lazy {
        ImageVector.Builder(
            name = "Pause",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(6f, 19f)
            horizontalLineToRelative(4f)
            verticalLineTo(5f)
            horizontalLineTo(6f)
            verticalLineToRelative(14f)
            close()
            moveTo(14f, 5f)
            verticalLineToRelative(14f)
            horizontalLineToRelative(4f)
            verticalLineTo(5f)
            horizontalLineToRelative(-4f)
            close()
        }.build()
    }

    val Videocam: ImageVector by lazy {
        ImageVector.Builder(
            name = "Videocam",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(17f, 10.5f)
            verticalLineTo(7f)
            curveToRelative(0f, -0.55f, -0.45f, -1f, -1f, -1f)
            horizontalLineTo(4f)
            curveToRelative(-0.55f, 0f, -1f, 0.45f, -1f, 1f)
            verticalLineToRelative(10f)
            curveToRelative(0f, 0.55f, 0.45f, 1f, 1f, 1f)
            horizontalLineToRelative(12f)
            curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
            verticalLineToRelative(-3.5f)
            lineToRelative(4f, 4f)
            verticalLineToRelative(-11f)
            lineToRelative(-4f, 4f)
            close()
        }.build()
    }

    val TextFields: ImageVector by lazy {
        ImageVector.Builder(
            name = "TextFields",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(2.5f, 4f)
            verticalLineToRelative(3f)
            horizontalLineToRelative(5f)
            verticalLineToRelative(12f)
            horizontalLineToRelative(3f)
            verticalLineTo(7f)
            horizontalLineToRelative(5f)
            verticalLineTo(4f)
            horizontalLineTo(2.5f)
            close()
            moveTo(21.5f, 9f)
            horizontalLineToRelative(-9f)
            verticalLineToRelative(3f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(7f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(-7f)
            horizontalLineToRelative(3f)
            verticalLineTo(9f)
            close()
        }.build()
    }

    val MusicNote: ImageVector by lazy {
        ImageVector.Builder(
            name = "MusicNote",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(12f, 3f)
            verticalLineToRelative(10.55f)
            curveToRelative(-0.59f, -0.34f, -1.27f, -0.55f, -2f, -0.55f)
            curveToRelative(-2.21f, 0f, -4f, 1.79f, -4f, 4f)
            reflectiveCurveToRelative(1.79f, 4f, 4f, 4f)
            reflectiveCurveToRelative(4f, -1.79f, 4f, -4f)
            verticalLineTo(7f)
            horizontalLineToRelative(4f)
            verticalLineTo(3f)
            horizontalLineToRelative(-6f)
            close()
        }.build()
    }

    val Palette: ImageVector by lazy {
        ImageVector.Builder(
            name = "Palette",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(12f, 3f)
            curveToRelative(-4.97f, 0f, -9f, 4.03f, -9f, 9f)
            reflectiveCurveToRelative(4.03f, 9f, 9f, 9f)
            curveToRelative(0.83f, 0f, 1.5f, -0.67f, 1.5f, -1.5f)
            curveToRelative(0f, -0.39f, -0.15f, -0.74f, -0.39f, -1.01f)
            curveToRelative(-0.23f, -0.26f, -0.38f, -0.61f, -0.38f, -0.99f)
            curveToRelative(0f, -0.83f, 0.67f, -1.5f, 1.5f, -1.5f)
            horizontalLineTo(16f)
            curveToRelative(2.76f, 0f, 5f, -2.24f, 5f, -5f)
            curveToRelative(0f, -4.42f, -4.03f, -8f, -9f, -8f)
            close()
            moveTo(6.5f, 12f)
            curveToRelative(-0.83f, 0f, -1.5f, -0.67f, -1.5f, -1.5f)
            reflectiveCurveTo(5.67f, 9f, 6.5f, 9f)
            reflectiveCurveTo(8f, 9.67f, 8f, 10.5f)
            reflectiveCurveTo(7.33f, 12f, 6.5f, 12f)
            close()
            moveTo(9.5f, 8f)
            curveTo(8.67f, 8f, 8f, 7.33f, 8f, 6.5f)
            reflectiveCurveTo(8.67f, 5f, 9.5f, 5f)
            reflectiveCurveTo(11f, 5.67f, 11f, 6.5f)
            reflectiveCurveTo(10.33f, 8f, 9.5f, 8f)
            close()
            moveTo(14.5f, 8f)
            curveToRelative(-0.83f, 0f, -1.5f, -0.67f, -1.5f, -1.5f)
            reflectiveCurveTo(13.67f, 5f, 14.5f, 5f)
            reflectiveCurveTo(16f, 5.67f, 16f, 6.5f)
            reflectiveCurveTo(15.33f, 8f, 14.5f, 8f)
            close()
            moveTo(17.5f, 12f)
            curveToRelative(-0.83f, 0f, -1.5f, -0.67f, -1.5f, -1.5f)
            reflectiveCurveTo(16.67f, 9f, 17.5f, 9f)
            reflectiveCurveTo(19f, 9.67f, 19f, 10.5f)
            reflectiveCurveTo(18.33f, 12f, 17.5f, 12f)
            close()
        }.build()
    }

    val CenterFocus: ImageVector by lazy {
        ImageVector.Builder(
            name = "CenterFocus",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(12f, 8f)
            curveToRelative(-2.21f, 0f, -4f, 1.79f, -4f, 4f)
            reflectiveCurveToRelative(1.79f, 4f, 4f, 4f)
            reflectiveCurveToRelative(4f, -1.79f, 4f, -4f)
            reflectiveCurveToRelative(-1.79f, -4f, -4f, -4f)
            close()
            moveTo(5f, 15f)
            horizontalLineTo(3f)
            verticalLineToRelative(4f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(4f)
            verticalLineToRelative(-2f)
            horizontalLineTo(5f)
            verticalLineToRelative(-4f)
            close()
            moveTo(5f, 5f)
            horizontalLineToRelative(4f)
            verticalLineTo(3f)
            horizontalLineTo(5f)
            curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(2f)
            verticalLineTo(5f)
            close()
            moveTo(19f, 3f)
            horizontalLineToRelative(-4f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(4f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(2f)
            verticalLineTo(5f)
            curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
            close()
            moveTo(19f, 19f)
            horizontalLineToRelative(-4f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(4f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(4f)
            close()
        }.build()
    }

    val KeyframeDiamond: ImageVector by lazy {
        ImageVector.Builder(
            name = "KeyframeDiamond",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(12f, 2f)
            lineTo(22f, 12f)
            lineTo(12f, 22f)
            lineTo(2f, 12f)
            close()
        }.build()
    }

    val Add: ImageVector by lazy {
        ImageVector.Builder(
            name = "Add",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.White)) {
            moveTo(19f, 13f)
            horizontalLineToRelative(-6f)
            verticalLineToRelative(6f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-6f)
            horizontalLineTo(5f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(6f)
            verticalLineTo(5f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(6f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(2f)
            close()
        }.build()
    }
}

