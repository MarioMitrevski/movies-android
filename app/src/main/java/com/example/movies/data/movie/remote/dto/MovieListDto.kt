package com.example.movies.data.movie.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
data class MovieListDto(
    val results: List<MovieDto>,
    val page: Int,
    @SerializedName("total_pages") val totalPages: Int
)
