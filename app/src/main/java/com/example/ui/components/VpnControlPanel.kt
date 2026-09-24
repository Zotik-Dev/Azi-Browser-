package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DEFAULT_DOH_PROVIDERS
import com.example.data.model.DEFAULT_VPN_SERVERS
import com.example.data.model.DohProvider
import com.example.data.model.VpnProtocol
import com.example.data.model.VpnServer
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCardDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanBg
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberEmeraldGlow
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.vpn.VpnController
import com.example.vpn.VpnState
import com.example.vpn.VpnStatus

@Composable
fun VpnControlPanel(
    vpnState: VpnState,
    onToggleConnection: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tunnel Status", "Server Locations", "Encryption & Shield")

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (vpnState.isConnected) 1.12f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VpnLock,
                contentDescription = null,
                tint = if (vpnState.isConnected) CyberCyan else CyberEmerald,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "FULL VPN & ENCRYPTION CONTROL",
                    color = CyberTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (vpnState.isConnected) "Encrypted Tunnel Active • Zero Logs" else "Tunnel Disconnected",
                    color = if (vpnState.isConnected) CyberCyan else CyberTextSecondary,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onClose,
                modifier = Modifier.testTag("close_vpn_sheet")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = CyberTextSecondary
                )
            }
        }

        // Subtabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CyberSurfaceElevated,
            contentColor = CyberEmerald,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = if (vpnState.isConnected) CyberCyan else CyberEmerald,
                    height = 2.5.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == index) CyberTextPrimary else CyberTextSecondary
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> VpnStatusTab(
                vpnState = vpnState,
                pulseScale = pulseScale,
                onToggleConnection = onToggleConnection
            )
            1 -> VpnServersTab(
                vpnState = vpnState,
                onSelectServer = { VpnController.selectServer(it) }
            )
            2 -> VpnEncryptionTab(
                vpnState = vpnState
            )
        }
    }
}

