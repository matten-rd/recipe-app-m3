package com.strand.minarecept.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.accompanist.insets.statusBarsPadding

@ExperimentalMaterial3Api
@Composable
fun NavigationTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String = "",
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    navigationIconBackgroundColor: Color = backgroundColor,
    imageVector: ImageVector = Icons.Rounded.ArrowBack,
    onNavigatePress: () -> Unit
) {
    RecipeTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = title,
        containerColor = backgroundColor,
    ) {
        IconButton(
            onClick = onNavigatePress,
            modifier = Modifier
                .clip(CircleShape)
                .background(navigationIconBackgroundColor)
        ) {
            Icon(imageVector = imageVector, contentDescription = null)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun RecipeTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String = "",
    containerColor: Color = MaterialTheme.colorScheme.surface,
    actions: @Composable (RowScope.() -> Unit) = {},
    navigationIcon: @Composable (() -> Unit)? = null
) {
    val backgroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = containerColor
    )
    val backgroundColor = backgroundColors.containerColor(
        scrollFraction = scrollBehavior?.scrollFraction ?: 0f
    ).value
    Box(modifier = Modifier.background(backgroundColor)) {
        CenterAlignedTopAppBar(
            title = { Text(text = title) },
            navigationIcon = navigationIcon ?: {},
            modifier = modifier.statusBarsPadding(),
            actions = actions,
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = containerColor
            )
        )
    }
}

