package com.example.movies.ui.movies

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */

sealed interface MoviesUiIntent {
    data object LoadNextPage : MoviesUiIntent
    data class OnSearchQueryChange(val query: String) : MoviesUiIntent
    data class OnMovieClick(val movieId: Int) : MoviesUiIntent
    data object OnRetry : MoviesUiIntent
}