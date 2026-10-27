package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.AppDatabase
import com.example.data.local.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.UUID

class AuthRepository(context: Context) {
    private val userDao = AppDatabase.getInstance(context).userDao()

    private val prefs = context.getSharedPreferences("tokenhance_auth_prefs", Context.MODE_PRIVATE)
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser = _currentUser.asStateFlow()

    init {
        // Restore active user session if exists
        val savedUserId = prefs.getString("active_user_id", null)
        if (savedUserId != null) {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                val user = userDao.getUserById(savedUserId)
                if (user != null) {
                    _currentUser.value = user
                } else {
                    createOrGetGuest()
                }
            }
        } else {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                createOrGetGuest()
            }
        }
    }

    private suspend fun createOrGetGuest(): UserEntity {
        val guestId = "guest_creator_default"
        var guest = userDao.getUserById(guestId)
        if (guest == null) {
            guest = UserEntity(
                id = guestId,
                username = "Creator",
                email = "creator@tokenhance.app",
                passwordHash = "",
                avatarIndex = 0,
                tier = "Creator · Free",
                videosEnhancedCount = 0,
                bandwidthSavedMb = 0f
            )
            userDao.insertUser(guest)
        }
        _currentUser.value = guest
        prefs.edit().putString("active_user_id", guestId).apply()
        return guest
    }

    suspend fun signUp(username: String, email: String, password: String):Result<UserEntity> {
        val trimmedEmail = email.trim().lowercase()
        val trimmedUser = username.trim()
        if (trimmedEmail.isEmpty() || !trimmedEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Введите корректный email адрес / Enter valid email"))
        }
        if (trimmedUser.length < 2) {
            return Result.failure(IllegalArgumentException("Имя пользователя слишком короткое / Username too short"))
        }
        if (password.length < 4) {
            return Result.failure(IllegalArgumentException("Пароль от 4 символов / Password min 4 chars"))
        }

        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null && existing.passwordHash.isNotEmpty()) {
            return Result.failure(IllegalArgumentException("Email уже зарегистрирован / Email already exists"))
        }

        val hash = hashPassword(password)
        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            username = trimmedUser,
            email = trimmedEmail,
            passwordHash = hash,
            avatarIndex = (trimmedUser.hashCode() % 5 + 5) % 5,
            tier = "Creator · All Features Unlocked",
            videosEnhancedCount = 0,
            bandwidthSavedMb = 0f
        )
        userDao.insertUser(newUser)
        _currentUser.value = newUser
        prefs.edit().putString("active_user_id", newUser.id).apply()
        return Result.success(newUser)
    }

    suspend fun signIn(email: String, password: String): Result<UserEntity> {
        val trimmedEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(trimmedEmail)
            ?: return Result.failure(IllegalArgumentException("Аккаунт с таким email не найден"))

        val hash = hashPassword(password)
        if (user.passwordHash != hash) {
            return Result.failure(IllegalArgumentException("Неверный пароль"))
        }

        _currentUser.value = user
        prefs.edit().putString("active_user_id", user.id).apply()
        return Result.success(user)
    }

    suspend fun continueAsGuest() {
        createOrGetGuest()
    }

    suspend fun signOut() {
        createOrGetGuest()
    }

    suspend fun onVideoEnhanced(savedMb: Float = 12.4f) {
        val user = _currentUser.value ?: return
        userDao.incrementEnhancedStats(user.id, savedMb)
        val refreshed = userDao.getUserById(user.id)
        if (refreshed != null) {
            _currentUser.value = refreshed
        }
    }

    suspend fun updateAvatarUri(uriString: String) {
        val user = _currentUser.value ?: return
        val updated = user.copy(avatarUri = uriString)
        userDao.updateUser(updated)
        _currentUser.value = updated
    }

    suspend fun saveAvatarFromUri(context: Context, sourceUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val user = _currentUser.value ?: return@withContext null
            val avatarDir = File(context.filesDir, "avatars").apply { mkdirs() }
            val avatarFile = File(avatarDir, "avatar_${user.id}.jpg")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(avatarFile).use { output ->
                    input.copyTo(output)
                }
            }
            val localPath = avatarFile.absolutePath
            updateAvatarUri(localPath)
            localPath
        } catch (_: Exception) {
            null
        }
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
