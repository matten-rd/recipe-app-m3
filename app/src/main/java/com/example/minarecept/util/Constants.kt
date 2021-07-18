package com.example.minarecept.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import com.example.minarecept.R

@Composable
fun getCategoryOptions(): List<String> {
    return stringArrayResource(id = R.array.categories).toList()
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