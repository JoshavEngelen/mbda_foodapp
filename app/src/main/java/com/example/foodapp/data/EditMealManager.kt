package com.example.foodapp.data

import android.content.Context
import androidx.core.content.edit

class EditMealManager(context: Context) {

    private val prefs = context.getSharedPreferences("edited_meals", Context.MODE_PRIVATE)
    private var cachedEdits = prefs.all.filterValues { it is String } as Map<String, String>

    fun saveEdit(id: String, name: String, instructions: String) {
        prefs.edit {
            putString("${id}_name", name)
                .putString("${id}_instructions", instructions)
        }

        val newCache = cachedEdits.toMutableMap()
        newCache["${id}_name"] = name
        newCache["${id}_instructions"] = instructions
        cachedEdits = newCache
    }

    fun getEditedName(id: String): String? = cachedEdits["${id}_name"]
    fun getEditedInstructions(id: String): String? = cachedEdits["${id}_instructions"]
}