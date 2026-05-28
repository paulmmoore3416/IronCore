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
import com.ironcore.metrics.ui.device.DeviceManagerScreen
import com.ironcore.metrics.ui.nutrition.MealDetailScreen
import com.ironcore.metrics.ui.workout.SpecializedWorkoutScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Workout : Screen("workout")
    object Nutrition : Screen("nutrition")
    object Settings : Screen("settings")
    object DeviceManager : Screen("device_manager")
    object MetricDetail : Screen("metric_detail/{metricType}") {
        fun createRoute(metricType: String) = "metric_detail/$metricType"
    }
    object MealDetail : Screen("meal_detail/{mealJson}") {
        fun createRoute(mealJson: String) = "meal_detail/${java.net.URLEncoder.encode(mealJson, "UTF-8")}"
    }
    object SpecializedWorkout : Screen("specialized_workout/{modality}") {
        fun createRoute(modality: String) = "specialized_workout/${java.net.URLEncoder.encode(modality, "UTF-8")}"
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
                        "device" -> navController.navigate(Screen.DeviceManager.route)
                        // Social and AR are disabled for Phase 1 (Production Readiness)
                        // "social" -> navController.navigate(Screen.SocialRoom.route)
                        // "ar" -> navController.navigate(Screen.ARBodyProgress.route)
                        else -> navController.navigate(Screen.MetricDetail.createRoute(type))
                    }
                }
            )
        }
        composable(Screen.Workout.route) {
            WorkoutLoggerScreen(
                onNavigateToModality = { modality -> 
                    navController.navigate(Screen.SpecializedWorkout.createRoute(modality))
                }
            )
        }
        composable(Screen.Nutrition.route) {
            NutritionScreen(onNavigateToMealDetail = { mealJson -> 
                navController.navigate(Screen.MealDetail.createRoute(mealJson))
            })
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.DeviceManager.route) {
            DeviceManagerScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.MealDetail.route) { backStackEntry ->
            val mealJson = backStackEntry.arguments?.getString("mealJson") ?: ""
            MealDetailScreen(
                mealJson = java.net.URLDecoder.decode(mealJson, "UTF-8"),
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.SpecializedWorkout.route) { backStackEntry ->
            val modality = backStackEntry.arguments?.getString("modality") ?: ""
            SpecializedWorkoutScreen(
                modality = java.net.URLDecoder.decode(modality, "UTF-8"),
                onBack = { navController.popBackStack() }
            )
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





