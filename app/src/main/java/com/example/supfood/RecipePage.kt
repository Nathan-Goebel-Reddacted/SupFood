package com.example.supfood


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.supfood.data.Recipe



@Composable
fun RecipePage(recipe: Recipe){
    val primaryColor = 0xFFFFd8a8
    val secondaryColor = 0xFF1971C2
    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(Color(primaryColor))
            .padding(5.dp)
    ){
        //images + basic info
        Row{
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth(0.5F)
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
            Column{
                Text(
                    text = recipe.title,
                    color = Color(secondaryColor),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text =
                        "Publisher:${recipe.publisher}\n" +
                                "rating:${recipe.rating}",
                    color = Color(secondaryColor),
                    fontSize = 14.sp
                )
                val uriHandler = LocalUriHandler.current
                ClickableText(
                    text = AnnotatedString("Link To Original"),
                    style = TextStyle(
                        color = Color(secondaryColor),
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline
                    ),
                    onClick = {
                        uriHandler.openUri(recipe.sourceUrl)
                    }
                )
            }
        }
        //list of ingredient

        for( ingredient in recipe.ingredientList){
            Text(
                text = ingredient,
                color = Color(secondaryColor),
                fontSize = 14.sp
            )
    }
    }
}

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
}

