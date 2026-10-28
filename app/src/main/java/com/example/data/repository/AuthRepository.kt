package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.AppDatabase
import com.example.data.local.UserEntity
import com.example.data.local.isRegistered
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.UUID

class AuthRepository(private val context: Context) {
    private val userDao = AppDatabase.getInstance(context).userDao()
    private val prefs = context.getSharedPreferences("tokenhance_auth_prefs", Context.MODE_PRIVATE)

    // Synchronously initialize the currentUser so there is ZERO flicker on cold app restart
    private val _currentUser = MutableStateFlow<UserEntity?>(loadInitialSession())
    val currentUser = _currentUser.asStateFlow()

    init {
        // Asynchronously synchronize with Room DB in background
        CoroutineScope(Dispatchers.IO).launch {
            syncWithDatabase()
        }
    }

    /**
     * Synchronously restores the active user session from local preferences.
     * Guarantees that registered users remain registered immediately upon APK launch.
     */
    private fun loadInitialSession(): UserEntity {
        val isRegistered = prefs.getBoolean(KEY_IS_REGISTERED, false)
        val savedUserId = prefs.getString(KEY_ACTIVE_USER_ID, null)

        if (isRegistered && !savedUserId.isNullOrBlank()) {
            val username = prefs.getString(KEY_USERNAME, "Creator") ?: "Creator"
            val email = prefs.getString(KEY_EMAIL, "") ?: ""
            val passwordHash = prefs.getString(KEY_PASSWORD_HASH, "registered_hash") ?: "registered_hash"
            val avatarUri = prefs.getString(KEY_AVATAR_URI, null)
            val avatarIndex = prefs.getInt(KEY_AVATAR_INDEX, 0)
            val videosCount = prefs.getInt(KEY_VIDEOS_COUNT, 0)
            val savedMb = prefs.getFloat(KEY_SAVED_MB, 0f)

            return UserEntity(
                id = savedUserId,
                username = username,
                email = email,
                passwordHash = passwordHash,
                avatarIndex = avatarIndex,
                tier = "TikTok Verified Creator",
                videosEnhancedCount = videosCount,
                bandwidthSavedMb = savedMb,
                avatarUri = avatarUri
            )
        }

        return getGuestEntity()
    }

    private fun getGuestEntity(): UserEntity = UserEntity(
        id = GUEST_ID,
        username = "Creator",
        email = "creator@tokenhance.app",
        passwordHash = "",
        avatarIndex = 0,
        tier = "Creator · Free",
        videosEnhancedCount = 0,
        bandwidthSavedMb = 0f,
        avatarUri = null
    )

    private suspend fun syncWithDatabase() {
        val current = _currentUser.value
        if (current != null && current.isRegistered) {
            val dbUser = userDao.getUserById(current.id)
            if (dbUser != null) {
                _currentUser.value = dbUser
                persistUserToPrefs(dbUser, isRegistered = true)
            } else {
                // If DB was cleared or migrated, restore the user entity into Room from persistent session cache
                userDao.insertUser(current)
            }
        } else {
            val guest = userDao.getUserById(GUEST_ID)
            if (guest == null) {
                userDao.insertUser(getGuestEntity())
            }
        }
    }

