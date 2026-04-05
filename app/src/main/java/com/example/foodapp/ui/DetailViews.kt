package com.example.foodapp.ui

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.core.net.toUri
import com.example.foodapp.api.MealUi
import com.example.foodapp.data.DetailViewModel
import kotlinx.coroutines.launch

@Composable
fun EditView(
    viewModel: DetailViewModel,
    onTakePhoto: () -> Unit,
    onPickPhoto: () -> Unit
) {
    val scope = rememberCoroutineScope()

    MealImageHeader(viewModel.editImageUri)

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
fun DisplayView(
    meal: MealUi,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onFavorite: () -> Unit
) {
    MealImageHeader(
        uriString = meal.imageUri
    )
    Spacer(Modifier.height(8.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
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
            onClick = onFavorite
        )
    }
    
    Spacer(Modifier.height(8.dp))
    Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) { Text("Edit Meal") }
    Spacer(Modifier.height(4.dp))
    Button(onClick = onShare, modifier = Modifier.fillMaxWidth()) { Text("Share Meal") }

    Spacer(Modifier.height(16.dp))
    Text(text = meal.instructions)
    Spacer(Modifier.height(16.dp))
}

@Composable
fun MealImageHeader(
    uriString: String?
) {
    val context = LocalContext.current
    val bitmap = intermediateBitmap(uriString, context)

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
                    contentDescription = "Meal Image",
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
private fun intermediateBitmap(uriString: String?, context: android.content.Context) =
    remember(uriString) {
        if (uriString.isNullOrEmpty()) null
        else try {
            val uri = uriString.toUri()
            if (uri.scheme == "file") {
                val path = uri.path
                if (path != null) {
                    BitmapFactory.decodeFile(path)?.asImageBitmap()
                } else null
            } else {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                }
            }
        } catch (e: Exception) {
            Log.e("DetailViews", "Error loading image: $uriString", e)
            null
        }
    }
