package com.example.recipe_app.Data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int =0,
    val title: String = "",
    val ingredients: String = "",
    val preparation: String = "",
    val preparationTime: Int = 0,
    val imageUrl: String = "",
    val userId: String = "",
    val createdAt: Long? = null,
    val updatedAt: Long? = null
)