package com.example.comp3025_assignment_03.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comp3025_assignment_03.model.MovieDetail
import com.example.comp3025_assignment_03.model.MovieSearchResult
import com.example.comp3025_assignment_03.repository.MovieRepository
import kotlinx.coroutines.launch
import com.example.comp3025_assignment_03.model.FavoriteMovie
import com.example.comp3025_assignment_03.repository.FavoriteMovieRepository

class MovieViewModel : ViewModel() {

    private val repository = MovieRepository()
    // repository for Firestore favorites
    private val favoriteRepository = FavoriteMovieRepository()

    private val _searchResults = MutableLiveData<List<MovieSearchResult>?>()
    val searchResults: MutableLiveData<List<MovieSearchResult>?> get() = _searchResults

    private val _movieDetail = MutableLiveData<MovieDetail>()
    val movieDetail: LiveData<MovieDetail> get() = _movieDetail

    // --- LiveData for Favorite Movies ---
    private val _favoriteMovies = MutableLiveData<List<FavoriteMovie>>()
    val favoriteMovies: LiveData<List<FavoriteMovie>> get() = _favoriteMovies

    private val _isLoadingFavorites = MutableLiveData<Boolean>()
    val isLoadingFavorites: LiveData<Boolean> get() = _isLoadingFavorites

    val _errorMessageFavorites = MutableLiveData<String?>()
    val errorMessageFavorites: LiveData<String?> get() = _errorMessageFavorites

    // --- LiveData for Single Selected/Editing Favorite ---
    private val _selectedFavoriteMovie = MutableLiveData<FavoriteMovie?>()
    val selectedFavoriteMovie: LiveData<FavoriteMovie?> get() = _selectedFavoriteMovie

    // --- Add/Edit Specific Loading/Error ---
    private val _isSavingFavorite = MutableLiveData<Boolean>()
    val isSavingFavorite: LiveData<Boolean> get() = _isSavingFavorite

    val _saveFavoriteError = MutableLiveData<String?>()
    val saveFavoriteError: LiveData<String?> get() = _saveFavoriteError


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

    // --- Functions for Favorites (Firestore) ---
    /** Fetches a single favorite movie detail for editing */
    fun fetchFavoriteMovieById(documentId: String) {
        _isLoadingFavorites.value = true // Reuse loading state or use a specific one
        _selectedFavoriteMovie.value = null // Clear previous
        viewModelScope.launch {
            val result = favoriteRepository.getFavoriteMovieById(documentId)
            _selectedFavoriteMovie.value = result // Post result (could be null if not found)
            _isLoadingFavorites.value = false
            if (result == null) {
                _errorMessageFavorites.value = "Could not load movie details for editing."
            }
        }
    }

    /** Clears the selected movie LiveData, e.g., when leaving Add/Edit screen */
    fun clearSelectedFavorite() {
        _selectedFavoriteMovie.value = null
    }

    /** Updates an existing favorite movie */
    fun updateFavorite(movie: FavoriteMovie) {
        _isSavingFavorite.value = true
        _saveFavoriteError.value = null
        viewModelScope.launch {
            val success = favoriteRepository.updateFavoriteMovie(movie)
            if (!success) {
                _saveFavoriteError.value = "Failed to update favorite."
            } else {
                // Optionally trigger a refresh of the main list after update
                // fetchFavoriteMovies()
                // Or update the item in the existing LiveData list
                val currentList = _favoriteMovies.value?.toMutableList()
                val index = currentList?.indexOfFirst { it.documentId == movie.documentId }
                if (index != null && index != -1) {
                    currentList[index] = movie
                    _favoriteMovies.value = currentList!!
                }
            }
            _isSavingFavorite.value = false
        }
    }

    // Modify addFavorite to handle saving state/errors
    /** Adds a movie to favorites in Firestore */
    fun addFavorite(movie: FavoriteMovie) {
        _isSavingFavorite.value = true
        _saveFavoriteError.value = null
        viewModelScope.launch {
            val success = favoriteRepository.addFavoriteMovie(movie)
            if (!success) {
                _saveFavoriteError.value = "Failed to add favorite."
            }
            _isSavingFavorite.value = false
        }
    }
    fun fetchFavoriteMovies() {
        _isLoadingFavorites.value = true
        _errorMessageFavorites.value = null // Clear previous errors
        viewModelScope.launch {
            val result = favoriteRepository.getFavoriteMoviesOnce()
            if (result != null) {
                _favoriteMovies.value = result!! // Post the list
            } else {
                // Handle error case - e.g., post empty list or set error message
                _favoriteMovies.value = emptyList()
                _errorMessageFavorites.value = "Failed to load favorites."
            }
            _isLoadingFavorites.value = false
        }
    }
    fun deleteFavorite(documentId: String) {
        viewModelScope.launch {
            val success = favoriteRepository.deleteFavoriteMovie(documentId)
            if (success) {
                // Remove the movie from the current LiveData list for immediate UI update
                val currentList = _favoriteMovies.value?.toMutableList() ?: mutableListOf()
                currentList.removeAll { it.documentId == documentId }
                _favoriteMovies.value = currentList!! // <-- This posts the updated list
            } else {
                _errorMessageFavorites.value = "Failed to delete favorite."
            }
        }
    }

}
