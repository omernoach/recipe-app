package com.example.recipe_app.Data

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromIngredientsList(ingredients: MutableList<Ingredient>): String {
        return Gson().toJson(ingredients)
    }

    @TypeConverter
    fun toIngredientsList(ingredientsString: String): MutableList<Ingredient> {
        val listType = object : com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken<MutableList<Ingredient>>() {}.type
        return Gson().fromJson(ingredientsString, listType)
    }
}