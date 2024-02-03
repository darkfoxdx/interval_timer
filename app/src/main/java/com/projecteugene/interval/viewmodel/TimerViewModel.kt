package com.projecteugene.interval.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _timers = MutableStateFlow<List<TimerData>>(listOf())
    private val _elapsedTime = MutableStateFlow(0L)
    private val _showDialog = MutableStateFlow(false)

    val timers = _timers.asStateFlow()
    val elapsedTime = _elapsedTime.asStateFlow()
    val showDialog = _showDialog.asStateFlow()

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
        _timers.update {list ->
            list.plus(newTimerData)
        }
    }

    fun removeTimer(position: Int) {
        _timers.update {list ->
            list.filterIndexed { index, _ -> index != position}
        }
    }

    fun removeAllTimer() {
        _timers.update {
            emptyList()
        }
    }

    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _elapsedTime.value++
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
    }

    fun stopTimer() {
        _elapsedTime.value = 0
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}