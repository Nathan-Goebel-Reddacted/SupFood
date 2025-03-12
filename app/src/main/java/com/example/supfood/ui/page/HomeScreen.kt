package com.example.supfood.ui.page

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.navigation.NavController
import com.example.supfood.data.Recipe
import com.example.supfood.logic.SupfoodViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, viewModel: SupfoodViewModel) {
    val primaryColor = Color(0xFFFFD8A8)
    val secondaryColor = Color(0xFF1971C2)

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val recipes by viewModel.recipes.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryColor)
            .padding(16.dp)
    ) {
        // 🔎 Barre de recherche
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .background(Color.White, MaterialTheme.shapes.medium)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    viewModel.searchRecipes(searchQuery.text)
                },
                colors = ButtonDefaults.buttonColors(secondaryColor),
                modifier = Modifier.height(50.dp)
            ) {
                Text("Rechercher")
            }
        }

        // 📌 Boutons de filtres
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Gluten", "Pasta", "Veggie").forEach { filter ->
                Button(
                    onClick = {
                        viewModel.searchRecipes(filter)
                    },
                    colors = ButtonDefaults.buttonColors(secondaryColor),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .height(45.dp)
                        .defaultMinSize(minWidth = 90.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(filter, fontSize = 14.sp, maxLines = 1, softWrap = false)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 🍽️ Liste des recettes avec Scroll Infini
        LazyColumn(
            state = listState, // ✅ Maintient la position du scroll
            modifier = Modifier.fillMaxSize()
        ) {
            items(recipes, key = { it.recipeId }) { recipe ->
                RecipeItem(recipe, navController)
            }

            // 🔄 Chargement automatique quand on arrive en bas
            item {
                LaunchedEffect(listState) {
                    snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                        .collect { lastVisibleItem ->
                            if (lastVisibleItem != null && lastVisibleItem >= recipes.size - 5) {
                                coroutineScope.launch {
                                    viewModel.loadMoreRecipes()
                                }
                            }
                        }
                }
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp))
                }
            }
        }
    }
}



@Composable
fun RecipeItem(recipe: Recipe, navController: NavController) {
    val secondaryColor = Color(0xFF1971C2)

    Log.d("RecipeItem", "Image URL: ${recipe.featuredImage}")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable {
                navController.navigate("recipe/${recipe.recipeId}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Image(
                painter = rememberAsyncImagePainter(model = recipe.featuredImage),
                contentDescription = "Recipe Image",
                modifier = Modifier.size(100.dp).padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(
                    recipe.title,
                    fontWeight = FontWeight.Bold,
                    color = secondaryColor,
                    fontSize = 16.sp
                )
                Text(
                    "⭐ ${recipe.rating}",
                    color = secondaryColor,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Par ${recipe.publisher}",
                    color = secondaryColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}
