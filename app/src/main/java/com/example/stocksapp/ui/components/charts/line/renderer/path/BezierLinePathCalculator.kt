package com.example.stocksapp.ui.components.charts.line.renderer.path

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import com.example.stocksapp.ui.components.charts.line.LineChartData
import com.example.stocksapp.ui.components.charts.line.LineChartUtils

class BezierLinePathCalculator : LinePathCalculator {
    private var previousDrawableArea: Rect? = null
    private var previousLineChartData: LineChartData? = null

    private var path: Path = Path()
    private val pathMeasure = PathMeasure()

    override fun calculateLinePath(
        drawableArea: Rect,
        data: LineChartData,
        transitionProgress: Float
    ): Path {
        if (data != previousLineChartData || drawableArea != previousDrawableArea) {
            // Only recalculate the path it it has actually changed
            path = Path().apply {
                val firstLocation = LineChartUtils.calculatePointLocation(
                    drawableArea = drawableArea,
                    lineChartData = data,
                    point = data.points.first(),
                    index = 0
                )
                moveTo(firstLocation.x, firstLocation.y)

                // TODO this can be written better
                data.points.drop(1).foldIndexed(firstLocation) { index, previousLocation, current ->
                    val currentLocation = LineChartUtils.calculatePointLocation(
                        drawableArea = drawableArea,
                        lineChartData = data,
                        point = current,
                        index = index + 1
                    )
                    val cpx = previousLocation.x + (currentLocation.x - previousLocation.x) / 2f
                    cubicTo(
                        cpx, previousLocation.y,
                        cpx, currentLocation.y,
                        currentLocation.x, currentLocation.y
                    )
                    currentLocation
                }
            }
            pathMeasure.setPath(path, false)
            previousLineChartData = data
            previousDrawableArea = drawableArea
        }

        return if (transitionProgress < 1f) {
            // Animation in progress, calculate the sub section
            Path().apply {
                pathMeasure.getSegment(
                    0f,
                    pathMeasure.length * transitionProgress,
                    this
                )
            }
        } else {
            path
        }
    }
}