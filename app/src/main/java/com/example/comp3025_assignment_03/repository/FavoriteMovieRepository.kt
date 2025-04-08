package com.example.comp3025_assignment_03.repository

import android.util.Log
import com.example.comp3025_assignment_03.model.FavoriteMovie // Import the FavoriteMovie model
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject // For converting documents
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await // For using await() with Firestore tasks

class FavoriteMovieRepository {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    // Helper function to get the current user's ID or return null
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Adds a new favorite movie document to Firestore under the current user.
     * Returns true on success, false on failure (e.g., no user logged in).
     */
    suspend fun addFavoriteMovie(movie: FavoriteMovie): Boolean {
        val userId = getCurrentUserId() ?: run {
            Log.w("FavoriteMovieRepo", "Cannot add favorite: No user logged in.")
            return false // Ensure user is logged in
        }
        // Ensure the movie object has the userId set before saving
        movie.userId = userId

        return try {
            // Add the movie to the user's 'favorites' sub-collection
            // Path: /users/{userId}/favorites/{newMovieId}
            db.collection("users").document(userId).collection("favorites")
                .add(movie) // Firestore generates the document ID
                .await() // Wait for the operation to complete
            Log.d("FavoriteMovieRepo", "Favorite movie added successfully.")
            true // Indicate success
        } catch (e: Exception) {
            Log.e("FavoriteMovieRepo", "Error adding favorite movie", e)
            false // Indicate failure
        }
    }

    /**
     * Fetches the current user's favorite movies from Firestore *once*.
     * Returns a list of FavoriteMovie objects or null on error/no user.
     */
    suspend fun getFavoriteMoviesOnce(): List<FavoriteMovie>? {
        val userId = getCurrentUserId() ?: run {
            Log.w("FavoriteMovieRepo", "Cannot get favorites: No user logged in.")
            return null // Need user logged in
        }

        return try {
            val snapshot = db.collection("users").document(userId).collection("favorites")
                .get() // Get documents once
                .await() // Wait for the task

            // Convert each document snapshot to a FavoriteMovie object
            val movies = snapshot.documents.mapNotNull { document ->
                try {
                    // Use .toObject<FavoriteMovie>() which leverages the @DocumentId annotation
                    document.toObject<FavoriteMovie>()
                } catch(e: Exception) {
                    Log.e("FavoriteMovieRepo", "Error converting document ${document.id}", e)
                    null // Skip documents that fail to convert
                }
            }
            Log.d("FavoriteMovieRepo", "Fetched ${movies.size} favorite movies.")
            movies
        } catch (e: Exception) {
            Log.e("FavoriteMovieRepo", "Error fetching favorite movies", e)
            null // Return null on error
        }
    }

    /**
     * Deletes a specific favorite movie document from Firestore.
     * Requires the unique document ID of the movie to delete.
     * Returns true on success, false on failure (e.g., no user, error).
     */
    suspend fun deleteFavoriteMovie(documentId: String): Boolean {
        val userId = getCurrentUserId() ?: run {
            Log.w("FavoriteMovieRepo", "Cannot delete favorite: No user logged in.")
            return false
        }

        if (documentId.isBlank()) {
            Log.w("FavoriteMovieRepo", "Cannot delete favorite: documentId is blank.")
            return false
        }

        return try {
            db.collection("users").document(userId)
                .collection("favorites").document(documentId)
                .delete()
                .await()
            Log.d("FavoriteMovieRepo", "Favorite movie deleted successfully: $documentId")
            true
        } catch (e: Exception) {
            Log.e("FavoriteMovieRepo", "Error deleting favorite movie: $documentId", e)
            false
        }
    }

    /**
     * Fetches a single favorite movie by its Firestore document ID.
     * Returns the FavoriteMovie object or null if not found or error.
     */
    suspend fun getFavoriteMovieById(documentId: String): FavoriteMovie? {
        val userId = getCurrentUserId() ?: run {
            Log.w("FavoriteMovieRepo", "Cannot get favorite by ID: No user logged in.")
            return null
        }

        if (documentId.isBlank()) {
            Log.w("FavoriteMovieRepo", "Cannot get favorite by ID: documentId is blank.")
            return null
        }

        return try {
            val docSnapshot = db.collection("users").document(userId)
                .collection("favorites").document(documentId)
                .get()
                .await()

            docSnapshot.toObject<FavoriteMovie>() // Returns null if document doesn't exist
        } catch (e: Exception) {
            Log.e("FavoriteMovieRepo", "Error fetching favorite by ID: $documentId", e)
            null
        }
    }

    /**
     * Updates an existing favorite movie document in Firestore.
     * Requires the movie object containing the updated data AND its documentId.
     * Returns true on success, false on failure.
     */
    suspend fun updateFavoriteMovie(movie: FavoriteMovie): Boolean {
        val userId = getCurrentUserId() ?: run {
            Log.w("FavoriteMovieRepo", "Cannot update favorite: No user logged in.")
            return false
        }
        val docId = movie.documentId ?: run {
            Log.w("FavoriteMovieRepo", "Cannot update favorite: documentId is missing.")
            return false
        }
        // Ensure the userId field matches the current user, though the path dictates ownership
        movie.userId = userId

        return try {
            db.collection("users").document(userId)
                .collection("favorites").document(docId)
                .set(movie) // Use set to overwrite the document with new data
                .await()
            Log.d("FavoriteMovieRepo", "Favorite movie updated successfully: $docId")
            true
        } catch (e: Exception) {
            Log.e("FavoriteMovieRepo", "Error updating favorite movie: $docId", e)
            false
        }
    }


}