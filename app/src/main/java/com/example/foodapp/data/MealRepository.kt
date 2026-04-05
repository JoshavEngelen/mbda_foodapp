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

        meals.map { meal ->
            val editedName = editMealManager.getEditedName(meal.idMeal)
            val editedInstructions = editMealManager.getEditedInstructions(meal.idMeal)
            val imageUri = editMealManager.getEditedImage(meal.idMeal)

            MealUi(
                id = meal.idMeal,
                name = editedName ?: meal.strMeal,
                instructions = editedInstructions ?: meal.strInstructions,
                isFavorite = favorites.contains(meal.idMeal),
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