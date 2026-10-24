package com.radriver.kannada.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.radriver.kannada.ui.dashboard.DashboardScreen
import com.radriver.kannada.ui.flashcards.FlashcardsScreen
import com.radriver.kannada.ui.mastered.MasteredWordsScreen
import com.radriver.kannada.ui.quiz.QuizScreen
import com.radriver.kannada.util.TtsManager

/**
 * Root scaffold: three-tab bottom navigation over a NavHost.
 *
 * Tab switches use the canonical save/restore pattern so each tab keeps its
 * NavBackStackEntry (and therefore its ViewModel and scroll state) alive, and
 * the system back button always returns to the start destination instead of
 * replaying every tab visit.
 */
@Composable
fun KannadaNavGraph(
    viewModelFactory: ViewModelProvider.Factory,
    ttsManager: TtsManager,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    // Single tab-switch lambda shared by the bottom bar and in-content links
    // (e.g. the dashboard's review card): every route change must use the
    // same save/restore block or tab ViewModels lose their state.
    val navigateToTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar {
                Screen.bottomNavItems.forEach { screen ->
                    val label = stringResource(screen.labelRes)
                    NavigationBarItem(
                        // childRoutes keeps a tab lit while one of its pushed
                        // screens (e.g. the mastered word list) is on top.
                        selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route || it.route in screen.childRoutes
                        } == true,
                        onClick = { navigateToTab(screen.route) },
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModelFactory = viewModelFactory,
                    ttsManager = ttsManager,
                    onGoToQuiz = { navigateToTab(Screen.Quiz.route) },
                    onGoToFlashcards = { navigateToTab(Screen.Flashcards.route) },
                    // A plain push, not navigateToTab: this screen sits on top
                    // of the dashboard, and popUpTo(start) would drop it.
                    onShowMasteredWords = { navController.navigate(Routes.MASTERED_WORDS) }
                )
            }
            composable(Screen.Flashcards.route) {
                FlashcardsScreen(viewModelFactory = viewModelFactory, ttsManager = ttsManager)
            }
            composable(Screen.Quiz.route) {
                QuizScreen(viewModelFactory = viewModelFactory, ttsManager = ttsManager)
            }
            composable(Routes.MASTERED_WORDS) {
                MasteredWordsScreen(
                    viewModelFactory = viewModelFactory,
                    ttsManager = ttsManager,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
