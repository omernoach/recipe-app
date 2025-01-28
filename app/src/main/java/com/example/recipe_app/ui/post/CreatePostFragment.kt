package com.example.recipe_app.ui.post

import CloudinaryUploader
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.recipe_app.Data.Ingredient
import com.example.recipe_app.R
import com.example.recipe_app.databinding.FragmentCreatePostBinding

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreatePostViewModel

    private val ingredientsList = mutableListOf<Ingredient>()
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[CreatePostViewModel::class.java]

        setHasOptionsMenu(true)

        // Image picker
        binding.btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            imagePickerLauncher.launch(intent)
        }

        binding.btnAddIngredient.setOnClickListener {
            val name = binding.editTextIngredientName.text.toString()
            val quantity = binding.editTextIngredientQuantity.text.toString().toIntOrNull() ?: 0
            val unit = binding.editTextIngredientUnit.text.toString()

            if (name.isNotEmpty() && unit.isNotEmpty()) {
                val ingredient = Ingredient(name, quantity, unit)
                ingredientsList.add(ingredient)
                updateIngredientsUI()
                binding.editTextIngredientName.text?.clear()
                binding.editTextIngredientQuantity.text?.clear()
                binding.editTextIngredientUnit.text?.clear()
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSubmitPost.setOnClickListener {
            val title = binding.editTextTitle.text.toString()
            val preparation = binding.editTextInstructions.text.toString()
            val preparationTime = binding.editTextPreparationTime.text.toString().toIntOrNull() ?: 0

            if (selectedImageUri != null) {
                // Upload the image to Cloudinary Storage
                CloudinaryUploader.uploadImage(selectedImageUri!!) { imageUrl ->
                    // Once the image is uploaded, create the post
                    if (imageUrl != null) {
                        viewModel.createPost(title, ingredientsList, preparation, preparationTime, imageUrl) { success ->
                            val message = if (success) "Post created successfully!" else "Error creating post."
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            if (success) findNavController().navigate(R.id.action_createPostFragment_to_homeFragment)
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please select an image for your post.", Toast.LENGTH_SHORT).show()
            }
        }

        setMenuVisibility(true)

        return binding.root
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    binding.imagePreview.setImageURI(uri)
                }
            }
        }



    private fun updateIngredientsUI() {
        val ingredientsText = ingredientsList.joinToString("\n") { "${it.name} - ${it.quantity} ${it.unit}" }
        binding.textIngredientsList.text = ingredientsText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigate(R.id.action_createPostFragment_to_homeFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
