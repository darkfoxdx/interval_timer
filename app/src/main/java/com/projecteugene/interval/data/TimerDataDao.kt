package com.projecteugene.interval.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDataDao {
    @Query("SELECT * FROM timerData")
    fun getTimers(): Flow<List<TimerData>>

    @Insert
    suspend fun insertTimerData(timerData: TimerData): Long

    @Delete
    suspend fun deleteTimerData(timerData: TimerData)

    @Query("DELETE FROM timerData")
    suspend fun deleteAll()
}
