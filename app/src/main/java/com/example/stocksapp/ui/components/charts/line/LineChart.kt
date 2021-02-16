package com.example.stocksapp.ui.components.charts.line

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import com.example.stocksapp.ui.components.charts.line.renderer.line.LineDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.line.SolidLineDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.path.LinePathCalculator
import com.example.stocksapp.ui.components.charts.line.renderer.path.StraightLinePathCalculator
import com.example.stocksapp.ui.components.charts.line.renderer.xaxis.SimpleXAxisDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.xaxis.XAxisDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.yaxis.SimpleYAxisDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.yaxis.YAxisDrawer

@Composable
fun LineChart(
    lineChartData: LineChartData,
    modifier: Modifier = Modifier,
    animation: AnimationSpec<Float> = TweenSpec(
        durationMillis = 2000,
        easing = LinearOutSlowInEasing
    ),
    linePathCalculator: LinePathCalculator = StraightLinePathCalculator(),
    lineDrawer: LineDrawer = SolidLineDrawer(),
    xAxisDrawer: XAxisDrawer = SimpleXAxisDrawer(),
    yAxisDrawer: YAxisDrawer = SimpleYAxisDrawer()
) {
    val transitionProgress = remember(lineChartData.points) { Animatable(initialValue = 0f) }
    LaunchedEffect(lineChartData.points) {
        transitionProgress.snapTo(0f)
        transitionProgress.animateTo(1f, animationSpec = animation)
    }

    Canvas(modifier = modifier.fillMaxSize()) {
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
                canvas = canvas,
                linePath = linePathCalculator.calculateLinePath(
                    drawableArea = chartDrawableArea,
                    data = lineChartData,
                    transitionProgress = transitionProgress.value
                )
            )
            xAxisDrawer.draw(
                drawScope = this,
                canvas = canvas,
                drawableArea = xAxisDrawableArea,
                labels = lineChartData.points.map { it.label }
            )
            yAxisDrawer.draw(
                drawScope = this,
                canvas = canvas,
                drawableArea = yAxisDrawableArea,
                minValue = lineChartData.minYValue,
                maxValue = lineChartData.maxYValue
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
}
