package com.example.foodapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.foodapp.api.MealUi
import com.example.foodapp.data.DetailUiState
import com.example.foodapp.data.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(viewModel: DetailViewModel, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DetailUiState.Error -> Text(state.message)
                is DetailUiState.Success -> {
                    val meal = state.meal
                    if (viewModel.isEditing) {
                        EditView(viewModel)
                    } else {
                        DisplayView(
                            meal = meal,
                            onEdit = { viewModel.startEditing(meal) },
                            onShare = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Check this recipe: ${meal.name}")
                                }
                                context.startActivity(Intent.createChooser(intent, "Share"))
                            },
                            onTakePhoto = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    cameraLauncher.launch(null)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            onFavorite = { viewModel.toggleFavorite() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditView(viewModel: DetailViewModel) {
    OutlinedTextField(
        value = viewModel.editName,
        onValueChange = { viewModel.editName = it },
        label = { Text("Name") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = viewModel.editInstructions,
        onValueChange = { viewModel.editInstructions = it },
        label = { Text("Instructions") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(16.dp))
    Row {
        Button(onClick = { viewModel.saveChanges() }) { Text("Save") }
        Spacer(Modifier.width(8.dp))
        OutlinedButton(onClick = { viewModel.cancelEditing() }) { Text("Cancel") }
    }
}

@Composable
fun DisplayView(
    meal: MealUi,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onTakePhoto: () -> Unit,
    onFavorite: () -> Unit
) {
    Text(text = meal.name, style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(8.dp))
    Text(text = meal.instructions)
    Spacer(Modifier.height(16.dp))

    Button(onClick = onEdit) { Text("Edit") }
    Spacer(Modifier.height(8.dp))
    Button(onClick = onShare) { Text("Share Recipe") }
    Spacer(Modifier.height(8.dp))
    Button(onClick = onTakePhoto) { Text("Take Photo") }
    Spacer(Modifier.height(8.dp))
    Button(onClick = onFavorite) {
        Text(if (meal.isFavorite) "Remove Favorite" else "Save Favorite")
    }
}
