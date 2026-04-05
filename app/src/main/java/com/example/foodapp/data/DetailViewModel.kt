package com.example.foodapp.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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
import com.example.foodapp.utils.StorageUtils
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
    private val mealId: String,
    private val repository: MealRepository
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
            val meal = meals.find { it.id == mealId }
            if (meal != null) {
                mealFlow.value = meal
            } else if (uiState is DetailUiState.Loading) {
                uiState = DetailUiState.Error("Meal not found")
            }
        } catch (e: Exception) {
            if (uiState is DetailUiState.Loading) {
                uiState = DetailUiState.Error("Failed to load meal")
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

    fun onImagePicked(context: Context, uri: Uri) {
        viewModelScope.launch {
            val internalUri = withContext(Dispatchers.IO) {
                StorageUtils.copyUriToInternal(context, uri)
            }
            editImageUri = internalUri?.toString() ?: uri.toString()
        }
    }

    fun onBitmapCaptured(context: Context, bitmap: Bitmap) {
        viewModelScope.launch {
            val uri = withContext(Dispatchers.IO) {
                StorageUtils.saveBitmapToInternal(context, bitmap)
            }
            if (uri != null) {
                editImageUri = uri.toString()
            }
        }
    }

    suspend fun saveChanges() {
        val uri = editImageUri?.toUri()
        repository.saveEdit(mealId, editName, editInstructions, uri)
        isEditing = false
    }

    fun toggleFavorite() {
        repository.toggleFavorite(mealId)
    }

    companion object {
        fun provideFactory(mealId: String, application: FoodApplication): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DetailViewModel(mealId, application.mealRepository)
            }
        }
    }
}
