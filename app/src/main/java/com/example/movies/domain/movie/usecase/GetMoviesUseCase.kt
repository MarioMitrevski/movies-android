package com.example.movies.domain.movie.usecase

import com.example.movies.BuildConfig
import com.example.movies.core.DefaultDispatcher
import com.example.movies.domain.core.Error
import com.example.movies.domain.core.PagingData
import com.example.movies.domain.core.Result
import com.example.movies.domain.core.map
import com.example.movies.domain.movie.MovieRepository
import com.example.movies.domain.movie.model.Movie
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
class GetMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(page: Int): Result<PagingData<Movie>, Error> {
        return withContext(dispatcher) {
            movieRepository.getMovies(page)
                .map { pagingData ->
                    pagingData.copy(data = pagingData.data.map { movie ->
                        val fullPosterPath = BuildConfig.IMAGES_BASE_URL + movie.posterPath
                        movie.copy(posterPath = fullPosterPath)
                    })
                }
        }
    }
}