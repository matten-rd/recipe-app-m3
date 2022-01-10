package com.strand.minarecept.ui.components.autocomplete

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.strand.minarecept.data.local.FirebaseRecipe
import com.strand.minarecept.ui.components.AutoCompleteSearchBar
import com.strand.minarecept.ui.components.autocomplete.utils.AutoCompleteSearchBarTag

@ExperimentalComposeUiApi
@Composable
fun AutoCompleteObject(
    recipes: List<FirebaseRecipe>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
) {
    AutoCompleteBox(
        items = recipes,
        itemContent = {
            RecipeAutoCompleteItem(recipe = it)
        }
    ) {
        val view = LocalView.current

        onItemSelected { recipe ->
            onSearchQueryChange(recipe.title)
            filter(searchQuery)
            view.clearFocus()
        }

        AutoCompleteSearchBar(
            modifier = Modifier.testTag(AutoCompleteSearchBarTag),
            value = searchQuery,
            onValueChanged = { query ->
                onSearchQueryChange(query)
                filter(searchQuery)
            },
            placeholder = "Sök...",
            onDoneActionClick = {
                view.clearFocus()
            },
            onFocusChanged = { focusState ->
                isSearching = focusState.isFocused
            }
        )

    }
}

@Composable
fun RecipeAutoCompleteItem(recipe: FirebaseRecipe) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = recipe.title, style = MaterialTheme.typography.titleSmall)
    }
}