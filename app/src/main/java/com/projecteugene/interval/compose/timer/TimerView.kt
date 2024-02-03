package com.projecteugene.interval.compose.timer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.projecteugene.interval.ui.theme.FixedColor

@Composable
fun TimerWidget(
    modifier: Modifier = Modifier,
    times: List<Long>,
    elapsedTime: Long,
) {
    Canvas(modifier = modifier) {
        val total = times.sum()
        val percentages = times.map { it * 1f / total }
        var angle = -90f
        val fullAngle = 360

        percentages.forEachIndexed { i, p ->
            val sweepAngle = p * fullAngle
            drawArc(
                color = FixedColor[i % FixedColor.size],
                startAngle = angle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 30f)
            )
            angle += sweepAngle

        }
        if (total != 0L) {
            val elapsedPercentage = elapsedTime % total * 1f / total
            val elapsedStartAngle = angle + (elapsedPercentage * fullAngle)
            drawArc(
                color = Color.Black,
                startAngle = elapsedStartAngle,
                sweepAngle = 1f,
                useCenter = true,
                style = Stroke(width = 30f)
            )
        }
    }
}

@Preview
@Composable
private fun TimerWidgetPreview(

) {
    TimerWidget(
        modifier = Modifier.size(350.dp, 350.dp),
        times = listOf(100L, 200L,300L),
        elapsedTime = 100L
    )
}