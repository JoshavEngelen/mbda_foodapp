package com.example.foodapp.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun MealImageHeader(
    uriString: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val bitmapState = produceState<Bitmap?>(initialValue = null, uriString) {
        value = withContext(Dispatchers.IO) {
            loadBitmap(context, uriString)
        }
    }
    val bitmap = bitmapState.value
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Meal Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = if (uriString.isNullOrEmpty()) "No photo added" else "Loading...",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun loadBitmap(context: Context, uriString: String?): Bitmap? {
    if (uriString.isNullOrEmpty()) return null
    return try {
        val uri = uriString.toUri()
        when (uri.scheme) {
            "http", "https" -> {
                val url = URL(uriString)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                connection.inputStream.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            }
            "file" -> {
                val path = uri.path
                if (path != null) {
                    BitmapFactory.decodeFile(path)
                } else null
            }
            else -> {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            }
        }
    } catch (e: Exception) {
        Log.e("MealImage", "Error loading image: $uriString", e)
        null
    }
}
