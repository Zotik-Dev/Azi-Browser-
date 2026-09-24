package com.example.data.model

enum class VpnProtocol(
    val displayName: String,
    val cipherDescription: String,
    val tag: String
) {
    WIREGUARD("WireGuard v2", "ChaCha20-Poly1305 (Ultra High-Speed, 0ms Handshake)", "FASTEST"),
    OPENVPN_UDP("OpenVPN 2.6 (UDP)", "AES-256-GCM + SHA384 HMAC (Military Standard)", "SECURE"),
    STEALTH_OBFUSCATION("Aegis Stealth Cloak", "Obfuscated TLS 1.3 Tunnel (Bypasses Deep Packet Inspection)", "UNBLOCK ALL"),
    IKEV2("IKEv2 / IPsec", "AES-256-CBC with Mobike Auto-reconnect", "STABLE")
}
