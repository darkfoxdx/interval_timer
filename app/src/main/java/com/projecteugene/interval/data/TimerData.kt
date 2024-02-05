package com.projecteugene.interval.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TimerData(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "time_in_seconds") val timeInSeconds: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun toString() = name
}
