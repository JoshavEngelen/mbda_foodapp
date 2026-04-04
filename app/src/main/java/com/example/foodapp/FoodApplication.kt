package com.example.foodapp

import android.app.Application
import com.example.foodapp.api.ApiService
import com.example.foodapp.data.EditMealManager
import com.example.foodapp.data.FavoritesManager
import com.example.foodapp.data.RecipeRepository

class FoodApplication : Application() {

    lateinit var recipeRepository: RecipeRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Single instance of these across the whole app
        val apiService = ApiService()
        val favoritesManager = FavoritesManager(this)
        val editMealManager = EditMealManager(this)

        recipeRepository = RecipeRepository(
            apiService = apiService,
            favoritesManager = favoritesManager,
            editMealManager = editMealManager
        )
    }
}