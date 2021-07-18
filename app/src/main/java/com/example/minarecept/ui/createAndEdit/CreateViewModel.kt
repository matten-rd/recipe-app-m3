package com.example.minarecept.ui.createAndEdit

import androidx.lifecycle.*
import com.example.minarecept.data.local.Recipe
import com.example.minarecept.data.local.room.RecipeDao
import com.example.minarecept.data.remote.parsing.parseUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val recipeDao: RecipeDao
) : ViewModel() {

    private val _url = MutableLiveData("")
    val url: LiveData<String> = _url

    fun onUrlChange(newUrl: String) {
        _url.value = newUrl
    }

    private val _recipe = MutableLiveData<Recipe>()
    val recipe: LiveData<Recipe> = _recipe

    fun onRecipeChange(newRecipe: Recipe) {
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
            insertRecipe(recipeData)
            _loading.emit(false)
        }
        onUrlChange("")
    }

    fun insertRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeDao.insertRecipe(recipe)
    }

    fun updateRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeDao.updateRecipe(recipe)
    }

    // Used for the edit screen
    fun loadCurrentRecipe(id: Int) = recipeDao.loadRecipeById(id = id).asLiveData()

}