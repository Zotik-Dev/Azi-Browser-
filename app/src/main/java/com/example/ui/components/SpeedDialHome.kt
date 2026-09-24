package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCardDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberIncognito
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.vpn.VpnState

data class SpeedDialItem(
    val title: String,
    val url: String,
    val iconEmoji: String,
    val category: String
)

val DEFAULT_SPEED_DIAL = listOf(
    SpeedDialItem("Google", "https://www.google.com", "🔍", "Search"),
    SpeedDialItem("YouTube", "https://www.youtube.com", "▶️", "Media"),
    SpeedDialItem("Wikipedia", "https://www.wikipedia.org", "📚", "Knowledge"),
    SpeedDialItem("DuckDuckGo", "https://duckduckgo.com", "🦆", "Privacy Search"),
    SpeedDialItem("Amazon", "https://www.amazon.com", "📦", "Shopping"),
    SpeedDialItem("Reddit", "https://www.reddit.com", "💬", "Community"),
    SpeedDialItem("BBC News", "https://www.bbc.com", "🌍", "News"),
    SpeedDialItem("GitHub", "https://github.com", "🐙", "Code")
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpeedDialHome(
    vpnState: VpnState,
    isIncognito: Boolean,
    onNavigate: (String) -> Unit,
    onOpenVpnControl: () -> Unit,
    onOpenSecurityAudit: () -> Unit,
    onNewIncognitoTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var searchInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val submitSearch = {
        if (searchInput.isNotBlank()) {
            focusManager.clearFocus()
            onNavigate(searchInput)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Clean, Friendly App Header
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            if (isIncognito) CyberIncognito.copy(alpha = 0.3f)
                            else if (vpnState.isConnected) CyberCyan.copy(alpha = 0.3f)
                            else CyberEmerald.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
                .border(
                    1.5.dp,
                    if (isIncognito) CyberIncognito
                    else if (vpnState.isConnected) CyberCyan
                    else CyberEmerald,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isIncognito) Icons.Default.Security else Icons.Default.Shield,
                contentDescription = "Aegis Browser",
                tint = if (isIncognito) CyberIncognito else if (vpnState.isConnected) CyberCyan else CyberEmerald,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isIncognito) "Private Browsing" else "Aegis Secure Browser",
            color = CyberTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )

        Text(
            text = if (isIncognito) "Zero history • Cookies cleared on tab close"
            else "Fast, private browsing with ad blocker & VPN shield",
            color = CyberTextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Prominent Central Search & URL Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_search_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (searchInput.isNotEmpty()) CyberEmerald else CyberBorder
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = CyberEmerald,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                BasicTextField(
                    value = searchInput,
                    onValueChange = { searchInput = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_search_input"),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = CyberTextPrimary,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(CyberEmerald),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { submitSearch() }
                    ),
                    decorationBox = { innerTextField ->
                        if (searchInput.isEmpty()) {
                            Text(
                                text = "Search Google or type website (e.g. google.com)",
                                color = CyberTextSecondary,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        innerTextField()
                    }
                )

                if (searchInput.isNotEmpty()) {
                    IconButton(
                        onClick = { searchInput = "" },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = CyberTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                // Search / Go Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberEmerald)
                        .clickable { submitSearch() }
                        .testTag("home_search_submit_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Search Now",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Popular Websites / Speed Dial Shortcuts
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Popular Websites",
                color = CyberTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Tap to open",
                color = CyberTextSecondary,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 4,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DEFAULT_SPEED_DIAL.forEach { item ->
                SpeedDialIconCell(
                    item = item,
                    onClick = { onNavigate(item.url) }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Simple VPN & Protection Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenVpnControl() }
                .testTag("home_vpn_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCardDark),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (vpnState.isConnected) CyberCyan.copy(alpha = 0.6f) else CyberBorder
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (vpnState.isConnected) CyberCyan.copy(alpha = 0.2f)
                                else CyberSurfaceElevated
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (vpnState.isConnected) {
                            Text(text = vpnState.activeServer.flagEmoji, fontSize = 22.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = "VPN",
                                tint = CyberTextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (vpnState.isConnected) CyberCyan else CyberEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (vpnState.isConnected) "VPN Protected" else "Standard Protection",
                                color = if (vpnState.isConnected) CyberCyan else CyberEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (vpnState.isConnected)
                                "Encrypted via ${vpnState.activeServer.countryName} (${vpnState.activeProtocol.displayName})"
                            else "Malware shield & ad blocker active",
                            color = CyberTextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = onOpenVpnControl,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (vpnState.isConnected) CyberCyan else CyberEmerald,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = if (vpnState.isConnected) "Manage" else "Turn On",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Protection Summary Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SimpleProtectionBadge(
                        label = "Ad Blocker",
                        status = "Active",
                        modifier = Modifier.weight(1f)
                    )
                    SimpleProtectionBadge(
                        label = "Virus Defense",
                        status = "Active",
                        modifier = Modifier.weight(1f)
                    )
                    SimpleProtectionBadge(
                        label = "DNS Shield",
                        status = "Encrypted",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Clean Secondary Actions (Security Audit & New Incognito Tab)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onOpenSecurityAudit,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = CyberEmerald,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Security Scan",
                    color = CyberTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (!isIncognito) {
                OutlinedButton(
                    onClick = onNewIncognitoTab,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyberIncognito,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Private Tab",
                        color = CyberTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SimpleProtectionBadge(
    label: String,
    status: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberSurfaceElevated)
            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = CyberTextSecondary,
                fontSize = 10.sp
            )
            Text(
                text = status,
                color = CyberEmerald,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SpeedDialIconCell(
    item: SpeedDialItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(72.dp)
            .clickable { onClick() }
            .testTag("speed_dial_${item.title.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CyberSurfaceElevated)
                .border(1.dp, CyberBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.iconEmoji,
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.title,
            color = CyberTextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
