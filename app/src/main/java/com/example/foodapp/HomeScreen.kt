package com.example.foodapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Recipe(val id: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onRecipeClick: (String) -> Unit) {

    // Dummy data (replace with ViewModel later)
    val recipes = remember {
        listOf(
            Recipe("1", "Pizza"),
            Recipe("2", "Pasta"),
            Recipe("3", "Burger")
        )
    }

    Scaffold (
        topBar = {
            TopAppBar(title = { Text("Food Finder") })
        }
    ){ padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(recipes) { recipe ->
                RecipeItem(recipe, onRecipeClick)
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
