package com.example.foodapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodapp.api.MealUi

@Composable
fun MealList(
    meals: List<MealUi>,
    onMealClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(meals) { meal ->
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
    }
}
