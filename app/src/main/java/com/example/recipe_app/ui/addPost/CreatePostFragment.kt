package com.example.recipe_app.ui.addPost

import CloudinaryUploader
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.recipe_app.Data.model.Ingredient
import com.example.recipe_app.R
import com.example.recipe_app.databinding.FragmentCreatePostBinding

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreatePostViewModel

    private val ingredientsList = mutableListOf<Ingredient>() // ✅ Stores ingredients
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

        // Add ingredient
        binding.btnAddIngredient.setOnClickListener {
            val name = binding.editTextIngredientName.text.toString()
            val quantity = binding.editTextIngredientQuantity.text.toString().toIntOrNull() ?: 0
            val unit = binding.editTextIngredientUnit.text.toString()

            if (name.isNotEmpty() && unit.isNotEmpty()) {
                val ingredient = Ingredient(name, quantity, unit)
                ingredientsList.add(ingredient) // ✅ Add to List<Ingredient>
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
                CloudinaryUploader.uploadImage(selectedImageUri!!) { imageUrl ->
                    if (imageUrl != null) {
                        viewModel.createPost(
                            title = title,
                            ingredients = ingredientsList.toList(), // ✅ Pass List<Ingredient>
                            preparation = preparation,
                            preparationTime = preparationTime,
                            imageUrl = imageUrl
                        ) { success ->
                            val message = if (success) "Post created successfully!" else "Error creating post."
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            if (success) findNavController().navigate(R.id.action_homeFragment_to_createPostFragment)
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please select an image for your post.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textIngredientsList.movementMethod = ScrollingMovementMethod.getInstance()

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
}
