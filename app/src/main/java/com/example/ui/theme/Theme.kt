package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AegisDarkColorScheme = darkColorScheme(
    primary = CyberEmerald,
    onPrimary = Color.Black,
    primaryContainer = CyberBadgeBg,
    onPrimaryContainer = CyberEmeraldGlow,

    secondary = CyberCyan,
    onSecondary = Color.Black,
    secondaryContainer = CyberCyanBg,
    onSecondaryContainer = CyberCyanGlow,

    tertiary = CyberIncognito,
    onTertiary = Color.White,

    background = CyberBackground,
    onBackground = CyberTextPrimary,

    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,

    error = CyberDanger,
    onError = Color.White,
    errorContainer = CyberDangerBg,
    onErrorContainer = CyberDanger,

    outline = CyberBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Force dark cybersecurity aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AegisDarkColorScheme,
        typography = Typography,
        content = content
    )
}
