package com.example.supfood

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.supfood.data.Recipe
import com.example.supfood.ui.RecipeViewModel
import com.example.supfood.ui.theme.SupFoodTheme

class MainActivity : ComponentActivity() {
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SupFoodTheme {
                RecipeScreen(recipeViewModel)
            }
        }
    }
}

@Composable
fun RecipeScreen(viewModel: RecipeViewModel) {
    val recipes by viewModel.recipes.collectAsState()

    LaunchedEffect(recipes) {
        recipes.forEachIndexed { index, recipe ->
            Log.d("RecipeScreen", "Displaying recipe $index: ${recipe.recipeId} - ${recipe.title}")
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = {
            Log.d("RecipeScreen", "Button clicked")
            viewModel.searchRecipes("pasta")
        }) {
            Text("Rechercher des recettes")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(recipes) { recipe ->
                RecipeItem(recipe)
            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${recipe.recipeId} - ${recipe.title}", style = MaterialTheme.typography.titleLarge)
            Text(text = "Note: ${recipe.rating}/5")
        }
    }
}

@Preview
@Composable
fun PreviewRecipeScreen() {
    SupFoodTheme {
        RecipeScreen(viewModel = RecipeViewModel(Application()))
    }
}