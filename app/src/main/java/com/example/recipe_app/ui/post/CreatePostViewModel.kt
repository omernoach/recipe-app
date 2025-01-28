package com.example.recipe_app.ui.post

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.recipe_app.Data.Ingredient
import com.example.recipe_app.Data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class CreatePostViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()


    fun createPost(
        title: String,
        ingredients: MutableList<Ingredient>,
        preparation: String,
        preparationTime: Int,
        imageUrl: String,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = firebaseAuth.currentUser?.uid ?: return onComplete(false)
        val postId = firestore.collection("posts").document().id


        savePost(postId, title, ingredients, preparation, preparationTime, userId, imageUrl, onComplete)
    }

    private fun savePost(
        postId: String,
        title: String,
        ingredients: MutableList<Ingredient>,
        preparation: String,
        preparationTime: Int,
        userId: String,
        imageUrl: String,
        onComplete: (Boolean) -> Unit
    ) {
        val post = Post(
            id = postId,
            title = title,
            ingredients = ingredients,
            preparation = preparation,
            preparationTime = preparationTime,
            imageUrl = imageUrl,
            userId = userId,
            createdAt = null,
            updatedAt = null
        )

        firestore.collection("posts").document(postId)
            .set(post)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}


