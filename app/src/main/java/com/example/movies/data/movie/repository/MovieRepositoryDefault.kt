package com.example.movies.data.movie.repository

import com.example.movies.data.movie.mappers.asMovie
import com.example.movies.domain.core.Error
import com.example.movies.domain.core.Result
import com.example.movies.domain.core.map
import com.example.movies.data.movie.mappers.asPagingData
import com.example.movies.data.movie.remote.RemoteMovieDataSource
import com.example.movies.data.movie.remote.dto.MovieDto
import com.example.movies.data.movie.remote.dto.MovieListDto
import com.example.movies.domain.core.PagingData
import com.example.movies.domain.movie.MovieRepository
import com.example.movies.domain.movie.model.Movie
import javax.inject.Inject

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
class MovieRepositoryDefault @Inject constructor(
    private val remoteMovieDataSource: RemoteMovieDataSource
) : MovieRepository {
    override suspend fun getMovies(page: Int): Result<PagingData<Movie>, Error> {
        return remoteMovieDataSource.getMovies(page)
            .map(MovieListDto::asPagingData)
    }

    override suspend fun searchMovies(page: Int, query: String): Result<PagingData<Movie>, Error> {
        return remoteMovieDataSource.searchMovies(page, query)
            .map(MovieListDto::asPagingData)
    }

    override suspend fun getMovieDetails(id: Int): Result<Movie, Error> {
        return remoteMovieDataSource.getMovieDetails(id)
            .map(MovieDto::asMovie)
    }
} 