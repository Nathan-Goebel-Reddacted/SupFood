package com.example.supfood.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = false) val recipeId: Int,
    val title: String,
    val publisher: String,
    val featuredImage: String,
    val rating: Int,
    val sourceUrl: String,
    val description: String,
    val cookingInstruction: String,
    val dateAdded: String,
    val dateUpdated: String,
    val longDateAdded: String,
    val longDateUpdated: String,
)

@Entity(tableName = "ingredients")
data class Ingredients(
    @PrimaryKey(autoGenerate = true) val ingredientsId:Int,
    val name: String,
)

@Entity(
    tableName = "ingredients_list",
    primaryKeys = ["ingredientId", "recipeId"],  // Composite Primary Key
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["recipeId"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Ingredients::class,
            parentColumns = ["ingredientId"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class IngredientsList(
    val ingredientId: Int,
    val recipeId: Int,
)