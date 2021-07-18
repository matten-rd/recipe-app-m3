package com.example.minarecept.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.minarecept.data.local.Recipe
import com.example.minarecept.data.local.room.RecipeDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val recipeDao: RecipeDao
) : ViewModel() {

    fun loadCurrentRecipe(id: Int) = recipeDao.loadRecipeById(id = id).asLiveData()

    fun insertRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeDao.insertRecipe(recipe)
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeDao.deleteRecipe(recipe)
    }

    fun onLikeClick(recipe: Recipe) = viewModelScope.launch {
        recipeDao.updateRecipe(recipe.copy(isFavorite = !recipe.isFavorite))
    }
}