package com.example.movies.ui

import kotlinx.serialization.Serializable

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
sealed interface Route {
    @Serializable
    data object MoviesGraph: Route

    @Serializable
    data object Movies: Route

    @Serializable
    data class MovieDetails(val id: Int): Route
}