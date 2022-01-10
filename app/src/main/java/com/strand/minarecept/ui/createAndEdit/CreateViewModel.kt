package com.strand.minarecept.ui.createAndEdit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.strand.minarecept.data.local.FirebaseRecipe
import com.strand.minarecept.data.local.room.RecipeDao
import com.strand.minarecept.data.remote.firebase.FirebaseRepo
import com.strand.minarecept.data.remote.firebase.RecipesResponse
import com.strand.minarecept.data.remote.parsing.parseUrl
import com.strand.minarecept.util.UiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val recipeDao: RecipeDao,
    private val firebaseRepo: FirebaseRepo
) : ViewModel() {

    private val _url = MutableLiveData("")
    val url: LiveData<String> = _url

    fun onUrlChange(newUrl: String) {
        _url.value = newUrl
    }

    private val _recipe = MutableLiveData<FirebaseRecipe>()
    val recipe: LiveData<FirebaseRecipe> = _recipe

    fun onRecipeChange(newRecipe: FirebaseRecipe) {
        _recipe.value = newRecipe
    }
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean>
        get() = _loading.asStateFlow()

    fun createUrlRecipe(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.emit(true)
            val recipeData = parseUrl(url)
            _recipe.postValue(recipeData)
            addRecipeToFirebase(recipeData)
            _loading.emit(false)
        }
        onUrlChange("")
    }

    fun addRecipeToFirebase(recipe: FirebaseRecipe) = viewModelScope.launch {
        firebaseRepo.addRecipeToFirebase(recipe, recipe.recipeId)
    }

    private val _recipeId = MutableStateFlow<String>("")
    val recipeId: StateFlow<String> = _recipeId

    fun setRecipeId(id: String) {
        _recipeId.value = id
    }

    @ExperimentalCoroutinesApi
    private val _editrecipe: Flow<UiResult<FirebaseRecipe>> = recipeId.flatMapLatest { id ->
        firebaseRepo.loadCurrentRecipeFromFirebase(id)
    }.map {
        if (it is RecipesResponse.OnSuccess) {
            UiResult.Success(it.querySnapshot?.toObject<FirebaseRecipe>())
        } else {
            UiResult.Failure("Error loading recipe")
        }
    }.catch { e ->
        Log.e("CreateViewModel", "Error loading recipe", e)
        emit(UiResult.Failure("Error loading recipe"))
    }

    @ExperimentalCoroutinesApi
    val editrecipe: StateFlow<UiResult<FirebaseRecipe>> = _editrecipe
        .stateIn(
            initialValue = UiResult.Loading,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

}