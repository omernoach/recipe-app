package com.example.recipe_app.Data.remote
import android.net.Uri
import android.util.Log
import com.example.recipe_app.Data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseService {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun generatePostId(): String = firestore.collection("posts").document().id

    suspend fun logoutUser() {
        try {
            auth.signOut()  // מתבצע לוגאאוט
            Log.d("FirebaseService", "User logged out successfully.")
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error during logout", e)
        }
    }

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
            val posts = snapshot.documents.mapNotNull { it.toObject(Post::class.java) }
            Log.d("FirebaseService", "Fetched ${posts.size} posts from Firebase.")
            posts
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error fetching posts from Firebase", e)
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

    suspend fun updatePost(post: Post): Boolean {
        return try {
            firestore.collection("posts").document(post.id).set(post).await()
            Log.d("FirebaseService", "Post updated successfully in Firebase: ${post.id}")
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error updating post in Firebase: ${post.id}", e)
            false
        }
    }

    suspend fun deletePost(postId: String): Boolean {
        return try {
            firestore.collection("posts").document(postId).delete().await()
            Log.d("FirebaseService", "Post deleted successfully from Firebase: $postId")
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error deleting post from Firebase: $postId", e)
            false
        }
    }

    suspend fun updateUserProfile(name: String, profileImageUri: Uri?) {
        try {
            val user = auth.currentUser
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .apply {
                    profileImageUri?.let { setPhotoUri(it) }
                }
                .build()

            user?.updateProfile(profileUpdates)?.await()
            firestore.collection("users").document(user!!.uid).update(
                mapOf(
                    "name" to name,
                    "profileImageUrl" to profileImageUri?.toString()
                )
            ).await()

        } catch (e: Exception) {
            Log.e("FirebaseService", "Error updating user profile", e)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }
}
