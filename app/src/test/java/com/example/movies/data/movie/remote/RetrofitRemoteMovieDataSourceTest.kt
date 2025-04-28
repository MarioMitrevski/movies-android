package com.example.movies.data.movie.remote

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
import retrofit2.Response
import java.io.IOException

class RetrofitRemoteMovieDataSourceTest {
    private lateinit var movieApi: MovieApi
    private lateinit var remoteMovieDataSource: RetrofitRemoteMovieDataSource

    @Before
    fun setup() {
        movieApi = mockk()
        remoteMovieDataSource = RetrofitRemoteMovieDataSource(movieApi)
    }

    @Test
    fun `getMovies returns success with movie list`() = runTest {
        // Given
        val page = 1
        val movieListDto = MovieListDto(
            page = 1,
            results = listOf(
                MovieDto(
                    id = 1,
                    title = "Test Movie",
                    description = "Test Overview",
                    posterPath = "/test.jpg",
                    voteCount = 10,
                    voteAverage = 8.5f
                )
            ),
            totalPages = 10,
        )
        coEvery { movieApi.getMovies(page) } returns Response.success(movieListDto)

        // When
        val result = remoteMovieDataSource.getMovies(page)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(movieListDto, (result as Result.Success).data)
    }

    @Test
    fun `searchMovies returns success with movie list`() = runTest {
        // Given
        val page = 1
        val query = "test"
        val movieListDto = MovieListDto(
            page = 1,
            results = listOf(
                MovieDto(
                    id = 1,
                    title = "Test Movie",
                    description = "Test Overview",
                    posterPath = "/test.jpg",
                    voteCount = 10,
                    voteAverage = 8.5f
                )
            ),
            totalPages = 10,
        )
        coEvery { movieApi.searchMovies(page, query) } returns Response.success(movieListDto)

        // When
        val result = remoteMovieDataSource.searchMovies(page, query)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(movieListDto, (result as Result.Success).data)
    }

    @Test
    fun `getMovieDetails returns success with movie details`() = runTest {
        // Given
        val movieId = 1
        val movieDto = MovieDto(
            id = 1,
            title = "Test Movie",
            description = "Test Overview",
            posterPath = "/test.jpg",
            voteCount = 10,
            voteAverage = 8.5f
        )
        coEvery { movieApi.getMovieDetails(movieId) } returns Response.success(movieDto)

        // When
        val result = remoteMovieDataSource.getMovieDetails(movieId)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(movieDto, (result as Result.Success).data)
    }

    @Test
    fun `getMovies returns network error when IOException occurs`() = runTest {
        // Given
        val page = 1
        coEvery { movieApi.getMovies(page) } throws IOException("Network error")

        // When
        val result = remoteMovieDataSource.getMovies(page)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).error is Error.Remote)
    }

    @Test
    fun `getMovies returns unknown error when unexpected exception occurs`() = runTest {
        // Given
        val page = 1
        coEvery { movieApi.getMovies(page) } throws RuntimeException("Unexpected error")

        // When
        val result = remoteMovieDataSource.getMovies(page)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).error is Error.Remote)
    }
} 