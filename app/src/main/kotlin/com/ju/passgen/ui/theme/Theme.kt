package com.ju.passgen.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Composition locals para acceder a colores extra fuera del Material scheme ──
data class JUExtendedColors(
    val accent3: Color,
    val cardBg: Color,
    val borderColor: Color,
    val mutedText: Color,
    val accentSoft: Color,
    val strengthVeryWeak: Color,
    val strengthWeak: Color,
    val strengthFair: Color,
    val strengthStrong: Color,
    val strengthMax: Color,
    val isDark: Boolean,
)

val LocalJUColors = staticCompositionLocalOf {
    JUExtendedColors(
        accent3          = DarkAccent3,
        cardBg           = DarkCard,
        borderColor      = DarkBorder,
        mutedText        = DarkMuted,
        accentSoft       = DarkAccentSoft,
        strengthVeryWeak = StrengthVeryWeak,
        strengthWeak     = StrengthWeak,
        strengthFair     = StrengthFair,
        strengthStrong   = StrengthStrong,
        strengthMax      = StrengthMax,
        isDark           = true,
    )
}

// Acceso conveniente: MaterialTheme.juColors
val MaterialTheme.juColors: JUExtendedColors
    @Composable get() = LocalJUColors.current

// ── Color Schemes ─────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary             = DarkAccent,
    onPrimary           = DarkBg,
    primaryContainer    = DarkAccentSoft,
    onPrimaryContainer  = DarkAccent,
    secondary           = DarkAccent2,
    onSecondary         = Color.White,
    tertiary            = DarkAccent3,
    background          = DarkBg,
    onBackground        = DarkText,
    surface             = DarkSurface,
    onSurface           = DarkText,
    surfaceVariant      = DarkCard,
    onSurfaceVariant    = DarkMuted,
    outline             = DarkBorder,
    error               = ColorError,
    onError             = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary             = LightAccent,
    onPrimary           = Color.White,
    primaryContainer    = LightAccentSoft,
    onPrimaryContainer  = LightAccent,
    secondary           = LightAccent2,
    onSecondary         = Color.White,
    tertiary            = LightAccent3,
    background          = LightBg,
    onBackground        = LightText,
    surface             = LightSurface,
    onSurface           = LightText,
    surfaceVariant      = LightCard,
    onSurfaceVariant    = LightMuted,
    outline             = LightBorder,
    error               = ColorError,
    onError             = Color.White,
)

// ── Theme ─────────────────────────────────────────────────────
@Composable
fun JUPassGenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val extColors = JUExtendedColors(
        accent3          = if (darkTheme) DarkAccent3  else LightAccent3,
        cardBg           = if (darkTheme) DarkCard     else LightCard,
        borderColor      = if (darkTheme) DarkBorder   else LightBorder,
        mutedText        = if (darkTheme) DarkMuted    else LightMuted,
        accentSoft       = if (darkTheme) DarkAccentSoft else LightAccentSoft,
        strengthVeryWeak = StrengthVeryWeak,
        strengthWeak     = StrengthWeak,
        strengthFair     = StrengthFair,
        strengthStrong   = StrengthStrong,
        strengthMax      = StrengthMax,
        isDark           = darkTheme,
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalJUColors provides extColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = JUTypography,
            shapes      = JUShapes,
            content     = content,
        )
    }
}
