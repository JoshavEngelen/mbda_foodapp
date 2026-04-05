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
import com.example.foodapp.data.MealListViewModel
import com.example.foodapp.data.UiState
import com.example.foodapp.ui.MealItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(mealListViewModel: MealListViewModel, onMealClick: (String) -> Unit) {

    Scaffold (
        topBar = {
            TopAppBar(title = { Text("FoodApp") })
        }
    ) { padding ->
        when (val state = mealListViewModel.uiState) {
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
                        Card (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { onMealClick(meal.id) }
                        ) {
                            MealItem(
                                meal = meal,
                                onFavoriteClick = { mealListViewModel.toggleFavorite(meal.id) },
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
