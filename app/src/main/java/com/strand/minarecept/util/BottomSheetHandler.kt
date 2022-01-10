package com.strand.minarecept.util

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween

internal val bottomSheetAnimationSpec = tween<Float>(
    durationMillis = 400,
    delayMillis = 20,
    easing = LinearOutSlowInEasing
)

