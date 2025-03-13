package com.example.supfood.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.supfood.data.Recipe



@Composable
fun RecipePage(recipe: Recipe) {
    val primaryColor = Color(0xFFFFD8A8)
    val secondaryColor = Color(0xFF1971C2)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryColor)
            .padding(5.dp)
    ) {
        // Images + Infos de base
        Row {
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(8.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = recipe.featuredImage),
                    contentDescription = "Recipe Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = recipe.title,
                    color = secondaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Publisher: ${recipe.publisher}\nRating: ${recipe.rating}",
                    color = secondaryColor,
                    fontSize = 14.sp
                )

                val uriHandler = LocalUriHandler.current
                Text(
                    text = AnnotatedString("Link To Original"),
                    style = TextStyle(
                        color = secondaryColor,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable {
                        uriHandler.openUri(recipe.sourceUrl)
                    }
                )
            }
        }

        // Liste d'ingrédients
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            recipe.ingredientList.forEach { ingredient ->
                Text(
                    text = "- $ingredient",
                    color = secondaryColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun RecipePreview() {
    val recipe = Recipe(
        recipeId = 2050,
        title = "Chicken, sweet potato & coconut curry",
        publisher = "jessica",
        featuredImage = "https://nyc3.digitaloceanspaces.com/food2fork/food2fork-static/featured_images/2050/featured_image.png",
        rating = 50,
        sourceUrl = "http://www.bbcgoodfood.com/recipes/1555/chicken-sweet-potato-and-coconut-curry",
        description = "N/A",
        cookingInstruction = "",
        dateAdded = "November 11 2020",
        dateUpdated = "November 11 2020",
        longDateAdded = "1606349252",
        longDateUpdated = "1606349252",
        ingredientList = listOf(
            "175g frozen peas",
            "300ml chicken stock",
            "1 tbsp sunflower oil",
            "2 tsp mild curry paste",
            "400ml can coconut milk",
            "4 tbsp red split lentils",
            "2 medium-sized sweet potatoes, peeled and cut into chunks",
            "2 large boneless, skinless chicken breasts, cut into pieces"
        )
    )
    RecipePage(recipe)
}*/

