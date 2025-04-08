package com.example.comp3025_assignment_03.view.details // Adjust package if needed

import android.os.Bundle
import android.widget.Toast // Import Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.comp3025_assignment_03.databinding.ActivityMovieDetailBinding // Adjust import if needed
import com.example.comp3025_assignment_03.model.FavoriteMovie // Import FavoriteMovie model
import com.example.comp3025_assignment_03.model.MovieDetail // Import MovieDetail model (from OMDb)
import com.example.comp3025_assignment_03.viewmodel.MovieViewModel

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailBinding
    private lateinit var movieViewModel: MovieViewModel
    private var currentMovieDetail: MovieDetail? = null // Store the fetched OMDb details
    private var currentImdbID: String? = null // Store the imdbID passed to this activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        // retrieve the imdbID passed from the search screen
        currentImdbID = intent.getStringExtra("imdbID") // Store it
        currentImdbID?.let {
            movieViewModel.fetchMovieDetails(it) // Fetch details from OMDb
        }

        observeViewModel() // Setup observers

        // back button to finish the activity and return to search screen
        binding.btnBack.setOnClickListener {
            finish()
        }

        // --- Add listener for the new button ---
        binding.btnAddToFavorites.setOnClickListener {
            addCurrentMovieToFavorites()
        }
    }

    private fun observeViewModel() {
        // Observe the OMDb movieDetail LiveData to update the UI
        movieViewModel.movieDetail.observe(this) { movie ->
            currentMovieDetail = movie // Store the latest details
            if (movie != null) {
                binding.tvTitle.text = movie.title
                binding.tvYear.text = "Year: ${movie.year}"
                binding.tvRated.text = "Rated: ${movie.rated}"
                binding.tvReleased.text = "Released: ${movie.released}"
                binding.tvRuntime.text = "Runtime: ${movie.runtime}"
                binding.tvGenre.text = "Genre: ${movie.genre}"
                binding.tvDirector.text = "Director: ${movie.director}"
                binding.tvWriter.text = "Writer: ${movie.writer}"
                binding.tvActors.text = "Actors: ${movie.actors}"
                binding.tvPlot.text = movie.plot
                Glide.with(this)
                    .load(movie.posterUrl)
                    .placeholder(com.example.comp3025_assignment_03.R.drawable.ic_launcher_background) // Use placeholders
                    .error(com.example.comp3025_assignment_03.R.drawable.ic_launcher_foreground)
                    .into(binding.ivPoster)

                // Enable the button once details are loaded
                binding.btnAddToFavorites.isEnabled = true

            } else {
                // Disable button if details fail to load
                binding.btnAddToFavorites.isEnabled = false
            }
        }
        // Optional: Observe saving errors from ViewModel
        movieViewModel.saveFavoriteError.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, "Error saving: $error", Toast.LENGTH_LONG).show()
                movieViewModel.clearSaveError() // Consume error
            }
        }
    }

    private fun addCurrentMovieToFavorites() {
        // Ensure we have details loaded and the imdbID
        if (currentMovieDetail == null || currentImdbID == null) {
            Toast.makeText(this, "Movie details not loaded yet.", Toast.LENGTH_SHORT).show()
            return
        }

        // --- Map MovieDetail (OMDb) to FavoriteMovie (Firestore) ---
        val favoriteMovie = FavoriteMovie(
            // documentId will be set by Firestore on add
            // userId will be set by the repository
            title = currentMovieDetail!!.title,
            year = currentMovieDetail!!.year,
            imdbID = currentImdbID, // Store the original IMDb ID
            posterUrl = currentMovieDetail!!.posterUrl,
            plot = currentMovieDetail!!.plot, // Include plot/description

            // Fields required by assignment list view, but not directly in OMDb MovieDetail:
            studio = null, // Set to null or default, expect user to edit later
            criticsRating = currentMovieDetail!!.rated // Using 'Rated' as a placeholder for 'Critics Rating'
            // Alternatively, use imdbRating if available or leave null
        )

        // Call the ViewModel to add the movie
        movieViewModel.addFavorite(favoriteMovie)

        // Provide feedback and maybe disable button/change text
        Toast.makeText(this, "${favoriteMovie.title} added to favorites!", Toast.LENGTH_SHORT).show()
        binding.btnAddToFavorites.isEnabled = false // Prevent adding multiple times
        binding.btnAddToFavorites.text = "Added!"

    }
    // Add this extension function if you haven't already (e.g., in AddEditMovieActivity)
    fun MovieViewModel.clearSaveError() {
        _saveFavoriteError.value = null
    }
}