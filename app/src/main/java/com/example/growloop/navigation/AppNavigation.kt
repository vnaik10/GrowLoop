package com.example.growloop.navigation

import BagsScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.growloop.ui.screens.Auth.AuthViewModel
import com.example.growloop.ui.screens.Auth.LoginPage
import com.example.growloop.ui.screens.Auth.RegistrationScreen
import com.example.growloop.ui.screens.bag.BagDetailsScreen
import com.example.responsivedashboard.SustainableDashboard

@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel()){
    val navController = rememberNavController()

    NavHost(navController, startDestination = Pages.REGISTER.toString()){

        composable(Pages.REGISTER.name){
            RegistrationScreen(navController)
        }

        composable(Pages.LOGIN.name) {
            LoginPage(navController)
        }
        composable(Pages.DASHBOARD.name){
            SustainableDashboard(navController) // Pass navController
        }
        composable("MyBag"){ // New: Add BagPage composable
           BagsScreen(navController)
        }

        composable(
            "bag_details/{bagId}",
            arguments = listOf(navArgument("bagId") { type = NavType.LongType })
        ) { backStackEntry ->
            val bagId = backStackEntry.arguments?.getLong("bagId") ?: 0L
            BagDetailsScreen(bagId, navController)
        }
    }
}
