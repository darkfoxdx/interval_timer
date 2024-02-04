package com.projecteugene.interval.compose.timer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.projecteugene.interval.data.TimerData
import com.projecteugene.interval.viewmodel.TimerViewModel
import kotlin.time.Duration.Companion.seconds

@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = hiltViewModel(),
) {
    val timers by viewModel.timers.collectAsState()
    val currentTimer by viewModel.currentTimer.collectAsState()
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val isRepeated by viewModel.isRepeated.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    TimerScreen(modifier,
        timers = timers,
        currentTimer = currentTimer,
        elapsedTime = elapsedTime,
        isRepeated = isRepeated,
        isRunning = isRunning,
        onTimerStart = {
            viewModel.startTimer()
        },
        onTimerPause = {
            viewModel.pauseTimer()
        },
        onTimerStop = {
            viewModel.stopTimer()
        },
        onClick = {
            viewModel.onShowDialog()
        },
        onDelete = {
            viewModel.removeTimer(it)
        },
        onDeleteAll = {
            viewModel.removeAllTimer()
        },
        onToggleRepeat = {
            viewModel.onToggleRepeat()
        })

    if (showDialog) {
        TimePickerDialog(onDismissRequest = {
            viewModel.onDismiss()
        }, onConfirmation = {
            viewModel.addTimer(it)
            viewModel.onDismiss()
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    timers: List<TimerData>,
    currentTimer: Pair<Int?, Long?>,
    isRepeated: Boolean,
    isRunning: Boolean,
    elapsedTime: Long,
    onTimerStart: () -> Unit,
    onTimerPause: () -> Unit,
    onTimerStop: () -> Unit,
    onClick: () -> Unit,
    onDelete: (Int) -> Unit,
    onDeleteAll: () -> Unit,
    onToggleRepeat: () -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Interval Timer") }, actions = {
            if (elapsedTime > 0) {
                TextButton(onClick = { onTimerStop() }) {
                    Text(text = "Reset")
                }
            }
        })
    }, content = {
        Column(
            modifier = modifier.padding(it), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Elapsed Time",
                style = MaterialTheme.typography.bodySmall,
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = elapsedTime.seconds.toComponents { hours, minutes, seconds, _ ->
                        "%02dh %02dm %02ds".format(hours, minutes, seconds)
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = modifier.padding(horizontal = 8.dp),
                )
                if (isRunning && timers.isNotEmpty()) {
                    IconButton(onClick = { onTimerPause() }) {
                        Icon(Icons.Default.PauseCircle, contentDescription = "Pause")
                    }
                } else if (!isRunning && timers.isNotEmpty()) {
                    IconButton(onClick = { onTimerStart() }) {
                        Icon(Icons.Default.PlayCircle, contentDescription = "Play")
                    }
                }
            }
            TimerList(
                modifier = modifier.weight(1f),
                timers = timers, currentTimer = currentTimer,
                onClick = onClick, onDelete = onDelete
            )
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onToggleRepeat() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isRepeated) Color.Cyan else Color.White
                    ),
                    border = BorderStroke(1.dp, Color.Blue),
                    modifier = modifier.padding(horizontal = 8.dp)
                ) {
                    Text("Repeat")
                }
                Spacer(modifier = modifier.weight(weight = 1f))
                Button(
                    onClick = { onDeleteAll() }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ), modifier = modifier.padding(horizontal = 8.dp)
                ) {
                    Text("Delete All")
                }
                Button(onClick = { onClick() }) {
                    Text("Add")
                }
            }
        }
    })
}

@Composable
fun TimerList(
    modifier: Modifier = Modifier,
    timers: List<TimerData>,
    currentTimer: Pair<Int?, Long?>,
    onClick: () -> Unit,
    onDelete: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.imePadding(), contentPadding = PaddingValues(
            horizontal = 16.dp, vertical = 16.dp
        )
    ) {
        itemsIndexed(
            timers
        ) { index, time ->
            val isHighlighted = currentTimer.first == index
            val countDownTimer = if (isHighlighted) currentTimer.second else null
            TimerItemView(time, isHighlighted, countDownTimer, {
                onClick()
            }) {
                onDelete(index)
            }
        }
    }
}

@Preview
@Composable
private fun TimerScreenPreview(
) {
    TimerScreen(
        timers = listOf(
            TimerData("Test 1", 100L),
            TimerData("Test 2", 200L),
            TimerData("Test 2", 200L),
            TimerData("Test 2", 200L),
            TimerData("Test 2", 200L),
            TimerData("Test 2", 200L),
            TimerData("Test 2", 200L),
        ),
        currentTimer = Pair(null, null),
        isRepeated = true,
        isRunning = false,
        elapsedTime = 100L,
        onTimerStart = {},
        onTimerPause = {},
        onTimerStop = {},
        onClick = {},
        onDelete = {},
        onDeleteAll = {},
        onToggleRepeat = {},
    )
}