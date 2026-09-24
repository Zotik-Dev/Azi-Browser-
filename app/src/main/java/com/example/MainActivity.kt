package com.example

import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.ui.components.BottomBrowserBar
import com.example.ui.components.BookmarksHistoryDialog
import com.example.ui.components.BrowserOmnibox
import com.example.ui.components.BrowserWebView
import com.example.ui.components.DownloadScannerDialog
import com.example.ui.components.SecurityAuditSheet
import com.example.ui.components.SpeedDialHome
import com.example.ui.components.TabManagerSheet
import com.example.ui.components.ThreatWarningView
import com.example.ui.components.VpnControlPanel
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.vpn.VpnController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                AegisBrowserApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AegisBrowserApp(
    viewModel: BrowserViewModel = viewModel()
) {
    val context = LocalContext.current
    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()
    val vpnState by viewModel.vpnState.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val history by viewModel.history.collectAsState()
    val threatEvents by viewModel.threatEvents.collectAsState()

    val showVpnSheet by viewModel.showVpnSheet.collectAsState()
    val showTabManager by viewModel.showTabManager.collectAsState()
    val showBookmarksHistory by viewModel.showBookmarksHistory.collectAsState()
    val showSecurityAudit by viewModel.showSecurityAudit.collectAsState()
    val pendingDownload by viewModel.pendingDownload.collectAsState()

    val currentTab = viewModel.currentTab
    val isCurrentBookmarked = bookmarks.any { it.url == currentTab?.url }

    // Map to retain active WebView instances
    val webViewMap = remember { mutableStateMapOf<String, WebView>() }
    val currentWebView = webViewMap[activeTabId]

    // Android VPN Permission Launcher
    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            VpnController.connect(context)
            Toast.makeText(context, "VPN Tunnel Initiated", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "VPN permission required for encrypted tunnel", Toast.LENGTH_LONG).show()
        }
    }

    val toggleVpn = {
        if (vpnState.isConnected) {
            VpnController.disconnect(context)
            Toast.makeText(context, "VPN Disconnected", Toast.LENGTH_SHORT).show()
        } else {
            val prepareIntent = VpnController.prepareVpn(context)
            if (prepareIntent != null) {
                vpnPermissionLauncher.launch(prepareIntent)
            } else {
                VpnController.connect(context)
            }
        }
    }

    // Hardware Back Button Handler
    BackHandler(enabled = true) {
        when {
            showVpnSheet -> viewModel.closeVpnSheet()
            showTabManager -> viewModel.closeTabManager()
            showBookmarksHistory -> viewModel.closeBookmarksHistory()
            showSecurityAudit -> viewModel.closeSecurityAudit()
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
            BrowserOmnibox(
                currentTab = currentTab,
                tabCount = tabs.size,
                vpnState = vpnState,
                onNavigate = { viewModel.navigate(it) },
                onReload = {
                    if (currentTab?.isHome == true) {
                        // Refresh home
                    } else {
                        currentWebView?.reload()
                    }
                },
                onOpenVpnSheet = { viewModel.openVpnSheet() },
                onOpenTabManager = { viewModel.openTabManager() },
                onOpenSecurityAudit = { viewModel.openSecurityAudit() },
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        bottomBar = {
            BottomBrowserBar(
                currentTab = currentTab,
                vpnState = vpnState,
                isCurrentBookmarked = isCurrentBookmarked,
                onBack = { currentWebView?.goBack() },
                onForward = { currentWebView?.goForward() },
                onHome = { viewModel.navigate("about:blank") },
                onOpenVpnSheet = { viewModel.openVpnSheet() },
                onOpenBookmarks = { viewModel.openBookmarksHistory() },
                onToggleBookmark = { viewModel.toggleBookmarkCurrentPage() },
                onToggleDesktop = { viewModel.toggleDesktopMode() },
                onOpenSecurityAudit = { viewModel.openSecurityAudit() },
                onNewTab = { viewModel.createNewTab() }
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

                    // Blank home page with speed dial
                    currentTab.isHome -> {
                        SpeedDialHome(
                            vpnState = vpnState,
                            isIncognito = currentTab.isIncognito,
                            onNavigate = { viewModel.navigate(it) },
                            onOpenVpnControl = { viewModel.openVpnSheet() },
                            onOpenSecurityAudit = { viewModel.openSecurityAudit() },
                            onNewIncognitoTab = { viewModel.createNewTab(isIncognito = true) }
                        )
                    }

                    // Web browser view
                    else -> {
                        BrowserWebView(
                            tab = currentTab,
                            isShieldActive = vpnState.isMalwareShieldActive,
                            isAdBlockerActive = vpnState.isAdBlockerActive,
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
            if (showVpnSheet) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.closeVpnSheet() },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = CyberBackground
                ) {
                    VpnControlPanel(
                        vpnState = vpnState,
                        onToggleConnection = toggleVpn,
                        onClose = { viewModel.closeVpnSheet() }
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
                        vpnState = vpnState,
                        threatEvents = threatEvents,
                        onPanicWipe = {
                            viewModel.panicWipeSession(context)
                            Toast.makeText(context, "Session wiped: Cache, cookies & history destroyed", Toast.LENGTH_SHORT).show()
                        },
                        onClose = { viewModel.closeSecurityAudit() }
                    )
                }
            }

            // Download scan alert dialog
            pendingDownload?.let { prompt ->
                DownloadScannerDialog(
                    prompt = prompt,
                    onConfirm = {
                        Toast.makeText(context, "Downloading ${prompt.fileName} via encrypted tunnel...", Toast.LENGTH_SHORT).show()
                    },
                    onDismiss = { viewModel.dismissDownloadPrompt() }
                )
            }
        }
    }
}
