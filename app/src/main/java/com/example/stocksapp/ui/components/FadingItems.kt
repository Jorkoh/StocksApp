package com.example.stocksapp.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.delay

fun LazyListScope.fadingItem(
    key: Any? = null,
    durationMillis: Int = 350,
    hasDelay: Boolean = false,
    content: @Composable LazyItemScope.(alphaModifier: Modifier) -> Unit
) {
    item(key = key) {
        val alpha = remember { Animatable(0f) }
        LaunchedEffect(alpha) {
            if (hasDelay) {
                delay(500)
            }
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = durationMillis, easing = LinearOutSlowInEasing)
            )
        }

        content(Modifier.alpha(alpha.value))
    }
}

fun LazyListScope.fadingItems(
    count: Int,
    key: ((index: Int) -> Any)? = null,
    durationMillis: Int = 350,
    delayDurationMillis: Int = 0,
    itemContent: @Composable LazyItemScope.(index: Int, alphaModifier: Modifier) -> Unit
) {
    items(
        count = count,
        key = key
    ) { index ->
        val alpha = remember { Animatable(0f) }
        LaunchedEffect(alpha) {
            if (delayDurationMillis > 0) {
                delay(delayDurationMillis.toLong())
            }
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = durationMillis, easing = LinearOutSlowInEasing)
            )
        }

        itemContent(index, Modifier.alpha(alpha.value))
    }
}

inline fun <T> LazyListScope.fadingItems(
    items: List<T>,
    noinline key: ((item: T) -> Any)? = null,
    durationMillis: Int = 350,
    hasDelay: Boolean = false,
    crossinline itemContent: @Composable LazyItemScope.(item: T, alphaModifier: Modifier) -> Unit
) {
    items(items.size, if (key != null) { index: Int -> key(items[index]) } else null) {
        val alpha = remember { Animatable(0f) }
        LaunchedEffect(alpha) {
            if (hasDelay) {
                delay(500)
            }
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = durationMillis, easing = LinearOutSlowInEasing)
            )
        }

        itemContent(items[it], Modifier.alpha(alpha.value))
    }
}