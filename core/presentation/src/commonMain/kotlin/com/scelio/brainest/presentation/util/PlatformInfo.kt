package com.scelio.brainest.presentation.util

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier

expect fun isIos(): Boolean

fun Modifier.statusBarsPaddingIfAndroid(): Modifier {
    return if (isIos()) {
        this
    } else {
        this.statusBarsPadding()
    }
}
