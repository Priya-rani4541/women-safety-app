package com.example.womensafetyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SheShieldApp() }
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
    QUICK_TOOLS,
    CROWD_NETWORK,
    PROFILE
}

@Composable
fun SheShieldApp() {
    var current by remember { mutableStateOf(Screen.SPLASH) }

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
            onNavigate = { label ->
                when (label) {
                    "Network"     -> current = Screen.CROWD_NETWORK
                    "Safe Route"  -> current = Screen.SAFE_ROUTE
                    "Quick Tools" -> current = Screen.QUICK_TOOLS
                    "Contacts"    -> current = Screen.EMERGENCY_CONTACTS
                }
            }
        )
        Screen.SOS -> SOSScreen(
            onCancel   = { current = Screen.HOME },
            onAlertSent = { current = Screen.SOS_SENT }
        )
        Screen.SOS_SENT -> SOSSentScreen(
            onCancelAlert = { current = Screen.HOME }
        )
        Screen.EMERGENCY_CONTACTS -> EmergencyContactsScreen(
            onBack = { current = Screen.HOME }
        )
        Screen.SAFE_ROUTE -> SafeRouteScreen(
            onBack = { current = Screen.HOME }
        )
        Screen.QUICK_TOOLS -> QuickToolsScreen(
            onBack = { current = Screen.HOME }
        )
        Screen.CROWD_NETWORK -> CrowdNetworkScreen(
//            onBack = { current = Screen.HOME }
            onNavigate = { current = Screen.HOME }
        )
        Screen.PROFILE -> ProfileScreen(
            onBack = { current = Screen.HOME }
        )
    }
}
