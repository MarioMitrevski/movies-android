package com.example.movies.domain.movie.usecase

import com.example.movies.BuildConfig
import com.example.movies.domain.core.Error
import com.example.movies.domain.core.PagingData
import com.example.movies.domain.core.Result
import com.example.movies.domain.movie.MovieRepository
import com.example.movies.domain.movie.model.Movie
import io.mockk.coEvery
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
class SearchMoviesUseCaseTest {
    private lateinit var movieRepository: MovieRepository
    private lateinit var searchMoviesUseCase: SearchMoviesUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        movieRepository = mockk()
        searchMoviesUseCase = SearchMoviesUseCase(movieRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns success with paging data and full poster path`() = runTest {
        // Given
        val page = 1
        val query = "test"
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            description = "Test Description",
            posterPath = "/test.jpg",
            voteAverage = 8.5f,
            voteCount = 100
        )
        val pagingData = PagingData(
            data = listOf(movie),
            totalPages = 10
        )
        coEvery { movieRepository.searchMovies(page, query) } returns Result.Success(pagingData)

        // When
        val result = searchMoviesUseCase(page, query)

        // Then
        assertTrue(result is Result.Success)
        val resultPagingData = (result as Result.Success).data
        val firstPage = resultPagingData.data
        assertEquals(1, firstPage.size)
        assertEquals(BuildConfig.IMAGES_BASE_URL + "/test.jpg", firstPage[0].posterPath)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        // Given
        val page = 1
        val query = "test"
        val error = Error.Remote.NO_INTERNET
        coEvery { movieRepository.searchMovies(page, query) } returns Result.Error(error)

        // When
        val result = searchMoviesUseCase(page, query)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).error)
    }
} 