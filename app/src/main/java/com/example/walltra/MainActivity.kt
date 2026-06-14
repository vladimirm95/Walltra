package com.example.walltra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.walltra.data.model.ThemeMode
import com.example.walltra.navigation.WalltraNavGraph
import com.example.walltra.ui.AppViewModel
import com.example.walltra.ui.common.LocalCurrency
import com.example.walltra.ui.theme.WalltraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: AppViewModel = hiltViewModel()
            val themeMode by appViewModel.themeMode.collectAsState()
            val currency by appViewModel.currency.collectAsState()

            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            WalltraTheme(darkTheme = darkTheme) {
                CompositionLocalProvider(LocalCurrency provides currency) {
                    val navController = rememberNavController()
                    WalltraNavGraph(navController = navController)
                }
            }
        }
    }
}