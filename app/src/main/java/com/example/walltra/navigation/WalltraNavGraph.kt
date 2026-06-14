package com.example.walltra.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walltra.ui.compare.CompareScreen
import com.example.walltra.ui.day.DayScreen
import com.example.walltra.ui.home.HomeScreen
import com.example.walltra.ui.periods.PeriodDetailScreen
import com.example.walltra.ui.periods.PeriodsScreen
import com.example.walltra.ui.settings.SettingsScreen

@Composable
fun WalltraNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(navController = navController)
        }

        composable<Screen.Day> {
            DayScreen(navController = navController)
        }

        composable<Screen.Periods> {
            PeriodsScreen(navController = navController)
        }

        composable<Screen.PeriodDetail> {
            PeriodDetailScreen(navController = navController)
        }

        composable<Screen.Compare> {
            CompareScreen(navController = navController)
        }

        composable<Screen.Settings> {
            SettingsScreen(navController = navController)
        }
    }
}