package com.example.recipe_app.Data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val uid: String, // Firebase user ID
    val name: String,
    val email: String,
    val profileImageUrl: String
)