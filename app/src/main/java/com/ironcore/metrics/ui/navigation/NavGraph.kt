package com.ironcore.metrics.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ironcore.metrics.ui.dashboard.DashboardScreen
import com.ironcore.metrics.ui.nutrition.NutritionScreen
import com.ironcore.metrics.ui.workout.WorkoutLoggerScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Workout : Screen("workout")
    object Nutrition : Screen("nutrition")
}

@Composable
fun IronCoreNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        composable(Screen.Workout.route) {
            WorkoutLoggerScreen()
        }
        composable(Screen.Nutrition.route) {
            NutritionScreen()
        }
    }
}


