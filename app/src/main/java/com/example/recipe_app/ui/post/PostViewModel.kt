package com.example.recipe_app.ui.post

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.recipe_app.Data.model.Post
import com.example.recipe_app.Data.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val postRepository: PostRepository = PostRepository(application)
    val posts: LiveData<List<Post>> = postRepository.getPosts()

    fun syncData() {
        viewModelScope.launch {
            Log.d("PostViewModel", "Syncing data...")
            postRepository.syncData()
            Log.d("PostViewModel", "Data synced")
        }
    }
}
