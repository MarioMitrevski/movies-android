package com.example.movies.ui.moviedetails

/**
 * Created by MarioMitrevski on 4/28/2025.
 * @author   MarioMitrevski
 * @since    4/28/2025.
 */
sealed interface MovieDetailsUiIntent {
    data object OnRetry : MovieDetailsUiIntent
    data object OnBackClick : MovieDetailsUiIntent
}