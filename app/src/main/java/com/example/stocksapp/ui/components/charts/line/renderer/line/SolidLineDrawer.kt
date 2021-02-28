package com.example.stocksapp.ui.components.charts.line.renderer.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class SolidLineDrawer(
    val thickness: Dp = 3.dp
) : LineDrawer {
    private lateinit var style: DrawStyle

    override fun drawLine(
        drawScope: DrawScope,
        color: Color,
        linePath: Path
    ) {
        with(drawScope) {
            if (!::style.isInitialized) {
                style = Stroke(width = thickness.toPx())
            }
            drawPath(
                path = linePath,
                color = color,
                style = style
            )
        }
    }
}
