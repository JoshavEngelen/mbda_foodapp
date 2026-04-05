package com.example.foodapp.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import java.io.InputStream
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
    
    val uri = uriString.toUri()
    val targetWidth = 1024
    val targetHeight = 1024

    return try {
        // Step 1: Decode image dimensions only
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        
        openStream(context, uri).use { boundsStream ->
            BitmapFactory.decodeStream(boundsStream, null, options)
        }

        // Step 2: Calculate sampling size to avoid memory pressure
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)
        options.inJustDecodeBounds = false

        // Step 3: Decode the actual downsampled bitmap
        openStream(context, uri).use { imageStream ->
            BitmapFactory.decodeStream(imageStream, null, options)
        }
    } catch (e: Exception) {
        Log.e("MealImage", "Error loading image: $uriString", e)
        null
    }
}

private fun openStream(context: Context, uri: Uri): InputStream? {
    return when (uri.scheme) {
        "http", "https" -> {
            val connection = URL(uri.toString()).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.inputStream
        }
        else -> {
            context.contentResolver.openInputStream(uri)
        }
    }
}

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = options.outHeight to options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
