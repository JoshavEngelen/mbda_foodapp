package com.example.foodapp.data

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import androidx.core.net.toUri

class ImageManager(context: Context) {

    private val prefs = context.getSharedPreferences("recipe_images", Context.MODE_PRIVATE)

    fun saveImage(recipeId: String, uri: Uri) {
        prefs.edit {
            putString(recipeId, uri.toString()) }
    }

    fun getImage(recipeId: String): Uri? {
        val uriString = prefs.getString(recipeId, null)
        return uriString?.toUri()
    }
}