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

    @SerializedName("featured_image") var featuredImage: String,
    var rating: Int,

    @SerializedName("source_url") var sourceUrl: String,
    var description: String? = null,

    @SerializedName("cooking_instructions") var cookingInstructions: String? = null,

    @SerializedName("date_added") var dateAdded: String,
    @SerializedName("date_updated") var dateUpdated: String,

    @SerializedName("long_date_added") var longDateAdded: Long,
    @SerializedName("long_date_updated") var longDateUpdated: Long,

    @Ignore @SerializedName("ingredients") var ingredientList: List<String> = listOf()
) {
    constructor() : this(0, "", "", "", 0, "", "", "", "", "", 0, 0, listOf())
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
    constructor() : this(0, 0)
}
