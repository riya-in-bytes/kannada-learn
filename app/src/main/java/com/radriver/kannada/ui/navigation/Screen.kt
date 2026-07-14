package com.radriver.kannada.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.ui.graphics.vector.ImageVector
import com.radriver.kannada.R

sealed class Screen(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    data object Dashboard : Screen("dashboard", R.string.nav_dashboard, Icons.Filled.Home)
    data object Flashcards : Screen("flashcards", R.string.nav_flashcards, Icons.Filled.Style)
    data object Quiz : Screen("quiz", R.string.nav_quiz, Icons.Filled.Quiz)

    companion object {
        val bottomNavItems = listOf(Dashboard, Flashcards, Quiz)
    }
}
