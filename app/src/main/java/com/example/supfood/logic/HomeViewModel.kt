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
            val localRecipes = recipeDao.getAllRecipes()
            if (localRecipes.isNotEmpty()) {
                allRecipes.addAll(localRecipes)
                _recipes.value = allRecipes
            } else {
                loadMoreRecipes()
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
        if (isLoading || !hasMoreData) {
            Log.w("SupfoodViewModel", "Tentative de chargement ignorée : isLoading=$isLoading, hasMoreData=$hasMoreData")
            return
        }
        isLoading = true

        viewModelScope.launch {
            Log.d("SupfoodViewModel", "Début du chargement des recettes: page=$currentPage, filtre=$currentFilter")

            try {
                val newRecipes = repository.fetchAndSaveRecipes(currentFilter, currentPage)

                if (newRecipes.isNotEmpty()) {
                    allRecipes.addAll(newRecipes)
                    currentPage++

                    Log.d("SupfoodViewModel", "Nouvelle page chargée. Nombre total de recettes en mémoire: ${allRecipes.size}")

                    // Limiter le nombre total de recettes en mémoire à 90 max
                    if (allRecipes.size > 90) {
                        val removedCount = allRecipes.size - 90
                        allRecipes.subList(0, removedCount).clear()
                        Log.i("SupfoodViewModel", "Suppression de $removedCount anciennes recettes pour limiter la mémoire")
                    }

                    _recipes.value = allRecipes.toList()
                } else {
                    hasMoreData = false
                    Log.w("SupfoodViewModel", "Aucune nouvelle recette trouvée, arrêt du chargement")
                }
            } catch (e: Exception) {
                Log.e("SupfoodViewModel", "Erreur lors du chargement des recettes", e)
            }

            isLoading = false
            Log.d("SupfoodViewModel", "Fin du chargement des recettes")
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
