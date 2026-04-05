package com.example.foodapp.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(modifier = modifier.fillMaxSize()) {
            MealImageHeader(
                uriString = meal.imageUri,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
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
    } else {
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            MealImageHeader(
                uriString = meal.imageUri,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
            
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
}
