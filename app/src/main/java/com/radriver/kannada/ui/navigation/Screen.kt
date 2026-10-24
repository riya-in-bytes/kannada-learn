package com.radriver.kannada.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.ui.graphics.vector.ImageVector
import com.radriver.kannada.R

/**
 * Destinations that are pushed on top of a tab instead of being tabs
 * themselves — they have no label or icon, so they aren't [Screen]s. Each one
 * is listed in the owning tab's [Screen.childRoutes] so the bottom bar keeps
 * that tab highlighted while the child is on screen.
 */
object Routes {
    const val MASTERED_WORDS = "mastered_words"
}

sealed class Screen(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    /** Routes pushed on top of this tab; see [Routes]. */
    open val childRoutes: Set<String> get() = emptySet()

    data object Dashboard : Screen("dashboard", R.string.nav_dashboard, Icons.Filled.Home) {
        override val childRoutes = setOf(Routes.MASTERED_WORDS)
    }

    data object Flashcards : Screen("flashcards", R.string.nav_flashcards, Icons.Filled.Style)
    data object Quiz : Screen("quiz", R.string.nav_quiz, Icons.Filled.Quiz)

    companion object {
        val bottomNavItems = listOf(Dashboard, Flashcards, Quiz)
    }
}
