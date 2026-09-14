package com.drew.qrqu.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val NeoBrutalismColorScheme = lightColorScheme(
    primary = BrutalRed,
    onPrimary = BrutalPureWhite,
    secondary = BrutalBlack,
    onSecondary = BrutalPureWhite,
    background = BrutalWhite,
    onBackground = BrutalBlack,
    surface = BrutalPureWhite,
    onSurface = BrutalBlack,
    error = BrutalRed,
    onError = BrutalPureWhite
)

@Composable
fun QrQuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is intentionally turned off to maintain the Neo-brutalism look
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = NeoBrutalismColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
