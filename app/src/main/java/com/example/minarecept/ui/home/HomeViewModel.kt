package com.example.minarecept.ui.home

import androidx.lifecycle.*
import com.example.minarecept.data.local.PreferencesManager
import com.example.minarecept.data.local.Recipe
import com.example.minarecept.data.local.room.RecipeDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recipeDao: RecipeDao,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : ViewModel() {

    private val _searchQuery = state.getLiveData("searchQuery", "")
    val searchQuery: LiveData<String> = _searchQuery

    fun onSearchQueryChange(newSearchQuery: String) {
        _searchQuery.value = newSearchQuery
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            delay(2000)
            _recipes = getAllRecipes()
            _isRefreshing.emit(false)
        }
    }

    val preferencesFlow = preferencesManager.preferencesFlow


    private var _recipes = getAllRecipes()
    val recipes: Flow<List<Recipe>> get() = _recipes

    @ExperimentalCoroutinesApi
    fun getAllRecipes(): Flow<List<Recipe>> {
        return combine(
            searchQuery.asFlow(),
            preferencesFlow
        ) { query, filterPrefs ->
            Pair(query, filterPrefs)
        }.flatMapLatest { (query, filterPrefs) ->
            recipeDao.getPagingSource(
                query,
                filterPrefs.sortOrder,
                filterPrefs.onlyFavorite,
                filterPrefs.durationStart,
                filterPrefs.durationEnd
            )
        }
    }

    fun onLikeClick(recipe: Recipe) = viewModelScope.launch {
        recipeDao.updateRecipe(recipe.copy(isFavorite = !recipe.isFavorite))
    }

    fun onClickThrough(recipe: Recipe) = viewModelScope.launch {
        recipeDao.updateRecipe(recipe.copy(clickedCount = recipe.clickedCount+1))
    }

    // Handle filtering and sortOrderOptions
    fun onOnlyFavoriteSelected(onlyFavorite: Boolean) = viewModelScope.launch {
        preferencesManager.updateOnlyFavorite(onlyFavorite)
    }

    fun onSortOrderSelected(sortOrder: Int) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onDurationSelected(durationStart: Float, durationEnd: Float) = viewModelScope.launch {
        val durStart = durationStart.toLong() * (60*1000)
        val durEnd = durationEnd.toLong() * (60*1000)
        preferencesManager.updateDuration(durStart, durEnd)
    }

}

data class HomeViewState(
    val recipes: List<Recipe> = emptyList(),
    val refreshing: Boolean = false
)