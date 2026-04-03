package com.example.foodapp.data

import android.content.Context
import androidx.core.content.edit

class FavoritesManager(context: Context) {

    private val prefs = context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FAVORITES = "favorite_ids"
    }

    fun getFavorites(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun addFavorite(id: String) {
        val current = getFavorites().toMutableSet()
        current.add(id)
        prefs.edit { putStringSet(KEY_FAVORITES, current) }
    }

    fun removeFavorite(id: String) {
        val current = getFavorites().toMutableSet()
        current.remove(id)
        prefs.edit { putStringSet(KEY_FAVORITES, current) }
    }

    fun isFavorite(id: String): Boolean {
        return getFavorites().contains(id)
    }
}