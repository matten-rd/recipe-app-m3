package com.example.minarecept.ui.saved

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import com.example.minarecept.ui.components.CompactListItem
import com.example.minarecept.ui.components.RecipeFab
import com.example.minarecept.ui.components.RecipeSpacer
import com.example.minarecept.ui.components.SearchTextField
import com.example.minarecept.ui.home.HomeViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun SavedScreen(
    contentPadding: PaddingValues,
    navController: NavController,
    openSheet: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    // TODO: Create a SavedScreenContent composable (this might not be possible/feasible)
    val recipeItems by homeViewModel.recipes.collectAsState(initial = emptyList())
    val searchQuery by homeViewModel.searchQuery.observeAsState(initial = "")

    val isRefreshing by homeViewModel.isRefreshing.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    /*
    val map = getCategoryOptions().map { it to mutableListOf<Recipe>() }.toMap()

    for (category in getCategoryOptions()) {
        for (recipe in recipeItems) {
            if (recipe.category?.contains(category) == true) {
                map[category]?.add(recipe)
            }
        }
    }
     */
    SwipeRefresh(
        indicatorPadding = contentPadding,
        state = swipeRefreshState,
        onRefresh = { homeViewModel.refresh() }
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
                    SearchTextField(
                        value = searchQuery,
                        onValueChange = { homeViewModel.onSearchQueryChange(it) },
                        placeholder = "Sök...",
                        modifier = Modifier.weight(1f)
                    )
                    RecipeSpacer()
                    RecipeFab(
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        },
                        onClick = openSheet
                    )
                }
            }

            items(recipeItems) { recipe ->
                CompactListItem(
                    recipeId = recipe.recipeId,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    url = recipe.thumbnailImage,
                    title = recipe.title,
                    isLiked = recipe.isFavorite,
                    onClick = {
                        homeViewModel.onClickThrough(recipe)
                        navController.navigate("detail/${recipe.recipeId}")
                    },
                    onIconClick = { homeViewModel.onLikeClick(recipe) }
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .height(96.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Inga fler recept.")
                }
            }

        }
    }

}
