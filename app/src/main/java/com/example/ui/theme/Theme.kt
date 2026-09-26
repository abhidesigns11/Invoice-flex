package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryContainer,
    onPrimary = PureWhite,
    primaryContainer = PrimaryCobalt,
    onPrimaryContainer = PureWhite,
    secondary = SecondarySlate,
    onSecondary = PureWhite,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = TertiaryEmerald,
    background = OnSurfaceObsidian,
    surface = Color(0xFF131B2E),
    surfaceVariant = Color(0xFF213145),
    onBackground = PureWhite,
    onSurface = PureWhite,
    onSurfaceVariant = OutlineVariant,
    outline = TextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryCobalt,
    onPrimary = PureWhite,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = PureWhite,
    secondary = SecondarySlate,
    onSecondary = PureWhite,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = TertiaryEmerald,
    onTertiary = PureWhite,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = PureWhite,
    background = CanvasBg,
    surface = PureWhite,
    surfaceVariant = SurfaceContainerLow,
    onBackground = OnSurfaceObsidian,
    onSurface = OnSurfaceObsidian,
    onSurfaceVariant = OnSurfaceVariant,
    outline = OutlineHairline
)

@Composable
fun MyApplicationTheme(
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
