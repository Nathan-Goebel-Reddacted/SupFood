package com.example.supfood.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.supfood.data.Recipe
import com.example.supfood.logic.HomeViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val primaryColor = Color(0xFFFFD8A8) // Fond pastel
    val secondaryColor = Color(0xFF1971C2) // Texte et boutons

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val recipes by viewModel.recipes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryColor)
            .padding(16.dp)
    ) {
        // 🔎 Barre de recherche
        Row(modifier = Modifier.fillMaxWidth() .padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
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
                onClick = { viewModel.searchRecipes(searchQuery.text) },
                colors = ButtonDefaults.buttonColors(secondaryColor),
                modifier = Modifier.height(50.dp)
            ) {
                Text("Rechercher")
            }
        }

        // 📌 Boutons de filtres
        Row(
            modifier = Modifier.fillMaxWidth() .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Gluten", "Pasta", "Veggie").forEach { filter ->
                Button(
                    onClick = { viewModel.searchRecipes(filter) }, // 🔹 Recherche automatique avec le filtre
                    colors = ButtonDefaults.buttonColors(secondaryColor),
                    modifier = Modifier
                        .weight(1f) // 📌 Tous les boutons auront la même largeur
                        .padding(4.dp)
                        .height(45.dp)
                        .defaultMinSize(minWidth = 90.dp),
                    shape = MaterialTheme.shapes.medium // Rend les boutons moins arrondis si nécessaire
                ) {
                    Text(
                        filter,
                        fontSize = 14.sp,
                        maxLines = 1, // 📌 Empêche le retour à la ligne
                        softWrap = false)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 🍽️ Liste des recettes
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(recipes) { recipe ->
                RecipeItem(recipe)
            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe) {
    val secondaryColor = Color(0xFF1971C2)

    Log.d("RecipeItem", "Image URL: ${recipe.featuredImage}")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Image(
                painter = rememberAsyncImagePainter(model = recipe.featuredImage),
                contentDescription = "Recipe Image",
                modifier = Modifier.size(100.dp) .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(recipe.title, fontWeight = FontWeight.Bold, color = secondaryColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Voir plus", color = secondaryColor, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
