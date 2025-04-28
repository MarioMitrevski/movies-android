package com.example.movies.data.movie.mappers

import com.example.movies.data.movie.remote.dto.MovieDto
import com.example.movies.data.movie.remote.dto.MovieListDto
import com.example.movies.domain.core.PagingData
import com.example.movies.domain.movie.model.Movie

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */

fun MovieDto.asMovie() = Movie(
    id = id,
    title = title,
    description = description,
    posterPath = posterPath,
    voteAverage = voteAverage,
    voteCount = voteCount,
)

fun MovieListDto.asPagingData() = PagingData(
    data = results.map(MovieDto::asMovie),
    totalPages = totalPages
)
