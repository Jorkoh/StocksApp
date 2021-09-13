package com.example.stocksapp.ui.components.charts.line

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import com.example.stocksapp.ui.components.charts.line.LineChartUtils.interpolateLineChartData
import com.example.stocksapp.ui.components.charts.line.renderer.line.LineDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.line.SolidLineDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.path.LinePathCalculator
import com.example.stocksapp.ui.components.charts.line.renderer.path.StraightLinePathCalculator
import com.example.stocksapp.ui.components.charts.line.renderer.xaxis.SimpleXAxisDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.xaxis.XAxisDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.yaxis.SimpleYAxisDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.yaxis.YAxisDrawer
import com.example.stocksapp.ui.theme.loss
import com.example.stocksapp.ui.theme.profit

private fun <T> spec() = spring<T>(stiffness = Spring.StiffnessVeryLow)

@Composable
fun LineChart(
    lineChartData: LineChartData,
    modifier: Modifier = Modifier,
    linePathCalculator: LinePathCalculator = StraightLinePathCalculator(),
    lineDrawer: LineDrawer = SolidLineDrawer(),
    xAxisDrawer: XAxisDrawer = SimpleXAxisDrawer(),
    yAxisDrawer: YAxisDrawer = SimpleYAxisDrawer(),
    profitColor: Color = MaterialTheme.colors.profit,
    lossColor: Color = MaterialTheme.colors.loss,
    neutralColor: Color = MaterialTheme.colors.onPrimary,
) {
    // Used to represent the data to transition from while animating between different data
    var oldData by remember { mutableStateOf(lineChartData) }
    // Used to represent the data to transition towards while animating between different data
    var targetData by remember { mutableStateOf(lineChartData) }
    // Used to represent the data currently being charted
    var visibleData by remember { mutableStateOf(lineChartData) }

    val lineColor = remember { Animatable(neutralColor) }
    val lineLength = remember { Animatable(0f) }
    val interpolationProgress = remember { Animatable(1f) }

    // Controls the color
    LaunchedEffect(lineChartData.points) {
        lineColor.animateTo(
            targetValue = when {
                lineChartData.points.last().value > lineChartData.points.first().value -> profitColor
                lineChartData.points.last().value < lineChartData.points.first().value -> lossColor
                else -> neutralColor
            }, animationSpec = spec()
        )
    }

    // Controls the length
    LaunchedEffect(lineChartData.points) {
        if (visibleData.points.size != lineChartData.points.size) {
            // Different amount of points, animate length to 0
            lineLength.animateTo(0f, animationSpec = spec())
        } else if (lineLength.value != lineLength.targetValue) {
            // Animation was interrupted, continue it
            lineLength.animateTo(lineLength.targetValue, animationSpec = spec())
        }

        if (lineLength.value == 0f && lineLength.targetValue == 0f) {
            // Bounce back to 1
            visibleData = lineChartData
            lineLength.animateTo(1f, animationSpec = spec())
        }
    }

    // Controls the interpolation
    LaunchedEffect(lineChartData.points) {
        if (visibleData.points.size == lineChartData.points.size) {
            // Same amount of points, animate between them
            oldData = visibleData
            targetData = lineChartData
            interpolationProgress.snapTo(0f)
            interpolationProgress.animateTo(1f, animationSpec = spec())
        } else if (interpolationProgress.value != interpolationProgress.targetValue) {
            // Animation was interrupted, continue it
            interpolationProgress.animateTo(
                interpolationProgress.targetValue,
                animationSpec = spec()
            )
        }
    }

    if (interpolationProgress.value != 1f) {
        visibleData = interpolateLineChartData(oldData, targetData, interpolationProgress.value)
    }

    Canvas(modifier = modifier) {
        drawIntoCanvas { canvas ->
            // Measure stuff
            val yAxisWidth = yAxisDrawer.calculateWidth(this)
            val xAxisHeight = xAxisDrawer.calculateHeight(this)
            val xAxisDrawableArea = Rect(
                left = yAxisWidth,
                top = size.height - xAxisHeight,
                right = size.width,
                bottom = size.height
            )
            val yAxisDrawableArea = Rect(
                left = 0f,
                top = 0f,
                right = yAxisWidth,
                bottom = size.height - xAxisHeight
            )
            val chartDrawableArea = Rect(
                left = yAxisWidth,
                top = 0f,
                right = size.width,
                bottom = size.height - xAxisHeight
            )

            // Draw stuff
            lineDrawer.drawLine(
                drawScope = this,
                linePath = linePathCalculator.calculateLinePath(
                    drawableArea = chartDrawableArea,
                    data = visibleData,
                    lineLength = lineLength.value
                ),
                color = lineColor.value
            )
            xAxisDrawer.draw(
                drawScope = this,
                canvas = canvas,
                drawableArea = xAxisDrawableArea,
                labels = visibleData.points.map { it.label }
            )
            yAxisDrawer.draw(
                drawScope = this,
                canvas = canvas,
                drawableArea = yAxisDrawableArea,
                minValue = visibleData.minYValue,
                maxValue = visibleData.maxYValue
            )
        }
    }
}

object LineChartUtils {
    fun calculatePointLocation(
        drawableArea: Rect,
        lineChartData: LineChartData,
        point: LineChartData.Point,
        index: Int
    ): Offset {
        val x = index / (lineChartData.points.size - 1f)

        val range = lineChartData.maxYValue - lineChartData.minYValue
        val y = (point.value - lineChartData.minYValue) / range

        return Offset(
            x = x * drawableArea.width + drawableArea.left,
            y = drawableArea.height - y * drawableArea.height,
        )
    }

    fun interpolateLineChartData(
        oldData: LineChartData,
        newData: LineChartData,
        transitionProgress: Float
    ) = LineChartData(
        newData.points.mapIndexed { index, newPoint ->
            val oldPoint = oldData.points[index]
            LineChartData.Point(
                value = oldPoint.value + (newPoint.value - oldPoint.value) * transitionProgress,
                label = newPoint.label
            )
        }
    )
}
