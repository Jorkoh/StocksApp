package com.example.stocksapp.ui.components.charts.line.renderer.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

interface LineDrawer {
    fun drawLine(drawScope: DrawScope, color: Color, linePath: Path)
}
