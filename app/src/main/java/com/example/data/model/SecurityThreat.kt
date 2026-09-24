package com.example.data.model

enum class ThreatType(val label: String, val severity: String) {
    MALWARE("Malware Payload", "CRITICAL"),
    VIRUS_DISTRIBUTION("Virus Distribution Vector", "CRITICAL"),
    PHISHING("Credential Phishing Portal", "HIGH"),
    CRYPTOJACKER("Cryptocurrency Mining Script", "MEDIUM"),
    EXPLOIT_KIT("Drive-by Download Exploit", "CRITICAL"),
    INSECURE_HTTP("Unencrypted Plaintext Connection", "LOW")
}

data class SecurityThreat(
    val url: String,
    val domain: String,
    val type: ThreatType,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
