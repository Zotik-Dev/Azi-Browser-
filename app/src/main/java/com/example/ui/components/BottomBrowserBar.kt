package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrowserTab
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.vpn.VpnState

@Composable
fun BottomBrowserBar(
    currentTab: BrowserTab?,
    vpnState: VpnState,
    isCurrentBookmarked: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onOpenVpnSheet: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleDesktop: () -> Unit,
    onOpenSecurityAudit: () -> Unit,
    onNewTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = CyberSurface,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Back Button
            IconButton(
                onClick = onBack,
                enabled = currentTab?.canGoBack == true,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = if (currentTab?.canGoBack == true) CyberTextPrimary else CyberTextSecondary.copy(alpha = 0.4f)
                )
            }

            // Forward Button
            IconButton(
                onClick = onForward,
                enabled = currentTab?.canGoForward == true,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_forward_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Go forward",
                    tint = if (currentTab?.canGoForward == true) CyberTextPrimary else CyberTextSecondary.copy(alpha = 0.4f)
                )
            }

            // Home Button
            IconButton(
                onClick = onHome,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_home_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = CyberTextPrimary
                )
            }

            // VPN / Shield Central Capsule Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (vpnState.isConnected) CyberCyan.copy(alpha = 0.2f)
                        else CyberEmerald.copy(alpha = 0.15f)
                    )
                    .border(
                        1.dp,
                        if (vpnState.isConnected) CyberCyan else CyberEmerald,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onOpenVpnSheet() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("nav_vpn_shield_toggle"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (vpnState.isConnected) Icons.Default.VpnKey else Icons.Default.Shield,
                        contentDescription = "VPN Shield",
                        tint = if (vpnState.isConnected) CyberCyan else CyberEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (vpnState.isConnected) "VPN ON" else "SHIELD",
                        color = if (vpnState.isConnected) CyberCyan else CyberEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bookmarks / Saved Button
            IconButton(
                onClick = onOpenBookmarks,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_bookmarks_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmarks,
                    contentDescription = "Bookmarks & History",
                    tint = CyberTextPrimary
                )
            }

            // More Menu Button
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_overflow_menu")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = CyberTextPrimary
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(CyberSurfaceElevated)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (isCurrentBookmarked) "Remove Bookmark" else "Bookmark This Page",
                                color = CyberTextPrimary,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isCurrentBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            onToggleBookmark()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                if (currentTab?.desktopMode == true) "Mobile Site View" else "Desktop Site View",
                                color = CyberTextPrimary,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (currentTab?.desktopMode == true) Icons.Default.PhoneAndroid else Icons.Default.Computer,
                                contentDescription = null,
                                tint = CyberTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            onToggleDesktop()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("New Tab", color = CyberTextPrimary, fontSize = 13.sp)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = CyberTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            onNewTab()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Security & Threat Audit", color = CyberTextPrimary, fontSize = 13.sp)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            onOpenSecurityAudit()
                        }
                    )
                }
            }
        }
    }
}
