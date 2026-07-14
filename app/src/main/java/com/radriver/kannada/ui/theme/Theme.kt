package com.radriver.kannada.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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

@Composable
fun KannadaLearnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
