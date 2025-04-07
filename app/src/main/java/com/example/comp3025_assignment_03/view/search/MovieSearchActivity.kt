package com.example.comp3025_assignment_03.view.search

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.comp3025_assignment_03.databinding.ActivityMovieSearchBinding
import com.example.comp3025_assignment_03.view.details.MovieDetailActivity
import com.example.comp3025_assignment_03.viewmodel.MovieViewModel

class MovieSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieSearchBinding
    private lateinit var movieViewModel: MovieViewModel
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set dark theme default
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        // setup RecyclerView adapter with item click listener
        movieAdapter = MovieAdapter { movie ->
            // navigate to details screen when an item is clicked
            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtra("imdbID", movie.imdbID)
            startActivity(intent)
        }

        // setup RecyclerView
        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(this@MovieSearchActivity)
            adapter = movieAdapter
        }

        // set click listener for the search button
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                movieViewModel.searchMovies(query)
            }
            binding.etSearch.clearFocus()
        }

        // observe search results LiveData
        movieViewModel.searchResults.observe(this) { movies ->
            movieAdapter.submitList(movies)
        }
    }
}
