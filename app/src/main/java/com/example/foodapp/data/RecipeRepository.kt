package com.example.foodapp.data

import com.example.foodapp.api.ApiService
import com.example.foodapp.api.Meal
import com.example.foodapp.api.MealUi

class RecipeRepository(
    private val apiService: ApiService,
    private val favoritesManager: FavoritesManager,
    private val editMealManager: EditMealManager
) {

    fun getMeals(): List<MealUi> {
        val meals = apiService.fetchMeals()
        val favorites = favoritesManager.getFavorites()

        return meals.map { meal ->

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

    fun saveEdit(id: String, name: String, instructions: String) {
        editMealManager.saveEdit(id, name, instructions)
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