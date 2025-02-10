package com.example.recipe_app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.recipe_app.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.lifecycleScope
import com.example.recipe_app.Data.repository.PostRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var postRepository: PostRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        Toast.makeText(this, "Welcome " + user?.photoUrl.toString(), Toast.LENGTH_SHORT).show()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_posts, R.id.navigation_user_posts, R.id.createPostFragment, R.id.navigation_userProfile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        postRepository = PostRepository(application)

        synchronizeFirebaseWithRoom()
    }

    private fun synchronizeFirebaseWithRoom() {
        lifecycleScope.launch {
            try {
                postRepository.syncData()

                Toast.makeText(this@MainActivity, "Firebase and ROOM synced!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error syncing Firebase with ROOM", e)
                Toast.makeText(this@MainActivity, "Failed to sync data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

