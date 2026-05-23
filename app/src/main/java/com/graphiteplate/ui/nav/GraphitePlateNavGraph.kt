package com.graphiteplate.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.graphiteplate.ui.screen.AddFoodScreen
import com.graphiteplate.ui.screen.HistoryScreen
import com.graphiteplate.ui.screen.HomeScreen
import com.graphiteplate.ui.screen.SettingsScreen
import com.graphiteplate.ui.screen.WeightScreen

/** Stable route identifiers. Kept simple — no deep-link arguments yet. */
object Routes {
    const val HOME = "home"
    const val ADD_FOOD = "add_food"
    const val HISTORY = "history"
    const val WEIGHT = "weight"
    const val SETTINGS = "settings"
}

@Composable
fun GraphitePlateNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onAddFoodClick = { navController.navigate(Routes.ADD_FOOD) },
                onHistoryClick = { navController.navigate(Routes.HISTORY) },
                onWeightClick = { navController.navigate(Routes.WEIGHT) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.ADD_FOOD) {
            AddFoodScreen(
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
            )
        }
        composable(Routes.HISTORY) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.WEIGHT) {
            WeightScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
