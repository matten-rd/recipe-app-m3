package com.strand.minarecept.ui.components.autocomplete

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

const val AutoCompleteBoxTag = "AutoCompleteBox"

@Composable
fun <T : AutoCompleteEntity> AutoCompleteBox(
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    content: @Composable AutoCompleteScope<T>.() -> Unit
) {
    val autoCompleteState = remember { AutoCompleteState(startItems = items) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        autoCompleteState.content()
        AnimatedVisibility(visible = autoCompleteState.isSearching) {
            LazyColumn(
                modifier = Modifier.autoComplete(autoCompleteState).padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(autoCompleteState.filteredItems) { idx, item ->
                    Surface(
                        modifier = Modifier.clickable { autoCompleteState.selectItem(item) },
                        shape = when(idx) {
                            0 -> RoundedCornerShape(16.dp, 16.dp, 4.dp, 4.dp)
                            items.size-1 -> RoundedCornerShape(4.dp, 4.dp, 16.dp,16.dp)
                            else -> RoundedCornerShape(4.dp)
                        },
                        tonalElevation = 8.dp
                    ) {
                        itemContent(item)
                    }
                }
            }
        }
    }
}

@SuppressLint("UnnecessaryComposedModifier")
private fun Modifier.autoComplete(
    autoCompleteItemScope: AutoCompleteDesignScope
): Modifier = composed {
    val baseModifier = if (autoCompleteItemScope.shouldWrapContentHeight)
        wrapContentHeight()
    else
        heightIn(0.dp, autoCompleteItemScope.boxMaxHeight)

    baseModifier
        .testTag(AutoCompleteBoxTag)
        .fillMaxWidth(autoCompleteItemScope.boxWidthPercentage)
        .background(
            color = MaterialTheme.colorScheme.surface,
            shape = autoCompleteItemScope.boxShape
        )
}