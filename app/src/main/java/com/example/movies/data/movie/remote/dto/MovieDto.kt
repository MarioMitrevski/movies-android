package com.example.movies.data.movie.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
data class MovieDto(
    val id: Int,
    val title: String,
    @SerializedName("overview") val description: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int
)