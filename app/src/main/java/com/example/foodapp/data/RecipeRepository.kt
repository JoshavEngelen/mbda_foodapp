package com.example.foodapp.data

import com.example.foodapp.api.ApiService
import com.example.foodapp.api.Meal

class RecipeRepository(
    private val apiService: ApiService,
    private val favoritesManager: FavoritesManager
) {

    fun getMeals(): List<Meal> {
        return apiService.fetchMeals()
    }

    fun getFavorites(): Set<String> {
        return favoritesManager.getFavorites()
    }

    fun toggleFavorite(id: String) {
        if (favoritesManager.isFavorite(id)) {
            favoritesManager.removeFavorite(id)
        } else {
            favoritesManager.addFavorite(id)
        }
    }
}