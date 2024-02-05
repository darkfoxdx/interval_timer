package com.projecteugene.interval.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projecteugene.interval.data.TimerData
import com.projecteugene.interval.data.TimerDataRepository
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
    private val _elapsedTime = MutableStateFlow(0L)
    private val _isRunning = MutableStateFlow(false)
    private val _showDialog = MutableStateFlow(false)
    private val _currentTimer = MutableStateFlow<Pair<Int?, Long?>>(Pair(null, null))

    val timers = timerDataRepository.getTimers()
    val elapsedTime = _elapsedTime.asStateFlow()
    val isRunning = _isRunning.asStateFlow()
    val showDialog = _showDialog.asStateFlow()
    val isRepeated = dataStoreManager.isRepeatable()
    val currentTimer = _currentTimer.asStateFlow()

    private val _cumulativeTimer = timers.map { data ->
        data.map { it.timeInSeconds }.runningReduce { acc, timerData -> acc + timerData }
    }


    fun onToggleRepeat() {
        viewModelScope.launch {
            dataStoreManager.toggleRepeatable()
        }
    }

    fun onShowDialog() {
        _showDialog.update {
            true
        }
    }

    fun onDismiss() {
        _showDialog.update {
            false
        }
    }

    private var timerJob: Job? = null

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
        val elapsedTime = _elapsedTime.value

        if (!isRepeated.first() && elapsedTime > total) {
            stopTimer()
            return
        }

        val realignedTime =
            if (elapsedTime.toInt() % total.toInt() == 0) total else elapsedTime % total
        val cumulativeTimer = _cumulativeTimer.first()
        val position = cumulativeTimer.indexOfFirst { realignedTime <= it }
        val countDownTimer = cumulativeTimer[position] - realignedTime
        _currentTimer.update {
            it.copy(first = position, second = countDownTimer)
        }
        if (countDownTimer == 0L) {
            mediaPlayerHelper.start()
            vibratorHelper.vibrate()
        }

    }

    fun startTimer() {
        _isRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _elapsedTime.value++
                checkTime()
            }
        }
    }

    fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
    }

    fun stopTimer() {
        _isRunning.value = false
        _currentTimer.value = Pair(null, null)
        _elapsedTime.value = 0
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        mediaPlayerHelper.release()
    }
}