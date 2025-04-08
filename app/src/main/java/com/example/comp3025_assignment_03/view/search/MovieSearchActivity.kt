package com.example.comp3025_assignment_03.view.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels // Import for activityViewModels delegate
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider // Can still use this, or the delegate
import com.example.comp3025_assignment_03.R
import com.example.comp3025_assignment_03.databinding.ActivityMovieSearchBinding
import com.example.comp3025_assignment_03.view.auth.LoginActivity
import com.example.comp3025_assignment_03.view.favorites.FavoritesFragment
import com.example.comp3025_assignment_03.viewmodel.MovieViewModel // Import ViewModel
// Import other necessary classes like FirebaseAuth if logout is handled here
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MovieSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieSearchBinding
    private lateinit var auth: FirebaseAuth
    // Get ViewModel scoped to this Activity - Fragments will share this instance
    private val viewModel: MovieViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // --- Setup Bottom Navigation Listener ---
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_search -> {
                    selectedFragment = SearchFragment()
                    // Keep search bar visible maybe?
                    binding.searchBarLayout.visibility = View.VISIBLE
                }
                R.id.navigation_favorites -> {
                    selectedFragment = FavoritesFragment()
                    // Hide search bar when viewing favorites?
                    binding.searchBarLayout.visibility = View.GONE
                }
                R.id.navigation_logout -> {
                    handleLogout()
                    return@setOnItemSelectedListener false // Don't select logout item
                }
            }

            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_container, it)
                    .commit()
            }
            true
        }

        // --- Load the default fragment ---
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.navigation_search
            // It will trigger the listener above to load SearchFragment
        }


        // --- Search Button Click Logic (Remains in Activity) ---
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                // Trigger the search in the shared ViewModel
                viewModel.searchMovies(query) // SearchFragment observes the results
            }
            binding.etSearch.clearFocus()
            // Optional: Hide keyboard
            // val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        // --- REMOVE RecyclerView setup and LiveData observation from Activity ---
        // setupRecyclerView() // REMOVE CALL
        // observeViewModel() // REMOVE CALL

    } // --- End of onCreate ---

    // --- REMOVE RecyclerView/Adapter related functions ---
    // private fun setupRecyclerView() { ... } // REMOVE
    // private fun observeViewModel() { ... } // REMOVE


    // Keep handleLogout and navigateToLogin
    private fun handleLogout() { /* ... */ }
    private fun navigateToLogin() { /* ... */ }

}