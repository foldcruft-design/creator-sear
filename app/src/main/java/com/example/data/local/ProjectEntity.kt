package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val originalUri: String,
    val enhancedUri: String,
    val presetName: String,
    val originalBitrateMbps: Float,
    val targetBitrateMbps: Float,
    val resolution: String,
    val fps: Int,
    val sharpnessLevel: Int,
    val shadowLiftPercent: Int,
    val antiBanding: Boolean,
    val tiktokReadinessScore: Int,
    val createdAt: Long = System.currentTimeMillis()
)
