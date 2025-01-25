package com.example.recipe_app.ui.post
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.recipe_app.R
import com.example.recipe_app.databinding.FragmentCreatePostBinding

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreatePostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[CreatePostViewModel::class.java]

        binding.btnSubmitPost.setOnClickListener {
            val title = binding.editTextTitle.text.toString()
            val ingredients = binding.editTextIngredients.text.toString()
            val preparation = binding.editTextInstructions.text.toString()
            val preparationTime = binding.editTextPreparationTime.text.toString().toIntOrNull() ?: 0

            viewModel.createPost(title, ingredients, preparation, preparationTime) { success ->
                val message = if (success) "Post created successfully!" else "Error creating post."
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                if (success) findNavController().navigate(R.id.action_createPostFragment_to_homeFragment)
            }
        }

        setMenuVisibility(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_createPostFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Navigate back to HomeFragment on Up button press
                findNavController().navigate(R.id.action_createPostFragment_to_homeFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
