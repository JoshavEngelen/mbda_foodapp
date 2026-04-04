package com.example.foodapp.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.foodapp.FoodApplication
import com.example.foodapp.api.MealUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val meal: MealUi) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class DetailViewModel(
    private val recipeId: String,
    private val repository: RecipeRepository
) : ViewModel() {

    private val mealFlow = MutableStateFlow<MealUi?>(null)

    var uiState: DetailUiState by mutableStateOf(DetailUiState.Loading)
        private set
    var isEditing by mutableStateOf(false)
        private set

    var editName by mutableStateOf("")
    var editInstructions by mutableStateOf("")
    var editImageUri: String? by mutableStateOf(null)

    init {
        viewModelScope.launch {
            combine(mealFlow.filterNotNull(), repository.favoritesFlow) { meal, favorites ->
                meal.copy(isFavorite = favorites.contains(meal.id))
            }.collect { updatedMeal ->
                uiState = DetailUiState.Success(updatedMeal)
            }
        }

        viewModelScope.launch {
            repository.editsChanged
                .onStart { emit(Unit) }
                .collect {
                    refreshMealData()
                }
        }
    }

    private suspend fun refreshMealData() {
        try {
            val meals = withContext(Dispatchers.IO) { repository.getMeals() }
            val meal = meals.find { it.id == recipeId }
            if (meal != null) {
                mealFlow.value = meal
            } else if (uiState is DetailUiState.Loading) {
                uiState = DetailUiState.Error("Recipe not found")
            }
        } catch (e: Exception) {
            if (uiState is DetailUiState.Loading) {
                uiState = DetailUiState.Error("Failed to load recipe")
            }
        }
    }

    fun startEditing(meal: MealUi) {
        editName = meal.name
        editInstructions = meal.instructions
        editImageUri = meal.imageUri
        isEditing = true
    }

    fun cancelEditing() {
        isEditing = false
    }

    suspend fun saveChanges() {
        val uri = editImageUri?.toUri()
        repository.saveEdit(recipeId, editName, editInstructions, uri)
        isEditing = false
    }

    fun toggleFavorite() {
        repository.toggleFavorite(recipeId)
    }

    companion object {
        fun provideFactory(recipeId: String, application: FoodApplication): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DetailViewModel(recipeId, application.recipeRepository)
            }
        }
    }
}
