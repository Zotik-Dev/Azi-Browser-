package com.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.BrowserViewModel
import com.example.ui.components.AziShieldsSheet
import com.example.ui.components.BottomBrowserBar
import com.example.ui.components.BookmarksHistoryDialog
import com.example.ui.components.BrowserOmnibox
import com.example.ui.components.BrowserWebView
import com.example.ui.components.ClearDataDialog
import com.example.ui.components.DownloadScannerDialog
import com.example.ui.components.FindInPageBar
import com.example.ui.components.SecurityAuditSheet
import com.example.ui.components.SpeedDialHome
import com.example.ui.components.TabManagerSheet
import com.example.ui.components.ThreatWarningView
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                AziBrowserApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AziBrowserApp(
    viewModel: BrowserViewModel = viewModel()
) {
    val context = LocalContext.current
    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()
    val shieldsState by viewModel.shieldsState.collectAsState()
    val searchEngine by viewModel.searchEngine.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val history by viewModel.history.collectAsState()
    val threatEvents by viewModel.threatEvents.collectAsState()

    val showShieldsSheet by viewModel.showShieldsSheet.collectAsState()
    val showTabManager by viewModel.showTabManager.collectAsState()
    val showBookmarksHistory by viewModel.showBookmarksHistory.collectAsState()
    val showSecurityAudit by viewModel.showSecurityAudit.collectAsState()
    val showClearDataDialog by viewModel.showClearDataDialog.collectAsState()
    val showFindInPage by viewModel.showFindInPage.collectAsState()
    val findInPageQuery by viewModel.findInPageQuery.collectAsState()
    val pendingDownload by viewModel.pendingDownload.collectAsState()

    val currentTab = viewModel.currentTab
    val isCurrentBookmarked = bookmarks.any { it.url == currentTab?.url }

    // Map to retain active WebView instances
    val webViewMap = remember { mutableStateMapOf<String, WebView>() }
    val currentWebView = webViewMap[activeTabId]

    // Hardware Back Button Handler
    BackHandler(enabled = true) {
        when {
            showFindInPage -> viewModel.closeFindInPage()
            showShieldsSheet -> viewModel.closeShieldsSheet()
            showTabManager -> viewModel.closeTabManager()
            showBookmarksHistory -> viewModel.closeBookmarksHistory()
            showSecurityAudit -> viewModel.closeSecurityAudit()
            showClearDataDialog -> viewModel.closeClearDataDialog()
            currentTab?.activeThreat != null -> viewModel.dismissThreatSafely()
            currentWebView?.canGoBack() == true -> currentWebView.goBack()
            currentTab != null && !currentTab.isHome -> viewModel.navigate("about:blank")
            tabs.size > 1 -> viewModel.closeTab(activeTabId)
            else -> (context as? Activity)?.finish()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                BrowserOmnibox(
                    currentTab = currentTab,
                    tabCount = tabs.size,
                    shieldsState = shieldsState,
                    onNavigate = { viewModel.navigate(it) },
                    onReload = {
                        if (currentTab?.isHome == true) {
                            // Home already active
                        } else {
                            currentWebView?.reload()
                        }
                    },
                    onOpenShields = { viewModel.openShieldsSheet() },
                    onOpenTabManager = { viewModel.openTabManager() },
                    onOpenSecurityAudit = { viewModel.openSecurityAudit() }
                )

                AnimatedVisibility(visible = showFindInPage) {
                    FindInPageBar(
                        query = findInPageQuery,
                        onQueryChange = { query ->
                            viewModel.updateFindQuery(query)
                            currentWebView?.findAllAsync(query)
                        },
                        onFindNext = { forward ->
                            currentWebView?.findNext(forward)
                        },
                        onClose = {
                            currentWebView?.clearMatches()
                            viewModel.closeFindInPage()
                        }
                    )
                }
            }
        },
        bottomBar = {
            BottomBrowserBar(
                currentTab = currentTab,
                shieldsState = shieldsState,
                isCurrentBookmarked = isCurrentBookmarked,
                onBack = { currentWebView?.goBack() },
                onForward = { currentWebView?.goForward() },
                onHome = { viewModel.navigate("about:blank") },
                onOpenShields = { viewModel.openShieldsSheet() },
                onOpenBookmarks = { viewModel.openBookmarksHistory() },
                onToggleBookmark = { viewModel.toggleBookmarkCurrentPage() },
                onToggleDesktop = { viewModel.toggleDesktopMode() },
                onOpenSecurityAudit = { viewModel.openSecurityAudit() },
                onNewTab = { viewModel.createNewTab() },
                onNewIncognitoTab = { viewModel.createNewTab(isIncognito = true) },
                onClearData = { viewModel.openClearDataDialog() },
                onShare = {
                    val urlToShare = currentTab?.url
                    if (!urlToShare.isNullOrBlank() && !currentTab.isHome) {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, urlToShare)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share URL"))
                    } else {
                        Toast.makeText(context, "No active page to share", Toast.LENGTH_SHORT).show()
                    }
                },
                onFindInPage = { viewModel.openFindInPage() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CyberBackground)
        ) {
            if (currentTab != null) {
                when {
                    // Security quarantine threat view
                    currentTab.activeThreat != null -> {
                        ThreatWarningView(
                            threat = currentTab.activeThreat,
                            onSafeReturn = { viewModel.dismissThreatSafely() },
                            onBypass = { viewModel.bypassThreatWarning() }
                        )
                    }

                    // Blank home page with Brave-style dashboard & speed dial
                    currentTab.isHome -> {
                        SpeedDialHome(
                            shieldsState = shieldsState,
                            isIncognito = currentTab.isIncognito,
                            searchEngine = searchEngine,
                            onChangeSearchEngine = { viewModel.setSearchEngine(it) },
                            onNavigate = { viewModel.navigate(it) },
                            onOpenShields = { viewModel.openShieldsSheet() },
                            onOpenBookmarks = { viewModel.openBookmarksHistory() },
                            onOpenHistory = { viewModel.openBookmarksHistory() },
                            onNewIncognitoTab = { viewModel.createNewTab(isIncognito = true) }
                        )
                    }

                    // Web browser view
                    else -> {
                        BrowserWebView(
                            tab = currentTab,
                            shieldsState = shieldsState,
                            onTabStateUpdate = { title, url, isLoading, progress, canGoBack, canGoForward, isSecureHttps, blockedTrackerCountInc ->
                                viewModel.updateTabState(
                                    tabId = currentTab.id,
                                    title = title,
                                    url = url,
                                    isLoading = isLoading,
                                    progress = progress,
                                    canGoBack = canGoBack,
                                    canGoForward = canGoForward,
                                    isSecureHttps = isSecureHttps,
                                    blockedTrackerCountInc = blockedTrackerCountInc
                                )
                            },
                            onThreatDetected = { url ->
                                viewModel.navigate(url)
                            },
                            onPromptDownload = { fileName, url, mimeType, contentLength ->
                                viewModel.promptDownload(fileName, url, mimeType, contentLength)
                            },
                            onWebViewCreated = { webView ->
                                webViewMap[currentTab.id] = webView
                            }
                        )
                    }
                }
            }

            // Overlays & Bottom Sheets
            if (showShieldsSheet) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.closeShieldsSheet() },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = CyberBackground
                ) {
                    AziShieldsSheet(
                        tab = currentTab,
                        shieldsState = shieldsState,
                        onToggleMasterShields = { viewModel.toggleMasterShields(it) },
                        onToggleBlockAds = { viewModel.toggleBlockAds(it) },
                        onToggleUpgradeHttps = { viewModel.toggleUpgradeHttps(it) },
                        onToggleBlockFingerprinting = { viewModel.toggleBlockFingerprinting(it) },
                        onToggleBlockCookiePopups = { viewModel.toggleBlockCookiePopups(it) },
                        onToggleBlockScripts = { viewModel.toggleBlockScripts(it) },
                        onClose = { viewModel.closeShieldsSheet() }
                    )
                }
            }

            if (showTabManager) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.closeTabManager() },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = CyberBackground
                ) {
                    TabManagerSheet(
                        tabs = tabs,
                        activeTabId = activeTabId,
                        onSelectTab = { viewModel.switchTab(it) },
                        onCloseTab = { viewModel.closeTab(it) },
                        onNewTab = { viewModel.createNewTab() },
                        onNewIncognitoTab = { viewModel.createNewTab(isIncognito = true) },
                        onCloseAll = { viewModel.closeAllTabs() },
                        onDismiss = { viewModel.closeTabManager() }
                    )
                }
            }

            if (showBookmarksHistory) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.closeBookmarksHistory() },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = CyberBackground
                ) {
                    BookmarksHistoryDialog(
                        bookmarks = bookmarks,
                        history = history,
                        onOpenUrl = {
                            viewModel.navigate(it)
                            viewModel.closeBookmarksHistory()
                        },
                        onDeleteBookmark = { viewModel.removeBookmark(it) },
                        onDeleteHistory = { viewModel.removeHistoryEntry(it) },
                        onClearAllHistory = { viewModel.clearHistory() },
                        onClose = { viewModel.closeBookmarksHistory() }
                    )
                }
            }

            if (showSecurityAudit) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.closeSecurityAudit() },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = CyberBackground
                ) {
                    SecurityAuditSheet(
                        shieldsState = shieldsState,
                        threatEvents = threatEvents,
                        onPanicWipe = {
                            viewModel.clearBrowsingData(
                                clearHistory = true,
                                clearCookies = true,
                                clearCache = true,
                                webViews = webViewMap.values
                            )
                            viewModel.closeAllTabs()
                            viewModel.closeSecurityAudit()
                            Toast.makeText(context, "Azi Privacy Wipe: Cache, cookies & history cleared", Toast.LENGTH_SHORT).show()
                        },
                        onClose = { viewModel.closeSecurityAudit() }
                    )
                }
            }

            // Clear Browsing Data Dialog
            if (showClearDataDialog) {
                ClearDataDialog(
                    onDismiss = { viewModel.closeClearDataDialog() },
                    onConfirmClear = { clearHistory, clearCookies, clearCache ->
                        viewModel.clearBrowsingData(
                            clearHistory = clearHistory,
                            clearCookies = clearCookies,
                            clearCache = clearCache,
                            webViews = webViewMap.values
                        )
                        Toast.makeText(context, "Selected browsing data deleted", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Download scan alert dialog
            pendingDownload?.let { prompt ->
                DownloadScannerDialog(
                    prompt = prompt,
                    onConfirm = {
                        Toast.makeText(context, "Downloading ${prompt.fileName}...", Toast.LENGTH_SHORT).show()
                    },
                    onDismiss = { viewModel.dismissDownloadPrompt() }
                )
            }
        }
    }
}
