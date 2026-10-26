package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val avatarIndex: Int = 0,
    val registeredAt: Long = System.currentTimeMillis(),
    val tier: String = "TikTok Ultra Pro",
    val videosEnhancedCount: Int = 0,
    val bandwidthSavedMb: Float = 0f
)
