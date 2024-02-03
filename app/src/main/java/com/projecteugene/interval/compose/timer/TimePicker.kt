package com.projecteugene.interval.compose.timer

import VerticalGrid
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.projecteugene.interval.data.TimerData

@Composable
fun TimerDismissButton(
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(50.dp)
            .wrapContentSize(align = Alignment.Center),  //avoid the oval shape
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.Blue),
        contentPadding = PaddingValues(0.dp),  //avoid the little icon
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFB4AB),
            contentColor = Color.Blue
        )
    ) {
        Icon(imageVector = Icons.Rounded.Close, contentDescription = "Confirm")
    }
}

@Composable
fun TimerConfirmButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(50.dp)
            .wrapContentSize(align = Alignment.Center),  //avoid the oval shape
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.Blue),
        contentPadding = PaddingValues(0.dp),  //avoid the little icon
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF8BDA67),
            contentColor = Color.Blue
        ),
        enabled = enabled
    ) {
        Icon(imageVector = Icons.Rounded.Check, contentDescription = "Confirm")
    }
}

@Composable
fun TimerPickerButton(
    onClick: (String) -> Unit,
    value: String
) {
    OutlinedButton(
        onClick = { onClick(value) },
        modifier = Modifier
            .size(50.dp)
            .wrapContentSize(align = Alignment.Center),  //avoid the oval shape
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.Blue),
        contentPadding = PaddingValues(0.dp),  //avoid the little icon
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Blue
        )
    ) {
        Text(
            text = value
        )
    }
}

@Composable
fun TimerPickerButton(
    onClick: () -> Unit,
    value: ImageVector,
    contentDescriptor: String = ""
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .size(50.dp)
            .wrapContentSize(align = Alignment.Center),  //avoid the oval shape
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.Blue),
        contentPadding = PaddingValues(0.dp),  //avoid the little icon
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Blue
        )
    ) {
        Icon(imageVector = value, contentDescription = contentDescriptor)
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (TimerData) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var input by remember { mutableStateOf("") }
    val maxCharacters = 100
    val maxLength = 6
    val hours = input.padStart(6, '0')
        .take(2)
    val minutes = input.padStart(6, '0')
        .takeLast(4)
        .take(2)
    val seconds = input.padStart(6, '0')
        .substring(4)
        .takeLast(2)
    val calculatedSeconds =
        (hours.toLong() * 3600) +
                (minutes.toLong() * 60) +
                seconds.toLong()
    Dialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            LazyColumn(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            if (it.length <= maxCharacters) {
                                name = it
                            }

                        },
                        modifier = Modifier.padding(16.dp, 16.dp, 16.dp),
                        label = { Text("Name") }
                    )
                    Text(
                        text = "${name.length}/$maxCharacters",
                        modifier = Modifier
                            .height(20.dp)
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.End)
                            .padding(end = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "%sh %sm %ss".format(
                            hours, minutes, seconds
                        ),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .wrapContentSize(Alignment.Center),
                    )
                    VerticalGrid(
                        columns = 3
                    ) {
                        TimerPickerButton(onClick = {
                            if (input.length < maxLength) {
                                input += it
                            }
                        }, value = "1")
                        TimerPickerButton(onClick = {
                            if (input.length < maxLength) {
                                input += it
                            }
                        }, value = "2")
                        TimerPickerButton(onClick = {
                            if (input.length < maxLength) {
                                input += it
                            }
                        }, value = "3")
                        TimerPickerButton(onClick = {
                            if (input.length < maxLength) {
                                input += it
                            }
                        }, value = "4")
                        TimerPickerButton(onClick = {
                            if (input.length < maxLength) {
                                input += it
                            }
                        }, value = "5")
                        TimerPickerButton(onClick = {
                            if (input.length < maxLength) {
                                input += it
                            }
                        }, value = "6")
                        TimerPickerButton(onClick = {
                            if (input.length < maxLength) {
                                input += it
                            }
                        }, value = "7")
                        TimerPickerButton(onClick = {
                            if (input.length < maxLength) {
                                input += it
                            }
                        }, value = "8")
                        TimerPickerButton(onClick = {
                            if (input.length < maxLength) {
                                input += it
                            }
                        }, value = "9")
                        TimerPickerButton(onClick = {
                            if (input.isNotEmpty() && input.length < maxLength - 1) {
                                input += it
                            } else if (input.isNotEmpty() && input.length < maxLength) {
                                input += "0"
                            }
                        }, value = "00")
                        TimerPickerButton(onClick = {
                            if (input.isNotEmpty() && input.length < maxLength) {
                                input += it
                            }
                        }, value = "0")
                        TimerPickerButton(onClick = {
                            input = input.dropLast(1)
                        }, value = Icons.Rounded.KeyboardArrowLeft)
                        TimerDismissButton(
                            onClick = onDismissRequest
                        )
                        Spacer(modifier = Modifier)
                        TimerConfirmButton(
                            enabled = name.isNotEmpty() && calculatedSeconds > 0,
                            onClick = {
                                val timerData = TimerData(
                                    name,
                                    calculatedSeconds
                                )
                                onConfirmation(timerData)
                            }
                        )
                    }
                }
            }

        }
    }
}


@Preview
@Composable
private fun TimerWidgetPreview() {
    TimePickerDialog(
        onDismissRequest = {},
        onConfirmation = {}
    )
}