package com.example.foodapp.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.foodapp.api.MealUi

@Composable
fun MealList(
    meals: List<MealUi>,
    onMealClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sortedMeals = remember(meals) {
        meals.sortedWith(
            compareByDescending<MealUi> { it.isFavorite }
                .thenBy { it.name }
        )
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.fillMaxSize()
        ) {
            items(sortedMeals) { meal ->
                MealCard(meal, onMealClick, onFavoriteClick)
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            items(sortedMeals) { meal ->
                MealCard(meal, onMealClick, onFavoriteClick)
            }
        }
    }
}

@Composable
private fun MealCard(
    meal: MealUi,
    onMealClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onMealClick(meal.id) }
    ) {
        MealItem(
            meal = meal,
            onFavoriteClick = { onFavoriteClick(meal.id) },
            modifier = Modifier.padding(16.dp)
        )
    }
}
