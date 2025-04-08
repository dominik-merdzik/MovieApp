package com.example.comp3025_assignment_03.view.favorites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.comp3025_assignment_03.R
import com.example.comp3025_assignment_03.databinding.ActivityFavoriteDetailBinding
import com.example.comp3025_assignment_03.model.FavoriteMovie
import com.example.comp3025_assignment_03.viewmodel.MovieViewModel

class FavoriteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteDetailBinding
    private lateinit var viewModel: MovieViewModel
    private var currentFavoriteMovie: FavoriteMovie? = null // Store the loaded favorite
    private var favoriteMovieId: String? = null

    companion object {
        // Use a consistent key for passing the ID via Intent
        const val EXTRA_FAVORITE_MOVIE_ID = "EXTRA_FAVORITE_MOVIE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        favoriteMovieId = intent.getStringExtra(EXTRA_FAVORITE_MOVIE_ID)

        if (favoriteMovieId == null) {
            Toast.makeText(this, "Error: Movie ID missing", Toast.LENGTH_LONG).show()
            finish() // Close activity if ID is missing
            return
        }

        observeViewModel()
        setupButtonClickListeners()

        // Fetch the details for this favorite movie
        viewModel.fetchFavoriteMovieById(favoriteMovieId!!)
    }

    private fun setupButtonClickListeners() {
        binding.btnDetailBack.setOnClickListener { finish() }

        binding.btnDetailDelete.setOnClickListener {
            handleDelete()
        }

        binding.btnDetailUpdate.setOnClickListener {
            handleUpdate()
        }
    }

    private fun observeViewModel() {
        viewModel.selectedFavoriteMovie.observe(this) { movie ->
            // Ensure the loaded movie corresponds to the ID we requested
            if (movie != null && movie.documentId == favoriteMovieId) {
                currentFavoriteMovie = movie // Store for reference
                populateUI(movie)
            } else if (movie == null && !viewModel.isLoadingFavorites.value!!) {
                // Handle case where movie wasn't found after loading finished
                Toast.makeText(this, "Could not load movie details.", Toast.LENGTH_SHORT).show()
                finish() // Go back if movie doesn't exist
            }
        }

        viewModel.errorMessageFavorites.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
                viewModel.clearFavoritesError() // Consume error
            }
        }
        // Observe saving/update errors specifically if needed
        viewModel.saveFavoriteError.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, "Update Error: $error", Toast.LENGTH_LONG).show()
                viewModel.clearSaveError() // Consume error
                setButtonsEnabled(true) // Re-enable buttons on error
            }
        }
    }

    private fun populateUI(movie: FavoriteMovie) {
        binding.tvDetailTitle.text = movie.title ?: "N/A"
        binding.tvDetailYear.text = "Year: ${movie.year ?: "N/A"}"
        binding.tvDetailStudio.text = "Studio: ${movie.studio ?: "N/A"}"
        binding.tvDetailRating.text = "Rating: ${movie.criticsRating ?: "N/A"}"
        binding.etDetailDescription.setText(movie.plot ?: "") // Populate editable field

        Glide.with(this)
            .load(movie.posterUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .into(binding.ivDetailPoster)
    }

    private fun handleDelete() {
        if (favoriteMovieId == null) return
        setButtonsEnabled(false) // Disable buttons during operation
        viewModel.deleteFavorite(favoriteMovieId!!)
        Toast.makeText(this, "Favorite deleted", Toast.LENGTH_SHORT).show()
        finish() // Go back after deleting
    }

    private fun handleUpdate() {
        if (currentFavoriteMovie == null || favoriteMovieId == null) {
            Toast.makeText(this, "Cannot update: Movie data not loaded", Toast.LENGTH_SHORT).show()
            return
        }
        setButtonsEnabled(false) // Disable buttons during operation

        val updatedDescription = binding.etDetailDescription.text.toString().trim()

        // Create a copy, only updating the description (plot)
        val movieToUpdate = currentFavoriteMovie!!.copy(
            plot = updatedDescription
            // All other fields remain the same as currentFavoriteMovie
        )

        viewModel.updateFavorite(movieToUpdate)
        Toast.makeText(this, "Updating description...", Toast.LENGTH_SHORT).show()
        // Assume success for now and finish. A better approach uses LiveData signal for success.
        finish()
    }

    private fun setButtonsEnabled(isEnabled: Boolean) {
        binding.btnDetailUpdate.isEnabled = isEnabled
        binding.btnDetailDelete.isEnabled = isEnabled
        binding.btnDetailBack.isEnabled = isEnabled
    }

    // Add these extension functions if you haven't already
    fun MovieViewModel.clearFavoritesError() {
        _errorMessageFavorites.value = null
    }
    fun MovieViewModel.clearSaveError() {
        _saveFavoriteError.value = null
    }
}