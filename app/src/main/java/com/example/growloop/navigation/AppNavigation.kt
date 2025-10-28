package com.example.growloop.navigation

import DonateScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.growloop.ui.screens.Auth.AuthViewModel
import com.example.growloop.ui.screens.Auth.LoginPage
import com.example.growloop.ui.screens.Auth.AuthState
import com.example.growloop.ui.screens.Auth.RegistrationScreen
import com.example.responsivedashboard.SustainableDashboard


@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel() // Get AuthViewModel here
) {
val navController: NavHostController = rememberNavController()
    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.UnAuthenticated -> {
                navController.navigate(Pages.LOGIN.name) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            }
            else -> Unit
        }
    }


    NavHost(
        navController = navController,
        startDestination = Pages.REGISTER.name
    ) {
        composable(Pages.REGISTER.name) {
            RegistrationScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(Pages.LOGIN.name) {
            LoginPage(navController = navController, authViewModel = authViewModel)
        }
        composable(Pages.DASHBOARD.name) {
            SustainableDashboard(navController = navController)
        }

        composable(Pages.DONATE.name) {
            DonateScreen(navController)
        }

    }
}