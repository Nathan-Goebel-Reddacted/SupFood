package com.example.supfood.logic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.supfood.data.AppDatabase
import com.example.supfood.data.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SupfoodViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao = AppDatabase.getDatabase(application).recipeDao()
    private val repository = RecipeRepository(recipeDao)

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    init {
        loadDefaultRecipes()
    }

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            val fetchedRecipes = repository.fetchAndSaveRecipes(query)
            _recipes.value = fetchedRecipes
        }
    }

    private fun loadDefaultRecipes() {
        viewModelScope.launch {
            val defaultRecipes = repository.fetchAndSaveRecipes() //
            _recipes.value = defaultRecipes
        }
    }
}
