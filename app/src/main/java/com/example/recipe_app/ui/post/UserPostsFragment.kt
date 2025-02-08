package com.example.recipe_app.ui.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipe_app.Data.model.Post
import com.example.recipe_app.R
import com.example.recipe_app.databinding.FragmentUserPostsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UserPostsFragment : Fragment() {

    private lateinit var postViewModel: PostViewModel
    private lateinit var postAdapter: PostAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentUserPostsBinding.inflate(inflater, container, false)

        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        postAdapter = PostAdapter { post ->
            deletePost(post)
        }

        binding.recyclerViewUserPosts.adapter = postAdapter
        binding.recyclerViewUserPosts.layoutManager = LinearLayoutManager(context)

        progressBar = binding.progressBar

        postViewModel.userPosts.observe(viewLifecycleOwner, Observer { posts ->
            Log.d("UserPostsFragment", "Posts received: $posts")
            postAdapter.submitList(posts)
            progressBar.visibility = View.GONE
        })

        return binding.root
    }

    private fun deletePost(post: Post) {
        postViewModel.deletePost(post) { success ->
            if (success) {
                Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