@Composable
private fun VpnStatusTab(
    vpnState: VpnState,
    pulseScale: Float,
    onToggleConnection: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            // Big Animated Power / Connect Button
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(if (vpnState.isConnected) pulseScale else 1f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (vpnState.isConnected) CyberCyan.copy(alpha = 0.35f)
                                else if (vpnState.status == VpnStatus.CONNECTING) CyberEmerald.copy(alpha = 0.25f)
                                else CyberCardDark,
                                Color.Transparent
                            )
                        )
                    )
                    .border(
                        2.5.dp,
                        if (vpnState.isConnected) CyberCyan
                        else if (vpnState.status == VpnStatus.CONNECTING) CyberEmerald
                        else CyberBorder,
                        CircleShape
                    )
                    .clickable { onToggleConnection() }
                    .testTag("vpn_toggle_button"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Toggle VPN",
                        tint = if (vpnState.isConnected) CyberCyan
                        else if (vpnState.status == VpnStatus.CONNECTING) CyberEmerald
                        else CyberTextSecondary,
                        modifier = Modifier.size(46.dp)
                    )
                    Text(
                        text = when (vpnState.status) {
                            VpnStatus.CONNECTED -> "DISCONNECT"
                            VpnStatus.CONNECTING -> "TUNNELING..."
                            VpnStatus.DISCONNECTING -> "CLOSING..."
                            VpnStatus.DISCONNECTED -> "CONNECT"
                        },
                        color = if (vpnState.isConnected) CyberCyan else CyberTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Node info badge
            Text(
                text = "${vpnState.activeServer.flagEmoji} ${vpnState.activeServer.countryName} (${vpnState.activeServer.city})",
                color = CyberTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Protocol: ${vpnState.activeProtocol.displayName} • Virtual IP: ${vpnState.assignedVirtualIp}",
                color = CyberTextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Live Telemetry Grid
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberCardDark),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (vpnState.isConnected) CyberCyan.copy(alpha = 0.5f) else CyberBorder
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "REAL-TIME TUNNEL TELEMETRY",
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TelemetryItem(
                            icon = Icons.Default.Download,
                            title = "Download Speed",
                            value = if (vpnState.isConnected) "${vpnState.currentDownloadSpeedMbps} Mbps" else "0.0 Mbps",
                            tint = CyberCyan
                        )
                        TelemetryItem(
                            icon = Icons.Default.Upload,
                            title = "Upload Speed",
                            value = if (vpnState.isConnected) "${vpnState.currentUploadSpeedMbps} Mbps" else "0.0 Mbps",
                            tint = CyberEmerald
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val duration = vpnState.connectedDurationSeconds
                        val formattedTime = String.format("%02d:%02d", duration / 60, duration % 60)
                        TelemetryItem(
                            icon = Icons.Default.Timer,
                            title = "Session Uptime",
                            value = if (vpnState.isConnected) formattedTime else "00:00",
                            tint = CyberTextPrimary
                        )
                        val totalBytes = (vpnState.bytesIn + vpnState.bytesOut) / (1024 * 1024)
                        TelemetryItem(
                            icon = Icons.Default.Lock,
                            title = "Encrypted Data",
                            value = if (vpnState.isConnected) "$totalBytes MB" else "0 MB",
                            tint = CyberEmeraldGlow
                        )
                    }
                }
            }
        }

        // Privacy Guarantee Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyberEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Unblock Sites & Bypass Geo-Blocks",
                            color = CyberTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "RAM-only servers with strict zero-log policy, DNS leak protection, and censorship-proof routing.",
                            color = CyberTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    tint: Color
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = title, color = CyberTextSecondary, fontSize = 11.sp)
        }
        Text(
            text = value,
            color = CyberTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun VpnServersTab(
    vpnState: VpnState,
    onSelectServer: (VpnServer) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(DEFAULT_VPN_SERVERS) { server ->
            val isSelected = server.id == vpnState.activeServer.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectServer(server) }
                    .testTag("vpn_server_${server.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) CyberCardDark else CyberSurfaceElevated
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) CyberCyan else CyberBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = server.flagEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = server.countryName,
                                color = CyberTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (server.isRecommended) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberEmerald.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "ZERO-LOGS",
                                        color = CyberEmerald,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${server.city} • ${server.cipherSuite}",
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${server.pingMs} ms",
                            color = if (server.pingMs < 30) CyberEmerald else CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${server.serverLoadPercent}% Load",
                            color = CyberTextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VpnEncryptionTab(
    vpnState: VpnState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // High-Speed Encryption Protocol
        item {
            Text(
                text = "HIGH-SPEED ENCRYPTION PROTOCOL",
                color = CyberTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            VpnProtocol.values().forEach { protocol ->
                val isSelected = protocol == vpnState.activeProtocol
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { VpnController.selectProtocol(protocol) },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) CyberCardDark else CyberSurfaceElevated
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) CyberCyan else CyberBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = protocol.displayName,
                                    color = CyberTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = protocol.tag,
                                        color = CyberCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = protocol.cipherDescription,
                                color = CyberTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // DNS-over-HTTPS (DoH) Provider
        item {
            Text(
                text = "ENCRYPTED DNS (DNS-OVER-HTTPS)",
                color = CyberTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            DEFAULT_DOH_PROVIDERS.forEach { doh ->
                val isSelected = doh.id == vpnState.activeDohProvider.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { VpnController.selectDohProvider(doh) },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) CyberCardDark else CyberSurfaceElevated
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) CyberEmerald else CyberBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = doh.name,
                                color = CyberTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = doh.specialty,
                                color = CyberTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Security Toggles (Kill Switch, AdBlocker, Virus Shield)
        item {
            Text(
                text = "CYBER DEFENSE CONTROLS",
                color = CyberTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            DefenseToggleCard(
                title = "Emergency Kill Switch",
                description = "Strictly block all non-VPN traffic to prevent accidental IP address leakage if tunnel drops.",
                checked = vpnState.isKillSwitchEnabled,
                onCheckedChange = { VpnController.toggleKillSwitch() },
                activeColor = CyberDanger
            )

            Spacer(modifier = Modifier.height(6.dp))

            DefenseToggleCard(
                title = "Virus & Malware Payload Shield",
                description = "Intercepts known malicious distribution vectors, trojans, ransomware, and credential phishing.",
                checked = vpnState.isMalwareShieldActive,
                onCheckedChange = { VpnController.toggleMalwareShield() },
                activeColor = CyberEmerald
            )

            Spacer(modifier = Modifier.height(6.dp))

            DefenseToggleCard(
                title = "Tracker & Ad Interceptor",
                description = "Discards third-party telemetry beacons, cryptominers, and surveillance profiling scripts.",
                checked = vpnState.isAdBlockerActive,
                onCheckedChange = { VpnController.toggleAdBlocker() },
                activeColor = CyberCyan
            )

            Spacer(modifier = Modifier.height(6.dp))

            DefenseToggleCard(
                title = "Anti-Fingerprinting Sandbox",
                description = "Masks WebRTC local IP addresses and standardizes canvas renderer signatures.",
                checked = vpnState.isAntiFingerprintingActive,
                onCheckedChange = { VpnController.toggleAntiFingerprinting() },
                activeColor = CyberEmerald
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DefenseToggleCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    activeColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = CyberTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = activeColor,
                    uncheckedThumbColor = CyberTextSecondary,
                    uncheckedTrackColor = CyberCardDark
                )
            )
        }
    }
}
