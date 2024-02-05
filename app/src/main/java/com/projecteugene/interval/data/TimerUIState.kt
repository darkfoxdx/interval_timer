package com.projecteugene.interval.data

data class TimerUIState(
    val elapsedTime: Long = 0,
    val currentTimer: Pair<Int?, Long?> = Pair(null, null),
    val showDialog: Boolean = false,
    val isRunning: Boolean = false,
)
