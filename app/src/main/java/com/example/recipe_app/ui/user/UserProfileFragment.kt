package com.example.recipe_app.ui.user

import UserProfileViewModel
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.recipe_app.R
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

class UserProfileFragment : Fragment() {

    private val userProfileViewModel: UserProfileViewModel by viewModels()

    private lateinit var profileImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var emailTextView: TextView
    private lateinit var saveButton: Button
    private lateinit var changeImageButton: Button
    private lateinit var logoutButton: ImageButton

    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        profileImageView = view.findViewById(R.id.profile_image)
        nameEditText = view.findViewById(R.id.name_edit_text)
        emailTextView = view.findViewById(R.id.email_text_view)
        saveButton = view.findViewById(R.id.btn_save)
        changeImageButton = view.findViewById(R.id.btn_change_image)
        logoutButton = view.findViewById(R.id.btn_logout)

        userProfileViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            nameEditText.setText(user?.name)
            emailTextView.text = user?.email
            Picasso.get()
                .load(user?.profileImageUrl)
                .into(profileImageView)

            Log.d("UserProfileFragment", "User data observed: Name=${user?.name}, Email=${user?.email}, ProfileImage=${user?.profileImageUrl}")
        })

        changeImageButton.setOnClickListener {
            pickImageFromGallery()
        }

        saveButton.setOnClickListener {
            val newName = nameEditText.text.toString().trim()
            if (newName.isNotEmpty()) {
                userProfileViewModel.updateUserProfile(newName, selectedImageUri)
                Snackbar.make(view, "Profile updated successfully!", Snackbar.LENGTH_SHORT).show()
                Log.d("UserProfileFragment", "User profile updated with new name: $newName")
            } else {
                Snackbar.make(view, "Name cannot be empty!", Snackbar.LENGTH_SHORT).show()
                Log.e("UserProfileFragment", "Failed to update profile: Name is empty")
            }
        }

        logoutButton.setOnClickListener {
            userProfileViewModel.logout()  // מבצע את הלוגאאוט
            Snackbar.make(view, "Logged out successfully!", Snackbar.LENGTH_SHORT).show()
            Log.d("UserProfileFragment", "User logged out")

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            profileImageView.setImageURI(selectedImageUri)
            Log.d("UserProfileFragment", "Selected image URI: $selectedImageUri")
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1001
    }
}
