package com.entrupy.sample.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.entrupy.sample.R

/**
 * Kanit FontFamily - Entrupy brand font
 * 
 * Complete font family with all weights (100-900) and italic variants.
 */
val Kanit = FontFamily(
    // Thin (100)
    Font(R.font.kanit_thin, FontWeight.Thin, FontStyle.Normal),
    Font(R.font.kanit_thinitalic, FontWeight.Thin, FontStyle.Italic),

    // Extra Light (200)
    Font(R.font.kanit_extralight, FontWeight.ExtraLight, FontStyle.Normal),
    Font(R.font.kanit_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),

    // Light (300)
    Font(R.font.kanit_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.kanit_lightitalic, FontWeight.Light, FontStyle.Italic),

    // Regular (400)
    Font(R.font.kanit_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.kanit_italic, FontWeight.Normal, FontStyle.Italic),

    // Medium (500)
    Font(R.font.kanit_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.kanit_mediumitalic, FontWeight.Medium, FontStyle.Italic),

    // Semi Bold (600)
    Font(R.font.kanit_semibold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.kanit_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),

    // Bold (700)
    Font(R.font.kanit_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.kanit_bolditalic, FontWeight.Bold, FontStyle.Italic),

    // Extra Bold (800)
    Font(R.font.kanit_extrabold, FontWeight.ExtraBold, FontStyle.Normal),
    Font(R.font.kanit_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),

    // Black (900)
    Font(R.font.kanit_black, FontWeight.Black, FontStyle.Normal),
    Font(R.font.kanit_blackitalic, FontWeight.Black, FontStyle.Italic),
)

/**
 * Entrupy Typography System using Kanit font.
 */
val EntrupyTypography = Typography(
    // Display styles - Large, impactful text
    displayLarge = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),

    // Headline styles - Section headers
    headlineLarge = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),

    // Title styles - Component headers
    titleLarge = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),

    // Body styles - Main content
    bodyLarge = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    ),

    // Label styles - Buttons, chips, captions
    labelLarge = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Kanit,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    ),
)

