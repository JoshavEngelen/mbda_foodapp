package com.example.foodapp.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.api.ApiService
import com.example.foodapp.api.MealUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<MealUi>) : UiState()
    data class Error(val message: String) : UiState()
}

class RecipeViewModel(context: Context) : ViewModel() {

    private val repository = RecipeRepository(
        apiService = ApiService(),
        favoritesManager = FavoritesManager(context),
        editMealManager = EditMealManager(context)
    )

    var uiState: UiState by mutableStateOf(UiState.Loading)
        private set

    fun fetchMeals() {
        viewModelScope.launch {
            uiState = UiState.Loading

            try {
                val meals = withContext(Dispatchers.IO) {
                    repository.getMeals()
                }

                val favorites = repository.getFavorites()

                val mappedMeals = meals.map {
                    MealUi(
                        id = it.id,
                        name = it.name,
                        instructions = it.instructions,
                        isFavorite = favorites.contains(it.id)
                    )
                }

                uiState = UiState.Success(mappedMeals)

            } catch (e: Exception) {
                uiState = UiState.Error("Fout bij ophalen data")
            }
        }
    }

    fun saveEdit(mealId: String, name: String, instructions: String) {
        repository.saveEdit(mealId, name, instructions)
        fetchMeals()
    }

    fun toggleFavorite(mealId: String) {
        repository.toggleFavorite(mealId)
        fetchMeals()
    }
}