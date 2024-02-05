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
    val extraColor1: Color = LightExtraColor1,
    val extraColor2: Color = LightExtraColor2,
    val extraColor3: Color = LightExtraColor3
)

val LightExtraColor1 = Color(color = 0xFF29B6F6)
val LightExtraColor2 = Color(color = 0xFF26A69A)
val LightExtraColor3 = Color(color = 0xFFEF5350)

val DarkExtraColor1 = Color(color = 0xFF0277BD)
val DarkExtraColor2 = Color(color = 0xFF00695C)
val DarkExtraColor3 = Color(color = 0xFFC62828)

val LightCustomColorsPalette = CustomColorsPalette()

val DarkCustomColorsPalette = CustomColorsPalette(
    extraColor1 = DarkExtraColor1,
    extraColor2 = DarkExtraColor2,
    extraColor3 = DarkExtraColor3
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