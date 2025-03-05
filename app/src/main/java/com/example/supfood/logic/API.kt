package com.example.supfood.logic

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
    ): APISearchResponse // 🔹 Retourne une liste de recettes

    @Headers("Authorization: Token 9c8b06d329136da358c2d00e76946b0111ce2c48")
    @GET("recipe/get/")
    suspend fun getRecipe(
        @Query("id") id: String
    ): Recipe // 🔹 Retourne directement un objet Recipe
}

// Classe qui gère la récupération des recettes
class RecipeRepository(private val recipeDao: RecipeDao) {
    private val api = RetrofitInstance.api

    suspend fun fetchRecipesPage(query: String, page: Int): List<Recipe> {
        return try {
            val response = api.searchRecipes(page, query)
            response.results // Retourne uniquement les recettes de cette page
        } catch (e: Exception) {
            e.printStackTrace()
            listOf() // Retourne une liste vide en cas d'erreur
        }
    }

    // 🔹 Récupérer plusieurs recettes en effectuant plusieurs appels API
    suspend fun searchRecipes(query: String, maxResults: Int = 30): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        var currentPage = 1
        var totalFetched = 0

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

    // 🔹 Récupérer une recette par ID (API)
    suspend fun getRecipe(id: String): Recipe? {
        return try {
            val recipe = api.getRecipe(id)
            recipe.ingredientList = recipe.ingredientList
            recipe
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchAndSaveRecipes(query: String, maxResults: Int = 30): List<Recipe> {
        val recipes = searchRecipes(query, maxResults)
        val savedRecipes = mutableListOf<Recipe>()

        for (recipe in recipes) {
            val existingRecipe = recipeDao.getRecipeWithIngredientsMapped(recipe.recipeId)

            if (existingRecipe == null) {
                recipeDao.saveRecipeWithIngredients(recipe, recipe.ingredientList)
                savedRecipes.add(recipe)
            } else {
                savedRecipes.add(existingRecipe)
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

