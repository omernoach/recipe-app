package com.example.recipe_app.Data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.recipe_app.Data.local.AppDatabase
import com.example.recipe_app.Data.local.PostDao
import com.example.recipe_app.Data.model.Post
import com.example.recipe_app.Data.remote.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class PostRepository(application: Application) {
    private val postDao: PostDao = AppDatabase.getDatabase(application).postDao()
    private val firebaseService: FirebaseService = FirebaseService()

    fun getPosts(): LiveData<List<Post>> {
        return postDao.getAllPosts()
    }

    fun getPostsByUser(userId: String): LiveData<List<Post>> {
        return postDao.getPostsByUser(userId)
    }


        suspend fun syncData() {
        try {
            val postsFromFirebase = firebaseService.getAllPosts()

            Log.d("PostRepository", "Fetched posts from Firebase: $postsFromFirebase")

            val postsInRoom = postDao.getAllPostsSync()

            val idsInFirebase = postsFromFirebase.map { it.id }
            val postsToDelete = postsInRoom.filter { it.id !in idsInFirebase }
            postsToDelete.forEach { post ->
                postDao.deletePost(post.id)
                Log.d("PostRepository", "Deleted post with ID ${post.id} from ROOM")
            }

            postDao.insertPosts(postsFromFirebase)

        } catch (e: Exception) {
            Log.e("PostRepository", "Error syncing Firebase with ROOM", e)
        }
    }

    suspend fun updatePost(post: Post): Boolean {
        return try {
            firebaseService.updatePost(post)
            postDao.updatePost(post)
            Log.d("PostRepository", "Post updated both in Firebase and Room: ${post.id}")
            true // הצלחה, מחזירים true
        } catch (e: Exception) {
            Log.e("PostRepository", "Error updating post", e)
            false // במקרה של שגיאה מחזירים false
        }
    }


    suspend fun deletePost(postId: String): Boolean  {
        return try {
            firebaseService.deletePost(postId)
            postDao.deletePost(postId)
            Log.d("PostRepository", "Post deleted from Firebase and Room: $postId")
            true
        } catch (e: Exception) {
            Log.e("PostRepository", "Error deleting post", e)
            false
        }
    }

    suspend fun getPostById(postId: String): Post? {
        return withContext(Dispatchers.IO) {
            postDao.getPostById(postId)
        }
    }


}



