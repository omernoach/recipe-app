package com.example.recipe_app.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipe_app.Data.local.UserDatabase
import com.example.recipe_app.Data.model.User
import com.google.android.material.textfield.TextInputLayout
import com.example.recipe_app.Data.remote.FirebaseService
import com.example.recipe_app.MainActivity
import com.example.recipe_app.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseService: FirebaseService
    private lateinit var emailEditText: TextInputLayout
    private lateinit var passwordEditText: TextInputLayout
    private lateinit var userDatabase: UserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userDatabase = UserDatabase.getDatabase(applicationContext)

        CoroutineScope(Dispatchers.Main).launch {
            val user = withContext(Dispatchers.IO) {
                userDatabase.userDao().getUser()
            }

            if (user != null) {
                Toast.makeText(this@LoginActivity, "Welcome back, ${user.name}!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                setContentView(R.layout.activity_login)
                setupLoginUI()
            }
        }
    }

    private fun setupLoginUI() {
        firebaseService = FirebaseService()
        userDatabase = UserDatabase.getDatabase(applicationContext)

        val loginButton = findViewById<Button>(R.id.btn_login)
        val registerText = findViewById<TextView>(R.id.tv_register)
        emailEditText = findViewById<TextInputLayout>(R.id.email_input)
        passwordEditText = findViewById<TextInputLayout>(R.id.password_input)

        loginButton.setOnClickListener { _: View? ->
            val email = emailEditText.editText?.text.toString().trim()
            val password = passwordEditText.editText?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(email, password)
        }

        registerText.setOnClickListener { _: View? ->
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val success = withContext(Dispatchers.IO) {
                firebaseService.loginUser(email, password)
            }

            if (success) {
                saveUserToRoom()
                Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private suspend fun saveUserToRoom() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val uid = user.uid
            val name = user.displayName ?: "Default Name"
            val profileImageUrl = user.photoUrl?.toString() ?: "default_image_url"

            val userToSave = User(uid = uid, email = user.email ?: "", name = name, profileImageUrl = profileImageUrl)
            userDatabase.userDao().insertUser(userToSave)
        }
    }
}
