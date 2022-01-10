package com.strand.minarecept.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.strand.minarecept.data.local.FirebaseRecipe
import com.strand.minarecept.data.remote.firebase.FirebaseRepo
import com.strand.minarecept.data.remote.firebase.RecipesResponse
import com.strand.minarecept.util.UiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) : ViewModel() {

    private val _recipeId = MutableStateFlow<String>("")
    val recipeId: StateFlow<String> = _recipeId

    fun setRecipeId(id: String) {
        _recipeId.value = id
    }

    @ExperimentalCoroutinesApi
    private val _recipe: Flow<UiResult<FirebaseRecipe>> = recipeId.flatMapLatest { id ->
            firebaseRepo.loadCurrentRecipeFromFirebase(id)
        }.map {
            if (it is RecipesResponse.OnSuccess) {
                UiResult.Success(it.querySnapshot?.toObject<FirebaseRecipe>())
            } else {
                UiResult.Failure("Error loading recipe")
            }
        }.catch { e ->
            Log.e("DetailViewModel", "Error loading recipe", e)
            emit(UiResult.Failure("Error loading recipe"))
        }

    @ExperimentalCoroutinesApi
    val recipe: StateFlow<UiResult<FirebaseRecipe>> = _recipe
        .stateIn(
            initialValue = UiResult.Loading,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    fun firebaseOnLikeClick(currentValue: Boolean, id: String) = viewModelScope.launch {
        firebaseRepo.updateRecipeField(id, "favorite", !currentValue)
    }

    fun firebaseDeleteRecipe(id: String) = viewModelScope.launch {
        firebaseRepo.deleteRecipe(id)
    }
}