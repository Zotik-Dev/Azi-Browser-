package com.example.data.model

data class VpnServer(
    val id: String,
    val countryName: String,
    val city: String,
    val countryCode: String,
    val flagEmoji: String,
    val pingMs: Int,
    val serverLoadPercent: Int,
    val ipAddress: String,
    val cipherSuite: String = "AES-256-GCM / ChaCha20-Poly1305",
    val isRecommended: Boolean = false,
    val p2pAllowed: Boolean = true
)

val DEFAULT_VPN_SERVERS = listOf(
    VpnServer(
        id = "ch-zurich",
        countryName = "Switzerland",
        city = "Zurich",
        countryCode = "CH",
        flagEmoji = "🇨🇭",
        pingMs = 18,
        serverLoadPercent = 24,
        ipAddress = "185.107.56.2",
        cipherSuite = "ChaCha20-Poly1305 (Post-Quantum Safe)",
        isRecommended = true
    ),
    VpnServer(
        id = "is-reykjavik",
        countryName = "Iceland",
        city = "Reykjavik",
        countryCode = "IS",
        flagEmoji = "🇮🇸",
        pingMs = 28,
        serverLoadPercent = 31,
        ipAddress = "194.187.168.10",
        cipherSuite = "AES-256-GCM (Zero Logs Jurisprudence)",
        isRecommended = true
    ),
    VpnServer(
        id = "nl-amsterdam",
        countryName = "Netherlands",
        city = "Amsterdam",
        countryCode = "NL",
        flagEmoji = "🇳🇱",
        pingMs = 22,
        serverLoadPercent = 42,
        ipAddress = "193.138.218.74",
        cipherSuite = "AES-256-GCM (10 Gbps High-Speed)"
    ),
    VpnServer(
        id = "us-newyork",
        countryName = "United States",
        city = "New York",
        countryCode = "US",
        flagEmoji = "🇺🇸",
        pingMs = 45,
        serverLoadPercent = 58,
        ipAddress = "198.54.130.12",
        cipherSuite = "ChaCha20-Poly1305 (Streaming Optimized)"
    ),
    VpnServer(
        id = "de-frankfurt",
        countryName = "Germany",
        city = "Frankfurt",
        countryCode = "DE",
        flagEmoji = "🇩🇪",
        pingMs = 25,
        serverLoadPercent = 38,
        ipAddress = "185.220.101.5",
        cipherSuite = "AES-256-GCM (Financial Hub)"
    ),
    VpnServer(
        id = "jp-tokyo",
        countryName = "Japan",
        city = "Tokyo",
        countryCode = "JP",
        flagEmoji = "🇯🇵",
        pingMs = 78,
        serverLoadPercent = 45,
        ipAddress = "133.130.102.40",
        cipherSuite = "ChaCha20-Poly1305 (Asia Fast Gateway)"
    ),
    VpnServer(
        id = "sg-singapore",
        countryName = "Singapore",
        city = "Singapore",
        countryCode = "SG",
        flagEmoji = "🇸🇬",
        pingMs = 62,
        serverLoadPercent = 39,
        ipAddress = "103.253.25.1",
        cipherSuite = "ChaCha20-Poly1305 (Low Latency Transit)"
    )
)
