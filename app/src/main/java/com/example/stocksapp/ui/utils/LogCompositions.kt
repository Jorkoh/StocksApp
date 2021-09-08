@file:Suppress("NOTHING_TO_INLINE")

package com.example.stocksapp.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import com.example.stocksapp.BuildConfig
import timber.log.Timber

class Ref(var value: Int)

// https://github.com/chrisbanes/tivi/blob/main/common-ui-compose/src/main/java/app/tivi/common/compose/Debug.kt
@Composable
inline fun LogCompositions(tag: String) {
    if (BuildConfig.DEBUG) {
        val ref = remember { Ref(0) }
        SideEffect { ref.value++ }
        Timber.d("$tag, compositions: ${ref.value}")
    }
}