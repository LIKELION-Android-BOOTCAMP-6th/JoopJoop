package com.example.joopjoop.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = TextPrimary,
    primaryContainer = OrangeDark,
    onPrimaryContainer = TextPrimary,
    background = BgDarkest,
    onBackground = TextPrimary,
    surface = BgDark,
    onSurface = TextPrimary,
    surfaceVariant = BgSurface,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor,
    tertiary = OrangeLight
)

private val LightColorScheme = darkColorScheme( // Design is primarily dark, so using dark-ish colors even for light mode as a fallback
    primary = OrangePrimary,
    onPrimary = TextPrimary,
    background = BgDarkest,
    onBackground = TextPrimary,
    surface = BgDark,
    onSurface = TextPrimary,
    surfaceVariant = BgSurface,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor
)

@Composable
fun JoopJoopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}