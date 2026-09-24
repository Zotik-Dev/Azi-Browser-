package com.example.ui

import android.app.Application
import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebStorage
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AegisDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.BrowserRepository
import com.example.data.local.HistoryEntity
import com.example.data.local.SecurityThreatEntity
import com.example.data.model.BrowserTab
import com.example.data.model.SecurityThreat
import com.example.security.DownloadRisk
import com.example.security.WebShieldEngine
import com.example.vpn.VpnController
import com.example.vpn.VpnState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DownloadPrompt(
    val fileName: String,
    val url: String,
    val mimeType: String,
    val contentLength: Long,
    val risk: DownloadRisk
)

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BrowserRepository(AegisDatabase.getDatabase(application))

    private val initialTab = BrowserTab(url = "about:blank", title = "New Tab")
    private val _tabs = MutableStateFlow<List<BrowserTab>>(listOf(initialTab))
    val tabs: StateFlow<List<BrowserTab>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow(initialTab.id)
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    val vpnState: StateFlow<VpnState> = VpnController.vpnState

    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.bookmarks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val history: StateFlow<List<HistoryEntity>> = repository.history.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val threatEvents: StateFlow<List<SecurityThreatEntity>> = repository.threatEvents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // UI overlays & sheets
    private val _showVpnSheet = MutableStateFlow(false)
    val showVpnSheet: StateFlow<Boolean> = _showVpnSheet.asStateFlow()

    private val _showTabManager = MutableStateFlow(false)
    val showTabManager: StateFlow<Boolean> = _showTabManager.asStateFlow()

    private val _showBookmarksHistory = MutableStateFlow(false)
    val showBookmarksHistory: StateFlow<Boolean> = _showBookmarksHistory.asStateFlow()

    private val _showSecurityAudit = MutableStateFlow(false)
    val showSecurityAudit: StateFlow<Boolean> = _showSecurityAudit.asStateFlow()

    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()

    private val _pendingDownload = MutableStateFlow<DownloadPrompt?>(null)
    val pendingDownload: StateFlow<DownloadPrompt?> = _pendingDownload.asStateFlow()

    val currentTab: BrowserTab?
        get() = _tabs.value.find { it.id == _activeTabId.value } ?: _tabs.value.firstOrNull()

    // Navigation and URL loading
    fun navigate(input: String) {
        val sanitized = WebShieldEngine.sanitizeUrl(input)
        val threat = if (vpnState.value.isMalwareShieldActive) {
            WebShieldEngine.evaluateUrl(sanitized)
        } else null

        if (threat != null) {
            viewModelScope.launch {
                repository.recordThreat(threat)
            }
            VpnController.incrementNeutralizedThreats()
            _tabs.update { list ->
                list.map { tab ->
                    if (tab.id == _activeTabId.value) {
                        tab.copy(
                            url = sanitized,
                            activeThreat = threat,
                            isLoading = false
                        )
                    } else tab
                }
            }
        } else {
            _tabs.update { list ->
                list.map { tab ->
                    if (tab.id == _activeTabId.value) {
                        tab.copy(
                            url = sanitized,
                            title = if (sanitized == "about:blank") "New Tab" else tab.title,
                            activeThreat = null,
                            isLoading = sanitized != "about:blank",
                            canGoBack = if (sanitized == "about:blank") false else tab.canGoBack,
                            canGoForward = if (sanitized == "about:blank") false else tab.canGoForward,
                            isSecureHttps = sanitized.startsWith("https://")
                        )
                    } else tab
                }
            }
        }
    }

    fun bypassThreatWarning() {
        val active = currentTab ?: return
        _tabs.update { list ->
            list.map { tab ->
                if (tab.id == active.id) {
                    tab.copy(activeThreat = null, isLoading = true)
                } else tab
            }
        }
    }

    fun dismissThreatSafely() {
        val active = currentTab ?: return
        _tabs.update { list ->
            list.map { tab ->
                if (tab.id == active.id) {
                    tab.copy(url = "about:blank", title = "New Tab", activeThreat = null, isLoading = false)
                } else tab
            }
        }
    }

    fun updateTabState(
        tabId: String,
        title: String? = null,
        url: String? = null,
        isLoading: Boolean? = null,
        progress: Int? = null,
        canGoBack: Boolean? = null,
        canGoForward: Boolean? = null,
        isSecureHttps: Boolean? = null,
        blockedTrackerCountInc: Int = 0
    ) {
        _tabs.update { list ->
            list.map { tab ->
                if (tab.id == tabId) {
                    val updatedUrl = url ?: tab.url
                    val updatedTitle = title ?: tab.title
                    if (url != null && url != tab.url && url != "about:blank" && !tab.isIncognito) {
                        viewModelScope.launch {
                            repository.addHistoryEntry(updatedTitle, updatedUrl)
                        }
                    }
                    if (blockedTrackerCountInc > 0) {
                        VpnController.incrementBlockedTrackers(blockedTrackerCountInc)
                    }
                    tab.copy(
                        title = updatedTitle,
                        url = updatedUrl,
                        isLoading = isLoading ?: tab.isLoading,
                        progress = progress ?: tab.progress,
                        canGoBack = canGoBack ?: tab.canGoBack,
                        canGoForward = canGoForward ?: tab.canGoForward,
                        isSecureHttps = isSecureHttps ?: tab.isSecureHttps,
                        blockedTrackersCount = tab.blockedTrackersCount + blockedTrackerCountInc
                    )
                } else tab
            }
        }
    }

    fun createNewTab(url: String = "about:blank", isIncognito: Boolean = false) {
        val newTab = BrowserTab(url = url, isIncognito = isIncognito)
        _tabs.update { it + newTab }
        _activeTabId.value = newTab.id
        _showTabManager.value = false
    }

    fun closeTab(tabId: String) {
        val list = _tabs.value
        if (list.size <= 1) {
            // Reset the only tab to blank
            _tabs.value = listOf(BrowserTab(url = "about:blank", title = "New Tab"))
            _activeTabId.value = _tabs.value.first().id
            return
        }

        val remaining = list.filter { it.id != tabId }
        _tabs.value = remaining
        if (_activeTabId.value == tabId) {
            _activeTabId.value = remaining.last().id
        }
    }

    fun switchTab(tabId: String) {
        _activeTabId.value = tabId
        _showTabManager.value = false
    }

    fun closeAllTabs() {
        val single = BrowserTab(url = "about:blank", title = "New Tab")
        _tabs.value = listOf(single)
        _activeTabId.value = single.id
        _showTabManager.value = false
    }

    fun toggleDesktopMode() {
        val active = currentTab ?: return
        _tabs.update { list ->
            list.map { tab ->
                if (tab.id == active.id) {
                    tab.copy(desktopMode = !tab.desktopMode)
                } else tab
            }
        }
    }

    fun toggleBookmarkCurrentPage() {
        val tab = currentTab ?: return
        if (tab.isHome) return

        viewModelScope.launch {
            val isAlready = bookmarks.value.any { it.url == tab.url }
            if (isAlready) {
                repository.removeBookmarkByUrl(tab.url)
            } else {
                repository.addBookmark(tab.title, tab.url)
            }
        }
    }

    fun removeBookmark(id: Long) {
        viewModelScope.launch { repository.removeBookmark(id) }
    }

    fun removeHistoryEntry(id: Long) {
        viewModelScope.launch { repository.removeHistoryEntry(id) }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }

    fun promptDownload(fileName: String, url: String, mimeType: String, contentLength: Long) {
        val risk = WebShieldEngine.inspectDownload(fileName, mimeType)
        _pendingDownload.value = DownloadPrompt(
            fileName = fileName,
            url = url,
            mimeType = mimeType,
            contentLength = contentLength,
            risk = risk
        )
    }

    fun dismissDownloadPrompt() {
        _pendingDownload.value = null
    }

    // Emergency Panic Button: Complete privacy wipe
    fun panicWipeSession(context: Context) {
        try {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
            WebStorage.getInstance().deleteAllData()
        } catch (e: Exception) {
            // Ignore
        }
        viewModelScope.launch {
            repository.clearHistory()
        }
        closeAllTabs()
        _showSecurityAudit.value = false
    }

    // Sheet controls
    fun openVpnSheet() { _showVpnSheet.value = true }
    fun closeVpnSheet() { _showVpnSheet.value = false }

    fun openTabManager() { _showTabManager.value = true }
    fun closeTabManager() { _showTabManager.value = false }

    fun openBookmarksHistory() { _showBookmarksHistory.value = true }
    fun closeBookmarksHistory() { _showBookmarksHistory.value = false }

    fun openSecurityAudit() { _showSecurityAudit.value = true }
    fun closeSecurityAudit() { _showSecurityAudit.value = false }

    fun openSettings() { _showSettings.value = true }
    fun closeSettings() { _showSettings.value = false }
}
