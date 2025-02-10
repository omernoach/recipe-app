
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
import com.example.recipe_app.databinding.FragmentPostBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PostFragment : Fragment() {

    private lateinit var postViewModel: PostViewModel
    private lateinit var postAdapter: PostAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var refreshButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(inflater, container, false)
        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        postAdapter = PostAdapter { post ->
            deletePost(post)
        }

        postViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            postAdapter.setCurrentUser(user)
        })
        binding.recyclerView.adapter = postAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        progressBar = binding.progressBar
        refreshButton = binding.root.findViewById(R.id.refreshButton)

        postViewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            Log.d("PostFragment", "Posts received: $posts")
            val sortedPosts = posts.sortedByDescending { it.updatedAt }
            postAdapter.submitList(sortedPosts)
            progressBar.visibility = View.GONE
        })


        refreshButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            refreshButton.alpha = 0.5f
            refreshButton.setBackgroundTintList(
                ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            )
            postViewModel.syncData()
        }

        postViewModel.syncDataStatus.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                progressBar.visibility = View.GONE
                refreshButton.alpha = 1f
                refreshButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.purple_500)
                )
                Toast.makeText(context, "Data synced successfully", Toast.LENGTH_SHORT).show()
            } else {
                progressBar.visibility = View.GONE
                refreshButton.alpha = 1f
                refreshButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.orange)
                )
                Log.e("PostFragment", "Sync failed")
            }
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


