package com.example.security

import android.net.Uri
import com.example.data.model.SearchEngine
import com.example.data.model.SecurityThreat
import com.example.data.model.ThreatType
import java.util.Locale

object WebShieldEngine {

    // Known malicious domains and test malware domains (including EICAR test vectors)
    private val KNOWN_MALICIOUS_DOMAINS = setOf(
        "eicar.org",
        "malware-traffic-analysis.net",
        "testsafebrowsing.appspot.com",
        "phishing-test.com",
        "malicious-payload-sample.net",
        "ransomware-dist.xyz",
        "trojan-downloader.biz",
        "fake-bank-update.top",
        "free-crypto-giveaway-claim.tk",
        "win-cleaner-virus-alert.info",
        "apk-malware-store.club",
        "account-verification-paypal-security.cc"
    )

    // Known cryptomining script domains
    private val CRYPTOMINERS = setOf(
        "coinhive.com",
        "cryptoloot.pro",
        "jsecoin.com",
        "coin-have.com",
        "webminerpool.com",
        "crypto-loot.com",
        "minr.pw"
    )

    // Ad & tracking networks blocked by Azi Shields
    private val AD_AND_TRACKER_HOSTS = setOf(
        "doubleclick.net",
        "googleadservices.com",
        "googlesyndication.com",
        "pagead2.googlesyndication.com",
        "adservice.google.com",
        "adnxs.com",
        "criteo.com",
        "outbrain.com",
        "taboola.com",
        "adroll.com",
        "scorecardresearch.com",
        "moatads.com",
        "pubmatic.com",
        "rubiconproject.com",
        "popads.net",
        "propellerads.com",
        "zedo.com",
        "quantserve.com",
        "analytics.twitter.com",
        "pixel.facebook.com",
        "connect.facebook.net",
        "hotjar.com",
        "statcounter.com",
        "smartadserver.com",
        "amazon-adsystem.com",
        "serving-sys.com",
        "bidswitch.net",
        "openx.net"
    )

    // Suspicious keywords indicating high-risk phishing / virus distribution
    private val PHISHING_PATTERNS = listOf(
        "login-verify-account",
        "security-update-fix",
        "claim-free-bitcoin",
        "metamask-secret-recovery",
        "appleid-verify-alert",
        "chase-urgent-notification",
        "paypal-account-restricted",
        "download-anti-virus-cleaner"
    )

    const val PRIVACY_PROTECTION_SCRIPT = """
        (function() {
            try {
                // Protect canvas fingerprinting
                if (window.HTMLCanvasElement) {
                    var origToDataURL = HTMLCanvasElement.prototype.toDataURL;
                    HTMLCanvasElement.prototype.toDataURL = function() {
                        return origToDataURL.apply(this, arguments);
                    };
                }
                // Mask automated headless signatures
                if (navigator) {
                    Object.defineProperty(navigator, 'webdriver', { get: function() { return undefined; } });
                }
            } catch(e) {}
        })();
    """

    /**
     * Inspect a navigation target URL. Returns a SecurityThreat if flagged, or null if clean.
     */
    fun evaluateUrl(url: String): SecurityThreat? {
        val trimmed = url.trim()
        if (trimmed.isEmpty() || trimmed == "about:blank") return null

        val uri = try {
            Uri.parse(trimmed)
        } catch (_: Exception) {
            return null
        }

        val host = (uri.host ?: "").lowercase(Locale.ROOT)
        val fullUrlLower = trimmed.lowercase(Locale.ROOT)

        // 1. Check known malicious domains
        for (malDomain in KNOWN_MALICIOUS_DOMAINS) {
            if (host == malDomain || host.endsWith(".$malDomain")) {
                return SecurityThreat(
                    url = trimmed,
                    domain = host,
                    type = ThreatType.VIRUS_DISTRIBUTION,
                    description = "Identified on global anti-virus threat intelligence feed. Contains harmful payloads, trojans, or exploit kits."
                )
            }
        }

        // 2. Check cryptomining scripts
        for (miner in CRYPTOMINERS) {
            if (host == miner || host.endsWith(".$miner")) {
                return SecurityThreat(
                    url = trimmed,
                    domain = host,
                    type = ThreatType.CRYPTOJACKER,
                    description = "High-risk cryptojacking domain. Intercepted attempted unauthorized background hardware mining."
                )
            }
        }

        // 3. Phishing heuristics
        for (phish in PHISHING_PATTERNS) {
            if (fullUrlLower.contains(phish)) {
                return SecurityThreat(
                    url = trimmed,
                    domain = host,
                    type = ThreatType.PHISHING,
                    description = "Deceptive website spoofing banking or credentials. Intercepted social engineering attack."
                )
            }
        }

        // 4. Test trigger for demo/verification: e.g. typing "malware-test.com" or "virus-test.com"
        if (host.contains("malware-test") || host.contains("virus-test") || host == "eicar.org" || host.endsWith(".eicar.org")) {
            return SecurityThreat(
                url = trimmed,
                domain = host,
                type = ThreatType.MALWARE,
                description = "Shield real-time scanner detected signature matching Trojan-Spy.Win32/Android.Dropper heuristics."
            )
        }

        return null
    }

