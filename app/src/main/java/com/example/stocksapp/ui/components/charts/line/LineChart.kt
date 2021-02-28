package com.example.stocksapp.ui.components.charts.line

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
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
import timber.log.Timber

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
    neutralColor: Color = MaterialTheme.colors.onPrimary
) {
    var previousData by remember { mutableStateOf(lineChartData) }
    val transitionState = remember { MutableTransitionState(ChartState.Collapsed) }

    if (transitionState.currentState == ChartState.Collapsed) {
        Timber.d("Setting target: EXPANDED")
        transitionState.targetState = ChartState.Expanded
        previousData = lineChartData
    }

    if (previousData != lineChartData) {
        Timber.d("Setting target: COLLAPSED")
        transitionState.targetState = ChartState.Collapsed
    }

    val transition = updateTransition(transitionState)

    val transitionProgress by transition.animateFloat(
        transitionSpec = {
            if (ChartState.Collapsed isTransitioningTo ChartState.Expanded) {
                tween(1000, easing = LinearOutSlowInEasing)
            } else {
                tween(1000, easing = FastOutLinearInEasing)
            }
        }
    ) { state ->
        when (state) {
            ChartState.Collapsed -> 0f
            ChartState.Expanded -> 1f
        }
    }

    val color by transition.animateColor(
        transitionSpec = {
            if (ChartState.Collapsed isTransitioningTo ChartState.Expanded) {
                tween(1000, easing = LinearOutSlowInEasing)
            } else {
                tween(1000, easing = FastOutLinearInEasing)
            }
        }
    ) { state ->
        when (state) {
            ChartState.Collapsed -> neutralColor
            ChartState.Expanded -> {
                val first = lineChartData.points.firstOrNull()?.value ?: 0f
                val last = lineChartData.points.lastOrNull()?.value ?: 0f
                when {
                    last > first -> profitColor
                    last < first -> lossColor
                    else -> neutralColor
                }
            }
        }
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
                linePath = linePathCalculator.calculateLinePath(
                    drawableArea = chartDrawableArea,
                    data = if (transitionState.targetState == ChartState.Collapsed) {
                        previousData
                    } else {
                        lineChartData
                    },
                    transitionProgress = transitionProgress
                ),
                color = color
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

private enum class ChartState {
    Collapsed,
    Expanded
}
