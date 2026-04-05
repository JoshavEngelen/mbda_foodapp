package com.example.foodapp.data

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class EditMealManager(context: Context) {

    private val prefs = context.getSharedPreferences("edited_meals", Context.MODE_PRIVATE)
    
    private val _editsChanged = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val editsChanged: SharedFlow<Unit> = _editsChanged.asSharedFlow()

    fun saveEdit(id: String, name: String, instructions: String, uri: Uri?) {
        val uriString = uri?.toString()
        prefs.edit {
            putString("${id}_name", name)
            putString("${id}_instructions", instructions)
            if (uriString != null) {
                putString("${id}_image", uriString)
            } else {
                remove("${id}_image")
            }
        }
        _editsChanged.tryEmit(Unit)
    }

    fun getEditedName(id: String): String? = prefs.getString("${id}_name", null)
    fun getEditedInstructions(id: String): String? = prefs.getString("${id}_instructions", null)
    fun getEditedImage(id: String): String? = prefs.getString("${id}_image", null)
}
