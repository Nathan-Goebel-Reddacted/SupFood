package com.example.supfood.logic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
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

    private var currentPage = 1
    var isLoading = false
    private var hasMoreData = true
    private var currentFilter = "beef"

    init {
        loadMoreRecipes() // Charger les premières recettes
    }

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _recipes.value = emptyList()
            currentPage = 1
            isLoading = true
            hasMoreData = true
            currentFilter = query

            val fetchedRecipes = repository.fetchAndSaveRecipes(query, page = currentPage)
            _recipes.value = fetchedRecipes
            isLoading = false
        }
    }

    fun loadMoreRecipes() {
        if (isLoading || !hasMoreData) return
        isLoading = true

        viewModelScope.launch {
            Log.d("DEBUG", "Chargement de plus de recettes avec filtre : $currentFilter, page $currentPage")

            val moreRecipes = repository.fetchAndSaveRecipes(currentFilter, page = currentPage)

            if (moreRecipes.isNotEmpty()) {
                _recipes.value += moreRecipes
                currentPage++
            } else {
                hasMoreData = false
            }

            isLoading = false
        }
    }
}
