package com.projecteugene.interval.data

data class TimerData(
    val name: String,
    val timeInSeconds: Long,
) {
    override fun toString() = name
}
