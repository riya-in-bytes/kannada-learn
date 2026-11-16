package com.riyainbytes.pocketshaala.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.riyainbytes.pocketshaala.domain.model.Language

private val LightColors = lightColorScheme(
    primary = Gold40,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = Brown40,
    secondaryContainer = BrownContainer,
    onSecondaryContainer = OnBrownContainer,
    tertiary = Green40,
    tertiaryContainer = GreenContainer,
    onTertiaryContainer = OnGreenContainer,
    background = LightBackground,
    surface = LightBackground
)

private val DarkColors = darkColorScheme(
    primary = Gold80,
    primaryContainer = GoldContainerDark,
    onPrimaryContainer = OnGoldContainerDark,
    secondary = Brown80,
    secondaryContainer = BrownContainerDark,
    onSecondaryContainer = OnBrownContainerDark,
    tertiary = Green80,
    tertiaryContainer = GreenContainerDark,
    onTertiaryContainer = OnGreenContainerDark,
    background = DarkBackground,
    surface = DarkBackground
)

private val GujaratiLightColors = lightColorScheme(
    primary = Maroon40,
    primaryContainer = MaroonContainer,
    onPrimaryContainer = OnMaroonContainer,
    secondary = Marigold40,
    secondaryContainer = MarigoldContainer,
    onSecondaryContainer = OnMarigoldContainer,
    tertiary = Teal40,
    tertiaryContainer = TealContainer,
    onTertiaryContainer = OnTealContainer,
    background = LightBackground,
    surface = LightBackground
)

private val GujaratiDarkColors = darkColorScheme(
    primary = Maroon80,
    onPrimary = OnMaroon80,
    primaryContainer = MaroonContainerDark,
    onPrimaryContainer = OnMaroonContainerDark,
    secondary = Marigold80,
    secondaryContainer = MarigoldContainerDark,
    onSecondaryContainer = OnMarigoldContainerDark,
    tertiary = Teal80,
    tertiaryContainer = TealContainerDark,
    onTertiaryContainer = OnTealContainerDark,
    background = DarkBackground,
    surface = DarkBackground
)

/** Each language gets its own accent palette, so the active one is obvious at a glance. */
@Composable
fun PocketShaalaTheme(
    language: Language = Language.DEFAULT,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (language) {
        Language.KANNADA -> if (darkTheme) DarkColors else LightColors
        Language.GUJARATI -> if (darkTheme) GujaratiDarkColors else GujaratiLightColors
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
