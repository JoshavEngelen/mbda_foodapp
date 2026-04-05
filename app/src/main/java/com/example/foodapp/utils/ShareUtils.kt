package com.example.foodapp.utils

import android.content.Context
import android.content.Intent
import com.example.foodapp.api.MealUi

object ShareUtils {
    fun shareMeal(context: Context, meal: MealUi) {
        val shareText = formatRecipeForSharing(meal)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, meal.name)
        }
        context.startActivity(Intent.createChooser(intent, "Share meal via"))
    }

    private fun formatRecipeForSharing(meal: MealUi): String {
        return """🍽 Recipe: ${meal.name}

📋 Instructions:
${meal.instructions}

🔗 Source:
https://www.themealdb.com/meal/${meal.id}
    """.trimIndent()
    }
}
