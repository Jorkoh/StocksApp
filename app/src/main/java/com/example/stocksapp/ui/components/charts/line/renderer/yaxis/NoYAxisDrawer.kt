package com.example.stocksapp.ui.components.charts.line.renderer.yaxis

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.DrawScope

object NoYAxisDrawer : YAxisDrawer{
    override fun calculateWidth(drawScope: DrawScope) = 0f

    override fun draw(
        drawScope: DrawScope,
        canvas: Canvas,
        drawableArea: Rect,
        minValue: Float,
        maxValue: Float
    ) {
        // Leave empty on purpose, we do not want to draw anything.
    }
}