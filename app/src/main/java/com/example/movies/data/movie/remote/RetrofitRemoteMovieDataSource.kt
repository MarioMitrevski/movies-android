package com.example.movies.data.movie.remote

import com.example.movies.domain.core.Error
import com.example.movies.domain.core.Result
import com.example.movies.data.core.apiCall
import com.example.movies.data.movie.remote.dto.MovieDto
import com.example.movies.data.movie.remote.dto.MovieListDto
import javax.inject.Inject

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */

class RetrofitRemoteMovieDataSource @Inject constructor(
    private val movieApi: MovieApi
) : RemoteMovieDataSource {
    override suspend fun getMovies(page: Int): Result<MovieListDto, Error> {
        return apiCall {
            movieApi.getMovies(page)
        }
    }

    override suspend fun searchMovies(page: Int, query: String): Result<MovieListDto, Error> {
        return apiCall {
            movieApi.searchMovies(page, query)
        }
    }

    override suspend fun getMovieDetails(id: Int): Result<MovieDto, Error> {
        return apiCall {
            movieApi.getMovieDetails(id)
        }
    }
}