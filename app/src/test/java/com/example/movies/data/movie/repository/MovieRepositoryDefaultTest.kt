package com.example.movies.data.movie.repository

import com.example.movies.data.movie.remote.RemoteMovieDataSource
import com.example.movies.data.movie.remote.dto.MovieDto
import com.example.movies.data.movie.remote.dto.MovieListDto
import com.example.movies.domain.core.Error
import com.example.movies.domain.core.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MovieRepositoryDefaultTest {
    private lateinit var remoteMovieDataSource: RemoteMovieDataSource
    private lateinit var movieRepository: MovieRepositoryDefault

    @Before
    fun setup() {
        remoteMovieDataSource = mockk()
        movieRepository = MovieRepositoryDefault(remoteMovieDataSource)
    }

    @Test
    fun `getMovies returns success with paging data`() = runTest {
        // Given
        val page = 1
        val movieListDto = MovieListDto(
            page = 1,
            results = listOf(
                MovieDto(
                    id = 1,
                    title = "Test Movie",
                    description = "Test Description",
                    posterPath = "/test.jpg",
                    voteAverage = 8.5f,
                    voteCount = 100
                )
            ),
            totalPages = 10,
        )
        coEvery { remoteMovieDataSource.getMovies(page) } returns Result.Success(movieListDto)

        // When
        val result = movieRepository.getMovies(page)

        // Then
        assertTrue(result is Result.Success)
        val pagingData = (result as Result.Success).data
        val firstPage = pagingData.data
        assertEquals(1, firstPage.size)
        assertEquals("Test Movie", firstPage[0].title)
    }

    @Test
    fun `searchMovies returns success with paging data`() = runTest {
        // Given
        val page = 1
        val query = "test"
        val movieListDto = MovieListDto(
            page = 1,
            results = listOf(
                MovieDto(
                    id = 1,
                    title = "Test Movie",
                    description = "Test Description",
                    posterPath = "/test.jpg",
                    voteAverage = 8.5f,
                    voteCount = 100
                )
            ),
            totalPages = 10,
        )
        coEvery { remoteMovieDataSource.searchMovies(page, query) } returns Result.Success(movieListDto)

        // When
        val result = movieRepository.searchMovies(page, query)

        // Then
        assertTrue(result is Result.Success)
        val pagingData = (result as Result.Success).data
        val firstPage = pagingData.data
        assertEquals(1, firstPage.size)
        assertEquals("Test Movie", firstPage[0].title)
    }

    @Test
    fun `getMovieDetails returns success with movie details`() = runTest {
        // Given
        val movieId = 1
        val movieDto = MovieDto(
            id = 1,
            title = "Test Movie",
            description = "Test Description",
            posterPath = "/test.jpg",
            voteAverage = 8.5f,
            voteCount = 100
        )
        coEvery { remoteMovieDataSource.getMovieDetails(movieId) } returns Result.Success(movieDto)

        // When
        val result = movieRepository.getMovieDetails(movieId)

        // Then
        assertTrue(result is Result.Success)
        val movie = (result as Result.Success).data
        assertEquals("Test Movie", movie.title)
        assertEquals("Test Description", movie.description)
        assertEquals(8.5f, movie.voteAverage)
    }

    @Test
    fun `getMovies returns error when remote data source fails`() = runTest {
        // Given
        val page = 1
        val error = Error.Remote.SERVER
        coEvery { remoteMovieDataSource.getMovies(page) } returns Result.Error(error)

        // When
        val result = movieRepository.getMovies(page)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).error)
    }

    @Test
    fun `searchMovies returns error when remote data source fails`() = runTest {
        val page = 1
        val query = "test"
        val error = Error.Remote.SERVER
        coEvery { remoteMovieDataSource.searchMovies(page, query) } returns Result.Error(error)

        val result = movieRepository.searchMovies(page, query)

        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).error)
    }

    @Test
    fun `getMovieDetails returns error when remote data source fails`() = runTest {
        // Given
        val movieId = 1
        val error = Error.Remote.SERVER
        coEvery { remoteMovieDataSource.getMovieDetails(movieId) } returns Result.Error(error)

        // When
        val result = movieRepository.getMovieDetails(movieId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).error)
    }
} 