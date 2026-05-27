package com.ironcore.metrics.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ironcore.metrics.ui.dashboard.DashboardScreen
import com.ironcore.metrics.ui.nutrition.NutritionScreen
import com.ironcore.metrics.ui.workout.WorkoutLoggerScreen

import com.ironcore.metrics.ui.onboarding.FirstRunScreen
import com.ironcore.metrics.ui.social.SocialRoomScreen
import com.ironcore.metrics.ui.ar.ARBodyProgressScreen
import com.ironcore.metrics.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Workout : Screen("workout")
    object Nutrition : Screen("nutrition")
    object Settings : Screen("settings")
    object MetricDetail : Screen("metric_detail/{metricType}") {
        fun createRoute(metricType: String) = "metric_detail/$metricType"
    }
    object HydrationLogging : Screen("hydration_logging")
    object SocialRoom : Screen("social_room")
    object ARBodyProgress : Screen("ar_body_progress")
}

@Composable
fun IronCoreNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            FirstRunScreen(onFinish = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onMetricClick = { type ->
                    when (type) {
                        "hydration" -> navController.navigate(Screen.HydrationLogging.route)
                        // Social and AR are disabled for Phase 1 (Production Readiness)
                        // "social" -> navController.navigate(Screen.SocialRoom.route)
                        // "ar" -> navController.navigate(Screen.ARBodyProgress.route)
                        else -> navController.navigate(Screen.MetricDetail.createRoute(type))
                    }
                }
            )
        }
        composable(Screen.Workout.route) {
            WorkoutLoggerScreen()
        }
        composable(Screen.Nutrition.route) {
            NutritionScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.MetricDetail.route) { backStackEntry ->
            val metricType = backStackEntry.arguments?.getString("metricType") ?: "unknown"
            MetricDetailScreen(metricType = metricType, onBack = { navController.popBackStack() })
        }
        composable(Screen.HydrationLogging.route) {
            HydrationLoggingScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.SocialRoom.route) {
            SocialRoomScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.ARBodyProgress.route) {
            ARBodyProgressScreen(onBack = { navController.popBackStack() })
        }
    }
}





