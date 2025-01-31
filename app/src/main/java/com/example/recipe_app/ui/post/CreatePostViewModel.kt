//package com.example.recipe_app.ui.post
//
//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.recipe_app.Data.AppDatabase
//import com.example.recipe_app.Data.Ingredient
//import com.example.recipe_app.Data.Post
//import com.example.recipe_app.Data.PostDao
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class CreatePostViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val firebaseAuth = FirebaseAuth.getInstance()
//    private val firestore = FirebaseFirestore.getInstance()
//    private val postDao: PostDao = AppDatabase.getDatabase(application).postDao()
//
//    fun createPost(
//        title: String,
//        ingredients: MutableList<Ingredient>,
//        preparation: String,
//        preparationTime: Int,
//        imageUrl: String,
//        onComplete: (Boolean) -> Unit
//    ) {
//        val userId = firebaseAuth.currentUser?.uid ?: return onComplete(false)
//
//        // יצירת הפוסט ב-Firebase
//        val post = Post(
//            title = title,
////            ingredients = ingredients,
//            preparation = preparation,
//            preparationTime = preparationTime,
//            imageUrl = imageUrl,
//            userId = userId,
//            createdAt = System.currentTimeMillis(),
//            updatedAt = System.currentTimeMillis()
//        )
//
//        savePostToFirebase(post, onComplete)
//    }
//
//    private fun savePostToFirebase(post: Post, onComplete: (Boolean) -> Unit) {
//        val postRef = firestore.collection("posts").document()
//        postRef.set(post)
//            .addOnSuccessListener {
//                // לאחר יצירת הפוסט ב-Firebase, נשמור אותו גם ב-ROOM
//                viewModelScope.launch {
//                    savePostToRoom(post, onComplete)
//                }
//            }
//            .addOnFailureListener {
//                onComplete(false)
//            }
//    }
//
//    private suspend fun savePostToRoom(post: Post, onComplete: (Boolean) -> Unit) {
//        withContext(Dispatchers.IO) {
//            try {
//                postDao.insert(post)  // שמירה ב-ROOM
//                withContext(Dispatchers.Main) { onComplete(true) }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) { onComplete(false) }
//            }
//        }
//    }
//}

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
        ingredients: String,
        preparation: String,
        preparationTime: Int,
        imageUrl: String,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = firebaseAuth.currentUser?.uid ?: return onComplete(false)

        val post = Post(
            title = title,
            ingredients = ingredients,
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
            onComplete(firebaseSuccess)  // קריאה ל-onComplete רק פעם אחת
        }
    }

    private suspend fun savePostToFirebase(post: Post): Boolean {
        return try {
            firestore.collection("posts").document().set(post).await()
            true
        } catch (e: Exception) {
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

