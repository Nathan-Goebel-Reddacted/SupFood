package com.example.supfood.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("pk") var recipeId: Int,
    var title: String,
    var publisher: String,
    var featuredImage: String,
    var rating: Int,
    var sourceUrl: String,
    var description: String,
    var cookingInstruction: String,
    var dateAdded: String,
    var dateUpdated: String,
    var longDateAdded: String,
    var longDateUpdated: String,

    @Ignore // 🔹 Room ne stockera pas `ingredientList`
    var ingredientList: List<String> = listOf()
){
    constructor() : this(0, "", "", "", 0, "", "", "", "", "", "", "") // ✅ Room peut maintenant instancier Recipe
}


@Entity(
    tableName = "ingredients",
    indices = [Index(value = ["ingredientsId"], unique = true), Index(value = ["name"], unique = true)]
)
data class Ingredients(
    @PrimaryKey(autoGenerate = true) var ingredientsId: Int,
    var name: String,
){
    constructor() : this(-1, "")
}

@Entity(
    tableName = "ingredients_list",
    primaryKeys = ["ingredientsId", "recipeId"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["recipeId"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredients::class,
            parentColumns = ["ingredientsId"],
            childColumns = ["ingredientsId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["recipeId"]), Index(value = ["ingredientsId"])]
)
data class IngredientsList(
    var ingredientsId: Int,
    var recipeId: Int
){
    constructor() : this(0, 0) // ✅ Room peut instancier IngredientsList
}
