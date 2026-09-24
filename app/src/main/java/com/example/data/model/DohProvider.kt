package com.example.data.model

data class DohProvider(
    val id: String,
    val name: String,
    val primaryIp: String,
    val dohUrl: String,
    val specialty: String,
    val isMalwareFiltering: Boolean
)

val DEFAULT_DOH_PROVIDERS = listOf(
    DohProvider(
        id = "quad9",
        name = "Quad9 Secure (Swiss Foundation)",
        primaryIp = "9.9.9.9",
        dohUrl = "https://dns.quad9.net/dns-query",
        specialty = "Active Threat Intelligence & Virus Blocking",
        isMalwareFiltering = true
    ),
    DohProvider(
        id = "cloudflare",
        name = "Cloudflare 1.1.1.1 (Privacy)",
        primaryIp = "1.1.1.1",
        dohUrl = "https://cloudflare-dns.com/dns-query",
        specialty = "Global Fast Anycast + No-Logging Guarantee",
        isMalwareFiltering = false
    ),
    DohProvider(
        id = "adguard",
        name = "AdGuard DNS (Anti-Tracker)",
        primaryIp = "94.140.14.14",
        dohUrl = "https://dns.adguard.com/dns-query",
        specialty = "Network-level Ad & Tracker Filter",
        isMalwareFiltering = true
    ),
    DohProvider(
        id = "google",
        name = "Google Public DNS",
        primaryIp = "8.8.8.8",
        dohUrl = "https://dns.google/dns-query",
        specialty = "High Availability DNS over HTTPS",
        isMalwareFiltering = false
    )
)
