package com.example.stocksapp.ui.components.charts.line

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.lerp
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

private enum class AnimationState {
    Collapsed,
    Expanded
}

private fun <T> spec() = spring<T>(stiffness = 100f)

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
    // Used to represent the data currently being charted
    var visibleData by remember { mutableStateOf(lineChartData) }

    val transitionState = remember { MutableTransitionState(AnimationState.Collapsed) }

    // Bounce back
    if (transitionState.currentState == AnimationState.Collapsed
        && transitionState.targetState == AnimationState.Collapsed
        && transitionState.isIdle
    ) {
        transitionState.targetState = AnimationState.Expanded
        visibleData = lineChartData
    }

    // Collapse line
    if (lineChartData.points != visibleData.points) {
        SideEffect {
            transitionState.targetState = AnimationState.Collapsed
        }
    }

    val transition = updateTransition(transitionState, label = "")

    val lineColor by transition.animateColor(transitionSpec = { spec() }, label = "") { animationState ->
        when (animationState) {
            AnimationState.Expanded -> {
                when {
                    visibleData.points.last().value > visibleData.points.first().value -> profitColor
                    visibleData.points.last().value < visibleData.points.first().value -> lossColor
                    else -> neutralColor
                }
            }
            AnimationState.Collapsed -> when {
                visibleData.points.last().value > visibleData.points.first().value -> lerp(
                    profitColor,
                    neutralColor,
                    0.5f
                )
                visibleData.points.last().value < visibleData.points.first().value -> lerp(
                    lossColor,
                    neutralColor,
                    0.5f
                )
                else -> neutralColor
            }
        }
    }

    val lineLength by transition.animateFloat(transitionSpec = { spec() }, label = "") { animationState ->
        when (animationState) {
            AnimationState.Expanded -> 1f
            AnimationState.Collapsed -> 0f
        }
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
                    lineLength = lineLength
                ),
                color = lineColor
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
}
