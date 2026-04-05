package com.example.foodapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

object StorageUtils {

    private fun getInternalFile(context: Context): File {
        val dir = File(context.filesDir, "photos").apply { if (!exists()) mkdirs() }
        val fileName = "meal_photo_${System.currentTimeMillis()}.jpg"
        return File(dir, fileName)
    }

    fun saveBitmapToInternal(context: Context, bitmap: Bitmap): Uri? {
        val file = getInternalFile(context)
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            file.toUri()
        } catch (e: Exception) {
            Log.e("StorageUtils", "Error saving bitmap", e)
            null
        }
    }

    fun copyUriToInternal(context: Context, uri: Uri): Uri? {
        val file = getInternalFile(context)
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.toUri()
        } catch (e: Exception) {
            Log.e("StorageUtils", "Error copying URI: $uri", e)
            null
        }
    }
}
