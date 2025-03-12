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
        loadRecipesFromLocal() // Charger depuis la base locale en priorité
    }

    /**
     * Charger les recettes depuis la base de données locale
     * Si vide, récupérer depuis l'API
     */
    fun loadRecipesFromLocal() {
        viewModelScope.launch {
            val localRecipes = recipeDao.getAllRecipes() // Récupérer depuis Room
            if (localRecipes.isNotEmpty()) {
                _recipes.value = localRecipes
            } else {
                loadMoreRecipes() // Si aucune recette locale, charger depuis l'API
            }
        }
    }

    /**
     * Rechercher des recettes en mode en ligne et hors ligne
     */
    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _recipes.value = emptyList() // ✅ Efface les résultats précédents
            currentPage = 1
            isLoading = true
            hasMoreData = true
            currentFilter = query

            try {
                val fetchedRecipes = repository.fetchAndSaveRecipes(query, page = currentPage)

                if (fetchedRecipes.isEmpty()) {
                    // ✅ Si aucune recette n'est trouvée, affiche un message par défaut
                    _recipes.value = listOf(
                        Recipe(
                            recipeId = -1,
                            title = "Aucune recette trouvée",
                            featuredImage = "",
                            publisher = "",
                            rating = 0,
                            sourceUrl = "",
                            description = "",
                            cookingInstructions = "",
                            dateAdded = "",
                            dateUpdated = "",
                            longDateAdded = 0,
                            longDateUpdated = 0,
                            ingredientList = listOf()
                        )
                    )
                } else {
                    _recipes.value = fetchedRecipes
                }
            } catch (e: Exception) {
                // ✅ Gestion des erreurs (problème API, internet, etc.)
                _recipes.value = listOf(
                    Recipe(
                        recipeId = -1,
                        title = "Erreur lors du chargement des recettes",
                        featuredImage = "",
                        publisher = "",
                        rating = 0,
                        sourceUrl = "",
                        description = "",
                        cookingInstructions = "",
                        dateAdded = "",
                        dateUpdated = "",
                        longDateAdded = 0,
                        longDateUpdated = 0,
                        ingredientList = listOf()
                    )
                )
            }

            isLoading = false
        }
    }


    /**
     * Charger plus de recettes avec gestion du mode hors-ligne
     */
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
