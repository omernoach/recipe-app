package com.example.recipe_app.ui.ProductSearch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipe_app.databinding.FragmentProductSearchBinding

class ProductSearchFragment : Fragment() {

    private val productSearchViewModel: ProductSearchViewModel by activityViewModels()
    private lateinit var binding: FragmentProductSearchBinding
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        productSearchViewModel.searchResults.observe(viewLifecycleOwner, Observer { items ->
            Log.d("ProductSearchFragment", "Search results received: ${items.size} items")
            adapter.submitList(items)
        })

        binding.searchButton.setOnClickListener {
            val query = binding.searchView.query.toString()
            if (query.isNotEmpty()) {
                productSearchViewModel.searchProduct(query)
            }
        }
    }
}