    private fun persistUserToPrefs(user: UserEntity, isRegistered: Boolean) {
        prefs.edit()
            .putBoolean(KEY_IS_REGISTERED, isRegistered)
            .putString(KEY_ACTIVE_USER_ID, user.id)
            .putString(KEY_USERNAME, user.username)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_PASSWORD_HASH, user.passwordHash)
            .putString(KEY_AVATAR_URI, user.avatarUri)
            .putInt(KEY_AVATAR_INDEX, user.avatarIndex)
            .putInt(KEY_VIDEOS_COUNT, user.videosEnhancedCount)
            .putFloat(KEY_SAVED_MB, user.bandwidthSavedMb)
            .commit() // Synchronous atomic flush to disk
    }

    suspend fun signUp(
        username: String,
        email: String,
        password: String,
        avatarUri: String? = null,
        avatarIndex: Int = 0
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val trimmedUser = username.trim()

        if (trimmedEmail.isEmpty() || !trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            return@withContext Result.failure(IllegalArgumentException("Введите корректный email (например, user@mail.com)"))
        }
        if (trimmedUser.length < 2) {
            return@withContext Result.failure(IllegalArgumentException("Имя пользователя должно быть от 2 символов"))
        }
        if (password.length < 4) {
            return@withContext Result.failure(IllegalArgumentException("Пароль должен содержать минимум 4 символа"))
        }

        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null && existing.passwordHash.isNotEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Пользователь с таким email уже зарегистрирован"))
        }

        val hash = hashPassword(password)
        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            username = trimmedUser,
            email = trimmedEmail,
            passwordHash = hash,
            avatarIndex = avatarIndex,
            tier = "TikTok Verified Creator",
            videosEnhancedCount = 0,
            bandwidthSavedMb = 0f,
            avatarUri = avatarUri
        )

        userDao.insertUser(newUser)
        persistUserToPrefs(newUser, isRegistered = true)
        _currentUser.value = newUser

        Result.success(newUser)
    }

    suspend fun signIn(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(trimmedEmail)
            ?: return@withContext Result.failure(IllegalArgumentException("Аккаунт с таким email не найден"))

        val hash = hashPassword(password)
        if (user.passwordHash != hash) {
            return@withContext Result.failure(IllegalArgumentException("Неверный пароль"))
        }

        persistUserToPrefs(user, isRegistered = true)
        _currentUser.value = user
        Result.success(user)
    }

    suspend fun continueAsGuest() = withContext(Dispatchers.IO) {
        val guest = getGuestEntity()
        userDao.insertUser(guest)
        prefs.edit()
            .putBoolean(KEY_IS_REGISTERED, false)
            .putString(KEY_ACTIVE_USER_ID, GUEST_ID)
            .commit()
        _currentUser.value = guest
    }

    suspend fun signOut() = withContext(Dispatchers.IO) {
        prefs.edit()
            .putBoolean(KEY_IS_REGISTERED, false)
            .remove(KEY_ACTIVE_USER_ID)
            .remove(KEY_USERNAME)
            .remove(KEY_EMAIL)
            .remove(KEY_PASSWORD_HASH)
            .remove(KEY_AVATAR_URI)
            .commit()

        val guest = getGuestEntity()
        _currentUser.value = guest
    }

    suspend fun onVideoEnhanced(savedMb: Float = 14.5f) = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext
        userDao.incrementEnhancedStats(user.id, savedMb)
        val refreshed = userDao.getUserById(user.id)
        if (refreshed != null) {
            _currentUser.value = refreshed
            if (refreshed.isRegistered) {
                persistUserToPrefs(refreshed, isRegistered = true)
            }
        }
    }

    suspend fun updateAvatarUri(uriString: String) = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext
        val updated = user.copy(avatarUri = uriString)
        userDao.updateUser(updated)
        _currentUser.value = updated
        if (updated.isRegistered) {
            persistUserToPrefs(updated, isRegistered = true)
        }
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

    companion object {
        const val GUEST_ID = "guest_creator_default"
        private const val KEY_IS_REGISTERED = "is_registered"
        private const val KEY_ACTIVE_USER_ID = "active_user_id"
        private const val KEY_USERNAME = "cached_username"
        private const val KEY_EMAIL = "cached_email"
        private const val KEY_PASSWORD_HASH = "cached_password_hash"
        private const val KEY_AVATAR_URI = "cached_avatar_uri"
        private const val KEY_AVATAR_INDEX = "cached_avatar_index"
        private const val KEY_VIDEOS_COUNT = "cached_videos_count"
        private const val KEY_SAVED_MB = "cached_saved_mb"
    }
}
