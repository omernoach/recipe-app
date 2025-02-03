package com.example.recipe_app.ui.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipe_app.R
import com.example.recipe_app.databinding.FragmentPostBinding

class PostFragment : Fragment() {

    private lateinit var postViewModel: PostViewModel
    private lateinit var postAdapter: PostAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var refreshButton: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(inflater, container, false)
        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        postAdapter = PostAdapter()
        binding.recyclerView.adapter = postAdapter

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        progressBar = binding.progressBar
        refreshButton = binding.root.findViewById(R.id.refreshButton)

        Log.d("PostFragment", "Fragment created and adapter set")

        postViewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            Log.d("PostFragment", "Posts received: $posts")
            postAdapter.submitList(posts)
            progressBar.visibility = View.GONE
        })

        syncData()


        refreshButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            syncData()
        }

        return binding.root
    }

    private fun syncData() {
        postViewModel.syncData()
    }
}

