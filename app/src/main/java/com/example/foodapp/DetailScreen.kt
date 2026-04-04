package com.example.foodapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.foodapp.api.MealUi
import com.example.foodapp.data.DetailUiState
import com.example.foodapp.data.DetailViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(viewModel: DetailViewModel, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val uri = saveBitmapToUri(context, it, "recipe_photo_${System.currentTimeMillis()}.jpg")
            viewModel.saveImage(uri)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.saveImage(it)
        }
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
            when (uiState) {
                is DetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DetailUiState.Error -> Text(uiState.message)
                is DetailUiState.Success -> {
                    val meal = uiState.meal
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
                            onPickPhoto = {
                                imagePickerLauncher.launch("image/*")
                            },
                            onFavorite = { viewModel.toggleFavorite() }
                        )
                    }
                }
            }
        }
    }
}

private fun saveBitmapToUri(context: Context, bitmap: Bitmap, fileName: String): Uri {
    val file = File(context.cacheDir, fileName)
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return file.toUri()
}

@Composable
fun EditView(viewModel: DetailViewModel) {
    val scope = rememberCoroutineScope()
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
        Button(onClick = {
            scope.launch {
                viewModel.saveChanges()
            }
        }) {
            Text("Save")
        }
        Spacer(Modifier.width(8.dp))
        OutlinedButton(onClick = { viewModel.cancelEditing() }) { Text("Cancel") }
    }
}

@Composable
fun RecipeImageHeader(
    uriString: String?,
    onTakePhoto: () -> Unit,
    onPickPhoto: () -> Unit
) {
    val context = LocalContext.current
    val bitmap = remember(uriString) {
        if (uriString == null) null
        else try {
            val uri = uriString.toUri()
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Recipe Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("No photo added", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onTakePhoto,
                modifier = Modifier.weight(1f)
            ) {
                Text("Take Photo")
            }
            OutlinedButton(
                onClick = onPickPhoto,
                modifier = Modifier.weight(1f)
            ) {
                Text("Select Photo")
            }
        }
    }
}

@Composable
fun DisplayView(
    meal: MealUi,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onTakePhoto: () -> Unit,
    onPickPhoto: () -> Unit,
    onFavorite: () -> Unit
) {
    RecipeImageHeader(
        uriString = meal.imageUri,
        onTakePhoto = onTakePhoto,
        onPickPhoto = onPickPhoto
    )

    Spacer(Modifier.height(8.dp))
    Text(text = meal.name, style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(8.dp))
    Text(text = meal.instructions)
    Spacer(Modifier.height(16.dp))

    Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) { Text("Edit Recipe") }
    Spacer(Modifier.height(8.dp))
    Button(onClick = onShare, modifier = Modifier.fillMaxWidth()) { Text("Share Recipe") }
    Spacer(Modifier.height(8.dp))
    Button(onClick = onFavorite, modifier = Modifier.fillMaxWidth()) {
        Text(if (meal.isFavorite) "Remove Favorite" else "Save Favorite")
    }
}