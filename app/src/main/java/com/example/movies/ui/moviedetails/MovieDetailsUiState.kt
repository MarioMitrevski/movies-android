package com.example.movies.ui.moviedetails

import com.example.movies.domain.core.Error
import com.example.movies.domain.movie.model.Movie

/**
 * Created by MarioMitrevski on 4/28/2025.
 * @author   MarioMitrevski
 * @since    4/28/2025.
 */
data class MovieDetailsUiState(
    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val error: Error? = null
)