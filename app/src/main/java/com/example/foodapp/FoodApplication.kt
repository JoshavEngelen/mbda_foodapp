package com.example.foodapp

import android.app.Application
import com.example.foodapp.api.ApiService
import com.example.foodapp.data.EditMealManager
import com.example.foodapp.data.FavoritesManager
import com.example.foodapp.data.MealRepository

class FoodApplication : Application() {

    lateinit var mealRepository: MealRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val apiService = ApiService()
        val favoritesManager = FavoritesManager(this)
        val editMealManager = EditMealManager(this)

        mealRepository = MealRepository(
            apiService = apiService,
            favoritesManager = favoritesManager,
            editMealManager = editMealManager
        )
    }
}