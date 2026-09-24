package com.example.data.model

import java.util.UUID

data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "New Tab",
    val url: String = "about:blank",
    val isIncognito: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isSecureHttps: Boolean = true,
    val blockedTrackersCount: Int = 0,
    val activeThreat: SecurityThreat? = null,
    val desktopMode: Boolean = false
) {
    val isHome: Boolean
        get() = url == "about:blank" || url.isEmpty()

    val displayTitle: String
        get() = if (isHome) "Home" else if (title.isBlank()) url else title
}
