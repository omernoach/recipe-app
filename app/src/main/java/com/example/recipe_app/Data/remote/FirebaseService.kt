package com.example.recipe_app.Data.remote
import android.net.Uri
import com.example.recipe_app.Data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseService {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun generatePostId(): String = firestore.collection("posts").document().id

    suspend fun registerUser(
        email: String,
        password: String,
        displayName: String,
        profileImageUri: Uri
    ): Boolean {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = auth.currentUser
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(profileImageUri)
                .build()
            user?.updateProfile(profileUpdates)?.await()

            true
        } catch (e: Exception) {
            false
        }
    }
    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun savePost(post: Post): Boolean {
        return try {
            firestore.collection("posts").document(post.id).set(post).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAllPosts(): List<Post> {
        return try {
            val snapshot = firestore.collection("posts").get().await()
            snapshot.documents.mapNotNull { it.toObject(Post::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPostsByUser(userId: String): List<Post> {
        return try {
            val snapshot = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Post::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
