package com.example.data.repository

import android.content.ContentValues
import android.content.Context
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
        report: TikTokReadinessReport
    ): ProjectEntity = withContext(Dispatchers.IO) {
        val projectId = UUID.randomUUID().toString()
        val project = ProjectEntity(
            id = projectId,
            userId = userId,
            title = videoItem.title,
            originalUri = videoItem.uri.toString(),
            enhancedUri = videoItem.uri.toString(),
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

    suspend fun exportVideoToGallery(
        videoItem: VideoItem,
        settings: EnhanceSettings
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            val fileName = "TokEnhance_${System.currentTimeMillis()}_1080p60.mp4"
            val resolver = context.contentResolver

            val inputStream: InputStream? = if (videoItem.rawResId != null) {
                context.resources.openRawResource(videoItem.rawResId)
            } else {
                resolver.openInputStream(videoItem.uri)
            }

            if (inputStream == null) return@withContext null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/TokEnhance")
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }

                val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val itemUri = resolver.insert(collection, contentValues) ?: return@withContext null

                resolver.openOutputStream(itemUri)?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                contentValues.clear()
                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                resolver.update(itemUri, contentValues, null, null)

                return@withContext itemUri
            } else {
                val moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                val targetDir = File(moviesDir, "TokEnhance").apply { mkdirs() }
                val targetFile = File(targetDir, fileName)

                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                return@withContext Uri.fromFile(targetFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
