package com.example.recipe_app.ui.addPost

import CloudinaryUploader
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.recipe_app.Data.model.Ingredient
import com.example.recipe_app.Data.model.Post
import com.example.recipe_app.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso


class EditPostFragment : Fragment(R.layout.fragment_create_post) {

    private val createPostViewModel: CreatePostViewModel by activityViewModels()
    private lateinit var post: Post
    private lateinit var titleInput: TextInputEditText
    private lateinit var instructionsInput: TextInputEditText
    private lateinit var preparationTimeInput: TextInputEditText
    private lateinit var imagePreview: ImageView
    private lateinit var btnSave: MaterialButton
    private lateinit var btnUploadImage: MaterialButton

    private var selectedImageUri: Uri? = null
    private val ingredientsList = mutableListOf<Ingredient>()
    private var selectedIngredientIndex: Int? = null
    private lateinit var textIngredientsList: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        titleInput = view.findViewById(R.id.editTextTitle)
        instructionsInput = view.findViewById(R.id.editTextInstructions)
        preparationTimeInput = view.findViewById(R.id.editTextPreparationTime)
        imagePreview = view.findViewById(R.id.imagePreview)
        btnSave = view.findViewById(R.id.btnSubmitPost)
        btnUploadImage = view.findViewById(R.id.btnUploadImage)
        textIngredientsList = view.findViewById(R.id.textIngredientsList)

        val editTextIngredientName = view.findViewById<TextInputEditText>(R.id.editTextIngredientName)
        val editTextIngredientQuantity = view.findViewById<TextInputEditText>(R.id.editTextIngredientQuantity)
        val editTextIngredientUnit = view.findViewById<TextInputEditText>(R.id.editTextIngredientUnit)
        val btnAddIngredient = view.findViewById<MaterialButton>(R.id.btnAddIngredient)

        textIngredientsList.movementMethod = ScrollingMovementMethod.getInstance()

        val postId = arguments?.getString("POST_ID") ?: return
        loadPost(postId)

        btnSave.setOnClickListener {
            savePost()
        }

        btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            imagePickerLauncher.launch(intent)
        }

        btnAddIngredient.setOnClickListener {
            val name = editTextIngredientName.text.toString()
            val quantity = editTextIngredientQuantity.text.toString().toIntOrNull() ?: 0
            val unit = editTextIngredientUnit.text.toString()

            if (name.isNotEmpty() && unit.isNotEmpty()) {
                if (selectedIngredientIndex != null) {
                    val updatedIngredient = Ingredient(name, quantity, unit)
                    ingredientsList[selectedIngredientIndex!!] = updatedIngredient
                    selectedIngredientIndex = null
                } else {
                    val ingredient = Ingredient(name, quantity, unit)
                    ingredientsList.add(ingredient)
                }
                updateIngredientsUI()
                editTextIngredientName.text?.clear()
                editTextIngredientQuantity.text?.clear()
                editTextIngredientUnit.text?.clear()
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    imagePreview.setImageURI(uri)
                }
            }
        }

    private fun loadPost(postId: String) {
        createPostViewModel.getPostById(postId) { retrievedPost ->
            if (retrievedPost != null) {
                post = retrievedPost
                titleInput.setText(post.title)
                instructionsInput.setText(post.preparation)
                preparationTimeInput.setText(post.preparationTime.toString())

                if (post.imageUrl.isNotEmpty()) {
                    Picasso.get().load(post.imageUrl).into(imagePreview)
                }

                ingredientsList.clear()
                ingredientsList.addAll(post.ingredients)
                updateIngredientsUI()
            } else {
                Toast.makeText(requireContext(), "Post not found", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private var lastClickTime: Long = 0

    @SuppressLint("ClickableViewAccessibility")
    private fun updateIngredientsUI() {
        val ingredientsText = ingredientsList.mapIndexed { index, ingredient ->
            "${index + 1}. ${ingredient.name} - ${ingredient.quantity} ${ingredient.unit}"
        }.joinToString("\n")

        textIngredientsList.text = ingredientsText

        textIngredientsList.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < 300) {
                    val clickedPosition = (event.y / textIngredientsList.lineHeight).toInt()
                    if (clickedPosition in 0 until ingredientsList.size) {
                        val removedIngredient = ingredientsList.removeAt(clickedPosition)
                        Toast.makeText(requireContext(), "${removedIngredient.name} removed.", Toast.LENGTH_SHORT).show()
                        updateIngredientsUI()
                    }
                }
                lastClickTime = currentTime
            }
            true
        }
    }

    private fun savePost() {
        if (selectedImageUri != null) {
            CloudinaryUploader.uploadImage(selectedImageUri!!) { imageUrl ->
                if (imageUrl != null) {
                    updatePost(imageUrl)
                } else {
                    Toast.makeText(requireContext(), "Error uploading image.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            updatePost(post.imageUrl)
        }
    }

    private fun updatePost(imageUrl: String) {
        val updatedPost = post.copy(
            title = titleInput.text.toString(),
            preparation = instructionsInput.text.toString(),
            preparationTime = preparationTimeInput.text.toString().toIntOrNull() ?: 0,
            ingredients = ingredientsList.toList(),
            imageUrl = imageUrl,
            updatedAt = System.currentTimeMillis()
        )

        createPostViewModel.updatePost(updatedPost) { success ->
            val message = if (success) {
                "Post updated successfully!"
            } else {
                "Error updating post."
            }


            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed({
                if (success) {
                    findNavController().popBackStack()
                }
            }, 500)
        }
    }
}

