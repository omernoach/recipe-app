package com.example.recipe_app.Data.repository

import android.net.Uri
import android.util.Log
import com.example.recipe_app.Data.local.UserDao
import com.example.recipe_app.Data.model.User
import com.example.recipe_app.Data.remote.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    private val firebaseService = FirebaseService()

    suspend fun updateUserInRoom(user: User) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }

    suspend fun updateUserInFirebase(name: String, profileImageUri: Uri?) = withContext(Dispatchers.IO) {
        firebaseService.updateUserProfile(name, profileImageUri)
    }

    suspend fun getUser(): User? {
        return userDao.getUser()
    }
    suspend fun getCurrentFirebaseUser(): User? = withContext(Dispatchers.IO) {
        val firebaseUser = firebaseService.getCurrentUser()
        firebaseUser?.let {
            User(
                uid = it.uid,
                name = it.displayName ?: "",
                email = it.email ?: "",
                profileImageUrl = it.photoUrl?.toString() ?: ""
            )
        }
    }
    suspend fun logoutUser() {
        firebaseService.logoutUser()
        try {
            userDao.deleteUser()
            Log.d("UserRepository", "User deleted from ROOM successfully.")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error deleting user from ROOM", e)
        }
    }
}
