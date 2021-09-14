package com.example.stocksapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stocksapp.data.repositories.stocks.ChartRange
import com.example.stocksapp.ui.theme.StocksAppTheme

@Composable
fun CustomChartRangeSelector(
    currentRange: ChartRange,
    onRangeSelected: (ChartRange) -> Unit,
    ranges: List<ChartRange>,
    modifier: Modifier = Modifier,
    indicatorColor: Color = MaterialTheme.colors.secondary,
    unselectedColor: Color = MaterialTheme.colors.onBackground,
    selectedColor: Color = contentColorFor(indicatorColor)
) {
    val springSpec = remember { SpringSpec<Float>(stiffness = 300f) }
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        val colorSpec = with(springSpec) { SpringSpec<Color>(dampingRatio, stiffness) }

        ranges.forEach { range ->
            val selected = range == currentRange
            val tint by animateColorAsState(
                targetValue = if (selected) selectedColor else unselectedColor,
                animationSpec = colorSpec
            )

            CustomChartRangeItem(
                content = {
                    Text(
                        text = stringResource(range.uiStringResource),
                        color = tint,
                        style = MaterialTheme.typography.h5.copy(fontSize = 16.sp),
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                },
                selected = selected,
                onSelected = { onRangeSelected(range) },
                animSpec = springSpec
            )
        }
    }
}

@Composable
private fun CustomChartRangeItem(
    content: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.secondary,
    shape: Shape = IndicatorShape,
    selected: Boolean,
    onSelected: () -> Unit,
    animSpec: AnimationSpec<Float>
) {
    // Animate the icon/text positions within the item based on selection
    val animationProgress by animateFloatAsState(if (selected) 1f else 0f, animSpec)
    Box(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onSelected,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .padding(ItemPadding)
            .clip(shape)
            .background(color.copy(alpha = animationProgress)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

private val IndicatorShape = RoundedCornerShape(percent = 50)
private val ItemPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

@Preview
@Composable
private fun CustomChartRangeSelectorPreview() {
    StocksAppTheme {
        CustomChartRangeSelector(
            currentRange = previewChartRanges.first(),
            onRangeSelected = { },
            ranges = previewChartRanges
        )
    }
}

@Preview
@Composable
private fun CustomChartRangeSelectorPreviewDark() {
    StocksAppTheme(darkTheme = true) {
        CustomChartRangeSelector(
            currentRange = previewChartRanges.first(),
            onRangeSelected = { },
            ranges = previewChartRanges
        )
    }
}

private val previewChartRanges = ChartRange.values().toList()
