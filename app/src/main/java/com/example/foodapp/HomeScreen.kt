package com.example.foodapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodapp.api.MealUi
import com.example.foodapp.data.RecipeViewModel
import com.example.foodapp.data.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: RecipeViewModel, onRecipeClick: (String) -> Unit) {
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
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${state.message}")
                }
            }

            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    items(state.data) { meal ->
                        MealItem(
                            meal = meal,
                            onRecipeClick = onRecipeClick,
                            onClick = { mealId -> viewModel.toggleFavorite(mealId) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MealItem(
    meal: MealUi,
    onRecipeClick: (String) -> Unit,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable { onRecipeClick(meal.id) }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = meal.name,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { onClick(meal.id) }) {
                Text(
                    text = if (meal.isFavorite) "★" else "☆",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}