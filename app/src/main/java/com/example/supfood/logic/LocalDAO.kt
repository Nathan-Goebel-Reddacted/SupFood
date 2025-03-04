package com.example.supfood.logic
import androidx.room.*
import com.example.supfood.data.Ingredients
import com.example.supfood.data.IngredientsList
import com.example.supfood.data.Recipe

@Dao
interface RecipeDao {

    // Insert Recipe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    // Insert Ingredient
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredients)

    // Insert into Junction Table (Recipe <-> Ingredient)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredientList(crossRef: IngredientsList)

    // Get all ingredients for a given recipe
    @Query("""
        SELECT ingredients.* FROM ingredients
        INNER JOIN ingredients_list ON ingredients.ingredientsId = ingredients_list.ingredientId
        WHERE ingredients_list.recipeId = :recipeId
    """)
    suspend fun getIngredientsForRecipe(recipeId: Int): List<Ingredients>

    // Get all recipes for a given ingredient
    @Query("""
        SELECT recipes.* FROM recipes
        INNER JOIN ingredients_list ON recipes.recipeId = ingredients_list.recipeId
        WHERE ingredients_list.ingredientId = :ingredientId
    """)
    suspend fun getRecipesForIngredient(ingredientId: Int): List<Recipe>
}
