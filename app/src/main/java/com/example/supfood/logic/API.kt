package com.example.supfood.logic

import android.util.Log
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

}

// Gère la récupération des recettes
class RecipeRepository(private val recipeDao: RecipeDao) {
    private val api = RetrofitInstance.api

    private suspend fun fetchRecipesPage(query: String="beef", page: Int): List<Recipe> {
        return try {
            Log.d("RecipeRepository", "Fetching recipes for query: $query, page: $page")
            val response = api.searchRecipes(page, query)
            Log.d("RecipeRepository", "Raw API response: $response")
            response.results
        } catch (e: Exception) {
            Log.e("RecipeRepository", "API call failed, fetching from database", e)
            recipeDao.searchRecipes(query)
        }
    }

    // 🔹 Récupérer plusieurs recettes en effectuant plusieurs appels API
    private suspend fun searchRecipes(query: String, page: Int, maxResults: Int = 30): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        var totalFetched = 0
        var currentPage = page

        while (totalFetched < maxResults) {
            val recipesPage = fetchRecipesPage(query, currentPage)

            if (recipesPage.isEmpty()) break

            val remainingNeeded = maxResults - totalFetched
            val recipesToAdd = recipesPage.take(remainingNeeded)

            recipes.addAll(recipesToAdd)
            totalFetched += recipesToAdd.size

            if (recipesToAdd.size < 30) break

            currentPage++
        }
        return recipes
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
                    recipeDao.insertRecipe(recipe)
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
}





