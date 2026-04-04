package com.example.foodapp.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<MealUi>) : UiState()
    data class Error(val message: String) : UiState()
}

class MealListViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val mealsFlow = MutableStateFlow<List<MealUi>>(emptyList())

    var uiState: UiState by mutableStateOf(UiState.Loading)
        private set

    init {
        viewModelScope.launch {
            combine(mealsFlow, repository.favoritesFlow) { meals, favorites ->
                meals.map { it.copy(isFavorite = favorites.contains(it.id)) }
            }
            .flowOn(Dispatchers.Default) // Perform mapping off the main thread
            .collect { updatedMeals ->
                if (uiState is UiState.Success || updatedMeals.isNotEmpty()) {
                    uiState = UiState.Success(updatedMeals)
                }
            }
        }
        // Initial fetch is handled here, so no need for LaunchedEffect(Unit) in the UI
        fetchMeals()
    }

    fun fetchMeals() {
        viewModelScope.launch {
            if (mealsFlow.value.isNotEmpty()) return@launch // Avoid redundant fetches

            if (uiState !is UiState.Success) {
                uiState = UiState.Loading
            }

            try {
                val meals = withContext(Dispatchers.IO) {
                    repository.getMeals()
                }
                mealsFlow.value = meals
            } catch (e: Exception) {
                uiState = UiState.Error("Failed to load meals")
            }
        }
    }

    fun toggleFavorite(mealId: String) {
        repository.toggleFavorite(mealId)
    }

    companion object {
        fun provideFactory(application: FoodApplication): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MealListViewModel(application.recipeRepository)
            }
        }
    }
}