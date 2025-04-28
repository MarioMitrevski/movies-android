package com.example.movies.ui.moviedetails

/**
 * Created by MarioMitrevski on 4/28/2025.
 * @author   MarioMitrevski
 * @since    4/28/2025.
 */
sealed interface MovieDetailsEffect {
    data object NavigateBack : MovieDetailsEffect
}