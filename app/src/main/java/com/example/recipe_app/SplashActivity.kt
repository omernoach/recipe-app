package com.example.recipe_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.cloudinary.android.MediaManager


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
