package com.example.minarecept.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.minarecept.ui.theme.Brown700
import com.example.minarecept.ui.theme.Pink50
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar

@Composable
fun RecipeTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    backgroundColor: Color = Brown700,
    contentColor: Color = Pink50,
    elevation: Dp = 0.dp,
    navigationIcon: @Composable() (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.h1) },
        navigationIcon = navigationIcon,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.statusBars,
            applyBottom = false
        ),
        modifier = modifier.fillMaxWidth(),
        elevation = elevation
    )
}