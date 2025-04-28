package com.example.movies.ui.movies

import com.example.movies.domain.core.Error
import com.example.movies.domain.movie.model.Movie

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
data class MoviesUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: Error? = null,
    val hasMorePages: Boolean = true,
    val searchQuery: String = "",
    val currentPage: Int = 1
)