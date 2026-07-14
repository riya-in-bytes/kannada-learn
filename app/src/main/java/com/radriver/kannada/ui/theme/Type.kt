package com.radriver.kannada.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Material defaults with two adjustments for Kannada script, which has tall
 * conjuncts and vowel signs: generous line heights on the styles used to
 * render it (displaySmall on flashcards, titleLarge on quiz options).
 */
val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 36.sp,
        lineHeight = 52.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 34.sp
    )
)
