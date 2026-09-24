package com.example.data.local

import com.example.data.model.SecurityThreat
import kotlinx.coroutines.flow.Flow

class BrowserRepository(private val database: AegisDatabase) {
    val bookmarks: Flow<List<BookmarkEntity>> = database.bookmarkDao().getAllBookmarks()
    val history: Flow<List<HistoryEntity>> = database.historyDao().getAllHistory()
    val threatEvents: Flow<List<SecurityThreatEntity>> = database.securityThreatDao().getAllThreats()
    val totalThreatsCount: Flow<Int> = database.securityThreatDao().getTotalThreatsCount()

    fun isBookmarked(url: String): Flow<Boolean> = database.bookmarkDao().isBookmarked(url)

    suspend fun addBookmark(title: String, url: String) {
        if (url.isBlank() || url == "about:blank") return
        val finalTitle = if (title.isBlank()) url else title
        database.bookmarkDao().insert(BookmarkEntity(title = finalTitle, url = url))
    }

    suspend fun removeBookmark(id: Long) {
        database.bookmarkDao().deleteById(id)
    }

    suspend fun removeBookmarkByUrl(url: String) {
        database.bookmarkDao().deleteByUrl(url)
    }

    suspend fun addHistoryEntry(title: String, url: String, isThreatBlocked: Boolean = false) {
        if (url.isBlank() || url == "about:blank") return
        val finalTitle = if (title.isBlank()) url else title
        database.historyDao().insert(
            HistoryEntity(
                title = finalTitle,
                url = url,
                isThreatBlocked = isThreatBlocked
            )
        )
    }

    suspend fun removeHistoryEntry(id: Long) {
        database.historyDao().deleteById(id)
    }

    suspend fun clearHistory() {
        database.historyDao().clearAllHistory()
    }

    suspend fun recordThreat(threat: SecurityThreat) {
        database.securityThreatDao().insert(
            SecurityThreatEntity(
                domain = threat.domain,
                url = threat.url,
                threatType = threat.type.name,
                description = threat.description,
                timestamp = threat.timestamp
            )
        )
    }

    suspend fun clearAllThreats() {
        database.securityThreatDao().clearThreatHistory()
    }
}
