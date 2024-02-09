package com.projecteugene.interval.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val MaterialTheme.customColorsPalette: CustomColorsPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColorsPalette.current

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Immutable
data class CustomColorsPalette(
    val redButtonColor: Color = RedButtonColor,
    val greenButtonColor: Color = GreenButtonColor,
    val blueButtonColor: Color = BlueButtonColor,
    val purpleButtonColor: Color = LightPurpleButtonColor,
    val purpleOutlineColor: Color = LightPurpleOutlineColor,
    val calculatorTextColor: Color = LightCalculatorTextColor,
)

val RedButtonColor = Color(color = 0xFFEF5350)
val GreenButtonColor = Color(color = 0xFF00C853)
val BlueButtonColor = Color(color = 0xFF2196F3)

val LightPurpleButtonColor = Color(color = 0xFFF3E5F5)
val LightPurpleOutlineColor = Color(color = 0xFF6A1B9A)
val LightCalculatorTextColor = Color(color = 0xFF4E616C)

val DarkPurpleButtonColor = Color(color = 0xFF9C27B0)
val DarkPurpleOutlineColor = Color(color = 0xFFE1BEE7)
val DarkCalculatorTextColor = Color(color = 0xFF35464F)

val LightCustomColorsPalette = CustomColorsPalette()

val DarkCustomColorsPalette = CustomColorsPalette(
    purpleButtonColor = DarkPurpleButtonColor,
    purpleOutlineColor = DarkPurpleOutlineColor,
    calculatorTextColor = DarkCalculatorTextColor
)

val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }

@Composable
fun IntervalTimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // logic for which custom palette to use
    val customColorsPalette =
        if (darkTheme) DarkCustomColorsPalette
        else LightCustomColorsPalette

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    CompositionLocalProvider(
        LocalCustomColorsPalette provides customColorsPalette // our custom palette
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }

}