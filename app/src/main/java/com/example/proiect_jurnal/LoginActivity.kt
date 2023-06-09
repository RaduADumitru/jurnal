package com.example.proiect_jurnal

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize Firebase Auth
        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Get references to the buttons
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // Set click listeners for the buttons
        createAccountButton.setOnClickListener {
            // Animation on tap
            val animatePostButton1: ObjectAnimator = ObjectAnimator.ofFloat(createAccountButton, "translationY", 50f)
            animatePostButton1.duration = 200

            val animatePostButton2: ObjectAnimator = ObjectAnimator.ofFloat(createAccountButton, "translationY", 0f)
            animatePostButton2.duration = 200

            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(animatePostButton1, animatePostButton2)
            animatorSet.start()

            val emailEditText = findViewById<EditText>(R.id.emailEditText)
            val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            createAccount(email, password)
        }

        loginButton.setOnClickListener {
                        // Animation on tap
            val animatePostButton3: ObjectAnimator = ObjectAnimator.ofFloat(loginButton, "translationY", 50f)
            animatePostButton3.duration = 200

            val animatePostButton4: ObjectAnimator = ObjectAnimator.ofFloat(loginButton, "translationY", 0f)
            animatePostButton4.duration = 200

            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(animatePostButton3, animatePostButton4)
            animatorSet.start()

            val emailEditText = findViewById<EditText>(R.id.emailEditText)
            val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            signIn(email, password)
        }
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            //reload()
        }
    }
    fun createAccount(email : String?, password : String?) {
        if(!email.isNullOrEmpty() && !password.isNullOrEmpty()) {auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Couldn't create account.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    //updateUI(null)
                }
            }}
        else {                    Toast.makeText(
            baseContext,
            "Couldn't create account.",
            Toast.LENGTH_SHORT,
        ).show()}

    }
    public fun signIn(email: String?, password : String?) {
        if(!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        //updateUI(null)
                    }
                }
        }
        else {
            // If sign in fails, display a message to the user.
            Toast.makeText(
                baseContext,
                "Authentication failed.",
                Toast.LENGTH_SHORT,
            ).show()
            //updateUI(null)
        }
    }
    public fun getCurrentUser() {
        val user = Firebase.auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl

            // Check if user's email is verified
            val emailVerified = it.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            val uid = it.uid
        }
    }
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // User is signed in, update UI accordingly
            // For example, you can navigate to the main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Optional, to close the LoginActivity after navigation
        } else {
            // User is signed out, update UI accordingly
            // For example, you can display a message or show login options
            Toast.makeText(this, "You are not signed in.", Toast.LENGTH_SHORT).show()
        }
    }
}