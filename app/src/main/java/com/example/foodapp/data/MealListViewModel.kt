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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<MealUi>) : UiState()
    data class Error(val message: String) : UiState()
}

class MealListViewModel(context: Context) : ViewModel() {

    private val repository = RecipeRepository(
        apiService = ApiService(),
        favoritesManager = FavoritesManager(context),
        editMealManager = EditMealManager(context)
    )

    private val mealsFlow = MutableStateFlow<List<MealUi>>(emptyList())

    var uiState: UiState by mutableStateOf(UiState.Loading)
        private set

    init {
        viewModelScope.launch {
            combine(mealsFlow, repository.favoritesFlow) { meals, favorites ->
                meals.map { it.copy(isFavorite = favorites.contains(it.id)) }
            }.collect { updatedMeals ->
                if (uiState is UiState.Success || updatedMeals.isNotEmpty()) {
                    uiState = UiState.Success(updatedMeals)
                }
            }
        }
        fetchMeals()
    }

    fun fetchMeals() {
        viewModelScope.launch {
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
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MealListViewModel(context)
            }
        }
    }
}