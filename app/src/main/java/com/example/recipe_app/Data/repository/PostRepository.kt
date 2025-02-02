//package com.example.recipe_app.Data.repository
//
//import com.example.recipe_app.Data.Post
//import com.example.recipe_app.Data.local.PostDao
//import com.example.recipe_app.Data.FirebaseService
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class PostRepository(
//    private val postDao: PostDao,
//    private val firebaseService: FirebaseService
//) {
//
//    fun getLocalPosts() = postDao.getAllPosts() // Get all posts from Room
//
//    suspend fun syncPosts() = withContext(Dispatchers.IO) {
//        val posts = firebaseService.getPosts() // Fetch from Firebase
//        postDao.clearAll() // Clear old data
//        postDao.insertAll(posts) // Save new posts in Room
//    }
//
//    fun observeFirebaseChanges() {
//        firebaseService.listenForUpdates { posts ->
//            postDao.insertAll(posts) // Update Room in real-time
//        }
//    }
//}
