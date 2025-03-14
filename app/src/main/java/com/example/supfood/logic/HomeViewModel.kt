package com.example.supfood.logic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
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
    private val allRecipes = mutableListOf<Recipe>()
    private var hasMoreData = true
    var isLoading = false
    private var currentFilter = "beef"

    init {
        loadRecipesFromLocal()
    }

    // Charger les recettes depuis la base locale
    private fun loadRecipesFromLocal() {
        viewModelScope.launch {
            try {
                val localRecipes = recipeDao.getAllRecipes()
                    .mapNotNull { recipeDao.getRecipeWithIngredientsMapped(it.recipeId) }

                if (localRecipes.isNotEmpty()) {
                    allRecipes.clear()
                    allRecipes.addAll(localRecipes)
                    _recipes.value = allRecipes.toList()
                } else {
                    if (getApplication<Application>().isOnline()) {
                        loadMoreRecipes()
                    } else {
                        Log.w("SupfoodViewModel", "No local recipes and no internet connection.")
                    }
                }
            } catch (e: Exception) {
                Log.e("SupfoodViewModel", "Error loading recipes from local database", e)
            }
        }
    }

    // Recherche avec filtrage
    fun searchRecipes(query: String) {
        viewModelScope.launch {
            allRecipes.clear()
            _recipes.value = emptyList()
            currentPage = 1
            hasMoreData = true
            currentFilter = query
            loadMoreRecipes()
        }
    }

    fun loadMoreRecipes() {
        if (isLoading || !hasMoreData) return

        isLoading = true
        viewModelScope.launch {
            try {
                if (getApplication<Application>().isOnline()) {
                    //Mode en ligne
                    val newRecipes = repository.fetchAndSaveRecipes(currentFilter, currentPage)

                    if (newRecipes.isNotEmpty()) {
                        val updatedList = allRecipes.toMutableList().apply { addAll(newRecipes) }
                        _recipes.value = updatedList.toList()
                        currentPage++
                    } else {
                        hasMoreData = false
                    }
                } else {
                    //Mode hors ligne
                    Log.w("SupfoodViewModel", "No internet. Searching local recipes with filter: $currentFilter")

                    val localRecipes = recipeDao.searchRecipes(currentFilter)
                        .mapNotNull { recipeDao.getRecipeWithIngredientsMapped(it.recipeId) }

                    if (localRecipes.isNotEmpty()) {
                        allRecipes.clear()
                        allRecipes.addAll(localRecipes)
                        _recipes.value = allRecipes.toList()
                    } else {
                        Log.w("SupfoodViewModel", "No local recipes available for filter: $currentFilter")
                    }
                }
            } catch (e: Exception) {
                Log.e("SupfoodViewModel", "Error loading recipes", e)
            }
            isLoading = false
        }
    }

    fun loadPreviousRecipes() {
        if (isLoading || currentPage == 1) {
            Log.w("SupfoodViewModel", "Tentative de rechargement ignorée : isLoading=$isLoading, currentPage=$currentPage")
            return
        }
        isLoading = true

        viewModelScope.launch {
            val previousPage = currentPage - 1
            Log.d("SupfoodViewModel", "Chargement des anciennes recettes: page=$previousPage, filtre=$currentFilter")

            try {
                val oldRecipes = repository.fetchAndSaveRecipes(currentFilter, previousPage)

                if (oldRecipes.isNotEmpty()) {
                    allRecipes.addAll(0, oldRecipes)
                    currentPage--

                    Log.d("SupfoodViewModel", "Anciennes recettes chargées. Nombre total de recettes en mémoire: ${allRecipes.size}")

                    // Supprimer les recettes trop loin en avant
                    if (allRecipes.size > 90) {
                        val removedCount = allRecipes.size - 90
                        allRecipes.subList(90, allRecipes.size).clear()
                        Log.i("SupfoodViewModel", "Suppression de $removedCount recettes trop récentes pour limiter la mémoire")
                    }

                    _recipes.value = allRecipes.toList()
                } else {
                    Log.w("SupfoodViewModel", "Aucune ancienne recette trouvée")
                }
            } catch (e: Exception) {
                Log.e("SupfoodViewModel", "Erreur lors du chargement des anciennes recettes", e)
            }

            isLoading = false
            Log.d("SupfoodViewModel", "Fin du chargement des anciennes recettes")
        }
    }

}
