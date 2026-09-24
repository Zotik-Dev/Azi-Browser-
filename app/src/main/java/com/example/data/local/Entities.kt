package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val visitedAt: Long = System.currentTimeMillis(),
    val isThreatBlocked: Boolean = false
)

@Entity(tableName = "security_events")
data class SecurityThreatEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val domain: String,
    val url: String,
    val threatType: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
