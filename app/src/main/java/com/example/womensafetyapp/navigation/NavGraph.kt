package com.example.womensafetyapp.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.womensafetyapp.ui.screens.*
import com.example.womensafetyapp.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object SOS : Screen("sos")
    object SOSSent : Screen("sos_sent")
    object EmergencyContacts : Screen("emergency_contacts")
    object HelplineNumbers : Screen("helpline_numbers")
    object SafeRoute : Screen("safe_route")
    object QuickTools : Screen("quick_tools")
    object CrowdNetwork : Screen("crowd_network")
    object Profile : Screen("profile")
}

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {

    // ✅ Reactive login state
    val isLoggedIn by remember { derivedStateOf { authViewModel.isLoggedIn } }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // 🔹 Splash (AUTO LOGIN HANDLING)
        composable(Screen.Splash.route) {
            SheShieldSplashScreen()

            // ✅ Navigation handled reactively
            LaunchedEffect(isLoggedIn) {
                if (isLoggedIn) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
        }

        // 🔹 Login
        composable(Screen.Login.route) {
            LoginScreen(
                onForgotPassword = {
                    // Optional future screen
                },
                onCreateAccount = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        // 🔹 Register
        composable(Screen.Register.route) {
            RegisterScreen(
                onSignIn = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        // 🔹 Home
        composable(Screen.Home.route) {
            HomeScreen(
                onSOSTriggered = {
                    navController.navigate(Screen.SOS.route)
                },
                onNavigate = { label ->
                    when (label) {
                        "Network" -> navController.navigate(Screen.CrowdNetwork.route)
                        "Safe Route" -> navController.navigate(Screen.SafeRoute.route)
                        "Quick Tools" -> navController.navigate(Screen.QuickTools.route)
                        "Contacts" -> navController.navigate(Screen.EmergencyContacts.route)
                        "Helpline" -> navController.navigate(Screen.HelplineNumbers.route)
                        "Profile" -> navController.navigate(Screen.Profile.route)
                    }
                }
            )
        }

        // 🔹 SOS
        composable(Screen.SOS.route) {
            SOSScreen(
                onCancel = { navController.popBackStack() },
                onAlertSent = { navController.navigate(Screen.SOSSent.route) }
            )
        }

        composable(Screen.SOSSent.route) {
            SOSSentScreen(
                onCancelAlert = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SOSSent.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.EmergencyContacts.route) {
            EmergencyContactsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.HelplineNumbers.route) {
            HelplineNumbersScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.SafeRoute.route) {
            SafeRouteScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.QuickTools.route) {
            QuickToolsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.CrowdNetwork.route) {
            CrowdNetworkScreen(
                onNavigate = { label ->
                    if (label == "Home") {
                        navController.navigate(Screen.Home.route)
                    }
                }
            )
        }

        // 🔹 Profile
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}