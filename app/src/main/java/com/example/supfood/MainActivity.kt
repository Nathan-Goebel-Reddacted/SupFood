package com.example.supfood
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.supfood.logic.SupfoodViewModel
import com.example.supfood.ui.page.HomeScreen
import com.example.supfood.ui.page.LoadScreen
import com.example.supfood.ui.page.RecipePage


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")

        setContent {
            val navController = rememberNavController()
            val viewModel: SupfoodViewModel = viewModel()
            val recipes by viewModel.recipes.collectAsState()
            Log.d("MainActivity", "SupfoodViewModel instance created")
            LaunchedEffect(recipes) {
                if (recipes.isNotEmpty()) {
                    Log.d("Navigation", "Navigating to home screen")
                    navController.navigate("home") {
                        popUpTo("load") { inclusive = true }
                    }
                }
            }

            NavHost(navController, startDestination = "load") {
                // Chargement au démarrage
                composable("load") {
                    Log.d("Navigation", "Showing LoadScreen()")
                    LoadScreen(navController)

                    val hasFetchedData = remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        if (!hasFetchedData.value) {
                            Log.d("MainActivity", "Fetching recipes...")
                            viewModel.searchRecipes("")
                            hasFetchedData.value = true
                        }
                    }
                }

                // Écran principal
                composable("home") {
                    Log.d("HomeScreen", "Displaying HomeScreen with ${recipes.size} recipes")

                    if (recipes.isNotEmpty()) {
                        HomeScreen(navController, viewModel)
                    } else {
                        LoadScreen(navController)
                    }
                }

                // Recipe detail screen
                composable("recipe/{recipeId}") { backStackEntry ->
                    val recipeId = backStackEntry.arguments?.getString("recipeId")?.toIntOrNull()
                    val recipe = recipes.find { it.recipeId == recipeId }

                    if (recipe != null) {
                        Log.d("RecipePage", "Navigating to RecipePage for recipe ID: $recipeId")
                        RecipePage(recipe)
                    } else {
                        Log.d("RecipePage", "Recipe not found for ID: $recipeId, returning to home")
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}