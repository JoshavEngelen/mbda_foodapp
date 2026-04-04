package com.example.foodapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
            val uri = saveBitmapToInternal(context, it)
            if (uri != null) {
                viewModel.editImageUri = uri.toString()
            }
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }
    
    // Using modern PickVisualMedia for better reliability
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val internalUri = copyUriToInternal(context, it)
            if (internalUri != null) {
                viewModel.editImageUri = internalUri.toString()
            } else {
                // Fallback to original URI if copy fails, though not ideal for persistence
                viewModel.editImageUri = it.toString()
            }
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
                        EditView(
                            viewModel,
                            onTakePhoto = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    cameraLauncher.launch(null)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            onPickPhoto = {
                                imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        )
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
                            onFavorite = { viewModel.toggleFavorite() }
                        )
                    }
                }
            }
        }
    }
}

private fun saveBitmapToInternal(context: Context, bitmap: Bitmap): Uri? {
    val dir = File(context.filesDir, "photos").apply { if (!exists()) mkdirs() }
    val fileName = "recipe_photo_${System.currentTimeMillis()}.jpg"
    val file = File(dir, fileName)
    return try {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        file.toUri()
    } catch (e: Exception) {
        Log.e("DetailScreen", "Error saving bitmap", e)
        null
    }
}

private fun copyUriToInternal(context: Context, uri: Uri): Uri? {
    val dir = File(context.filesDir, "photos").apply { if (!exists()) mkdirs() }
    val fileName = "recipe_photo_${System.currentTimeMillis()}.jpg"
    val file = File(dir, fileName)
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        file.toUri()
    } catch (e: Exception) {
        Log.e("DetailScreen", "Error copying URI: $uri", e)
        null
    }
}

@Composable
fun EditView(
    viewModel: DetailViewModel,
    onTakePhoto: () -> Unit,
    onPickPhoto: () -> Unit
) {
    val scope = rememberCoroutineScope()

    RecipeImageHeader(viewModel.editImageUri)

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
    uriString: String?
) {
    val context = LocalContext.current
    val bitmap = remember(uriString) {
        if (uriString.isNullOrEmpty()) null
        else try {
            val uri = uriString.toUri()
            // If it's a file URI, load it directly from the path to avoid permission issues
            if (uri.scheme == "file") {
                val path = uri.path
                if (path != null) {
                    BitmapFactory.decodeFile(path)?.asImageBitmap()
                } else null
            } else {
                // For content:// or http://, use the content resolver
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                }
            }
        } catch (e: Exception) {
            Log.e("DetailScreen", "Error loading image: $uriString", e)
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
    }
}

@Composable
fun DisplayView(
    meal: MealUi,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onFavorite: () -> Unit
) {
    RecipeImageHeader(
        uriString = meal.imageUri
    )
    Spacer(Modifier.height(8.dp))
    Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) { Text("Edit Recipe") }
    Spacer(Modifier.height(4.dp))
    Button(onClick = onShare, modifier = Modifier.fillMaxWidth()) { Text("Share Recipe") }
    Spacer(Modifier.height(4.dp))
    Button(onClick = onFavorite, modifier = Modifier.fillMaxWidth()) {
        Text(if (meal.isFavorite) "Remove Favorite" else "Save Favorite")
    }

    Spacer(Modifier.height(8.dp))
    Text(text = meal.name, style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(8.dp))
    Text(text = meal.instructions)
    Spacer(Modifier.height(16.dp))
}
