package com.projecteugene.interval.compose.timer

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.projecteugene.interval.data.TimerData
import com.projecteugene.interval.data.TimerUIState
import com.projecteugene.interval.ui.theme.IntervalTimerTheme
import com.projecteugene.interval.ui.theme.customColorsPalette
import com.projecteugene.interval.viewmodel.TimerViewModel
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = hiltViewModel(),
) {
    val timerUIState by viewModel.timerUIState.collectAsState()
    val timers by viewModel.timers.collectAsState(initial = emptyList())
    val isRepeated by viewModel.isRepeated.collectAsState(initial = false)
    TimerScreen(modifier,
        timers = timers,
        timerUIState = timerUIState,
        isRepeated = isRepeated,
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
        },
        onDismiss = {
            viewModel.onDismiss()
        },
        onAddTimer = {
            viewModel.addTimer(it)
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    timers: List<TimerData>,
    timerUIState: TimerUIState,
    isRepeated: Boolean,
    onTimerStart: () -> Unit,
    onTimerPause: () -> Unit,
    onTimerStop: () -> Unit,
    onClick: () -> Unit,
    onDelete: (TimerData) -> Unit,
    onDeleteAll: () -> Unit,
    onToggleRepeat: () -> Unit,
    onDismiss: () -> Unit,
    onAddTimer: (TimerData) -> Unit,
) {

    Scaffold(topBar = {
        TopAppBar(title = { Text("Interval Timer") }, actions = {
            if (timerUIState.elapsedTime > 0) {
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
                    text = timerUIState.elapsedTime.seconds.toComponents { hours, minutes, seconds, _ ->
                        "%02dh %02dm %02ds".format(hours, minutes, seconds)
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = modifier.padding(horizontal = 8.dp),
                )
                if (timerUIState.isRunning && timers.isNotEmpty()) {
                    IconButton(onClick = { onTimerPause() }) {
                        Icon(Icons.Default.PauseCircle, contentDescription = "Pause")
                    }
                } else if (!timerUIState.isRunning && timers.isNotEmpty()) {
                    IconButton(onClick = { onTimerStart() }) {
                        Icon(Icons.Default.PlayCircle, contentDescription = "Play")
                    }
                }
            }
            TimerList(
                modifier = modifier.weight(1f),
                timers = timers,
                currentTimer = timerUIState.currentTimer,
                onClick = onClick,
                onDelete = onDelete
            )
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onToggleRepeat() }, colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isRepeated) MaterialTheme.customColorsPalette.purpleButtonColor
                        else MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.customColorsPalette.purpleOutlineColor,
                    ), border = BorderStroke(
                        1.dp, MaterialTheme.customColorsPalette.purpleOutlineColor
                    ), modifier = modifier.padding(horizontal = 8.dp)
                ) {
                    Text("Repeat")
                }
                Spacer(modifier = modifier.weight(weight = 1f))
                Button(
                    onClick = { onDeleteAll() }, colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.customColorsPalette.redButtonColor
                    ), modifier = modifier.padding(horizontal = 8.dp)
                ) {
                    Text("Delete All")
                }
                Button(
                    onClick = { onClick() }, colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.customColorsPalette.blueButtonColor
                    )
                ) {
                    Text("Add")
                }
            }
        }
    })

    if (timerUIState.showDialog) {
        TimePickerDialog(onDismissRequest = {
            onDismiss()
        }, onConfirmation = {
            onAddTimer(it)
            onDismiss()
        })
    }
}


@Composable
fun TimerList(
    modifier: Modifier = Modifier,
    timers: List<TimerData>,
    currentTimer: Pair<Int?, Long?>,
    onClick: () -> Unit,
    onDelete: (TimerData) -> Unit
) {
    val composableScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.imePadding(), contentPadding = PaddingValues(
            horizontal = 16.dp, vertical = 16.dp
        ), state = listState
    ) {
        itemsIndexed(
            timers
        ) { index, time ->
            val isHighlighted = currentTimer.first == index
            val countDownTimer = if (isHighlighted) currentTimer.second else null
            TimerItemView(time, isHighlighted, countDownTimer, {
                onClick()
            }) {
                onDelete(time)
            }
        }
    }

    LaunchedEffect(currentTimer.first ?: 0) {
        composableScope.launch {
            listState.animateScrollToItem(currentTimer.first ?: 0)
        }
    }
}

@Preview(name = "Normal")
@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Tablet",device = "spec:width=1920dp,height=1080dp,dpi=160")
@Preview(name = "Tablet Dark mode",device = "spec:width=1920dp,height=1080dp,dpi=160", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TimerScreenPreview(
) {
    IntervalTimerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
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
                timerUIState = TimerUIState(
                    elapsedTime = 100L,
                ),
                isRepeated = true,
                onTimerStart = {},
                onTimerPause = {},
                onTimerStop = {},
                onClick = {},
                onDelete = {},
                onDeleteAll = {},
                onToggleRepeat = {},
                onDismiss = {},
                onAddTimer = {},
            )
        }
    }
}