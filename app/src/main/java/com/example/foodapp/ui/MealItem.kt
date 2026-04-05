package com.example.foodapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodapp.api.MealUi

@Composable
fun MealItem(
    meal: MealUi,
    onMealClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable { onMealClick(meal.id) }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MealTitle(
                name = meal.name,
                modifier = Modifier.weight(1f)
            )

            FavoriteButton(
                isFavorite = meal.isFavorite,
                onClick = { onFavoriteClick(meal.id) }
            )
        }
    }
}

@Composable
fun MealTitle(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}
