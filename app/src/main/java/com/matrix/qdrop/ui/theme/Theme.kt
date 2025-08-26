package com.matrix.qdrop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat

val JetBlack = Color(0xFF000000)
val VibrantBlue = Color(0xFF40C4FF)
val NeonPurple = Color(0xFFB388FF)
val ElectricPink = Color(0xFFFF4081)
val SuperGreen = Color(0xFF00B403)
val DarkGraySurface = Color(0xFF1E1E1E)
val DeepSea = Color(0xFF0743A9)
val TextPrimary = Color(0xFFE0E0E0)

private val IntelliJDarkColorScheme = darkColorScheme(
    primary = VibrantBlue,
    secondary = NeonPurple,
    tertiary = ElectricPink,
    background = JetBlack,
    surface = DarkGraySurface,
    onPrimary = JetBlack,
    onSecondary = JetBlack,
    onTertiary = JetBlack,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun QDropTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> IntelliJDarkColorScheme
        else -> IntelliJDarkColorScheme
    }

    SetStatusBarDarkIcons(false)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = InterTypography,
        content = content
    )
}

@Composable
fun SetStatusBarDarkIcons(enabled: Boolean) {
    val view = LocalView.current
    val activity = LocalContext.current as Activity

    SideEffect {
        val window = activity.window
        WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = enabled
    }
}

val InterTypography = Typography(
    displayLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 57.sp),
    displayMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 45.sp),
    displaySmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 36.sp),
    headlineLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 28.sp),
    headlineSmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    bodyLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 11.sp)
)

