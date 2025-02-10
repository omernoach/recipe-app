package com.example.recipe_app.ui.post

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipe_app.Data.local.UserDao
import com.example.recipe_app.Data.local.UserDatabase
import com.example.recipe_app.Data.model.Post
import com.example.recipe_app.Data.model.User
import com.example.recipe_app.Data.repository.PostRepository
import com.example.recipe_app.Data.repository.UserRepository
import kotlinx.coroutines.launch

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val postRepository: PostRepository = PostRepository(application)
    val posts: LiveData<List<Post>> = postRepository.getPosts()
    private val _userPosts = MutableLiveData<List<Post>>()
    val userPosts: LiveData<List<Post>> get() = _userPosts
    private val userDao: UserDao = UserDatabase.getDatabase(application).userDao()
    private val repository : UserRepository = UserRepository(userDao);


    private val _syncDataStatus = MutableLiveData<Boolean>()
    val syncDataStatus: LiveData<Boolean> get() = _syncDataStatus

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = repository.getUser()
            _currentUser.value = user
            loadUserPosts()
        }
    }

    private fun loadUserPosts() {
        val user = _currentUser.value
        if (user != null) {
            postRepository.getPostsByUser(user.uid).observeForever { posts ->
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

        fun deletePost(post: Post, onComplete: (Boolean) -> Unit) {
            viewModelScope.launch {
                val success = postRepository.deletePost(post.id)
                onComplete(success)
            }
        }
}

