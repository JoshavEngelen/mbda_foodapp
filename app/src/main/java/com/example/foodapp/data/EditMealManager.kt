package com.example.foodapp.data

import android.content.Context
import androidx.core.content.edit

class EditMealManager(context: Context) {

    private val prefs = context.getSharedPreferences("edited_meals", Context.MODE_PRIVATE)

    fun saveEdit(id: String, name: String, instructions: String) {
        prefs.edit {
            putString("${id}_name", name)
                .putString("${id}_instructions", instructions)
        }
    }

    fun getEditedName(id: String): String? {
        return prefs.getString("${id}_name", null)
    }

    fun getEditedInstructions(id: String): String? {
        return prefs.getString("${id}_instructions", null)
    }
}