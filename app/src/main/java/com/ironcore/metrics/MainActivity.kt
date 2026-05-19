package com.ironcore.metrics

import android.os.Bundle
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.health.connect.client.PermissionController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ironcore.metrics.data.health.HealthConnectManager
import com.ironcore.metrics.ui.navigation.IronCoreNavGraph
import com.ironcore.metrics.ui.navigation.Screen
import com.ironcore.metrics.ui.theme.IronCoreTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var healthConnectManager: HealthConnectManager

    private val requestPermissionLauncher = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(healthConnectManager.permissions)) {
            // Permissions granted, refresh app data
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IronCoreTheme {
                MainScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // If we want the UI to refresh when user returns from settings
        // DashboardScreen already has a LaunchedEffect(Unit), but manual refresh helps
    }

    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        val items = listOf(
            NavigationItem("Overview", Screen.Dashboard.route, Icons.Default.Dashboard),
            NavigationItem("Workouts", Screen.Workout.route, Icons.Default.FitnessCenter),
            NavigationItem("Nutrition", Screen.Nutrition.route, Icons.Default.Restaurant),
            NavigationItem("Settings", Screen.Settings.route, Icons.Default.Settings)
        )

        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                IronCoreNavGraph(navController = navController)
            }
        }
    }

    fun requestPermissions() {
        requestPermissionLauncher.launch(healthConnectManager.permissions)
    }
}

data class NavigationItem(val label: String, val route: String, val icon: ImageVector)
