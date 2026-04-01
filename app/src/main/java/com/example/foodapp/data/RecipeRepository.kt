package com.example.foodapp.data

import com.example.foodapp.api.ApiService
import com.example.foodapp.api.Meal

class RecipeRepository(
    private val apiService: ApiService = ApiService()
) {

    fun getMeals(): List<Meal> {
        return apiService.fetchMeals()
    }
}