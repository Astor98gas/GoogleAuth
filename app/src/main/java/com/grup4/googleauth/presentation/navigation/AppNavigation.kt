package com.grup4.googleauth.presentation.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grup4.googleauth.MainActivity
import com.grup4.googleauth.presentation.screen.HomeScreen


class AppNavigationActivity : ComponentActivity() {
    private val auth: FirebaseAuth = Firebase.auth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (auth.currentUser == null) {
                AppNavigation(authentiocated = false)
            } else {
                AppNavigation(authentiocated = true)
            }
        }
    }
}

@Composable
fun AppNavigation(authentiocated: Boolean) {
    val navController = rememberNavController()
    if (authentiocated) {
        NavHost(navController = navController, startDestination = "home") {
            composable("login") { MainActivity() }
            composable("home") { HomeScreen() }
        }
    } else {
        navController.navigate("login")
    }
}
