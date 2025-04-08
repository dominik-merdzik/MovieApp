package com.example.comp3025_assignment_03.view.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider // Use androidx lifecycle
import com.example.comp3025_assignment_03.databinding.ActivityLoginBinding
import com.example.comp3025_assignment_03.view.search.MovieSearchActivity // Or your main movie list activity
import com.example.comp3025_assignment_03.viewmodel.MovieViewModel // Assuming auth logic lives here for now
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth // Declaration
    private lateinit var movieViewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth HERE, before first use
        auth = Firebase.auth

        // Now you can safely check the current user
        if (auth.currentUser != null) {
            navigateToMovieList()
            return // Skip login UI if already authenticated
        }

        // Initialize ViewModel
        // Ensure ViewModel handles Auth state and potentially repository calls
        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        // Setup button listeners
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }

        binding.btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        // Sign in using Firebase Auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "signInWithEmail:success")
                    navigateToMovieList()
                } else {
                    Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun navigateToMovieList() {
        val intent = Intent(this, MovieSearchActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.pbLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.btnGoToRegister.isEnabled = !isLoading
    }
}