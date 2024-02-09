package com.projecteugene.interval.compose

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projecteugene.interval.compose.timer.TimerScreen
import com.projecteugene.interval.ui.theme.IntervalTimerTheme

@Composable
fun IntervalTimerApp() {
    val navController = rememberNavController()
    IntervalTimerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            IntervalTimerHost(
                navController = navController
            )
        }
    }
}

@Composable
fun IntervalTimerHost(
    navController: NavHostController
) {
    val activity = (LocalContext.current as Activity)
    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            TimerScreen()
        }
    }
}