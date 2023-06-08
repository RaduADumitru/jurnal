package com.example.proiect_jurnal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class CreatePostFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        // Initialize Firebase Auth and Database
        auth = Firebase.auth
        database = Firebase.database.reference

        val titleEditText = view.findViewById<EditText>(R.id.titleEditText)
        val contentEditText = view.findViewById<EditText>(R.id.contentEditText)
        val postButton = view.findViewById<Button>(R.id.postButton)

        postButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = contentEditText.text.toString()
            val userId = auth.currentUser?.uid

            // Validate input fields
            if (title.isNotEmpty() && description.isNotEmpty() && userId != null) {
                // Create a unique ID for the post
                val postId = database.child("posts").child(userId).push().key

                //TODO: poza si video

                // Create a post object
                val post = Post(postId = postId, title = title, content = description, uid = userId)

                if (postId != null) {
                    database.child("posts").child(userId).child(postId).setValue(post)
                    Toast.makeText(requireContext(), "Post created successfully", Toast.LENGTH_SHORT).show()
                }

            }
                else {
                // Display an error message if any field is empty or user is not authenticated
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}