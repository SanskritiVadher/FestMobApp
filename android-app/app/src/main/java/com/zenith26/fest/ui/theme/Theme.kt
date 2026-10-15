package com.zenith26.fest.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TomatoPrimary,
    onPrimary = Color.White,
    primaryContainer = PaperCardBacking,
    onPrimaryContainer = InkPrimary,
    secondary = MustardYellow,
    onSecondary = InkPrimary,
    background = PaperBackground,
    onBackground = InkPrimary,
    surface = PaperCard,
    onSurface = InkPrimary,
    error = TomatoPrimary,
    onError = Color.White,
    outline = InkBorder
)

@Composable
fun Zenith26Theme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PaperBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

