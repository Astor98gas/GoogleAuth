package com.grup4.googleauth.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.grup4.googleauth.presentation.viewModel.AuthCheckViewModel

@Composable
fun AuthCheckScreen(
    navController: NavController,
    viewModel: AuthCheckViewModel = AuthCheckViewModel(),
) {
    LaunchedEffect(Unit) {
        if (!viewModel.init()) {
            navController.navigate("login")
        } else {
            navController.navigate("home")
        }
    }
}