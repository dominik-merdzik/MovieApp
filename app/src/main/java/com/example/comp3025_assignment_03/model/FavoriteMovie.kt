package com.example.comp3025_assignment_03.model

import com.google.firebase.firestore.DocumentId // Import this for mapping the document ID

// Data class for storing favorite movie details in Firestore
data class FavoriteMovie(
    // Use @DocumentId to automatically map the Firestore document ID to this field
    @DocumentId var documentId: String? = null,

    var userId: String? = null, // Store the Firebase Auth UID of the user who favorited it
    var title: String? = null,
    var year: String? = null,     // Keep year if needed
    var imdbID: String? = null,   // Keep IMDb ID if you want to link back or prevent duplicates
    var posterUrl: String? = null, // Poster thumbnail URL

    // --- Fields required by Assignment 3 ---
    var studio: String? = null,       // You'll need to decide where this comes from (OMDb doesn't provide it easily)
    // Maybe add manually? Or find another API? For now, include the field.
    var criticsRating: String? = null, // Similar issue as studio. OMDb has ratings, but maybe not "Critics Rating" specifically.
    // Could be IMDb rating, Rotten Tomatoes, etc. Needs clarification or manual input.

    // --- Optional extra fields you might want ---
    var plot: String? = null
    // var genre: String? = null,
    // var director: String? = null,
    // var actors: String? = null,

    // Add a no-argument constructor for Firestore deserialization
    // Firestore needs this to recreate the object from database data
) {
    constructor() : this(null, null, null, null, null, null, null, null, null)
}