package com.example.comp3025_assignment_03.view.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.comp3025_assignment_03.R
import com.example.comp3025_assignment_03.databinding.ItemFavoriteMovieBinding // Still using this binding name
import com.example.comp3025_assignment_03.model.FavoriteMovie

// Simplified listener: just pass the clicked movie
typealias OnFavoriteItemClickListener = (FavoriteMovie) -> Unit

class FavoriteMovieAdapter(private val onItemClicked: OnFavoriteItemClickListener) : // Changed constructor
    ListAdapter<FavoriteMovie, FavoriteMovieAdapter.FavoriteViewHolder>(DiffCallback()) {

    inner class FavoriteViewHolder(private val binding: ItemFavoriteMovieBinding) :
        RecyclerView.ViewHolder(binding.root) { // itemView is the CardView

        // Store the current movie bound to this holder
        private var currentMovie: FavoriteMovie? = null

        init {
            // Set the click listener on the root itemView (the CardView)
            itemView.setOnClickListener {
                currentMovie?.let { movie ->
                    onItemClicked(movie) // Invoke the lambda passed to the adapter
                }
            }
        }

        fun bind(movie: FavoriteMovie) {
            currentMovie = movie // Keep track of the movie for the click listener

            binding.tvFavoriteTitle.text = movie.title ?: "No Title"
            binding.tvFavoriteStudio.text = "Studio: ${movie.studio ?: "N/A"}"
            binding.tvFavoriteRating.text = "Rating: ${movie.criticsRating ?: "N/A"}"

            // Load image using Glide
            Glide.with(binding.ivFavoritePoster.context)
                .load(movie.posterUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.ivFavoritePoster)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteMovieBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // DiffUtil remains the same
    class DiffCallback : DiffUtil.ItemCallback<FavoriteMovie>() {
        override fun areItemsTheSame(oldItem: FavoriteMovie, newItem: FavoriteMovie): Boolean {
            return oldItem.documentId == newItem.documentId
        }
        override fun areContentsTheSame(oldItem: FavoriteMovie, newItem: FavoriteMovie): Boolean {
            return oldItem == newItem
        }
    }
}