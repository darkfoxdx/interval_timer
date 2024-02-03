package com.projecteugene.interval.compose

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projecteugene.interval.compose.timer.TimerScreen

@Composable
fun IntervalTimerApp() {
    val navController = rememberNavController()
    IntervalTimerHost(
        navController = navController
    )
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