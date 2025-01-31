package com.example.recipe_app.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PostDao {

    @Insert
    suspend fun insert(post: Post)

    @Update
    suspend fun update(post: Post)

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: Int): Post?  // עדכון לסוג ID כ-Int

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Int)  // עדכון לסוג ID כ-Int
}