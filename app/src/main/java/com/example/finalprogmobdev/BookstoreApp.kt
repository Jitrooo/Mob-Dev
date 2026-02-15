package com.example.finalprogmobdev

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun BookstoreApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "introduction"
    ) {
        composable("introduction") {
            IntroductionScreen(
                onRegisterClick = { navController.navigate("register") },
                onLoginClick = { navController.navigate("login") }
            )
        }

        composable("register") {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("introduction") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("introduction") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToProducts = { navController.navigate("products") },
                onNavigateToCart = { navController.navigate("cart") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }

        composable("products") {
            ProductsScreen(
                onBackClick = { navController.navigate("home") },
                onNavigateToCart = { navController.navigate("cart") }
            )
        }

        composable("cart") {
            CartScreen(
                onBackClick = { navController.navigate("home") },
                onCheckout = { navController.navigate("checkout") }
            )
        }

        composable("checkout") {
            CheckoutScreen(
                onBackClick = { navController.popBackStack() },
                onPlaceOrder = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.navigate("home") },
                onNavigateToAllOrders = { navController.navigate("allOrders") },
                onNavigateToTrackOrders = { navController.navigate("trackOrders") },
                onLogout = {
                    navController.navigate("introduction") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("allOrders") {
            AllOrdersScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("trackOrders") {
            TrackOrdersScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}