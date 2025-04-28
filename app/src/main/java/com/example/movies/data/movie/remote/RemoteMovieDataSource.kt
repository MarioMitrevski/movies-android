package com.example.movies.data.movie.remote

import com.example.movies.data.movie.remote.dto.MovieDto
import com.example.movies.domain.core.Error
import com.example.movies.domain.core.Result
import com.example.movies.data.movie.remote.dto.MovieListDto

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
interface RemoteMovieDataSource {
    suspend fun getMovies(page: Int): Result<MovieListDto, Error>
    suspend fun searchMovies(page: Int, query: String): Result<MovieListDto, Error>
    suspend fun getMovieDetails(id: Int): Result<MovieDto, Error>
}