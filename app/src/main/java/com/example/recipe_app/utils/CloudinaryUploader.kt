import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class CloudinaryUploader {
    companion object {
        fun uploadImage(uri: Uri, callback: (imageUrl: String?) -> Unit) {
            MediaManager.get().upload(uri)
                .unsigned("unsigned") // Replace with your unsigned preset name
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val imageUrl = resultData["secure_url"] as? String
                        callback(imageUrl)
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        callback(null)
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        callback(null)
                    }
                }).dispatch()
        }
    }
}
