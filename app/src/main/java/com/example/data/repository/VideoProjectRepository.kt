package com.example.data.repository

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.data.local.AppDatabase
import com.example.data.local.ProjectEntity
import com.example.domain.model.EnhanceSettings
import com.example.domain.model.TikTokReadinessReport
import com.example.domain.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class VideoProjectRepository(private val context: Context) {
    private val projectDao = AppDatabase.getInstance(context).projectDao()

    fun getProjectsFlow(userId: String): Flow<List<ProjectEntity>> {
        return projectDao.getProjectsByUser(userId)
    }

    suspend fun saveProject(
        userId: String,
        videoItem: VideoItem,
        settings: EnhanceSettings,
        report: TikTokReadinessReport,
        enhancedFilePath: String? = null
    ): ProjectEntity = withContext(Dispatchers.IO) {
        val projectId = UUID.randomUUID().toString()
        val project = ProjectEntity(
            id = projectId,
            userId = userId,
            title = videoItem.title,
            originalUri = videoItem.uri.toString(),
            enhancedUri = enhancedFilePath ?: videoItem.uri.toString(),
            presetName = settings.preset.title,
            originalBitrateMbps = videoItem.originalBitrateMbps,
            targetBitrateMbps = settings.targetBitrateMbps,
            resolution = settings.targetResolution,
            fps = settings.targetFps,
            sharpnessLevel = settings.sharpness,
            shadowLiftPercent = settings.shadowLift,
            antiBanding = settings.antiBanding,
            tiktokReadinessScore = report.score,
            createdAt = System.currentTimeMillis()
        )
        projectDao.insertProject(project)
        project
    }

    suspend fun deleteProject(projectId: String) = withContext(Dispatchers.IO) {
        projectDao.deleteProjectById(projectId)
    }

    /**
     * Directly saves the enhanced video to the device's public Gallery (Movies/TokEnhance).
     * Scans media so it appears immediately in the user's Gallery / Photos app.
     */
    suspend fun exportVideoToGallery(
        videoItem: VideoItem,
        enhancedFile: File? = null
    ): Pair<Uri?, String?> = withContext(Dispatchers.IO) {
        try {
            val fileName = enhancedFile?.name?.takeIf { it.startsWith("TokEnhance_") }
                ?: "TokEnhance_${System.currentTimeMillis()}.mp4"
            val resolver = context.contentResolver

            val inputStream: InputStream? = when {
                enhancedFile != null && enhancedFile.exists() -> FileInputStream(enhancedFile)
                videoItem.localFilePath != null -> FileInputStream(File(videoItem.localFilePath))
                videoItem.rawResId != null -> context.resources.openRawResource(videoItem.rawResId)
                else -> resolver.openInputStream(videoItem.uri)
            }

            if (inputStream == null) return@withContext Pair(null, null)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/TokEnhance")
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }

                val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val itemUri = resolver.insert(collection, contentValues) ?: return@withContext Pair(null, null)

                resolver.openOutputStream(itemUri)?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                contentValues.clear()
                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                resolver.update(itemUri, contentValues, null, null)

                // Scan file so Gallery shows it instantly
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath + "/TokEnhance/" + fileName),
                    arrayOf("video/mp4"),
                    null
                )

                return@withContext Pair(itemUri, "Movies/TokEnhance/$fileName")
            } else {
                val moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                val targetDir = File(moviesDir, "TokEnhance").apply { mkdirs() }
                val targetFile = File(targetDir, fileName)

                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(targetFile.absolutePath),
                    arrayOf("video/mp4"),
                    null
                )

                return@withContext Pair(Uri.fromFile(targetFile), targetFile.absolutePath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, null)
        }
    }
}
