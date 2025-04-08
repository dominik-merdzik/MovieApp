package com.example.comp3025_assignment_03.view.favorites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.comp3025_assignment_03.databinding.ActivityFavoriteListBinding
import com.example.comp3025_assignment_03.model.FavoriteMovie
// Import the new Detail Activity (we'll create it next)
// import com.example.comp3025_assignment_03.view.favorites.FavoriteDetailActivity
import com.example.comp3025_assignment_03.viewmodel.MovieViewModel

// No longer needs to implement FavoriteItemClickListener
class FavoriteListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteListBinding
    private lateinit var viewModel: MovieViewModel
    private lateinit var favoriteAdapter: FavoriteMovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchFavoriteMovies()
    }

    private fun setupRecyclerView() {
        // Initialize adapter, passing the click handling lambda directly
        favoriteAdapter = FavoriteMovieAdapter { clickedMovie ->
            // This lambda block executes when an item is clicked
            navigateToFavoriteDetail(clickedMovie.documentId)
        }
        binding.rvFavoriteMovies.adapter = favoriteAdapter
        binding.rvFavoriteMovies.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        viewModel.favoriteMovies.observe(this) { movies ->
            favoriteAdapter.submitList(movies)
            binding.tvErrorFavorites.visibility = View.GONE
        }
        // ... observe isLoadingFavorites and errorMessageFavorites as before ...
        viewModel.isLoadingFavorites.observe(this) { isLoading ->
            binding.pbLoadingFavorites.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.errorMessageFavorites.observe(this) { error ->
            if (error != null && binding.pbLoadingFavorites.visibility == View.GONE) {
                binding.tvErrorFavorites.visibility = View.VISIBLE
                binding.tvErrorFavorites.text = error
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            } else {
                binding.tvErrorFavorites.visibility = View.GONE
            }
        }
    }

    // Function to navigate to the new Detail screen (to be created)
    private fun navigateToFavoriteDetail(movieId: String?) {
        if (movieId == null) {
            Toast.makeText(this, "Error: Cannot view details without ID", Toast.LENGTH_SHORT).show()
            return
        }
        // Use the actual class and the key defined in FavoriteDetailActivity
        val intent = Intent(this, FavoriteDetailActivity::class.java)
        intent.putExtra(FavoriteDetailActivity.EXTRA_FAVORITE_MOVIE_ID, movieId)
        startActivity(intent)
        // Toast.makeText(this, "Navigate to details for ID: $movieId", Toast.LENGTH_SHORT).show() // Remove placeholder
    }

    // Remove the old onEditClick and onDeleteClick override functions
    // override fun onEditClick(...) { ... } // REMOVE
    // override fun onDeleteClick(...) { ... } // REMOVE
}