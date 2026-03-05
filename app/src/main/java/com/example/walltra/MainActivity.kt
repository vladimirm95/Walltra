package com.example.walltra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.walltra.navigation.WalltraNavGraph
import com.example.walltra.ui.theme.WalltraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WalltraTheme {
                val navController = rememberNavController()
                WalltraNavGraph(navController = navController)
            }
        }
    }
}