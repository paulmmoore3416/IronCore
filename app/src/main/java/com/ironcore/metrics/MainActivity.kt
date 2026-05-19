package com.ironcore.metrics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint

import androidx.navigation.compose.rememberNavController
import com.ironcore.metrics.ui.media.MediaControlOverlay
import com.ironcore.metrics.ui.navigation.IronCoreNavGraph
import com.ironcore.metrics.ui.theme.IronCoreTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IronCoreTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { MediaControlOverlay() }
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
        }
    }
}
