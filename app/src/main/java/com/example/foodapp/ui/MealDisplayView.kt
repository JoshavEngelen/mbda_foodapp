package com.example.foodapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodapp.api.MealUi

@Composable
fun DisplayView(
    meal: MealUi,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        MealImageHeader(uriString = meal.imageUri)
        
        Spacer(Modifier.height(8.dp))

        MealItem(
            meal = meal,
            onFavoriteClick = onFavorite
        )
        
        Spacer(Modifier.height(8.dp))
        Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) { Text("Edit Meal") }
        Spacer(Modifier.height(4.dp))
        Button(onClick = onShare, modifier = Modifier.fillMaxWidth()) { Text("Share Meal") }

        Spacer(Modifier.height(16.dp))
        Text(text = meal.instructions)
        Spacer(Modifier.height(16.dp))
    }
}
