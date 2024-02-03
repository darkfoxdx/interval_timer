package com.projecteugene.interval.compose.timer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.projecteugene.interval.data.TimerData
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerItemView(data: TimerData, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Row(Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(
                    text = data.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(16.dp, 16.dp, 16.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
                Text(
                    text = data.timeInSeconds.seconds.toComponents { hours, minutes, seconds, _ ->
                        "%02dh %02dm %02ds".format(hours, minutes, seconds)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(16.dp, 4.dp, 16.dp, 16.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
            Icon(
                Icons.Rounded.Delete,
                contentDescription = "Delete",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        onDelete()
                    }
            )
        }
    }
}

@Preview
@Composable
private fun TimerScreenPreview(
) {
    TimerItemView(TimerData("Test", 100L), {}, {})
}