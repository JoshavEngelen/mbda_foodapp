package com.example.foodapp.api

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ApiService {

    fun fetchMeals(): List<Meal> {
        val url = URL("https://www.themealdb.com/api/json/v1/1/search.php?s=")
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "GET"
            connection.connect()

            val response = connection.inputStream.bufferedReader().readText()
            parseMeals(response)

        } finally {
            connection.disconnect()
        }
    }

    private fun parseMeals(json: String): List<Meal> {
        val jsonObject = JSONObject(json)
        val mealsArray = jsonObject.getJSONArray("meals")

        val meals = mutableListOf<Meal>()

        for (i in 0 until mealsArray.length()) {
            val item = mealsArray.getJSONObject(i)

            meals.add(
                Meal(
                    idMeal = item.getString("idMeal"),
                    strMeal = item.getString("strMeal"),
                    strInstructions = item.getString("strInstructions"),
                    strMealThumb = item.getString("strMealThumb")
                )
            )
        }

        return meals
    }
}