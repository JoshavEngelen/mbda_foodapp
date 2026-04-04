package com.example.foodapp.data

import com.example.foodapp.api.ApiService
import com.example.foodapp.api.MealUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class RecipeRepository(
    private val apiService: ApiService,
    private val favoritesManager: FavoritesManager,
    private val editMealManager: EditMealManager
) {
    val favoritesFlow: StateFlow<Set<String>> = FavoritesManager.favoritesFlow

    suspend fun getMeals(): List<MealUi> = withContext(Dispatchers.IO) {
        val meals = apiService.fetchMeals()
        val favorites = favoritesManager.getFavorites()

        meals.map { meal ->
            val editedName = editMealManager.getEditedName(meal.idMeal)
            val editedInstructions = editMealManager.getEditedInstructions(meal.idMeal)

            MealUi(
                id = meal.idMeal,
                name = editedName ?: meal.strMeal,
                instructions = editedInstructions ?: meal.strInstructions,
                isFavorite = favorites.contains(meal.idMeal)
            )
        }
    }

    suspend fun saveEdit(id: String, name: String, instructions: String) = withContext(Dispatchers.IO) {
        editMealManager.saveEdit(id, name, instructions)
    }

    fun toggleFavorite(id: String) {
        if (favoritesManager.isFavorite(id)) {
            favoritesManager.removeFavorite(id)
        } else {
            favoritesManager.addFavorite(id)
        }
    }
}