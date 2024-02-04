package com.projecteugene.interval.viewmodel

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.projecteugene.interval.R
import com.projecteugene.interval.data.TimerData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for plant list.
 */
@HiltViewModel
class TimerViewModel @Inject internal constructor(
    application: Application
) : AndroidViewModel(application) {
    private val _timers = MutableStateFlow<List<TimerData>>(listOf())
    private val _cumulativeTimer = MutableStateFlow<List<Long>>(listOf())
    private val _elapsedTime = MutableStateFlow(0L)
    private val _isRunning = MutableStateFlow(false)
    private val _showDialog = MutableStateFlow(false)
    private val _isRepeated = MutableStateFlow(false)
    private val _currentTimer = MutableStateFlow<Pair<Int?, Long?>>(Pair(null, null))

    val timers = _timers.asStateFlow()
    val elapsedTime = _elapsedTime.asStateFlow()
    val isRunning = _isRunning.asStateFlow()
    val showDialog = _showDialog.asStateFlow()
    val isRepeated = _isRepeated.asStateFlow()
    val currentTimer = _currentTimer.asStateFlow()

    private var mp: MediaPlayer = MediaPlayer.create(application.applicationContext, R.raw.tone1)

    fun onToggleRepeat() {
        _isRepeated.update {
            !it
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

    private fun updateCumulativeList() {
        _cumulativeTimer.update {
            _timers.value
                .map { it.timeInSeconds }
                .runningReduce { acc, timerData -> acc + timerData }
        }
    }

    fun addTimer(newTimerData: TimerData) {
        _timers.update {list ->
            list.plus(newTimerData)
        }
        updateCumulativeList()
    }

    fun removeTimer(position: Int) {
        _timers.update {list ->
            list.filterIndexed { index, _ -> index != position}
        }
        updateCumulativeList()
    }

    fun removeAllTimer() {
        _timers.update {
            emptyList()
        }
    }

    private fun checkTime() {
        val elapsedTime = _elapsedTime.value
        val total = _timers.value.sumOf { it.timeInSeconds }
        val realignedTime = if (elapsedTime.toInt() % total.toInt() == 0) total else elapsedTime % total
        val cumulativeTimer = _cumulativeTimer.value
        val position = cumulativeTimer.indexOfFirst { realignedTime <= it }
        val countDownTimer = cumulativeTimer[position] - realignedTime
        _currentTimer.update {
            it.copy(first = position, second = countDownTimer)
        }
        if (countDownTimer == 0L) {
            mp.start()
        }

        if (!_isRepeated.value && elapsedTime > total) {
            stopTimer()
            return
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
        mp.release()
    }
}