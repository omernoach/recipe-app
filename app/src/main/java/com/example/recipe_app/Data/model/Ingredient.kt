package com.example.recipe_app.Data.model

data class Ingredient(
    val name: String = "",
    val quantity: Int = 0,
    val unit: String = ""
) {
    // קונסטרוקטור ברירת מחדל
    constructor() : this("", 0, "")
}


