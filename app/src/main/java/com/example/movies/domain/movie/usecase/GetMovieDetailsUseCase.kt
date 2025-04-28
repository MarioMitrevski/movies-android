package com.example.movies.domain.movie.usecase

import com.example.movies.BuildConfig
import com.example.movies.core.DefaultDispatcher
import com.example.movies.domain.core.Error
import com.example.movies.domain.core.Result
import com.example.movies.domain.core.map
import com.example.movies.domain.movie.MovieRepository
import com.example.movies.domain.movie.model.Movie
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by MarioMitrevski on 4/28/2025.
 * @author   MarioMitrevski
 * @since    4/28/2025.
 */

class GetMovieDetailsUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher

) {
    suspend operator fun invoke(movieId: Int): Result<Movie, Error> {
        return withContext(dispatcher) {
            movieRepository.getMovieDetails(movieId).map { movie ->
                movie.copy(posterPath = BuildConfig.IMAGES_BASE_URL + movie.posterPath)
            }
        }
    }
}