package com.example.foodapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.foodapp.api.MealUi

@Composable
fun MealItem(
    meal: MealUi,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = meal.name,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.weight(1f)
        )

        FavoriteButton(
            isFavorite = meal.isFavorite,
            onClick = onFavoriteClick
        )
    }
}
