package com.example.data.model

data class AziShieldsState(
    val isEnabled: Boolean = true,
    val blockTrackersAndAds: Boolean = true,
    val upgradeHttps: Boolean = true,
    val blockFingerprinting: Boolean = true,
    val blockCookiePopups: Boolean = true,
    val blockScripts: Boolean = false,
    val totalTrackersBlocked: Long = 138L,
    val totalBandwidthSavedMb: Float = 2.4f,
    val totalTimeSavedSeconds: Long = 9L
)
