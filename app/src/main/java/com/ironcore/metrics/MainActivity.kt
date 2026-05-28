package com.ironcore.metrics

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
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
import com.ironcore.metrics.data.auth.PermissionManager
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
    
    @Inject
    lateinit var permissionManager: PermissionManager

    private var onPermissionsGrantedCallback: (() -> Unit)? = null
    
    private val requestHealthConnectPermissionLauncher = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        android.util.Log.d("MainActivity", "Health Connect permission result received")
        android.util.Log.d("MainActivity", "Granted permissions: ${granted.size}")
        android.util.Log.d("MainActivity", "Required permissions: ${healthConnectManager.permissions.size}")
        
        // Always trigger callback to refresh UI, even if not all permissions granted
        // This allows the UI to update and show current state
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            android.util.Log.d("MainActivity", "Triggering permission callback after delay")
            onPermissionsGrantedCallback?.invoke()
        }, 500)
        
        if (granted.containsAll(healthConnectManager.permissions)) {
            android.util.Log.d("MainActivity", "✅ All Health Connect permissions granted!")
        } else {
            val missing = healthConnectManager.permissions.filter { !granted.contains(it) }
            android.util.Log.w("MainActivity", "❌ Missing Health Connect permissions: ${missing.size}")
            missing.forEach { perm ->
                android.util.Log.w("MainActivity", "  - $perm")
            }
        }
    }
    
    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        android.util.Log.d("MainActivity", "Runtime permissions result received")
        android.util.Log.d("MainActivity", "Total permissions requested: ${permissions.size}")
        
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            android.util.Log.d("MainActivity", "All runtime permissions granted!")
            // All runtime permissions granted, trigger callback to refresh UI
            onPermissionsGrantedCallback?.invoke()
        } else {
            // Some permissions denied - app will still work with reduced functionality
            val deniedPermissions = permissions.filterValues { !it }.keys
            android.util.Log.w("MainActivity", "Some runtime permissions denied: ${deniedPermissions.size}")
            deniedPermissions.forEach { perm ->
                android.util.Log.w("MainActivity", "  - $perm")
            }
            // Still trigger callback to update UI with current permission state
            onPermissionsGrantedCallback?.invoke()
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

    fun requestPermissions(onGranted: (() -> Unit)? = null) {
        android.util.Log.d("MainActivity", "requestPermissions() called")
        
        // Store callback to trigger after permissions are granted
        onPermissionsGrantedCallback = onGranted
        
        // Check if Health Connect is available
        if (!healthConnectManager.isAvailable()) {
            android.util.Log.e("MainActivity", "Health Connect is NOT available!")
            showHealthConnectNotInstalledDialog()
            return
        }
        
        android.util.Log.d("MainActivity", "Health Connect is available")
        android.util.Log.d("MainActivity", "Opening Health Connect app directly")
        
        try {
            // Open Health Connect app directly to the app permissions screen
            val intent = android.content.Intent().apply {
                action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = android.net.Uri.fromParts("package", "com.google.android.apps.healthdata", null)
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            startActivity(intent)
            android.util.Log.d("MainActivity", "Opened Health Connect settings")
            
            // Show a toast to guide the user
            android.widget.Toast.makeText(
                this,
                "Please tap 'App permissions' → 'IronCore Metrics' and grant all permissions",
                android.widget.Toast.LENGTH_LONG
            ).show()
            
            // Trigger callback after delay to refresh UI when user returns
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                android.util.Log.d("MainActivity", "Triggering permission callback after Health Connect visit")
                onPermissionsGrantedCallback?.invoke()
            }, 3000)
            
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error opening Health Connect: ${e.message}")
            // Fallback to permission launcher
            requestHealthConnectPermissionLauncher.launch(healthConnectManager.permissions)
        }
        
        // Request runtime permissions after a short delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val missingPermissions = permissionManager.getMissingPermissions()
            android.util.Log.d("MainActivity", "Missing runtime permissions: ${missingPermissions.size}")
            if (missingPermissions.isNotEmpty()) {
                requestMultiplePermissionsLauncher.launch(missingPermissions.toTypedArray())
            }
        }, 1000)
    }
    
    private fun showHealthConnectNotInstalledDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Health Connect Required")
            .setMessage("IronCore Metrics requires Health Connect to access your health data.\n\n" +
                       "Please install Health Connect from the Google Play Store first.")
            .setPositiveButton("Open Play Store") { _, _ ->
                try {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse("market://details?id=com.google.android.apps.healthdata")
                        setPackage("com.android.vending")
                    }
                    startActivity(intent)
                } catch (e: android.content.ActivityNotFoundException) {
                    // Fallback to browser
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")
                    }
                    startActivity(intent)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

data class NavigationItem(val label: String, val route: String, val icon: ImageVector)
