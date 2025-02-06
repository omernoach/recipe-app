package com.example.recipe_app.Data.local

import androidx.room.TypeConverter
import com.example.recipe_app.Data.model.Ingredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromIngredientList(value: List<Ingredient>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toIngredientList(value: String): List<Ingredient> {
        val listType = object : TypeToken<List<Ingredient>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
