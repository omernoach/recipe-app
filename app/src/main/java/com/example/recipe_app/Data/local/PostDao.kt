package com.example.recipe_app.Data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.recipe_app.Data.model.Post

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: Post)

    @Update
    suspend fun update(post: Post)

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): Post?

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: String)
}