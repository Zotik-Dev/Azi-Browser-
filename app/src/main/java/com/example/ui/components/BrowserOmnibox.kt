package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrowserTab
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberIncognito
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.vpn.VpnState

@Composable
fun BrowserOmnibox(
    currentTab: BrowserTab?,
    tabCount: Int,
    vpnState: VpnState,
    onNavigate: (String) -> Unit,
    onReload: () -> Unit,
    onOpenVpnSheet: () -> Unit,
    onOpenTabManager: () -> Unit,
    onOpenSecurityAudit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember(currentTab?.url) {
        val display = if (currentTab == null || currentTab.isHome) "" else currentTab.url
        mutableStateOf(display)
    }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val handleGo = {
        if (textInput.isNotBlank()) {
            focusManager.clearFocus()
            onNavigate(textInput)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberBackground)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main Address & Search Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .background(CyberSurfaceVariant)
                    .border(
                        1.dp,
                        when {
                            isFocused -> CyberEmerald
                            vpnState.isConnected -> CyberCyan.copy(alpha = 0.5f)
                            else -> CyberBorder
                        },
                        RoundedCornerShape(23.dp)
                    )
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Security/Lock indicator
                    IconButton(
                        onClick = { onOpenSecurityAudit() },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("security_cert_button")
                    ) {
                        when {
                            currentTab?.isIncognito == true -> {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Incognito Tab",
                                    tint = CyberIncognito,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            currentTab?.isSecureHttps == true -> {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Secure HTTPS Connection",
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            currentTab?.isHome == true -> {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Insecure Connection",
                                    tint = CyberDanger,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }

                    // Text Field for URL / Search
                    BasicTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .onFocusChanged { isFocused = it.isFocused }
                            .testTag("url_input_field"),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = CyberTextPrimary,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(CyberEmerald),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = { handleGo() }
                        ),
                        decorationBox = { innerTextField ->
                            if (textInput.isEmpty() && !isFocused) {
                                Text(
                                    text = if (currentTab?.isIncognito == true) "Private search or type URL" else "Search or enter website name...",
                                    color = CyberTextSecondary,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            innerTextField()
                        }
                    )

                    // Clear button when user typed text
                    if (textInput.isNotEmpty()) {
                        IconButton(
                            onClick = { textInput = "" },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear input",
                                tint = CyberTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Direct "GO" Button when typing, or Reload button when browsing
                    if (isFocused || (textInput.isNotBlank() && (currentTab?.isHome == true || textInput != currentTab?.url))) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CyberEmerald)
                                .clickable { handleGo() }
                                .testTag("go_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Go",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else if (currentTab?.isLoading == true) {
                        IconButton(
                            onClick = { onReload() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Stop loading",
                                tint = CyberTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else if (currentTab?.isHome == false) {
                        IconButton(
                            onClick = { onReload() },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("reload_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reload",
                                tint = CyberTextSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // VPN Quick Status Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (vpnState.isConnected) CyberCyan.copy(alpha = 0.2f)
                        else CyberSurfaceElevated
                    )
                    .border(
                        1.dp,
                        if (vpnState.isConnected) CyberCyan else CyberBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onOpenVpnSheet() }
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .testTag("vpn_status_pill"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (vpnState.isConnected) {
                        Text(
                            text = vpnState.activeServer.flagEmoji,
                            fontSize = 14.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "VPN Shield",
                            tint = if (vpnState.isMalwareShieldActive) CyberEmerald else CyberTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Tab Switcher Pill
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberSurfaceElevated)
                    .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                    .clickable { onOpenTabManager() }
                    .testTag("tab_manager_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tabCount.coerceAtLeast(1).toString(),
                    color = CyberTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Web Page Loading Progress Bar
        AnimatedVisibility(visible = currentTab?.isLoading == true) {
            val progress = (currentTab?.progress ?: 0) / 100f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .padding(top = 4.dp),
                color = if (vpnState.isConnected) CyberCyan else CyberEmerald,
                trackColor = Color.Transparent
            )
        }
    }
}
