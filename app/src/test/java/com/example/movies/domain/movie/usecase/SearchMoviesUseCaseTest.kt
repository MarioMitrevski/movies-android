package com.example.movies.domain.movie.usecase

import com.example.movies.BuildConfig
import com.example.movies.domain.core.Error
import com.example.movies.domain.core.PagingData
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
        val query = "test"
        val page = 1
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
        coVerify(exactly = 1) { movieRepository.searchMovies(page, query) }
        assertTrue(result is Result.Success)
        val resultPagingData = (result as Result.Success).data
        val firstPage = resultPagingData.data
        assertEquals(1, firstPage.size)
        assertEquals(BuildConfig.IMAGES_BASE_URL + "/test.jpg", firstPage[0].posterPath)
    }

    @Test
    fun `invoke returns empty list when no search results`() = runTest {
        // Given
        val query = "nonexistent"
        val page = 1
        val pagingData = PagingData(
            data = emptyList<Movie>(),
            totalPages = 0
        )
        coEvery { movieRepository.searchMovies(page, query) } returns Result.Success(pagingData)

        // When
        val result = searchMoviesUseCase(page, query)

        // Then
        assertTrue(result is Result.Success)
        val resultPagingData = (result as Result.Success).data
        assertTrue(resultPagingData.data.isEmpty())
        assertEquals(0, resultPagingData.totalPages)
    }

    @Test
    fun `invoke handles different page numbers correctly`() = runTest {
        // Given
        val query = "test"
        val page = 2
        val movie = Movie(
            id = 2,
            title = "Page 2 Movie",
            description = "Description",
            posterPath = "/page2.jpg",
            voteAverage = 7.5f,
            voteCount = 50
        )
        val pagingData = PagingData(
            data = listOf(movie),
            totalPages = 3
        )
        coEvery { movieRepository.searchMovies(page, query) } returns Result.Success(pagingData)

        // When
        val result = searchMoviesUseCase(page, query)

        // Then
        assertTrue(result is Result.Success)
        val resultPagingData = (result as Result.Success).data
        assertEquals(1, resultPagingData.data.size)
        assertEquals("Page 2 Movie", resultPagingData.data[0].title)
    }

    @Test
    fun `invoke handles special characters in query`() = runTest {
        // Given
        val query = "test & movie"
        val page = 1
        val movie = Movie(
            id = 1,
            title = "Test & Movie",
            description = "Description",
            posterPath = "/test.jpg",
            voteAverage = 8.5f,
            voteCount = 100
        )
        val pagingData = PagingData(
            data = listOf(movie),
            totalPages = 1
        )
        coEvery { movieRepository.searchMovies(page, query) } returns Result.Success(pagingData)

        // When
        val result = searchMoviesUseCase(page, query)

        // Then
        assertTrue(result is Result.Success)
        val resultPagingData = (result as Result.Success).data
        assertEquals(1, resultPagingData.data.size)
        assertEquals("Test & Movie", resultPagingData.data[0].title)
    }

    @Test
    fun `invoke handles empty query string`() = runTest {
        // Given
        val query = ""
        val page = 1
        val pagingData = PagingData(
            data = emptyList<Movie>(),
            totalPages = 0
        )
        coEvery { movieRepository.searchMovies(page, query) } returns Result.Success(pagingData)

        // When
        val result = searchMoviesUseCase(page, query)

        // Then
        assertTrue(result is Result.Success)
        val resultPagingData = (result as Result.Success).data
        assertTrue(resultPagingData.data.isEmpty())
    }

    @Test
    fun `invoke returns server error when server fails`() = runTest {
        // Given
        val query = "test"
        val page = 1
        val error = Error.Remote.SERVER
        coEvery { movieRepository.searchMovies(page, query) } returns Result.Error(error)

        // When
        val result = searchMoviesUseCase(page, query)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).error)
    }

    @Test
    fun `invoke returns network error when no internet`() = runTest {
        // Given
        val query = "test"
        val page = 1
        val error = Error.Remote.NO_INTERNET
        coEvery { movieRepository.searchMovies(page, query) } returns Result.Error(error)

        // When
        val result = searchMoviesUseCase(page, query)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).error)
    }
} 