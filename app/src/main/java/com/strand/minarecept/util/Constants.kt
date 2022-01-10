package com.strand.minarecept.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource

@Composable
fun getStringArray(resId: Int): List<String> {
    return stringArrayResource(id = resId).toList()
}

@Composable
fun getQuantityStringZero(resId: Int, zeroResId: Int, quantity: Int): String {
    val resources = LocalContext.current.resources
    return if (quantity == 0) {
        resources.getString(zeroResId)
    } else {
        resources.getQuantityString(resId, quantity, quantity)
    }
}

internal val categories = listOf<String>(
    "Förrätt",
    "Huvudrätt",
    "Efterrätt",
    "Bakning"
)