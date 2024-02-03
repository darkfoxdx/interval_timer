package com.projecteugene.interval.compose.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    TimerScreen(
        modifier,
        timers = timers,
        elapsedTime = elapsedTime,
        onTimerStart = {
            viewModel.startTimer()
        },
        onClick = {
            viewModel.onShowDialog()
        },
        onDelete = {
            viewModel.removeTimer(it)
        },
        onDeleteAll = {
            viewModel.removeAllTimer()
        }
    )

    if (showDialog) {
        TimePickerDialog(
            onDismissRequest = {
               viewModel.onDismiss()
            },
            onConfirmation = {
                viewModel.addTimer(it)
                viewModel.onDismiss()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    timers: List<TimerData>,
    elapsedTime: Long,
    onTimerStart: () -> Unit,
    onClick: () -> Unit,
    onDelete: (Int) -> Unit,
    onDeleteAll: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Title") })
        },
        content = {
            Column(
                modifier = modifier.padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = elapsedTime.seconds.toComponents { hours, minutes, seconds, _ ->
                        "%02dh %02dm %02ds".format(hours, minutes, seconds)
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .wrapContentSize(Alignment.Center),
                )
                IconButton(onClick = { onTimerStart() }) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = "Play")
                }
                TimerList(
                    timers = timers,
                    onClick = onClick,
                    onDelete = onDelete
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onDeleteAll() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        modifier = modifier.padding(horizontal = 8.dp)) {
                        Text("Delete All")
                    }
                    Button(onClick = { onClick() }) {
                        Text("Add")
                    }
                }
            }
        }
    )
}

@Composable
fun TimerList(
    modifier: Modifier = Modifier,
    timers: List<TimerData>,
    onClick: () -> Unit,
    onDelete: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.imePadding(),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 16.dp
        )
    ) {
        itemsIndexed(
            timers
        ) { index, time ->
            TimerItemView(time, {
                onClick()
            }, {
                onDelete(index)
            })
        }
    }
}

@Preview
@Composable
private fun TimerScreenPreview(
) {
    TimerScreen(
        timers = listOf(TimerData("Test 1", 100L), TimerData("Test 2", 200L)),
        elapsedTime = 100L,
        onTimerStart = {},
        onClick = {},
        onDelete = {},
        onDeleteAll = {},
    )
}