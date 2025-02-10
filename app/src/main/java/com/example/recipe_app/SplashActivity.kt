package com.example.recipe_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.cloudinary.android.MediaManager
import com.example.recipe_app.ui.user.LoginActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        val config = mapOf(
            "cloud_name" to "djargrig1",
            "api_key" to "119892779381239",
            "api_secret" to "gY2jO4PNNyXBIMMsCbMzGfP3Fx0"
        )
        MediaManager.init(this, config)

        val intent = Intent(
            this,
            LoginActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}
