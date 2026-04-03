package com.example.foodapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.foodapp.data.FavoritesManager
import com.example.foodapp.data.RecipeViewModel
import com.example.foodapp.data.UiState

data class Recipe(val id: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onRecipeClick: (String) -> Unit) {
    // In a real app, use viewModel() from lifecycle-viewmodel-compose
    val viewModel = remember { RecipeViewModel() }

    val context = LocalContext.current
    val favoritesManager = remember { FavoritesManager(context) }

    LaunchedEffect(Unit) {
        viewModel.fetchMeals()
    }

    Scaffold (
        topBar = {
            TopAppBar(title = { Text("FoodApp") })
        }
    ) { padding ->
        when (val state = viewModel.uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("Error: ${state.message}")
                }
            }

            is UiState.Success ->
                LazyColumn (
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    items(state.data) { meal ->
                        val isFav = favoritesManager.isFavorite(meal.idMeal)
                        RecipeItem(
                            recipe = Recipe(
                                meal.idMeal,
                                if (isFav) "⭐ ${meal.strMeal}" else meal.strMeal
                            ),
                            onClick = onRecipeClick
                        )
                    }
                }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(recipe.id) }
    ) {
        Text(
            text = recipe.name,
            modifier = Modifier.padding(16.dp)
        )
    }
}