package com.example.data.model

import android.net.Uri

enum class SearchEngine(
    val displayName: String,
    val searchUrlPrefix: String,
    val iconEmoji: String
) {
    BRAVE("Brave Search", "https://search.brave.com/search?q=", "🦁"),
    GOOGLE("Google", "https://www.google.com/search?q=", "🔍"),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=", "🦆"),
    BING("Bing", "https://www.bing.com/search?q=", "🌐");

    fun buildQueryUrl(query: String): String {
        return "$searchUrlPrefix${Uri.encode(query)}"
    }
}
