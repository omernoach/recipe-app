package com.example.recipe_app.Data.local

import androidx.lifecycle.LiveData
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>)

    @Update
    suspend fun update(post: Post)

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostById(postId: String): Post

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: String)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    @Query("SELECT * FROM posts")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM posts")
    suspend fun getAllPostsSync(): List<Post>

    @Query("SELECT * FROM posts WHERE userId = :userId")
    fun getPostsByUser(userId: String): LiveData<List<Post>>

    @Update
    suspend fun updatePost(post: Post)

}