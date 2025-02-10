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
            val userData = userRepository.getUser()
            if (userData != null) {
                Log.d(
                    "UserProfileViewModel",
                    "Fetched user from Room: Name=${userData.name}, Email=${userData.email}, ProfileImage=${userData.profileImageUrl}"
                )
                _user.postValue(userData)
            } else {
                Log.d("UserProfileViewModel", "No user data found in Room database.")
            }
            val userDataFromFirebase = userRepository.getCurrentFirebaseUser()
            if (userDataFromFirebase != null) {
                Log.d(
                    "UserProfileViewModel",
                    "Fetched user from Firebase: Name=${userDataFromFirebase.name}, Email=${userDataFromFirebase.email}, ProfileImage=${userDataFromFirebase.profileImageUrl}"
                )
            } else {
                Log.d("UserProfileViewModel", "No user data found in Firebase.")
            }
        }
    }

    fun updateUserProfile(newName: String, profileImageUri: Uri?) {
        viewModelScope.launch {
            // אם יש תמונה חדשה, נעלה אותה ל-Cloudinary
            val imageUrl = profileImageUri?.let { uri ->
                // העלאת התמונה והמתנה לתוצאה
                var resultUrl: String? = null
                val uploadResult = suspendCancellableCoroutine<String?> { continuation ->
                    CloudinaryUploader.uploadImage(uri) { uploadedImageUrl ->
                        continuation.resume(uploadedImageUrl) { }
                    }
                }
                resultUrl = uploadResult
                resultUrl
            } ?: _user.value?.profileImageUrl // אם לא נבחרה תמונה חדשה, נשמור את התמונה הקודמת

            // אם התמונה הועלתה בהצלחה או שנשמרה התמונה הקודמת, נעדכן את Firebase ו-Room
            if (imageUrl != null) {
                // עדכון ב-Firebase
                userRepository.updateUserInFirebase(newName, Uri.parse(imageUrl))

                // Fetch updated user from Firebase to verify
                val firebaseUser = userRepository.getCurrentFirebaseUser()
                firebaseUser?.let {
                    Log.d("UserProfileViewModel", "Firebase Update: Name=${it.name}, Email=${it.email}, ProfileImage=${it.profileImageUrl}")
                } ?: Log.e("UserProfileViewModel", "Failed to fetch updated user from Firebase.")

                // עדכון ב-Room DB
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
            userRepository.logoutUser()  // מבצע את הלוגאאוט גם מ-Firebase וגם מ-ROOM
            _user.postValue(null)  // לאחר הלוגאאוט, מעדכן את ה-LiveData ל-null
        }
    }
}