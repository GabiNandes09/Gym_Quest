package com.rogue.gymquest.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.rogue.gymquest.presentation.viewmodel.states.ChartPoint

@Composable
fun WeightTrendChart(points: List<ChartPoint>, modifier: Modifier = Modifier) {
    if (points.size < 2) {
        Box(modifier = modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
            Text(
                "Registre mais sessões para ver a evolução de carga aqui.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val minWeight = points.minOf { it.weight }
    val maxWeight = points.maxOf { it.weight }
    val range = (maxWeight - minWeight).takeIf { it > 0 } ?: 1.0
    val lineColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.fillMaxWidth().height(160.dp)) {
        val stepX = if (points.size > 1) size.width / (points.size - 1) else 0f
        val padding = 12f
        val drawableHeight = size.height - padding * 2

        val coordinates = points.mapIndexed { index, point ->
            val x = index * stepX
            val normalized = ((point.weight - minWeight) / range).toFloat()
            val y = padding + drawableHeight - (normalized * drawableHeight)
            Offset(x, y)
        }

        for (i in 0 until coordinates.size - 1) {
            drawLine(
                color = lineColor,
                start = coordinates[i],
                end = coordinates[i + 1],
                strokeWidth = 5f
            )
        }
        coordinates.forEach { offset ->
            drawCircle(color = lineColor, radius = 7f, center = offset)
        }
    }
}
