package com.grup4.googleauth.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grup4.googleauth.presentation.screen.AuthCheckScreen
import com.grup4.googleauth.presentation.screen.HomeScreen
import com.grup4.googleauth.presentation.screen.LoginScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "authCheck") {
        composable("authCheck") { AuthCheckScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
    }
}
