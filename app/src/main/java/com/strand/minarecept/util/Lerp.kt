package com.strand.minarecept.util

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * Linearly interpolate between two values
 */
fun lerp3(
    startValue: Float,
    endValue: Float,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float
): Float {
    return startValue + fraction * (endValue - startValue)
}


/**
 * Linearly interpolate between two [Color]s when the [fraction] is in a given range.
 */
fun lerpColor5(
    startColor: Color,
    endColor: Color,
    @FloatRange(from = 0.0, to = 1.0) startFraction: Float,
    @FloatRange(from = 0.0, to = 1.0) endFraction: Float,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float
): Color {
    if (fraction < startFraction) return startColor
    if (fraction > endFraction) return endColor

    return lerp(
        startColor,
        endColor,
        (fraction - startFraction) / (endFraction - startFraction)
    )
}