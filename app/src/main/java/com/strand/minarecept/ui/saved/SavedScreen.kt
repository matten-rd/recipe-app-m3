package com.strand.minarecept.ui.saved

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import com.strand.minarecept.data.local.FirebaseRecipe
import com.strand.minarecept.ui.components.CompactListItem
import com.strand.minarecept.ui.components.LoadingScreen
import com.strand.minarecept.ui.components.RecipeFab
import com.strand.minarecept.ui.components.RecipeSpacer
import com.strand.minarecept.ui.components.autocomplete.AutoCompleteObject
import com.strand.minarecept.util.UiResult
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun SavedScreen(
    contentPadding: PaddingValues,
    navigateToRecipe: (String) -> Unit,
    openSheet: () -> Unit,
    savedViewModel: SavedViewModel = hiltViewModel()
) {
    val searchQuery by savedViewModel.searchQuery.observeAsState(initial = "")
    val firebaseRecipes by savedViewModel.recipes.collectAsState()

    when (firebaseRecipes) {
        is UiResult.Success<List<FirebaseRecipe>> -> {
            val recipes = (firebaseRecipes as UiResult.Success<List<FirebaseRecipe>>).data!!.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
            SavedScreenContent(
                firebaseRecipes = recipes,
                contentPadding = contentPadding,
                searchQuery = searchQuery,
                onSearchQueryChange = { savedViewModel.onSearchQueryChange(it) },
                navigateToRecipe = navigateToRecipe,
                openSheet = openSheet,
                onLikeClick = { isFavorite, id ->
                    savedViewModel.firebaseOnLikeClick(isFavorite, id)
                }
            )
        }
        is UiResult.Loading -> {
            LoadingScreen()
        }
        is UiResult.Failure<*> -> {
            // TODO: Display error screen / error snackbar
        }
    }
}

@ExperimentalCoilApi
@ExperimentalComposeUiApi
@Composable
fun SavedScreenContent(
    firebaseRecipes: List<FirebaseRecipe>,
    contentPadding: PaddingValues,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    navigateToRecipe: (String) -> Unit,
    openSheet: () -> Unit,
    onLikeClick: (Boolean, String) -> Unit
) {
    LazyColumn(
        contentPadding = contentPadding,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AutoCompleteObject(
                        recipes = firebaseRecipes,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { onSearchQueryChange(it) }
                    )
                }
                RecipeSpacer()
                RecipeFab(
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            contentDescription = null,
                        )
                    },
                    onClick = openSheet
                )
            }
        }

        items(firebaseRecipes) { recipe ->
            CompactListItem(
                modifier = Modifier.padding(horizontal = 16.dp),
                url = recipe.thumbnailImage,
                title = recipe.title,
                isLiked = recipe.isFavorite,
                onClick = { navigateToRecipe(recipe.recipeId) },
                onIconClick = { onLikeClick(recipe.isFavorite, recipe.recipeId) }
            )
        }

        item {
            Row(
                modifier = Modifier
                    .height(96.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Inga fler recept.")
            }
        }
    }
}


