package com.example.foodapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.data.FavoritesManager
import com.example.foodapp.data.RecipeViewModel
import com.example.foodapp.data.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(recipeId: String, viewModel: RecipeViewModel, onBackClick: () -> Unit) {
    val state = viewModel.uiState

    val meal = (state as? UiState.Success)
        ?.data
        ?.find { it.id == recipeId }

    var editMode by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf(meal?.name ?: "") }
    var instructions by remember { mutableStateOf(meal?.instructions ?: "") }

    val context = LocalContext.current
    val favoritesManager = remember { FavoritesManager(context) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var isFavorite by remember {
        mutableStateOf(favoritesManager.isFavorite(recipeId))
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        imageBitmap = bitmap
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        }
    }

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
                .verticalScroll(rememberScrollState())
        ) {

            if (meal == null) {
                Text("Loading...")
                return@Column
            }

            if (editMode) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    viewModel.saveEdit(recipeId, name, instructions)
                    editMode = false
                }) {
                    Text("Save")
                }

            }
            else {
                Text(text = meal.name, style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = meal.instructions)

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    editMode = true
                }) {
                    Text("Edit")
                }

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
                    when {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            cameraLauncher.launch(null)
                        }

                        else -> {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                }) {
                    Text("Take Photo")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    if (isFavorite) {
                        favoritesManager.removeFavorite(recipeId)
                    } else {
                        favoritesManager.addFavorite(recipeId)
                    }
                    isFavorite = !isFavorite
                }) {
                    Text(if (isFavorite) "Remove Favorite" else "Save Favorite")
                }
            }
        }
    }
}
