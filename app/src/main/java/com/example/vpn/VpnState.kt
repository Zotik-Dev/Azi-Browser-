package com.example.vpn

import com.example.data.model.DEFAULT_DOH_PROVIDERS
import com.example.data.model.DEFAULT_VPN_SERVERS
import com.example.data.model.DohProvider
import com.example.data.model.VpnProtocol
import com.example.data.model.VpnServer

enum class VpnStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

data class VpnState(
    val status: VpnStatus = VpnStatus.DISCONNECTED,
    val activeServer: VpnServer = DEFAULT_VPN_SERVERS.first(),
    val activeProtocol: VpnProtocol = VpnProtocol.WIREGUARD,
    val activeDohProvider: DohProvider = DEFAULT_DOH_PROVIDERS.first(),
    val isKillSwitchEnabled: Boolean = false,
    val isAdBlockerActive: Boolean = true,
    val isMalwareShieldActive: Boolean = true,
    val isAntiFingerprintingActive: Boolean = true,
    val bytesIn: Long = 0L,
    val bytesOut: Long = 0L,
    val currentDownloadSpeedMbps: Double = 0.0,
    val currentUploadSpeedMbps: Double = 0.0,
    val connectedDurationSeconds: Long = 0L,
    val assignedVirtualIp: String = "10.8.0.2",
    val blockedTrackersSessionCount: Int = 0,
    val neutralizedThreatsCount: Int = 0
) {
    val isConnected: Boolean get() = status == VpnStatus.CONNECTED
    val isBusy: Boolean get() = status == VpnStatus.CONNECTING || status == VpnStatus.DISCONNECTING
}
