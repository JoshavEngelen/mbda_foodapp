package com.example.foodapp.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.foodapp.api.ApiService
import com.example.foodapp.api.MealUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val meal: MealUi) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class DetailViewModel(
    private val recipeId: String,
    context: Context
) : ViewModel() {

    private val repository = RecipeRepository(
        apiService = ApiService(),
        favoritesManager = FavoritesManager(context),
        editMealManager = EditMealManager(context)
    )

    var uiState: DetailUiState by mutableStateOf(DetailUiState.Loading)
        private set
    var isEditing by mutableStateOf(false)
        private set

    var editName by mutableStateOf("")
    var editInstructions by mutableStateOf("")

    init {
        loadMeal()
    }

    fun loadMeal() {
        viewModelScope.launch {
            uiState = DetailUiState.Loading
            try {
                val meals = withContext(Dispatchers.IO) { repository.getMeals() }
                val meal = meals.find { it.id == recipeId }
                if (meal != null) {
                    uiState = DetailUiState.Success(meal)
                } else {
                    uiState = DetailUiState.Error("Recipe not found")
                }
            } catch (e: Exception) {
                uiState = DetailUiState.Error("Failed to load recipe")
            }
        }
    }

    fun startEditing(meal: MealUi) {
        editName = meal.name
        editInstructions = meal.instructions
        isEditing = true
    }

    fun cancelEditing() {
        isEditing = false
    }

    fun saveChanges() {
        repository.saveEdit(recipeId, editName, editInstructions)
        isEditing = false
        loadMeal()
    }

    fun toggleFavorite() {
        repository.toggleFavorite(recipeId)
        loadMeal()
    }

    companion object {
        fun provideFactory(recipeId: String, context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DetailViewModel(recipeId, context)
            }
        }
    }
}
