package com.example.proiect_jurnal

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.*

class CreatePostFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private var downloadUrl: String? = null
    private val PICK_IMAGE_REQUEST = 22
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            imageUri?.let { uri ->
                uploadImage(uri)
            }
        }
    }
    private fun uploadImage(imageUri: Uri) {
        val imageName = UUID.randomUUID().toString() + ".jpg"
        // Create a reference to the desired location in Firebase Storage
        val uid = auth.currentUser?.uid
        val imageRef: StorageReference = storageRef.child("images/$uid/$imageName")

        // Upload the file to Firebase Storage
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // File uploaded successfully
                // You can get the download URL of the uploaded file if needed
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    downloadUrl = uri.toString()
                    // Do something with the download URL
                }
                Toast.makeText(
                    requireContext(),
                    "Image succesfully uploaded!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Error uploading image",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Initialize Firebase Auth and Database
        auth = Firebase.auth
        database = Firebase.database.reference

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        val titleEditText = view.findViewById<EditText>(R.id.titleEditText)
        val contentEditText = view.findViewById<EditText>(R.id.contentEditText)
        val chooseImageButton = view.findViewById<Button>(R.id.chooseImageButton)
        val postButton = view.findViewById<Button>(R.id.postButton)
        val motionLayout = view.findViewById<MotionLayout>(R.id.motionLayout)



        chooseImageButton.setOnClickListener {
            pickImage()
        }

        postButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = contentEditText.text.toString()
            val userId = auth.currentUser?.uid

            // Validate input fields
            if (title.isNotEmpty() && userId != null) {
                // Create a unique ID for the post
                val postId = database.child("posts").child(userId).push().key

                //TODO: poza si video

                // Create a post object
                val post = Post(postId = postId, title = title, content = description, uid = userId, picture = downloadUrl)

                if (postId != null) {
                    database.child("posts").child(userId).child(postId).setValue(post).addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Post created successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        motionLayout.transitionToEnd()
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Error uploading post!",
                            Toast.LENGTH_SHORT
                        ).show()
                        motionLayout.transitionToEnd()
                    }
                    downloadUrl = null
                }
            }
            else {
                // Display an error message if any field is empty or user is not authenticated
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                motionLayout.transitionToEnd()
            }
        }
        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout, startId: Int, endId: Int) {}

            override fun onTransitionChange(
                motionLayout: MotionLayout,
                startId: Int,
                endId: Int,
                progress: Float
            ) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                if (currentId == R.id.end) {
                    motionLayout.transitionToStart()
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {}
        })
    }
}