package com.example.recipe_app.ui.addPost

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipe_app.Data.local.AppDatabase
import com.example.recipe_app.Data.model.Ingredient
import com.example.recipe_app.Data.model.Post
import com.example.recipe_app.Data.local.PostDao
import com.example.recipe_app.Data.remote.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePostViewModel(application: Application) : AndroidViewModel(application) {

    private val postDao: PostDao = AppDatabase.getDatabase(application).postDao()
    private val firebaseService = FirebaseService()

    // LiveData for posts
    val postsLiveData = MutableLiveData<List<Post>>()

    // Create a new post
    fun createPost(
        title: String,
        ingredients: List<Ingredient>,
        preparation: String,
        preparationTime: Int,
        imageUrl: String,
        onComplete: (Boolean) -> Unit
    ) {
        val post = Post(
            id = firebaseService.generatePostId(),
            title = title,
            ingredients = ingredients,
            preparation = preparation,
            preparationTime = preparationTime,
            imageUrl = imageUrl,
            userId = firebaseService.getCurrentUserId() ?: return onComplete(false),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val firebaseSuccess = firebaseService.savePost(post)
            if (firebaseSuccess) {
                savePostToRoom(post)
            }
            onComplete(firebaseSuccess)
        }
    }

    // Update an existing post
    fun updatePost(post: Post, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val firebaseSuccess = firebaseService.savePost(post)
            if (firebaseSuccess) {
                savePostToRoom(post)
            }
            onComplete(firebaseSuccess)
        }
    }

    // Get all posts from Firebase and update LiveData
    fun loadAllPosts() {
        viewModelScope.launch {
            val posts = firebaseService.getAllPosts()
            postsLiveData.postValue(posts)
        }
    }

    // Get posts from a specific user and update LiveData
    fun loadPostsByUser(userId: String) {
        viewModelScope.launch {
            val posts = firebaseService.getPostsByUser(userId)
            postsLiveData.postValue(posts)
        }
    }

    // Save the post to Room (local database)
    private suspend fun savePostToRoom(post: Post) {
        try {
            withContext(Dispatchers.IO) {
                postDao.insert(post)
            }
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error saving post: ${e.message}", e)
        }
    }
}
