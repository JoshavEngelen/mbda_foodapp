package com.example.foodapp.data

import android.net.Uri
import com.example.foodapp.api.ApiService
import com.example.foodapp.api.MealUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class MealRepository(
    private val apiService: ApiService,
    private val favoritesManager: FavoritesManager,
    private val editMealManager: EditMealManager
) {
    val favoritesFlow: StateFlow<Set<String>> = FavoritesManager.favoritesFlow
    val editsChanged: Flow<Unit> = editMealManager.editsChanged

    suspend fun getMeals(): List<MealUi> = withContext(Dispatchers.IO) {
        val meals = apiService.fetchMeals()
        val favorites = favoritesManager.getFavorites()
        val allEdits = editMealManager.getAllEdits()

        meals.map { meal ->
            val id = meal.idMeal
            val editedName = allEdits["${id}_name"] as? String
            val editedInstructions = allEdits["${id}_instructions"] as? String
            val imageUri = allEdits["${id}_image"] as? String

            MealUi(
                id = id,
                name = editedName ?: meal.strMeal,
                instructions = editedInstructions ?: meal.strInstructions,
                isFavorite = favorites.contains(id),
                imageUri = imageUri ?: meal.strMealThumb
            )
        }
    }

    suspend fun saveEdit(id: String, name: String, instructions: String, uri: Uri?) = withContext(Dispatchers.IO) {
        editMealManager.saveEdit(id, name, instructions, uri)
    }

    fun toggleFavorite(id: String) {
        if (favoritesManager.isFavorite(id)) {
            favoritesManager.removeFavorite(id)
        } else {
            favoritesManager.addFavorite(id)
        }
    }
}