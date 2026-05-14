package com.example.womensafetyapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// --------------------------------------------------
// DARK THEME
// --------------------------------------------------

private val DarkColorScheme = darkColorScheme(

    primary = PurplePrimary,

    secondary = PinkPrimary,

    background = PurpleDark,

    surface = PurpleCard,

    surfaceVariant = PurpleCard,

    onPrimary = White,

    onSecondary = White,

    onBackground = White,

    onSurface = White
)

// --------------------------------------------------
// LIGHT THEME
// --------------------------------------------------

private val LightColorScheme = lightColorScheme(

    primary = PurplePrimary,

    secondary = PinkPrimary,

    background = LavenderBg,

    surface = White,

    surfaceVariant = LavenderBg,

    onPrimary = White,

    onSecondary = White,

    onBackground = TextDark,

    onSurface = TextDark
)

// --------------------------------------------------
// MAIN THEME
// --------------------------------------------------

@Composable
fun WomenSafetyAppTheme(

    darkTheme: Boolean = isSystemInDarkTheme(),

    dynamicColor: Boolean = false,

    content: @Composable () -> Unit
) {

    val colorScheme = when {

        dynamicColor &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {

            val context = LocalContext.current

            if (darkTheme)
                dynamicDarkColorScheme(context)
            else
                dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme

        else -> LightColorScheme
    }

    MaterialTheme(

        colorScheme = colorScheme,

        typography = Typography,

        content = content
    )
}