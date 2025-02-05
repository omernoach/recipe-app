package com.example.recipe_app.ui.post

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipe_app.Data.model.Post
import com.example.recipe_app.Data.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val postRepository: PostRepository = PostRepository(application)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val posts: LiveData<List<Post>> = postRepository.getPosts()
    private val _userPosts = MutableLiveData<List<Post>>()
    val userPosts: LiveData<List<Post>> get() = _userPosts

    private val _syncDataStatus = MutableLiveData<Boolean>()
    val syncDataStatus: LiveData<Boolean> get() = _syncDataStatus

    init {
        loadUserPosts()
    }

    private fun loadUserPosts() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            postRepository.getPostsByUser(userId).observeForever { posts ->
                _userPosts.value = posts
            }
        } else {
            _userPosts.value = emptyList()
            Log.e("PostViewModel", "No user is logged in.")
        }
    }

    fun syncData() {
        viewModelScope.launch {
            try {
                Log.d("PostViewModel", "Syncing data...")
                postRepository.syncData()
                _syncDataStatus.value = true
                Log.d("PostViewModel", "Data synced")
            } catch (e: Exception) {
                _syncDataStatus.value = false
                Log.e("PostViewModel", "Error syncing data: ${e.message}")
            }
        }
    }
}

