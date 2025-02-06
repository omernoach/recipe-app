package com.example.recipe_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.example.recipe_app.Data.remote.FirebaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseService: FirebaseService
    private lateinit var emailEditText: TextInputLayout
    private lateinit var displayNameEditText: TextInputLayout
    private lateinit var passwordEditText: TextInputLayout
    private lateinit var confirmPasswordEditText: TextInputLayout
    private lateinit var loginText: TextView
    private lateinit var registerButton: Button
    private lateinit var imagePickerButton: Button
    private var profileImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val imagePreview = findViewById<ImageView>(R.id.img_preview)
            imagePreview.setImageURI(it)

            profileImageUri = it
            Toast.makeText(this, "Image selected successfully!", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseService = FirebaseService()

        emailEditText = findViewById(R.id.email_input)
        displayNameEditText = findViewById(R.id.display_name)
        passwordEditText = findViewById(R.id.password_input)
        confirmPasswordEditText = findViewById(R.id.confirm_password_input)
        registerButton = findViewById(R.id.btn_register)
        loginText = findViewById(R.id.tv_login)
        imagePickerButton = findViewById(R.id.btn_pick_image)

        imagePickerButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        registerButton.setOnClickListener {
            val email = emailEditText.editText?.text.toString().trim()
            val displayName = displayNameEditText.editText?.text.toString().trim()
            val password = passwordEditText.editText?.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.editText?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || displayName.isEmpty() || profileImageUri == null) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CloudinaryUploader.uploadImage(profileImageUri!!) { imageUrl ->
                if (imageUrl != null) {
                    val imageUri = Uri.parse(imageUrl)
                    CoroutineScope(Dispatchers.Main).launch {
                        val success = withContext(Dispatchers.IO) {
                            firebaseService.registerUser(email, password, displayName, imageUri)
                        }

                        if (success) {
                            Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loginText.setOnClickListener { _: View? ->
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
