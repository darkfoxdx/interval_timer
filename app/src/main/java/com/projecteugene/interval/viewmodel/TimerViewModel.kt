package com.projecteugene.interval.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projecteugene.interval.data.TimerData
import com.projecteugene.interval.data.TimerDataRepository
import com.projecteugene.interval.data.TimerUIState
import com.projecteugene.interval.utilities.DataStoreManager
import com.projecteugene.interval.utilities.MediaPlayerHelper
import com.projecteugene.interval.utilities.VibratorHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val vibratorHelper: VibratorHelper,
    private val mediaPlayerHelper: MediaPlayerHelper,
    private val timerDataRepository: TimerDataRepository
) : ViewModel() {
    private val _timerUIState = MutableStateFlow(TimerUIState())
    private var timerJob: Job? = null

    val timers = timerDataRepository.getTimers()
    val isRepeated = dataStoreManager.isRepeatable()
    val timerUIState = _timerUIState.asStateFlow()

    private val _cumulativeTimer = timers.map { data ->
        data.map { it.timeInSeconds }.runningReduce { acc, timerData -> acc + timerData }
    }

    fun onToggleRepeat() {
        viewModelScope.launch {
            dataStoreManager.toggleRepeatable()
        }
    }

    fun onShowDialog() {
        _timerUIState.update {
            it.copy(showDialog = true)
        }
    }

    fun onDismiss() {
        _timerUIState.update {
            it.copy(showDialog = false)
        }
    }

    fun addTimer(newTimerData: TimerData) {
        viewModelScope.launch {
            timerDataRepository.insertTimerData(newTimerData)
        }
    }

    fun removeTimer(timerData: TimerData) {
        viewModelScope.launch {
            timerDataRepository.removeTimerData(timerData)
        }
    }

    fun removeAllTimer() {
        viewModelScope.launch {
            timerDataRepository.removeAll()
        }
    }

    private suspend fun checkTime() {
        val total = timers.first().sumOf { it.timeInSeconds }
        val elapsedTime = _timerUIState.value.elapsedTime

        if (!isRepeated.first() && elapsedTime > total) {
            stopTimer()
            return
        }

        val realignedTime =
            if (elapsedTime.toInt() % total.toInt() == 0) total else elapsedTime % total
        val cumulativeTimer = _cumulativeTimer.first()
        val position = cumulativeTimer.indexOfFirst { realignedTime <= it }
        val countDownTimer = cumulativeTimer[position] - realignedTime
        _timerUIState.update {
            it.copy(currentTimer = Pair(first = position, second = countDownTimer))
        }
        if (countDownTimer == 0L) {
            mediaPlayerHelper.start()
            vibratorHelper.vibrate()
        }

    }

    fun startTimer() {
        _timerUIState.update {
            it.copy(isRunning = true)
        }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _timerUIState.update {
                    it.copy(elapsedTime = it.elapsedTime + 1)
                }
                checkTime()
            }
        }
    }

    fun pauseTimer() {
        _timerUIState.update {
            it.copy(isRunning = false)
        }
        timerJob?.cancel()
    }

    fun stopTimer() {
        _timerUIState.update {
            it.copy(isRunning = false, currentTimer = Pair(null, null), elapsedTime = 0)
        }
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        mediaPlayerHelper.release()
    }
}