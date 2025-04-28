package com.example.movies.domain.movie.model

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
data class Movie(
    val id: Int,
    val title: String,
    val description: String,
    val posterPath: String?,
    val voteAverage: Float,
    val voteCount: Int
)
