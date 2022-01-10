package com.strand.minarecept.ui.saved

import android.util.Log
import androidx.lifecycle.*
import com.strand.minarecept.data.local.FirebaseRecipe
import com.strand.minarecept.data.local.PreferencesManager
import com.strand.minarecept.data.local.Recipe
import com.strand.minarecept.data.local.room.RecipeDao
import com.strand.minarecept.data.remote.firebase.FirebaseRepo
import com.strand.minarecept.data.remote.firebase.RecipesResponse
import com.strand.minarecept.ui.components.Answer
import com.strand.minarecept.ui.components.withAnswerSelected
import com.strand.minarecept.util.UiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SavedViewModel @Inject constructor(
    private val recipeDao: RecipeDao,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle,
    private val firebaseRepo: FirebaseRepo
) : ViewModel() {

    private val _searchQuery = state.getLiveData("searchQuery", "")
    val searchQuery: LiveData<String> = _searchQuery

    fun onSearchQueryChange(newSearchQuery: String) {
        _searchQuery.value = newSearchQuery
    }

    val preferencesFlow = preferencesManager.preferencesFlow

    // FIXME: maybe move this to a repository
    private val _recipes: Flow<UiResult<List<FirebaseRecipe>>> =
        firebaseRepo.getAllRecipesFromFirebase()
            .map {
                if (it is RecipesResponse.OnSuccess) {
                    UiResult.Success(
                        it.querySnapshot?.toObjects(FirebaseRecipe::class.java)
                            ?.distinct().orEmpty()
                    )
                } else {
                    UiResult.Failure("Error loading recipes")
                }
            }.filter {
                // TODO: handle search (contains) and filter here
                if (it is UiResult.Success<List<FirebaseRecipe>>) {
                    !it.data.isNullOrEmpty()
                } else {
                    true
                }
            }.catch { e ->
                Log.e("SavedViewModel", "Error loading recipes", e)
                UiResult.Failure("Error loading recipes")
            }


    val recipes: StateFlow<UiResult<List<FirebaseRecipe>>> = _recipes
        .stateIn(
            initialValue = UiResult.Loading,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )


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

    fun onCategoriesSelected(
        answer: Answer.MultipleChoice?,
        newAnswer: String,
        selected: Boolean
    ) = viewModelScope.launch {
        if (answer == null) {
            preferencesManager.updateCategories(setOf(newAnswer))
        } else {
            val newAnswers = answer
                .withAnswerSelected(newAnswer, selected)
                .answersStringSet
            preferencesManager.updateCategories(newAnswers.toSet())
        }
    }


    fun firebaseOnLikeClick(currentValue: Boolean, id: String) = viewModelScope.launch {
        firebaseRepo.updateRecipeField(id, "favorite", !currentValue)
    }


}