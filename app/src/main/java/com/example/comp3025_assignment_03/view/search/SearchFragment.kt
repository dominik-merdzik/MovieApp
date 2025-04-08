package com.example.comp3025_assignment_03.view.search

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.comp3025_assignment_03.databinding.FragmentSearchBinding // Ensure this binding class name is correct
import com.example.comp3025_assignment_03.view.details.MovieDetailActivity // For navigation
import com.example.comp3025_assignment_03.viewmodel.MovieViewModel
// Import the original MovieAdapter for search results
import com.example.comp3025_assignment_03.view.search.MovieAdapter


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieViewModel by activityViewModels()
    private lateinit var movieAdapter: MovieAdapter // Use the adapter for OMDb results

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        // Note: The triggering of the search (e.g., button click) happens in the Activity
        // This fragment just displays the results.
    }

    private fun setupRecyclerView() {
        // Initialize the adapter for search results (MovieAdapter)
        movieAdapter = MovieAdapter { movieSearchResult ->
            // Handle click on a search result item -> navigate to MovieDetailActivity
            val intent = Intent(requireActivity(), MovieDetailActivity::class.java)
            intent.putExtra("imdbID", movieSearchResult.imdbID)
            startActivity(intent)
        }

        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }
    }

    private fun observeViewModel() {
        // Observe the OMDb search results LiveData
        viewModel.searchResults.observe(viewLifecycleOwner) { movies ->
            // Submit the list (could be null) to the adapter
            movieAdapter.submitList(movies)
            // Optional: Handle empty state display here
        }

        // Optional: Observe loading/error states specific to search if you add them to ViewModel
        /*
        viewModel.isLoadingSearch.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide loading indicator
        }
        viewModel.searchError.observe(viewLifecycleOwner) { error ->
           // Show error message
        }
        */
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}