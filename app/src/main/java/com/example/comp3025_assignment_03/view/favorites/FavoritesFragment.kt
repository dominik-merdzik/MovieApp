package com.example.comp3025_assignment_03.view.favorites

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels // Use activityViewModels
import androidx.lifecycle.LifecycleOwner // Import LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.comp3025_assignment_03.databinding.FragmentFavoritesBinding // Use Fragment binding
import com.example.comp3025_assignment_03.model.FavoriteMovie
// Import the Detail Activity
import com.example.comp3025_assignment_03.view.favorites.FavoriteDetailActivity
import com.example.comp3025_assignment_03.viewmodel.MovieViewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    // Use activityViewModels to share the ViewModel with the Activity and other Fragments
    private val viewModel: MovieViewModel by activityViewModels()
    private lateinit var favoriteAdapter: FavoriteMovieAdapter // The adapter for favorites list

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchFavoriteMovies()
    }

    private fun setupRecyclerView() {
        // Initialize adapter, passing the click handling lambda
        favoriteAdapter = FavoriteMovieAdapter { clickedMovie ->
            // Handle item click -> navigate to detail screen
            navigateToFavoriteDetail(clickedMovie.documentId)
        }
        binding.rvFavoriteMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoriteAdapter
        }
    }

    private fun observeViewModel() {
        // Use viewLifecycleOwner for observing LiveData in Fragments
        val owner: LifecycleOwner = viewLifecycleOwner

        viewModel.favoriteMovies.observe(owner) { movies ->
            favoriteAdapter.submitList(movies)
            binding.tvErrorFavorites.visibility = View.GONE // Hide error on new data
        }
        viewModel.isLoadingFavorites.observe(owner) { isLoading ->
            binding.pbLoadingFavorites.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Optionally hide RecyclerView when loading or error is shown
            binding.rvFavoriteMovies.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        }
        viewModel.errorMessageFavorites.observe(owner) { error ->
            if (error != null && binding.pbLoadingFavorites.visibility == View.GONE) {
                binding.tvErrorFavorites.visibility = View.VISIBLE
                binding.tvErrorFavorites.text = error
                binding.rvFavoriteMovies.visibility = View.INVISIBLE // Hide list on error
                // Optionally clear the error in the ViewModel after showing it
                // viewModel.clearFavoritesError()
            } else {
                binding.tvErrorFavorites.visibility = View.GONE
                // Ensure RecyclerView is visible if no error and not loading
                if (binding.pbLoadingFavorites.visibility == View.GONE) {
                    binding.rvFavoriteMovies.visibility = View.VISIBLE
                }
            }
        }
    }

    // Function to navigate to the Detail screen
    private fun navigateToFavoriteDetail(movieId: String?) {
        if (movieId == null) {
            Toast.makeText(requireContext(), "Error: Cannot view details without ID", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(requireActivity(), FavoriteDetailActivity::class.java)
        intent.putExtra(FavoriteDetailActivity.EXTRA_FAVORITE_MOVIE_ID, movieId)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Important to prevent memory leaks
    }
}