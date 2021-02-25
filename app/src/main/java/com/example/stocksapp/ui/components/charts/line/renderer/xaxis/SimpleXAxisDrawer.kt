package com.example.stocksapp.ui.components.charts.line.renderer.xaxis

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SimpleXAxisDrawer(
    private val labelTextSize: TextUnit = 12.sp,
    private val labelTextColor: Color = Color.Black,
    /** 1 means we draw everything. 2 means we draw every other, and so on. */
    private val labelRatio: Int = 1,
    private val axisLineThickness: Dp = 1.dp,
    private val axisLineColor: Color = Color.Black
) : XAxisDrawer {
    private val axisLinePaint = Paint().apply {
        isAntiAlias = true
        color = axisLineColor
        style = PaintingStyle.Stroke
    }

    private val textPaint = android.graphics.Paint().apply {
        isAntiAlias = true
        color = labelTextColor.toArgb()
    }

    override fun calculateHeight(drawScope: DrawScope) = with(drawScope) {
        (3f / 2f) * (labelTextSize.toPx() + axisLineThickness.toPx())
    }

    override fun draw(
        drawScope: DrawScope,
        canvas: Canvas,
        drawableArea: Rect,
        labels: List<String>
    ) {
        with(drawScope) {
            // Draw axis
            val lineThickness = axisLineThickness.toPx()
            val axisY = drawableArea.top + (lineThickness / 2f)
            canvas.drawLine(
                p1 = Offset(x = drawableArea.left, y = axisY),
                p2 = Offset(x = drawableArea.right, y = axisY),
                paint = axisLinePaint.apply { strokeWidth = lineThickness }
            )

            // Draw labels
            val labelPaint = textPaint.apply {
                textSize = labelTextSize.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
            }
            val labelIncrements = drawableArea.width / (labels.size - 1)
            labels.forEachIndexed { index, label ->
                if (index.rem(labelRatio) == 0) {
                    val labelX = drawableArea.left + (labelIncrements * (index))
                    val labelY = drawableArea.bottom

                    canvas.nativeCanvas.drawText(label, labelX, labelY, labelPaint)
                }
            }
        }
    }
}
