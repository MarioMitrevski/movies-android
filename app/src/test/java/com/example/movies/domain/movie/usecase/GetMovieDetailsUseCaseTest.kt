package com.example.movies.domain.movie.usecase

import com.example.movies.BuildConfig
import com.example.movies.domain.core.Error
import com.example.movies.domain.core.Result
import com.example.movies.domain.movie.MovieRepository
import com.example.movies.domain.movie.model.Movie
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetMovieDetailsUseCaseTest {
    private lateinit var movieRepository: MovieRepository
    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        movieRepository = mockk()
        getMovieDetailsUseCase = GetMovieDetailsUseCase(movieRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns success with movie details and full poster path`() = runTest {
        // Given
        val movieId = 1
        val movieDetails = Movie(
            id = movieId,
            title = "Test Movie",
            description = "Test Description",
            posterPath = "/test.jpg",
            voteAverage = 8.5f,
            voteCount = 100
        )
        coEvery { movieRepository.getMovieDetails(movieId) } returns Result.Success(movieDetails)

        // When
        val result = getMovieDetailsUseCase(movieId)

        // Then
        coVerify { movieRepository.getMovieDetails(movieId) }
        assertTrue(result is Result.Success)
        val resultMovieDetails = (result as Result.Success).data
        assertEquals(BuildConfig.IMAGES_BASE_URL + "/test.jpg", resultMovieDetails.posterPath)
        assertEquals(movieDetails.title, resultMovieDetails.title)
        assertEquals(movieDetails.description, resultMovieDetails.description)
        assertEquals(movieDetails.voteAverage, resultMovieDetails.voteAverage)
        assertEquals(movieDetails.voteCount, resultMovieDetails.voteCount)
    }

    @Test
    fun `invoke returns server error when server fails`() = runTest {
        // Given
        val movieId = 1
        val error = Error.Remote.SERVER
        coEvery { movieRepository.getMovieDetails(movieId) } returns Result.Error(error)

        // When
        val result = getMovieDetailsUseCase(movieId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).error)
    }

    @Test
    fun `invoke returns network error when no internet`() = runTest {
        // Given
        val movieId = 1
        val error = Error.Remote.NO_INTERNET
        coEvery { movieRepository.getMovieDetails(movieId) } returns Result.Error(error)

        // When
        val result = getMovieDetailsUseCase(movieId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).error)
    }
} 