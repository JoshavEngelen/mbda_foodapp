package com.example.foodapp

import kotlinx.serialization.Serializable

sealed interface NavDestinations {
    @Serializable
    data object Home : NavDestinations

    @Serializable
    data class Detail(val mealId: String) : NavDestinations
}
