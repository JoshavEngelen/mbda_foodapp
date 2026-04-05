package com.example.foodapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MutedSage,
    secondary = SoftSand,
    tertiary = MutedRed,
    background = DarkCharcoal,
    surface = DarkCharcoal,
    onPrimary = DarkCharcoal,
    onSecondary = DarkCharcoal,
    onBackground = CreamWhite,
    onSurface = CreamWhite
)

private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    secondary = DeepOlive,
    tertiary = EarthyRed,
    background = WarmSand,
    surface = CreamWhite,
    onPrimary = CreamWhite,
    onSecondary = CreamWhite,
    onBackground = DarkCharcoal,
    onSurface = DarkCharcoal
)

@Composable
fun FoodAppTheme(
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
