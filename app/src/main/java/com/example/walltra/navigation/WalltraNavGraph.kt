package com.example.walltra.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.walltra.ui.home.HomeScreen

@Composable
fun WalltraNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(navController = navController)
        }

        composable<Screen.Day> { backStackEntry ->
            val screen: Screen.Day = backStackEntry.toRoute()
            // DayScreen(date = screen.date, navController = navController)
        }

        composable<Screen.Periods> {
            // PeriodsScreen(navController)
        }

        composable<Screen.Compare> {
            // CompareScreen(navController)
        }
    }
}