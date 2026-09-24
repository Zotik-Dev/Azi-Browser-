package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.BrowserTab
import com.example.security.WebShieldEngine
import java.io.ByteArrayInputStream
import java.util.Locale

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserWebView(
    tab: BrowserTab,
    isShieldActive: Boolean,
    isAdBlockerActive: Boolean,
    onTabStateUpdate: (
        title: String?,
        url: String?,
        isLoading: Boolean?,
        progress: Int?,
        canGoBack: Boolean?,
        canGoForward: Boolean?,
        isSecureHttps: Boolean?,
        blockedTrackerCountInc: Int
    ) -> Unit,
    onThreatDetected: (url: String) -> Unit,
    onPromptDownload: (fileName: String, url: String, mimeType: String, contentLength: Long) -> Unit,
    onWebViewCreated: (WebView) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val webView = remember(tab.id) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                builtInZoomControls = true
                displayZoomControls = false
                setSupportZoom(true)
                allowFileAccess = false
                allowContentAccess = false
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

                // Clean mobile User-Agent
                userAgentString = if (tab.desktopMode) {
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
                } else {
                    "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
                }

                if (tab.isIncognito) {
                    cacheMode = WebSettings.LOAD_NO_CACHE
                    saveFormData = false
                } else {
                    cacheMode = WebSettings.LOAD_DEFAULT
                }
            }

            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(!tab.isIncognito)
            try {
                cookieManager.setAcceptThirdPartyCookies(this, !tab.isIncognito)
            } catch (_: Exception) {}

            setDownloadListener { url, _, contentDisposition, mimeType, contentLength ->
                val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
                onPromptDownload(fileName, url, mimeType ?: "application/octet-stream", contentLength)
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val uri = request?.url ?: return false
                    val targetUrl = uri.toString()
                    val scheme = (uri.scheme ?: "").lowercase(Locale.ROOT)

                    // Allow normal HTTP/HTTPS web loading
                    if (scheme == "http" || scheme == "https") {
                        if (isShieldActive) {
                            val threat = WebShieldEngine.evaluateUrl(targetUrl)
                            if (threat != null) {
                                onThreatDetected(targetUrl)
                                return true
                            }
                        }
                        return false
                    }

                    // External protocols: mailto:, tel:, intent:, market:
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        // Suppress unhandled schemes safely
                    }
                    return true
                }

                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    val reqUrl = request?.url?.toString() ?: return null
                    if (isAdBlockerActive && WebShieldEngine.isTrackerOrAd(reqUrl)) {
                        val mimeType = when {
                            reqUrl.endsWith(".js", ignoreCase = true) -> "application/javascript"
                            reqUrl.endsWith(".css", ignoreCase = true) -> "text/css"
                            reqUrl.endsWith(".png", ignoreCase = true) -> "image/png"
                            reqUrl.endsWith(".gif", ignoreCase = true) -> "image/gif"
                            reqUrl.endsWith(".jpg", ignoreCase = true) || reqUrl.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
                            else -> "text/plain"
                        }
                        return WebResourceResponse(
                            mimeType,
                            "UTF-8",
                            ByteArrayInputStream(ByteArray(0))
                        )
                    }
                    return super.shouldInterceptRequest(view, request)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    val safeUrl = url ?: ""
                    onTabStateUpdate(
                        null,
                        safeUrl,
                        true,
                        15,
                        view?.canGoBack() ?: false,
                        view?.canGoForward() ?: false,
                        safeUrl.startsWith("https://", ignoreCase = true),
                        0
                    )
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    val safeUrl = url ?: ""
                    onTabStateUpdate(
                        view?.title,
                        safeUrl,
                        false,
                        100,
                        view?.canGoBack() ?: false,
                        view?.canGoForward() ?: false,
                        safeUrl.startsWith("https://", ignoreCase = true),
                        0
                    )
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    if (request?.isForMainFrame == true) {
                        onTabStateUpdate(
                            "Unable to load page",
                            request.url?.toString(),
                            false,
                            100,
                            view?.canGoBack() ?: false,
                            view?.canGoForward() ?: false,
                            false,
                            0
                        )
                    }
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    onTabStateUpdate(null, null, null, null, null, null, false, 0)
                    handler?.proceed()
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    onTabStateUpdate(
                        null,
                        null,
                        newProgress < 100,
                        newProgress,
                        view?.canGoBack() ?: false,
                        view?.canGoForward() ?: false,
                        null,
                        0
                    )
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    if (!title.isNullOrBlank()) {
                        onTabStateUpdate(title, null, null, null, null, null, null, 0)
                    }
                }
            }
        }
    }

    LaunchedEffect(webView) {
        onWebViewCreated(webView)
    }

    // Handle desktop mode toggle
    LaunchedEffect(tab.desktopMode) {
        webView.settings.userAgentString = if (tab.desktopMode) {
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
        } else {
            "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
        }
        if (!tab.isHome) {
            webView.reload()
        }
    }

    // Load initial URL if not home
    LaunchedEffect(tab.id, tab.url) {
        if (!tab.isHome && tab.url.isNotBlank() && webView.url != tab.url) {
            webView.loadUrl(tab.url)
        }
    }

    AndroidView(
        factory = {
            (webView.parent as? ViewGroup)?.removeView(webView)
            webView
        },
        update = { v ->
            if (!tab.isHome && tab.url.isNotBlank() && v.url != tab.url) {
                v.loadUrl(tab.url)
            }
        },
        modifier = modifier.fillMaxSize()
    )

    DisposableEffect(tab.id) {
        onDispose {
            if (tab.isIncognito) {
                webView.clearCache(true)
                webView.clearHistory()
            }
        }
    }
}
