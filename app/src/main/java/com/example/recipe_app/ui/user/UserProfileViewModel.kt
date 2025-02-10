import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipe_app.Data.local.UserDao
import com.example.recipe_app.Data.local.UserDatabase
import com.example.recipe_app.Data.model.User
import com.example.recipe_app.Data.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao: UserDao = UserDatabase.getDatabase(application).userDao()
    private val userRepository = UserRepository(userDao)

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            // קודם כל, בודקים אם יש נתונים מהפיירבייס
            val userDataFromFirebase = userRepository.getCurrentFirebaseUser()
            if (userDataFromFirebase != null) {
                Log.d("UserProfileViewModel", "Fetched user from Firebase: Name=${userDataFromFirebase.name}, Email=${userDataFromFirebase.email}, ProfileImage=${userDataFromFirebase.profileImageUrl}")

                // עדכון ה- Room עם הנתונים מהפיירבייס
                userRepository.updateUserInRoom(userDataFromFirebase)
                _user.postValue(userDataFromFirebase)
            } else {
                Log.d("UserProfileViewModel", "No user data found in Firebase. Checking Room database...")

                // אם אין נתונים בפיירבייס, שולפים מה- Room
                val userData = userRepository.getUser()
                if (userData != null) {
                    Log.d("UserProfileViewModel", "Fetched user from Room: Name=${userData.name}, Email=${userData.email}, ProfileImage=${userData.profileImageUrl}")
                    _user.postValue(userData)
                } else {
                    Log.d("UserProfileViewModel", "No user data found in Room database.")
                }
            }
        }
    }


    fun updateUserProfile(newName: String, profileImageUri: Uri?) {
        viewModelScope.launch {
            val imageUrl = profileImageUri?.let { uri ->
                var resultUrl: String? = null
                val uploadResult = suspendCancellableCoroutine<String?> { continuation ->
                    CloudinaryUploader.uploadImage(uri) { uploadedImageUrl ->
                        continuation.resume(uploadedImageUrl) { }
                    }
                }
                resultUrl = uploadResult
                resultUrl
            } ?: _user.value?.profileImageUrl

            if (imageUrl != null) {
                userRepository.updateUserInFirebase(newName, Uri.parse(imageUrl))

                val firebaseUser = userRepository.getCurrentFirebaseUser()
                firebaseUser?.let {
                    Log.d("UserProfileViewModel", "Firebase Update: Name=${it.name}, Email=${it.email}, ProfileImage=${it.profileImageUrl}")
                } ?: Log.e("UserProfileViewModel", "Failed to fetch updated user from Firebase.")

                val updatedUser = _user.value?.copy(name = newName, profileImageUrl = imageUrl)
                if (updatedUser != null) {
                    userRepository.updateUserInRoom(updatedUser)
                    _user.postValue(updatedUser)
                    Log.d("UserProfileViewModel", "Updated user in Room: Name=${updatedUser.name}, Email=${updatedUser.email}, ProfileImage=${updatedUser.profileImageUrl}")
                } else {
                    Log.e("UserProfileViewModel", "Attempted to update user but no existing user data found in Room.")
                }
            } else {
                Log.e("UserProfileViewModel", "Image upload failed, profile not updated.")
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            userRepository.logoutUser()
            _user.postValue(null)
        }
    }
}