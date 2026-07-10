package com.ju.passgen.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ju.passgen.R

// ── Orbitron — carga individual de cada peso ──────────────────
// Si el TTF no está disponible (build local sin fuentes),
// FontFamily.Default se usa como fallback automático de Compose
val OrbitronFamily: FontFamily by lazy {
    try {
        FontFamily(
            Font(R.font.orbitron_regular,  FontWeight.Normal),
            Font(R.font.orbitron_medium,   FontWeight.Medium),
            Font(R.font.orbitron_semibold, FontWeight.SemiBold),
            Font(R.font.orbitron_bold,     FontWeight.Bold),
            Font(R.font.orbitron_black,    FontWeight.Black),
        )
    } catch (_: Throwable) {
        FontFamily.Default
    }
}

// ── Space Mono ────────────────────────────────────────────────
val SpaceMonoFamily: FontFamily by lazy {
    try {
        FontFamily(
            Font(R.font.space_mono_regular, FontWeight.Normal),
            Font(R.font.space_mono_bold,    FontWeight.Bold),
        )
    } catch (_: Throwable) {
        FontFamily.Monospace
    }
}

val JUTypography = Typography(
    displayLarge = TextStyle(
        fontFamily    = OrbitronFamily,
        fontWeight    = FontWeight.Black,
        fontSize      = 26.sp,
        lineHeight    = 32.sp,
        letterSpacing = 0.5.sp,
    ),
    displayMedium = TextStyle(
        fontFamily    = OrbitronFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 11.sp,
        lineHeight    = 16.sp,
        letterSpacing = 2.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily    = OrbitronFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 16.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.3.sp,
    ),
    titleSmall = TextStyle(
        fontFamily    = OrbitronFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 10.sp,
        lineHeight    = 14.sp,
        letterSpacing = 1.0.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily    = SpaceMonoFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 17.sp,
        lineHeight    = 26.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily    = OrbitronFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 10.sp,
        lineHeight    = 15.sp,
        letterSpacing = 0.4.sp,
    ),
    bodySmall = TextStyle(
        fontFamily    = OrbitronFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 15.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.sp,
    ),
    labelLarge = TextStyle(
        fontFamily    = OrbitronFamily,
        fontWeight    = FontWeight.Black,
        fontSize      = 14.sp,
        lineHeight    = 18.sp,
        letterSpacing = 2.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily    = OrbitronFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 8.sp,
        lineHeight    = 12.sp,
        letterSpacing = 1.8.sp,
    ),
    labelMedium = TextStyle(
        fontFamily    = SpaceMonoFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 12.sp,
        lineHeight    = 18.sp,
        letterSpacing = 0.sp,
    ),
)
