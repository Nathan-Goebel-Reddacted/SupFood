package com.example.supfood.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.supfood.data.AppDatabase
import com.example.supfood.data.Recipe
import com.example.supfood.logic.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao = AppDatabase.getDatabase(application).recipeDao()
    private val repository = RecipeRepository(recipeDao)

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    fun searchRecipes(query: String) {
        Log.d("RecipeViewModel", "searchRecipes called with query: $query")
        viewModelScope.launch {
            try {
                val fetchedRecipes = repository.fetchAndSaveRecipes(query)
                _recipes.value = fetchedRecipes.toList()
                Log.d("RecipeViewModel", "Recipes fetched successfully: ${fetchedRecipes.size} recipes")
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching recipes", e)
            }
        }
    }
}

