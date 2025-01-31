package com.example.recipe_app.ui.post

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app.Data.AppDatabase
import com.example.recipe_app.Data.Ingredient
import com.example.recipe_app.Data.Post
import com.example.recipe_app.Data.PostDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CreatePostViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val postDao: PostDao = AppDatabase.getDatabase(application).postDao()

    fun createPost(
        title: String,
        ingredients: List<Ingredient>, // ✅ Updated to List<Ingredient>
        preparation: String,
        preparationTime: Int,
        imageUrl: String,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = firebaseAuth.currentUser?.uid ?: return onComplete(false)
        val postId = firestore.collection("posts").document().id
        val post = Post(
            id = postId,
            title = title,
            ingredients = ingredients, // ✅ Save List<Ingredient>
            preparation = preparation,
            preparationTime = preparationTime,
            imageUrl = imageUrl,
            userId = userId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val firebaseSuccess = savePostToFirebase(post)
            if (firebaseSuccess) {
                savePostToRoom(post)
            }
            onComplete(firebaseSuccess)
        }
    }

    private suspend fun savePostToFirebase(post: Post): Boolean {
        return try {
            firestore.collection("posts").document(post.id).set(post).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error saving post: ${e.message}", e)
            false
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
}
