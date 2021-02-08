package com.example.stocksapp.ui.components.charts.line

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

object LineChartUtils {
    fun calculateDrawableArea(
        xAxisDrawableArea: Rect,
        yAxisDrawableArea: Rect,
        size: Size
    ) = Rect(
        left = yAxisDrawableArea.right,
        top = 0f,
        bottom = xAxisDrawableArea.top,
        right = size.width
    )

    // TODO size calculation should go into the drawer classes not here
    fun calculateXAxisDrawableArea(
        yAxisWidth: Float,
        labelHeight: Float,
        size: Size
    ): Rect {
        val top = size.height - labelHeight

        return Rect(
            left = yAxisWidth,
            top = top,
            bottom = size.height,
            right = size.width
        )
    }

    fun Density.calculateYAxisDrawableArea(
        xAxisLabelSize: Float,
        size: Size
    ): Rect {
        // Either 50dp or 10% of the chart width.
        val right = minOf(50.dp.toPx(), size.width * 10f / 100f)

        return Rect(
            left = 0f,
            top = 0f,
            bottom = size.height - xAxisLabelSize,
            right = right
        )
    }

    fun calculatePointLocation(
        drawableArea: Rect,
        lineChartData: LineChartData,
        point: LineChartData.Point,
        index: Int
    ): Offset {
        val x = (index.toFloat() / (lineChartData.points.size - 1))

        val range = lineChartData.maxYValue - lineChartData.minYValue
        val y = (point.value - lineChartData.minYValue) / range

        return Offset(
            x = (x * drawableArea.width) + drawableArea.left,
            y = drawableArea.height - (y * drawableArea.height)
        )
    }

    fun withProgress(
        index: Int,
        lineChartData: LineChartData,
        transitionProgress: Float,
        showWithProgress: (progress: Float) -> Unit
    ) {
        val nextToReachIndex = (lineChartData.points.size * transitionProgress).toInt() + 1

        if (index == nextToReachIndex) {
            // It's the next to reach
            val perIndex = 1f / lineChartData.points.size
            val down = (index - 1) * perIndex

            showWithProgress((transitionProgress - down) / perIndex)
        } else if (index < nextToReachIndex) {
            // Point has already been passed
            showWithProgress(1f)
        }
        // If it's beyond the next to reach don't show it
    }

    // TODO understand the progress system and move to [StraightLinePathCalculator]
    fun calculateLinePath(
        drawableArea: Rect,
        lineChartData: LineChartData,
        transitionProgress: Float
    ): Path = Path().apply {
        Log.d("TESTING CHARTS", "CALCULATING LINE PATH")
        var prevPointLocation: Offset? = null
        lineChartData.points.forEachIndexed { index, point ->
            withProgress(
                index = index,
                transitionProgress = transitionProgress,
                lineChartData = lineChartData
            ) { progress ->
                val pointLocation = calculatePointLocation(
                    drawableArea = drawableArea,
                    lineChartData = lineChartData,
                    point = point,
                    index = index
                )
                when {
                    index == 0 -> {
                        // First point
                        moveTo(pointLocation.x, pointLocation.y)
                    }
                    progress <= 1f -> {
                        // We have to change the `dy` based on the progress
                        val prevX = prevPointLocation!!.x
                        val prevY = prevPointLocation!!.y

                        val x = (pointLocation.x - prevX) * progress + prevX
                        val y = (pointLocation.y - prevY) * progress + prevY

                        lineTo(x, y)
                    }
                    else -> {
                        // Point has already been passed
                        lineTo(pointLocation.x, pointLocation.y)
                    }
                }
                prevPointLocation = pointLocation
            }
        }
    }
}