package com.example.supfood.logic

import androidx.room.*
import com.example.supfood.data.Ingredients
import com.example.supfood.data.IngredientsList
import com.example.supfood.data.Recipe

@Dao
interface RecipeDao {

    //Insérer une recette dans la base
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    //Insérer un ingrédient dans la base
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredients): Long

    //Insérer une relation `Recipe <-> Ingredient` (évite les doublons)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredientList(crossRef: IngredientsList)

    //Vérifier si un ingrédient existe déjà en base
    @Query("SELECT * FROM ingredients WHERE name = :name LIMIT 1")
    suspend fun getIngredientByName(name: String): Ingredients?

    //Vérifier si une relation `Recipe <-> Ingredient` existe déjà
    @Query("""
        SELECT * FROM ingredients_list WHERE ingredientsId = :ingredientId AND recipeId = :recipeId LIMIT 1
    """)
    suspend fun getIngredientListEntry(ingredientId: Int, recipeId: Int): IngredientsList?

    //Récupérer une recette spécifique par son ID
    @Query("SELECT * FROM recipes WHERE recipeId = :id LIMIT 1")
    suspend fun getRecipeById(id: Int): Recipe?

    //Récupérer tous les ingrédients associés à une recette
    @Query("""
        SELECT ingredients.* FROM ingredients
        INNER JOIN ingredients_list ON ingredients.ingredientsId = ingredients_list.ingredientsId
        WHERE ingredients_list.recipeId = :recipeId
    """)
    suspend fun getIngredientsForRecipe(recipeId: Int): List<Ingredients>

    // Récupérer toutes les recettes enregistrées en local
    @Query("SELECT * FROM recipes ORDER BY longDateAdded DESC")
    suspend fun getAllRecipes(): List<Recipe>

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' ORDER BY longDateAdded DESC")
    suspend fun searchRecipes(query: String): List<Recipe>

    //Insérer un ingrédient en évitant les doublons
    suspend fun safeInsertIngredient(ingredient: Ingredients): Int {
        val existingIngredient = getIngredientByName(ingredient.name)
        return existingIngredient?.ingredientsId ?: insertIngredient(ingredient).toInt()
    }

    //Insérer une relation `Recipe <-> Ingredient` uniquement si elle n’existe pas
    suspend fun safeInsertIngredientList(crossRef: IngredientsList) {
        val existingEntry = getIngredientListEntry(crossRef.ingredientsId, crossRef.recipeId)
        if (existingEntry == null) {
            insertIngredientList(crossRef)
        }
    }

    //Récupérer une recette avec ses ingrédients en une seule transaction
    @Transaction
    suspend fun getRecipeWithIngredientsMapped(recipeId: Int): Recipe? {
        val recipe = getRecipeById(recipeId) ?: return null
        val ingredients = getIngredientsForRecipe(recipeId).map { it.name }
        return recipe.apply { ingredientList = ingredients }
    }

    //Insérer une recette et ses ingrédients en une seule transaction
    @Transaction
    suspend fun saveRecipeWithIngredients(recipe: Recipe, ingredients: List<String>) {
        insertRecipe(recipe)

        for (ingredientName in ingredients) {
            val ingredientId = safeInsertIngredient(Ingredients(0, ingredientName))
            safeInsertIngredientList(IngredientsList(ingredientId, recipe.recipeId))
        }
    }

}


