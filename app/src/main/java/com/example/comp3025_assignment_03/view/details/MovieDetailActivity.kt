package com.example.comp3025_assignment_03.view.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.comp3025_assignment_03.databinding.ActivityMovieDetailBinding
import com.example.comp3025_assignment_03.viewmodel.MovieViewModel

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailBinding
    private lateinit var movieViewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        // retrieve the imdbID passed from the search screen
        val imdbID = intent.getStringExtra("imdbID")
        imdbID?.let {
            movieViewModel.fetchMovieDetails(it)
        }

        // observe the movieDetail LiveData to update the UI
        movieViewModel.movieDetail.observe(this) { movie ->
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
                    .into(binding.ivPoster)
            }
        }

        // back button to finish the activity and return to search screen
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
