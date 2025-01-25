package com.example.recipe_app.Data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp


data class Post(
    val title: String = "",
    val ingredients: String = "",
    val preparation: String = "",
    val preparationTime: Int = 0,
    val imageUrl: String = "",
    val userId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
    val id: String = ""
)
