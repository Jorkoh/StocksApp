package com.example.stocksapp.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.stocksapp.R
import com.example.stocksapp.data.model.News
import com.example.stocksapp.ui.theme.StocksAppTheme
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

enum class ListItemState {
    Collapsed,
    Expanded
}

private val imageWidthCollapsed = 84.dp
private val imageHeightCollapsed = 84.dp
private val imageWidthExpanded = 120.dp
private val imageHeightExpanded = 156.dp

private fun <T> transitionSpec() = spring<T>(
    dampingRatio = Spring.DampingRatioLowBouncy,
    stiffness = 600f
)

@Composable
fun NewsListItem(
    news: News,
    itemState: ListItemState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onReadMoreClicked: () -> Unit
) {
    val transition = updateTransition(itemState, "baseTransition")

    val cardElevation by transition.animateDp(
        transitionSpec = { transitionSpec() },
        label = "cardElevationTransition"
    ) { state ->
        when (state) {
            ListItemState.Collapsed -> 0.dp
            ListItemState.Expanded -> 8.dp
        }
    }

    val cardColor by transition.animateColor(
        transitionSpec = { transitionSpec() },
        label = "cardColorTransition"
    ) { state ->
        when (state) {
            ListItemState.Collapsed -> MaterialTheme.colors.background
            ListItemState.Expanded -> MaterialTheme.colors.surface
        }
    }

    Card(
        elevation = cardElevation,
        backgroundColor = cardColor,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            NewsExpandedHeadline(
                this,
                headline = news.headline,
                transition = transition
            )
            Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                NewsImage(
                    imageUrl = news.imageUrl,
                    transition = transition,
                    readMoreClickable = itemState == ListItemState.Expanded,
                    onReadMoreClicked = onReadMoreClicked
                )

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                        .fillMaxHeight()
                ) {
                    NewsContent(
                        headline = news.headline,
                        summary = news.summary,
                        transition = transition
                    )
                    NewsFoot(
                        columnScope = this,
                        source = news.source,
                        date = news.date
                    )
                }
            }
        }
    }
}

@Composable
private fun NewsExpandedHeadline(
    columnScope: ColumnScope,
    headline: String,
    transition: Transition<ListItemState>
) {
    with(columnScope) {
        transition.AnimatedVisibility(
            visible = { itemState -> itemState == ListItemState.Expanded },
            enter = expandVertically(
                animationSpec = transitionSpec(),
                expandFrom = Alignment.Top
            ) + fadeIn(animationSpec = transitionSpec()),
            exit = shrinkVertically(
                animationSpec = transitionSpec(),
                shrinkTowards = Alignment.Top
            ) + fadeOut(animationSpec = transitionSpec())
        ) {
            Text(
                text = headline,
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .align(Alignment.Start),
            )
        }
    }
}

@Composable
private fun NewsImage(
    imageUrl: String,
    transition: Transition<ListItemState>,
    readMoreClickable: Boolean,
    onReadMoreClicked: () -> Unit
) {
    val imageSize by transition.animateSize(
        transitionSpec = { transitionSpec() },
        label = "imageSizeTransition"
    ) { state ->
        when (state) {
            ListItemState.Collapsed -> Size(imageWidthCollapsed.value, imageHeightCollapsed.value)
            ListItemState.Expanded -> Size(imageWidthExpanded.value, imageHeightExpanded.value)
        }
    }

    val readMoreAlpha by transition.animateFloat(
        transitionSpec = { transitionSpec() },
        label = "readMoreAlphaTransition"
    ) { state ->
        when (state) {
            ListItemState.Collapsed -> 0f
            ListItemState.Expanded -> 0.85f
        }
    }

    Box(
        modifier = Modifier
            .size(imageSize.width.dp, imageSize.height.dp)
            .clip(MaterialTheme.shapes.large)
    ) {
        Image(
            painter = rememberImagePainter(
                data = imageUrl,
                builder = { crossfade(true) }
            ),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.size(imageSize.width.dp, imageSize.height.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .alpha(readMoreAlpha)
                .background(color = MaterialTheme.colors.surface)
                .run {
                    if (readMoreClickable) clickable(onClick = onReadMoreClicked) else this
                }
                .padding(vertical = 6.dp)
        ) {
            Text(
                text = stringResource(R.string.read_more_button),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.subtitle1.copy(fontSize = 12.sp),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun NewsContent(
    headline: String,
    summary: String,
    transition: Transition<ListItemState>
) {
    transition.AnimatedContent(
        transitionSpec = {
            fadeIn(animationSpec = transitionSpec()) with fadeOut(animationSpec = transitionSpec())
        }
    ) { targetState ->
        when (targetState) {
            ListItemState.Collapsed -> Text(
                text = headline,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1
            )
            ListItemState.Expanded -> Text(
                text = summary,
                maxLines = 8,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body2
            )
        }

    }
}

@Composable
private fun NewsFoot(
    columnScope: ColumnScope,
    source: String,
    date: Instant
) {
    val locale = Locale.current
    val dateString = remember(date) {
        DateTimeFormatter
            .ofLocalizedDate(FormatStyle.SHORT)
            .withLocale(java.util.Locale(locale.language))
            .withZone(ZoneId.systemDefault()).format(date)
    }

    with(columnScope) {
        Text(
            text = "$source - $dateString",
            style = MaterialTheme.typography.subtitle2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
        )
    }
}

@Composable
fun NewsListItemPlaceholder(modifier: Modifier = Modifier) {
    Row(modifier.padding(horizontal = 24.dp, vertical = 14.dp)) {
        Box(
            modifier = Modifier
                .size(imageWidthCollapsed, imageHeightCollapsed)
                .clip(MaterialTheme.shapes.large)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                )
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .padding(start = 16.dp)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                )
        )
    }
}

@Preview
@Composable
fun NewsListItemPreview() {
    StocksAppTheme {
        Surface {
            NewsListItem(
                news = News(
                    id = 0,
                    date = Instant.now(),
                    headline = "This is a test headline",
                    source = "This is a test source",
                    url = "https://developer.android.com/jetpack/compose/animation#animatedcontent",
                    summary = "This is a test summary",
                    symbols = listOf("ABC", "XYZ"),
                    imageUrl = "https://s3-symbol-logo.tradingview.com/amc-entertainment-holdings--600.png",
                    fetchTimestamp = Instant.now()
                ),
                itemState = ListItemState.Collapsed,
                onClick = { },
                onReadMoreClicked = { }
            )
        }
    }
}
