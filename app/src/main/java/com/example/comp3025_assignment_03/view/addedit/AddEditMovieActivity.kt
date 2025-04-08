package com.example.comp3025_assignment_03.view.addedit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.comp3025_assignment_03.databinding.ActivityAddEditMovieBinding
import com.example.comp3025_assignment_03.model.FavoriteMovie
import com.example.comp3025_assignment_03.viewmodel.MovieViewModel

class AddEditMovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditMovieBinding
    private lateinit var viewModel: MovieViewModel
    private var editingMovieId: String? = null // Store the ID if editing
    private var currentMovie: FavoriteMovie? = null // Store loaded movie for updates

    companion object {
        const val EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID" // Key for passing ID via Intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        // Check if an ID was passed for editing
        editingMovieId = intent.getStringExtra(EXTRA_MOVIE_ID)

        setupUI()
        observeViewModel()

        if (editingMovieId != null) {
            // Edit mode: Fetch the existing movie data
            title = "Edit Favorite Movie" // Set activity title
            viewModel.fetchFavoriteMovieById(editingMovieId!!)
        } else {
            // Add mode: Clear any stale selected movie data
            title = "Add New Favorite" // Set activity title
            viewModel.clearSelectedFavorite()
        }

        binding.btnSave.setOnClickListener { handleSave() }
        binding.btnCancel.setOnClickListener { finish() } // Simply close activity on cancel
    }

    private fun setupUI() {
        // Initial UI setup if needed
    }

    private fun observeViewModel() {
        // Observe the movie details when editing
        viewModel.selectedFavoriteMovie.observe(this) { movie ->
            if (editingMovieId != null && movie != null && movie.documentId == editingMovieId) {
                // Populate fields only if we are in edit mode and the loaded movie matches the ID
                currentMovie = movie // Store for update reference
                binding.etTitle.setText(movie.title ?: "")
                binding.etStudio.setText(movie.studio ?: "")
                binding.etRating.setText(movie.criticsRating ?: "")
                binding.etPosterUrl.setText(movie.posterUrl ?: "")
                binding.etDescription.setText(movie.plot ?: "") // Assuming FavoriteMovie has plot field
            }
        }

        // Observe saving state
        viewModel.isSavingFavorite.observe(this) { isLoading ->
            binding.pbSaving.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
            binding.btnCancel.isEnabled = !isLoading
        }

        // Observe saving errors
        viewModel.saveFavoriteError.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
                viewModel.clearSaveError() // Consume the error
            }
        }
        // Observe loading state (when fetching for edit)
        viewModel.isLoadingFavorites.observe(this) { isLoading ->
            // Can use a different progress bar or overlay if needed
            binding.pbSaving.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun handleSave() {
        // --- Input Validation (Basic Example) ---
        val title = binding.etTitle.text.toString().trim()
        val studio = binding.etStudio.text.toString().trim()
        val rating = binding.etRating.text.toString().trim()
        val posterUrl = binding.etPosterUrl.text.toString().trim()
        val description = binding.etDescription.text.toString().trim() // Optional

        if (title.isEmpty()) {
            binding.tilTitle.error = "Title cannot be empty"
            return
        } else {
            binding.tilTitle.error = null // Clear error
        }
        // Add more validation as needed...


        // Create or Update FavoriteMovie object
        val movieToSave = currentMovie ?: FavoriteMovie() // Use existing if editing, else new
        movieToSave.title = title
        movieToSave.studio = studio
        movieToSave.criticsRating = rating
        movieToSave.posterUrl = posterUrl
        movieToSave.plot = description // Save description/plot if field exists in model


        if (editingMovieId != null) {
            // --- Update Existing Movie ---
            movieToSave.documentId = editingMovieId // Ensure ID is set for update
            viewModel.updateFavorite(movieToSave)
        } else {
            // --- Add New Movie ---
            // Assuming 'year' and 'imdbID' might come from an initial OMDb search result
            // You'd need a way to pass those to this screen if adding directly
            // movieToSave.year = passedYear
            // movieToSave.imdbID = passedImdbId
            viewModel.addFavorite(movieToSave)
        }

        // Optionally observe a success LiveData from ViewModel before finishing
        // For now, just finish() - assumes operation completes quickly or handles errors via Toast
        Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show()
        // Consider using a SingleLiveEvent from ViewModel to signal success and then finish()
        finish() // Close activity after initiating save
    }

    // Add this extension function to MovieViewModel to clear the error state
    fun MovieViewModel.clearSaveError() {
        _saveFavoriteError.value = null
    }
    // Add plot field to FavoriteMovie data class if you haven't already
}