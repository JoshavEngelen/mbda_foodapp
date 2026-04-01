package com.example.foodapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodapp.data.RecipeViewModel
import com.example.foodapp.data.UiState

data class Recipe(val id: String, val name: String)

@Composable
fun HomeScreen(onRecipeClick: (String) -> Unit) {
    // In a real app, use viewModel() from lifecycle-viewmodel-compose
    val viewModel = remember { RecipeViewModel() }

    LaunchedEffect(Unit) {
        viewModel.fetchMeals()
    }

    when (val state = viewModel.uiState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Error: ${state.message}")
            }
        }

        is UiState.Success ->
            LazyColumn {
                items(state.data) { meal ->
                    RecipeItem(
                        recipe = Recipe(meal.idMeal, meal.strMeal),
                        onClick = onRecipeClick
                    )
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