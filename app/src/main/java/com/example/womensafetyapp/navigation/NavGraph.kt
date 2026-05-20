package com.example.womensafetyapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.womensafetyapp.ui.screens.*
import com.example.womensafetyapp.ui.screens.EmergencyContactScreen
import com.example.womensafetyapp.ui.screens.LiveTrackingScreen
import com.example.womensafetyapp.viewmodel.AuthViewModel

sealed class Screen(val route: String) {

    object Splash : Screen("splash")

    object Login : Screen("login")

    object Register : Screen("register")

    object Home : Screen("home")

    object SOS : Screen("sos")

    object SOSSent : Screen("sos_sent")

    object EmergencyContacts : Screen("emergency_contacts")

    object SafeRoute : Screen("safe_route")

    // MAP SCREEN
    object Map : Screen("map")

    // LIVE TRACKING SCREEN
    object LiveTracking :
        Screen("live_tracking/{userId}")

    object CrowdNetwork : Screen("crowd_network")

    object Profile : Screen("profile")
}

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {

    val isLoggedIn by remember {
        derivedStateOf {
            authViewModel.isLoggedIn
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // SPLASH
        composable(Screen.Splash.route) {

            SheShieldSplashScreen()

            LaunchedEffect(isLoggedIn) {

                if (isLoggedIn) {

                    navController.navigate(Screen.Home.route) {

                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }

                } else {

                    navController.navigate(Screen.Login.route) {

                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }

        // LOGIN
        composable(Screen.Login.route) {

            LoginScreen(

                onCreateAccount = {

                    navController.navigate(Screen.Register.route)
                },

                onSignIn = { _, _ ->

                    navController.navigate(Screen.Home.route) {

                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // REGISTER
        composable(Screen.Register.route) {

            RegisterScreen(

                onSignIn = {

                    navController.navigate(Screen.Login.route)
                },

                onCreateAccount = { _, _, _, _ ->

                    navController.navigate(Screen.Home.route) {

                        popUpTo(Screen.Register.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // HOME
        composable(Screen.Home.route) {

            HomeScreen(

                onSOSTriggered = {

                    navController.navigate(Screen.SOS.route)
                },

                onNavigate = { screen ->

                    when (screen) {

                        Screen.Profile ->
                            navController.navigate(Screen.Profile.route)

                        Screen.SafeRoute ->
                            navController.navigate(Screen.Map.route)

                        Screen.EmergencyContacts ->
                            navController.navigate(Screen.EmergencyContacts.route)

                        Screen.CrowdNetwork ->
                            navController.navigate(Screen.CrowdNetwork.route)

                        else -> {}
                    }
                }
            )
        }

        // SOS
        composable(Screen.SOS.route) {

            SOSScreen(

                onCancel = {
                    navController.popBackStack()
                },

                onAlertSent = {

                    navController.navigate(Screen.SOSSent.route)
                }
            )
        }

        // SOS SENT
        composable(Screen.SOSSent.route) {

            SOSSentScreen(

                onCancelAlert = {

                    navController.navigate(Screen.Home.route) {

                        popUpTo(Screen.Home.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }

        // CONTACTS
        composable(Screen.EmergencyContacts.route) {

            EmergencyContactScreen(

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // SAFE ROUTE
        composable(Screen.SafeRoute.route) {

            SafeRouteScreen(

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // MAP SCREEN
        composable(Screen.Map.route) {

            MapScreen(

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // LIVE TRACKING SCREEN
        composable(

            route = "live_tracking/{userId}"

        ) { backStackEntry ->

            val userId =
                backStackEntry.arguments
                    ?.getString("userId")
                    ?: ""

            LiveTrackingScreen(
                userId = userId
            )
        }

        // CROWD NETWORK
        composable(Screen.CrowdNetwork.route) {

            CrowdNetworkScreen(

                onNavigate = {
                    navController.navigate(Screen.Home.route)
                }
            )
        }

        // PROFILE
        composable(Screen.Profile.route) {

            ProfileScreen(

                onLogout = {

                    navController.navigate(Screen.Login.route) {

                        popUpTo(0)
                    }
                },

                onNavigateToContacts = {

                    navController.navigate(
                        Screen.EmergencyContacts.route
                    )
                }
            )
        }
    }
}