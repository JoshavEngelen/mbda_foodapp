package com.example.foodapp.api

data class Meal(
    val idMeal: String,
    val strMeal: String,
    val strInstructions: String,
    val strMealThumb: String
)

data class MealUi(
    val id: String,
    val name: String,
    val isFavorite: Boolean
)