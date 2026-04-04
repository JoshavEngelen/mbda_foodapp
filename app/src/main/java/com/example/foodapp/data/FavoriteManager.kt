package com.example.foodapp.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesManager(context: Context) {

    private val prefs = context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FAVORITES = "favorite_ids"

        private val _favoritesFlow = MutableStateFlow<Set<String>>(emptySet())
        val favoritesFlow: StateFlow<Set<String>> = _favoritesFlow.asStateFlow()
    }

    init {
        _favoritesFlow.value = getFavoritesFromPrefs()
    }

    private fun getFavoritesFromPrefs(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun getFavorites(): Set<String> = _favoritesFlow.value

    fun addFavorite(id: String) {
        val current = getFavoritesFromPrefs().toMutableSet()
        current.add(id)
        prefs.edit { putStringSet(KEY_FAVORITES, current) }
        _favoritesFlow.value = current
    }

    fun removeFavorite(id: String) {
        val current = getFavoritesFromPrefs().toMutableSet()
        current.remove(id)
        prefs.edit { putStringSet(KEY_FAVORITES, current) }
        _favoritesFlow.value = current
    }

    fun isFavorite(id: String): Boolean {
        return getFavorites().contains(id)
    }
}