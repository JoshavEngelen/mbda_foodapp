package com.example.foodapp.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.api.Meal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<Meal>) : UiState()
    data class Error(val message: String) : UiState()
}

class RecipeViewModel : ViewModel() {

    private val repository = RecipeRepository()

    var uiState: UiState by mutableStateOf(UiState.Loading)
        private set

    fun fetchMeals() {
        uiState = UiState.Loading

        viewModelScope.launch {
            try {
                val meals = withContext(Dispatchers.IO) {
                    repository.getMeals()
                }

                uiState = UiState.Success(meals)
            } catch (e: Exception) {
                uiState = UiState.Error("Fout bij ophalen data: ${e.localizedMessage}")
            }
        }
    }
}