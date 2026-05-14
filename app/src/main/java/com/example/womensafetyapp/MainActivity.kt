//package com.example.womensafetyapp
//
//import android.os.Bundle
//import com.google.firebase.auth.FirebaseAuth
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.runtime.*
//import com.example.womensafetyapp.navigation.Screen
//import com.example.womensafetyapp.ui.screens.CrowdNetworkScreen
//import com.example.womensafetyapp.ui.screens.EmergencyContactScreen
//import com.example.womensafetyapp.ui.screens.HomeScreen
//import com.example.womensafetyapp.ui.screens.LoginScreen
//import com.example.womensafetyapp.ui.screens.ProfileScreen
//import com.example.womensafetyapp.ui.screens.RegisterScreen
//import com.example.womensafetyapp.ui.screens.SOSScreen
//import com.example.womensafetyapp.ui.screens.SOSSentScreen
//import com.example.womensafetyapp.ui.screens.SafeRouteScreen
//import com.example.womensafetyapp.ui.screens.SheShieldSplashScreen
//import com.example.womensafetyapp.ui.theme.WomenSafetyAppTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            WomenSafetyAppTheme {
//                SheShieldApp()
//            }
//        }
//    }
//}
//
////enum class Screen {
////    SPLASH,
////    LOGIN,
////    REGISTER,
////    HOME,
////    SOS,
////    SOS_SENT,
////    EMERGENCY_CONTACTS,
////    SAFE_ROUTE,
////    CROWD_NETWORK,
////    PROFILE
////}
//
//@Composable
//fun SheShieldApp() {
//
//    // logged in user stays logged in
//    val auth = FirebaseAuth.getInstance()
//
//    var current by remember {
//        mutableStateOf(
//            if (auth.currentUser != null)
//                Screen.Home
//            else
//                Screen.Splash
//        )
//    }
//
//
//
//    when (current) {
//        Screen.Splash -> SheShieldSplashScreen(
//            onFinished = {
//                current =
//                    if (auth.currentUser != null)
//                        Screen.Home
//                    else
//                        Screen.Login
//            }
//        )
//        Screen.Login -> LoginScreen(
//            onCreateAccount = { current = Screen.Register },
//            onSignIn = { _, _ -> current = Screen.Home }
//        )
//        Screen.Register -> RegisterScreen(
//            onSignIn = { current = Screen.Login },
//            onCreateAccount = { _, _, _, _ -> current = Screen.Home }
//        )
//        Screen.Home -> HomeScreen(
//            onSOSTriggered = { current = Screen.SOS },
////            onNavigate = { label ->
////                when (label) {
////                    "Network"     -> current = Screen.CROWD_NETWORK
////                    "Safe Route"  -> current = Screen.SAFE_ROUTE
////                    "Quick Tools" -> current = Screen.QUICK_TOOLS
////                    "Contacts"    -> current = Screen.EMERGENCY_CONTACTS
////                }
////            }
//            onNavigate = { screen ->
//                current = screen
//            }
//
//        )
//        Screen.SOS -> SOSScreen(
//            onCancel = { current = Screen.Home },
//            onAlertSent = { current = Screen.SOSSent }
//        )
//        Screen.SOSSent -> SOSSentScreen(
//            onCancelAlert = { current = Screen.SOS }
//        )
//        Screen.EmergencyContacts -> EmergencyContactScreen(
//            onBack = { current = Screen.Home }
//        )
//
//        Screen.SafeRoute -> SafeRouteScreen()
//
////        Screen.QUICK_TOOLS -> QuickToolsScreen(
////            onBack = { current = Screen.HOME }
////        )
//
//        Screen.CrowdNetwork -> CrowdNetworkScreen(
//            onNavigate = { current = Screen.Home }
//        )
//
//
//        Screen.Profile -> ProfileScreen(
//            onNavigateToContacts = {
//                current = Screen.EmergencyContacts
//            }
//        )
//    }
//}


package com.example.womensafetyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.womensafetyapp.navigation.SetupNavGraph
import com.example.womensafetyapp.ui.theme.WomenSafetyAppTheme
import com.example.womensafetyapp.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            WomenSafetyAppTheme {
                val navController = rememberNavController()

                val authViewModel: AuthViewModel = viewModel()

                SetupNavGraph(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }
}