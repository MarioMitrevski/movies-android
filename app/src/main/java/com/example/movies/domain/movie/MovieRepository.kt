package com.example.movies.domain.movie

import com.example.movies.domain.core.PagingData
import com.example.movies.domain.core.Error
import com.example.movies.domain.core.Result
import com.example.movies.domain.movie.model.Movie

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
interface MovieRepository {
    suspend fun getMovies(page: Int): Result<PagingData<Movie>, Error>
    suspend fun searchMovies(page: Int, query: String): Result<PagingData<Movie>, Error>
    suspend fun getMovieDetails(id: Int): Result<Movie, Error>
}