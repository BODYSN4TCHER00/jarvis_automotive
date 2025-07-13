package com.example.jarvis.components.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jarvis.screens.HomeScreen
import com.example.jarvis.screens.JobsScreen
import com.example.jarvis.screens.ToolsScreen
import com.example.jarvis.screens.JobToolsScreen

@Composable
fun NavigationComponent() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        composable("jobs") {
            JobsScreen(navController)
        }
        composable("tools") {
            ToolsScreen(navController)
        }
        composable("jobTools/{jobName}") { backStackEntry ->
            val jobName = backStackEntry.arguments?.getString("jobName") ?: "Trabajo"
            JobToolsScreen(navController, jobName)
        }
    }
}
