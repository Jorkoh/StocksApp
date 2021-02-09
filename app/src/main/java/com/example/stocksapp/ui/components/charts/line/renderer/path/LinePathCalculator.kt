package com.example.stocksapp.ui.components.charts.line.renderer.path

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import com.example.stocksapp.ui.components.charts.line.LineChartData

interface LinePathCalculator {
    fun calculateLinePath(
        drawableArea: Rect,
        lineChartData: LineChartData,
        transitionProgress: Float,
    ): Path
}