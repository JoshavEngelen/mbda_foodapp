package com.example.foodapp

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(recipeId: String, onBackClick: () -> Unit) {

    val context = LocalContext.current

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Recipe Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ){ padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text(text = "Recipe ID: $recipeId")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "Check this recipe: $recipeId")
                }
                context.startActivity(Intent.createChooser(intent, "Share"))
            }) {
                Text("Share Recipe")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                // Placeholder for camera feature
            }) {
                Text("Take Photo (Camera)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                // Placeholder for saving favorite
            }) {
                Text("Save Favorite")
            }
        }
    }
}