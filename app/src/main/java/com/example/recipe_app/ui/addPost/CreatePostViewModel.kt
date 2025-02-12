package com.example.recipe_app.ui.addPost

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app.Data.local.AppDatabase
import com.example.recipe_app.Data.model.Ingredient
import com.example.recipe_app.Data.model.Post
import com.example.recipe_app.Data.local.PostDao
import com.example.recipe_app.Data.remote.FirebaseService
import com.example.recipe_app.Data.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePostViewModel(application: Application) : AndroidViewModel(application) {

    private val postDao: PostDao = AppDatabase.getDatabase(application).postDao()
    private val firebaseService = FirebaseService()
    private val postRepository: PostRepository = PostRepository(application)


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

    private suspend fun savePostToRoom(post: Post) {
        try {
            withContext(Dispatchers.IO) {
                postDao.insert(post)
            }
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error saving post: ${e.message}", e)
        }
    }
    fun getPostById(postId: String, onResult: (Post?) -> Unit) {
        viewModelScope.launch {
            val post = withContext(Dispatchers.IO) {
                postRepository.getPostById(postId)
            }
            onResult(post)
        }
    }


    fun updatePost(post: Post, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = postRepository.updatePost(post)
            onComplete(success)
        }
    }
}
