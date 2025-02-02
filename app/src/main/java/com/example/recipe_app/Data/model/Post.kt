package com.example.recipe_app.Data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.recipe_app.Data.local.Converters

@Entity(tableName = "posts")
@TypeConverters(Converters::class)
data class Post(
    @PrimaryKey val id: String,
    val title: String = "",
    val ingredients: List<Ingredient> = listOf(),
    val preparation: String = "",
    val preparationTime: Int = 0,
    val imageUrl: String = "",
    val userId: String = "",
    val createdAt: Long? = null,
    val updatedAt: Long? = null
)
