package com.example.supfood.logic


import android.util.Log
import androidx.room.Transaction
import com.example.supfood.data.APISearchResponse
import com.example.supfood.data.Recipe
import com.example.supfood.data.RetrofitInstance
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// Interface pour les appels API
interface Food2ForkApi {

    @Headers("Authorization: Token 9c8b06d329136da358c2d00e76946b0111ce2c48")
    @GET("recipe/search/")
    suspend fun searchRecipes(
        @Query("page") page: Int,
        @Query("query") query: String
    ): APISearchResponse

    @Headers("Authorization: Token 9c8b06d329136da358c2d00e76946b0111ce2c48")
    @GET("recipe/get/")
    suspend fun getRecipe(
        @Query("id") id: Int
    ): Recipe
}

// Classe qui gère la récupération des recettes
class RecipeRepository(private val recipeDao: RecipeDao) {
    private val api = RetrofitInstance.api

    suspend fun fetchRecipesPage(query: String="beef", page: Int): List<Recipe> {
        return try {
            Log.d("RecipeRepository", "Fetching recipes for query: $query, page: $page")
            val response = api.searchRecipes(page, query) // Ici on passe bien `page`
            Log.d("RecipeRepository", "Raw API response: $response")
            response.results
        } catch (e: Exception) {
            listOf()
        }
    }

    // 🔹 Récupérer plusieurs recettes en effectuant plusieurs appels API
    suspend fun searchRecipes(query: String, page: Int, maxResults: Int = 30): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        var totalFetched = 0
        var currentPage = page // Utilisation de `page` passé en paramètre

        while (totalFetched < maxResults) {
            val recipesPage = fetchRecipesPage(query, currentPage)

            if (recipesPage.isEmpty()) break // Stop si plus de résultats

            val remainingNeeded = maxResults - totalFetched
            val recipesToAdd = recipesPage.take(remainingNeeded)

            recipes.addAll(recipesToAdd)
            totalFetched += recipesToAdd.size

            if (recipesToAdd.size < 30) break // Si une page contient moins de 30 recettes, plus rien à charger

            currentPage++ // Passer à la page suivante
        }
        return recipes
    }


    // 🔹 Récupérer une recette par ID (API)
    suspend fun getRecipe(id: Int): Recipe? {
        return try {
            val recipe = api.getRecipe(id)
            recipe.ingredientList = recipe.ingredientList
            recipe
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchAndSaveRecipes(query: String = "beef", page: Int = 1, maxResults: Int = 30): List<Recipe> {
        val recipes = searchRecipes(query, page, maxResults)
        val savedRecipes = mutableListOf<Recipe>()
        if (recipes.isEmpty()) {
            Log.d("RecipeRepository", "No more recipes.")
        }

        for (recipe in recipes) {
            try {
                val existingRecipe = recipeDao.getRecipeWithIngredientsMapped(recipe.recipeId)
                if (existingRecipe == null) {
                    recipeDao.saveRecipeWithIngredients(recipe, recipe.ingredientList)
                    savedRecipes.add(recipe)
                    Log.d("RecipeRepository", "Saved new recipe: ${recipe.title}")
                } else {
                    savedRecipes.add(existingRecipe)
                    Log.d("RecipeRepository", "Recipe already exists: ${recipe.title}")
                }
            } catch (e: Exception) {
                Log.e("RecipeRepository", "Error saving recipe: ${recipe.title}", e)
            }
        }
        return savedRecipes
    }

    @Transaction
    suspend fun getRecipeWithIngredientsMapped(recipeId: Int): Recipe? {
        val recipe = recipeDao.getRecipeById(recipeId) ?: return null
        val ingredients = recipeDao.getIngredientsForRecipe(recipeId).map { it.name }
        return recipe.apply { ingredientList = ingredients }
    }

}





