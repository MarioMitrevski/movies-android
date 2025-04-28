package com.example.movies.ui.movies

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
sealed interface MoviesEffect {
    data class NavigateToDetails(val movieId: Int) : MoviesEffect
    data object ScrollToTop : MoviesEffect
}