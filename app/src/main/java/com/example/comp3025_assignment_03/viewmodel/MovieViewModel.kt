package com.example.comp3025_assignment_03.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comp3025_assignment_03.model.MovieDetail
import com.example.comp3025_assignment_03.model.MovieSearchResult
import com.example.comp3025_assignment_03.repository.MovieRepository
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val repository = MovieRepository()

    private val _searchResults = MutableLiveData<List<MovieSearchResult>?>()
    val searchResults: MutableLiveData<List<MovieSearchResult>?> get() = _searchResults

    private val _movieDetail = MutableLiveData<MovieDetail>()
    val movieDetail: LiveData<MovieDetail> get() = _movieDetail

    // search for movies by query string
    fun searchMovies(query: String) {
        viewModelScope.launch {
            repository.searchMovies(query)?.let { response ->
                _searchResults.value = response.search
            }
        }
    }

    // fetch movie details by imdbID
    fun fetchMovieDetails(imdbID: String) {
        viewModelScope.launch {
            repository.getMovieDetails(imdbID)?.let {
                _movieDetail.value = it
            }
        }
    }
}