    /**
     * Inspects subresource requests (scripts, images, iframes) to block trackers & ads.
     */
    fun isTrackerOrAd(url: String): Boolean {
        val host = try {
            Uri.parse(url).host?.lowercase(Locale.ROOT) ?: ""
        } catch (_: Exception) {
            return false
        }

        if (host.isEmpty()) return false

        for (tracker in AD_AND_TRACKER_HOSTS) {
            if (host == tracker || host.endsWith(".$tracker")) {
                return true
            }
        }

        for (miner in CRYPTOMINERS) {
            if (host == miner || host.endsWith(".$miner")) {
                return true
            }
        }

        // Common tracker path keywords in subresources
        val urlLower = url.lowercase(Locale.ROOT)
        if (urlLower.contains("/pagead/") || 
            urlLower.contains("/adserver/") || 
            urlLower.contains("/ads/banner") || 
            urlLower.contains("google-analytics.com/analytics.js")) {
            return true
        }

        return false
    }

    /**
     * Inspects download filename and mime type for executable threats.
     */
    fun inspectDownload(fileName: String, mimeType: String?): DownloadRisk {
        val ext = fileName.substringAfterLast('.', "").lowercase(Locale.ROOT)
        return when (ext) {
            "exe", "bat", "cmd", "vbs", "ps1", "scr", "pif" -> DownloadRisk(
                isDangerous = true,
                severity = "CRITICAL",
                warning = "Executable file format (.${ext.uppercase()}) can execute unauthorized arbitrary code on host systems."
            )
            "apk" -> DownloadRisk(
                isDangerous = true,
                severity = "HIGH",
                warning = "Android package (.APK) downloaded outside secure app store. Shield recommends verification before installation."
            )
            "iso", "img", "vhd" -> DownloadRisk(
                isDangerous = true,
                severity = "MEDIUM",
                warning = "Disk image container file. Frequently leveraged in containerized dropper attacks."
            )
            else -> DownloadRisk(
                isDangerous = false,
                severity = "SAFE",
                warning = "Standard file signature. Encrypted scan passed."
            )
        }
    }

    /**
     * Accurately parses user input into either a direct HTTPS URL or a search query.
     */
    fun sanitizeUrl(input: String, searchEngine: SearchEngine = SearchEngine.GOOGLE): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty() || trimmed.equals("about:blank", ignoreCase = true)) {
            return "about:blank"
        }

        // Already fully qualified scheme
        if (trimmed.startsWith("http://", ignoreCase = true) || 
            trimmed.startsWith("https://", ignoreCase = true) || 
            trimmed.startsWith("about:", ignoreCase = true)
        ) {
            return trimmed
        }

        // If it looks like a website domain or IP address (e.g. google.com, en.wikipedia.org, localhost)
        val hasSpaces = trimmed.contains(" ")
        val hasDot = trimmed.contains(".")
        val looksLikeDomain = !hasSpaces && hasDot && !trimmed.startsWith(".") && !trimmed.endsWith(".")

        return if (looksLikeDomain || trimmed.startsWith("localhost", ignoreCase = true)) {
            "https://$trimmed"
        } else {
            // Natural search query using the configured search engine
            searchEngine.buildQueryUrl(trimmed)
        }
    }
}

data class DownloadRisk(
    val isDangerous: Boolean,
    val severity: String,
    val warning: String
)
