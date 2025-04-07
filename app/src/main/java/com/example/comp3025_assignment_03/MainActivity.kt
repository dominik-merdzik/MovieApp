package com.example.comp3025_assignment_03

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.comp3025_assignment_03.repository.MovieRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Search for movies with the title "The breakfast club" using the MovieRepository class and log the results (TESTING)
        CoroutineScope(Dispatchers.IO).launch {
            val repository = MovieRepository()
            val searchResponse = repository.searchMovies("The breakfast club")
            if (searchResponse?.search != null) {
                Log.d("MainActivity", "Found ${searchResponse.search.size} movies:")
                searchResponse.search.forEach { result ->
                    Log.d("MainActivity", "${result.title} (${result.year})")
                }
            } else {
                Log.d("MainActivity", "No movies found or an error occurred.")
            }
        }
    }
}
