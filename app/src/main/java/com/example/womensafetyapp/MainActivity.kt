package com.example.womensafetyapp

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.womensafetyapp.ui.theme.WomenSafetyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WomenSafetyAppTheme {
                SheShieldApp()
            }
        }
    }
}

enum class Screen {
    SPLASH,
    LOGIN,
    REGISTER,
    HOME,
    SOS,
    SOS_SENT,
    EMERGENCY_CONTACTS,
    SAFE_ROUTE,
    CROWD_NETWORK,
    PROFILE
}

@Composable
fun SheShieldApp() {

    // logged in user stays logged in
    val auth = FirebaseAuth.getInstance()

    var current by remember {
        mutableStateOf(
            if (auth.currentUser != null)
                Screen.HOME
            else
                Screen.SPLASH
        )
    }



    when (current) {
        Screen.SPLASH -> SheShieldSplashScreen(
            onFinished = { current = Screen.LOGIN }
        )
        Screen.LOGIN -> LoginScreen(
            onCreateAccount = { current = Screen.REGISTER },
            onSignIn = { _, _ -> current = Screen.HOME }
        )
        Screen.REGISTER -> RegisterScreen(
            onSignIn = { current = Screen.LOGIN },
            onCreateAccount = { _, _, _, _ -> current = Screen.HOME }
        )
        Screen.HOME -> HomeScreen(
            onSOSTriggered = { current = Screen.SOS },
//            onNavigate = { label ->
//                when (label) {
//                    "Network"     -> current = Screen.CROWD_NETWORK
//                    "Safe Route"  -> current = Screen.SAFE_ROUTE
//                    "Quick Tools" -> current = Screen.QUICK_TOOLS
//                    "Contacts"    -> current = Screen.EMERGENCY_CONTACTS
//                }
//            }
            onNavigate = { screen ->
                current = screen
            }

        )
        Screen.SOS -> SOSScreen(
            onCancel   = { current = Screen.HOME },
            onAlertSent = { current = Screen.SOS_SENT }
        )
        Screen.SOS_SENT -> SOSSentScreen(
            onCancelAlert = { current = Screen.SOS }
        )
        Screen.EMERGENCY_CONTACTS -> EmergencyContactsScreen(
            onBack = { current = Screen.HOME }
        )

        Screen.SAFE_ROUTE -> SafeRouteScreen(
            onBack = { current = Screen.HOME }
        )

//        Screen.QUICK_TOOLS -> QuickToolsScreen(
//            onBack = { current = Screen.HOME }
//        )

        Screen.CROWD_NETWORK -> CrowdNetworkScreen(
            onNavigate = { current = Screen.HOME }
        )


        Screen.PROFILE -> ProfileScreen(
            onNavigateToContacts = {
                current = Screen.EMERGENCY_CONTACTS
            }
        )
    }
}
