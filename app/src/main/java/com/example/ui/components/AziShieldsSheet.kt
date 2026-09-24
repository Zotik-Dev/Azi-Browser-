package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Https
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AziShieldsState
import com.example.data.model.BrowserTab
import com.example.ui.theme.AziOrange
import com.example.ui.theme.AziOrangeGlow
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

@Composable
fun AziShieldsSheet(
    tab: BrowserTab?,
    shieldsState: AziShieldsState,
    onToggleMasterShields: (Boolean) -> Unit,
    onToggleBlockAds: (Boolean) -> Unit,
    onToggleUpgradeHttps: (Boolean) -> Unit,
    onToggleBlockFingerprinting: (Boolean) -> Unit,
    onToggleBlockCookiePopups: (Boolean) -> Unit,
    onToggleBlockScripts: (Boolean) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val isShieldsActive = shieldsState.isEnabled

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberBackground)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(scrollState)
    ) {
        // Drag handle indicator
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(CyberBorder)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Shields Header Card (Brave Style)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("azi_shields_header_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isShieldsActive) AziOrange.copy(alpha = 0.12f) else CyberSurfaceElevated
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isShieldsActive) AziOrange else CyberBorder
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isShieldsActive) AziOrange else CyberSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Azi Shield",
                            tint = if (isShieldsActive) Color.White else CyberTextSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Azi Shields",
                            color = CyberTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (tab == null || tab.isHome) "Protection active" else tab.displayDomain,
                            color = if (isShieldsActive) AziOrangeGlow else CyberTextSecondary,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Switch(
                        checked = isShieldsActive,
                        onCheckedChange = onToggleMasterShields,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AziOrange,
                            uncheckedThumbColor = CyberTextSecondary,
                            uncheckedTrackColor = CyberSurfaceVariant
                        ),
                        modifier = Modifier.testTag("master_shields_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Blocked items summary pill
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberBackground.copy(alpha = 0.6f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Trackers & ads blocked on this site:",
                        color = CyberTextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${tab?.blockedTrackersCount ?: 0}",
                        color = if (isShieldsActive) AziOrange else CyberTextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("shields_blocked_count")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Advanced Protection Toggles (Brave Style)
        Text(
            text = "Protection Controls",
            color = CyberTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ShieldToggleRow(
                    icon = Icons.Default.Block,
                    title = "Block Cross-Site Trackers & Ads",
                    subtitle = "Eliminates ads, scripts, and analytic trackers",
                    checked = shieldsState.blockTrackersAndAds,
                    enabled = isShieldsActive,
                    onCheckedChange = onToggleBlockAds,
                    testTag = "toggle_block_ads"
                )

                HorizontalDivider(color = CyberBorder, thickness = 0.5.dp)

                ShieldToggleRow(
                    icon = Icons.Default.Https,
                    title = "Upgrade Connections to HTTPS",
                    subtitle = "Enforces SSL encryption on insecure websites",
                    checked = shieldsState.upgradeHttps,
                    enabled = isShieldsActive,
                    onCheckedChange = onToggleUpgradeHttps,
                    testTag = "toggle_upgrade_https"
                )

                HorizontalDivider(color = CyberBorder, thickness = 0.5.dp)

                ShieldToggleRow(
                    icon = Icons.Default.Fingerprint,
                    title = "Block Device Fingerprinting",
                    subtitle = "Protects canvas, audio, and device telemetry",
                    checked = shieldsState.blockFingerprinting,
                    enabled = isShieldsActive,
                    onCheckedChange = onToggleBlockFingerprinting,
                    testTag = "toggle_block_fingerprinting"
                )

                HorizontalDivider(color = CyberBorder, thickness = 0.5.dp)

                ShieldToggleRow(
                    icon = Icons.Default.Cookie,
                    title = "Block Cookie Consent Popups",
                    subtitle = "Auto-blocks annoying GDPR banners & overlays",
                    checked = shieldsState.blockCookiePopups,
                    enabled = isShieldsActive,
                    onCheckedChange = onToggleBlockCookiePopups,
                    testTag = "toggle_block_cookie_popups"
                )

                HorizontalDivider(color = CyberBorder, thickness = 0.5.dp)

                ShieldToggleRow(
                    icon = Icons.Default.Security,
                    title = "Block All Scripts (NoScript)",
                    subtitle = "Blocks JavaScript entirely for maximum paranoia",
                    checked = shieldsState.blockScripts,
                    enabled = isShieldsActive,
                    onCheckedChange = onToggleBlockScripts,
                    testTag = "toggle_block_scripts"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Global Brave-style metrics
        Text(
            text = "Lifetime Shields Impact",
            color = CyberTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ShieldMetricBox(
                value = "${shieldsState.totalTrackersBlocked}",
                label = "Trackers Blocked",
                accentColor = AziOrange,
                modifier = Modifier.weight(1f)
            )
            ShieldMetricBox(
                value = "${shieldsState.totalBandwidthSavedMb} MB",
                label = "Data Saved",
                accentColor = CyberEmerald,
                modifier = Modifier.weight(1f)
            )
            ShieldMetricBox(
                value = "${shieldsState.totalTimeSavedSeconds} s",
                label = "Time Saved",
                accentColor = AziOrangeGlow,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun ShieldToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled && checked) AziOrange else CyberTextSecondary,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (enabled) CyberTextPrimary else CyberTextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = CyberTextSecondary,
                fontSize = 11.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AziOrange,
                uncheckedThumbColor = CyberTextSecondary,
                uncheckedTrackColor = CyberSurfaceVariant
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
fun ShieldMetricBox(
    value: String,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CyberSurfaceElevated)
            .border(1.dp, CyberBorder, RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = accentColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = CyberTextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
