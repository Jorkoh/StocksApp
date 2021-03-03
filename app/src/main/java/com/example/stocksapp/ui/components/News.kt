package com.example.stocksapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stocksapp.R
import com.example.stocksapp.data.model.News
import dev.chrisbanes.accompanist.coil.CoilImage
import java.text.DateFormat
import java.util.Date
import kotlin.math.roundToInt

fun <T> testSpec() = spring<T>()

@Composable
fun NewsListItem(
    news: News,
    itemState: ListItemState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onReadMoreClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        val newsDate = remember(news.date) {
            DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.SHORT
            ).format(Date(news.date))
        }
        val density = LocalDensity.current
        val startPaddingExpanding = remember(density) { with(density) { 100.dp.toPx() } }
        val startPaddingCollapsing = remember(density) { with(density) { 140.dp.toPx() } }

        AnimatedVisibility(
            visible = itemState == ListItemState.Expanded,
            enter = expandIn(
                animationSpec = testSpec(),
                expandFrom = Alignment.TopEnd,
                initialSize = { IntSize((it.width - startPaddingExpanding).roundToInt(), 0) }
            ) + fadeIn(animationSpec = testSpec()),
            exit = shrinkOut(
                animationSpec = testSpec(),
                shrinkTowards = Alignment.TopEnd,
                targetSize = { IntSize((it.width - startPaddingCollapsing).roundToInt(), 0) }
            ) + fadeOut(animationSpec = testSpec())
        ) {
            Text(
                text = news.headline,
                style = MaterialTheme.typography.h6.copy(fontSize = 18.sp),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .align(Alignment.Start),
            )
        }
        Row {
            Column {
                CoilImage(
                    data = news.imageUrl,
                    contentDescription = null,
                    fadeIn = true,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(MaterialTheme.shapes.large)
                )
                AnimatedVisibility(
                    visible = itemState == ListItemState.Expanded,
                    enter = fadeIn(animationSpec = testSpec()) +
                        expandVertically(animationSpec = testSpec()),
                    exit = fadeOut(animationSpec = testSpec()) +
                        shrinkVertically(animationSpec = testSpec())
                ) {
                    Text(
                        text = news.symbols.joinToString(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.caption.copy(fontSize = 13.sp)
                    )
                    Button(onClick = { onReadMoreClicked() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_launch),
                            contentDescription = stringResource(id = R.string.read_more_button)
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f, true)
                    .padding(start = 16.dp)
            ) {
                AnimatedVisibility(
                    visible = itemState == ListItemState.Collapsed,
                    enter = expandIn(animationSpec = testSpec()) + fadeIn(animationSpec = testSpec()),
                    exit = shrinkOut(animationSpec = testSpec()) + fadeOut(animationSpec = testSpec())
                ) {
                    Text(
                        text = news.headline,
                        style = MaterialTheme.typography.h6.copy(fontSize = 18.sp)
                    )
                }
                AnimatedVisibility(
                    visible = itemState == ListItemState.Expanded,
                    enter = fadeIn(animationSpec = testSpec()) +
                        expandVertically(animationSpec = testSpec()),
                    exit = fadeOut(animationSpec = testSpec()) +
                        shrinkVertically(animationSpec = testSpec())
                ) {
                    Text(
                        text = news.summary,
                        maxLines = 8,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.body2
                    )
                }
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = "${news.source} - $newsDate",
                        style = MaterialTheme.typography.caption.copy(fontSize = 13.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

enum class ListItemState {
    Collapsed,
    Expanded
}
