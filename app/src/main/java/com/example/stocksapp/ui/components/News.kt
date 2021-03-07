package com.example.stocksapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stocksapp.R
import com.example.stocksapp.data.model.News
import java.text.DateFormat
import java.util.Date

fun <T> testSpec() = tween<T>(durationMillis = 270)
fun <T> testSpecDelayed() = tween<T>(durationMillis = 125, delayMillis = 300)

@Composable
fun NewsListItem(
    news: News,
    itemState: ListItemState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onReadMoreClicked: () -> Unit
) {
    val imageSize: Dp by animateDpAsState(
        targetValue = when (itemState) {
            ListItemState.Collapsed -> 84.dp
            ListItemState.Expanded -> 120.dp
        },
        animationSpec = testSpecDelayed()
    )

    val readMoreSize: Dp by animateDpAsState(
        targetValue = when (itemState) {
            ListItemState.Collapsed -> 0.dp
            ListItemState.Expanded -> 36.dp
        },
        animationSpec = testSpecDelayed()
    )

    val cardElevation: Dp by animateDpAsState(
        targetValue = when (itemState) {
            ListItemState.Collapsed -> 0.dp
            ListItemState.Expanded -> 8.dp
        },
        animationSpec = testSpecDelayed()
    )

    Card(
        elevation = cardElevation,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            val newsDate = remember(news.date) {
                DateFormat.getDateInstance(DateFormat.SHORT).format(Date(news.date))
            }

            AnimatedVisibility(
                visible = itemState == ListItemState.Expanded,
                enter = expandVertically(
                    animationSpec = testSpec(),
                    expandFrom = Alignment.Top
                ) + fadeIn(animationSpec = testSpec()),
                exit = shrinkVertically(
                    animationSpec = testSpec(),
                    shrinkTowards = Alignment.Top
                ) + fadeOut(animationSpec = testSpec())
            ) {
                Text(
                    text = news.headline,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                        .align(Alignment.Start),
                )
            }
            Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                Column {
                    // https://github.com/chrisbanes/accompanist/pull/220 will solve the crash
                    // CoilImage(
                    //     data = news.imageUrl,
                    //     contentDescription = null,
                    //     fadeIn = true,
                    //     contentScale = ContentScale.Crop,
                    //     modifier = Modifier
                    //         .size(imageSize)
                    //         .clip(MaterialTheme.shapes.large)
                    // )
                    Box(
                        modifier = Modifier
                            .size(imageSize)
                            .background(color = Color.Red, shape = MaterialTheme.shapes.large)
                    )
                    Button(
                        onClick = onReadMoreClicked,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 3.dp),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .height(readMoreSize)
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.read_more_button),
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_launch),
                            contentDescription = null
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    AnimatedVisibility(
                        visible = itemState == ListItemState.Collapsed,
                        enter = expandVertically(animationSpec = testSpec()) + fadeIn(animationSpec = testSpec()),
                        exit = shrinkVertically(animationSpec = testSpec()) + fadeOut(animationSpec = testSpec())
                    ) {
                        Text(
                            text = news.headline,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.h6.copy(fontSize = 16.sp)
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
                    Spacer(Modifier.weight(1f))
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = "${news.source} - $newsDate",
                            style = MaterialTheme.typography.caption.copy(fontSize = 13.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

enum class ListItemState {
    Collapsed,
    Expanded
}
