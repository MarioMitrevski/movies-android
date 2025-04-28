package com.example.movies.ui.moviedetails

import androidx.lifecycle.SavedStateHandle
import com.example.movies.domain.core.Error
import com.example.movies.domain.core.Result
import com.example.movies.domain.movie.model.Movie
import com.example.movies.domain.movie.usecase.GetMovieDetailsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailsViewModelTest {
    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: MovieDetailsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testMovieId = 1

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getMovieDetailsUseCase = mockk()
        savedStateHandle = SavedStateHandle().apply {
            set("id", testMovieId)
        }
        setupInitialMovieDetailsSuccess()
        viewModel = MovieDetailsViewModel(getMovieDetailsUseCase, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads movie details`() = runTest {
        // Then
        val state = viewModel.state.first()
        assertEquals("Test Movie", state.movie?.title)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `retry loads movie details again`() = runTest {
        // Given
        val newMovie = Movie(
            id = 1,
            title = "Updated Movie",
            description = "Updated Description",
            posterPath = "/updated.jpg",
            voteAverage = 9.0f,
            voteCount = 200
        )
        coEvery { getMovieDetailsUseCase(testMovieId) } returns Result.Success(newMovie)

        // When
        viewModel.onIntent(MovieDetailsUiIntent.OnRetry)

        // Then
        val state = viewModel.state.first()
        assertEquals("Updated Movie", state.movie?.title)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `error state is handled correctly`() = runTest {
        // Given
        val error = Error.Remote.NO_INTERNET
        coEvery { getMovieDetailsUseCase(testMovieId) } returns Result.Error(error)

        // When
        viewModel.onIntent(MovieDetailsUiIntent.OnRetry)

        // Then
        val state = viewModel.state.first()
        assertEquals(error, state.error)
        assertFalse(state.isLoading)
    }

    private fun setupInitialMovieDetailsSuccess() {
        val movie = Movie(
            id = testMovieId,
            title = "Test Movie",
            description = "Test Description",
            posterPath = "/test.jpg",
            voteAverage = 8.5f,
            voteCount = 100
        )
        coEvery { getMovieDetailsUseCase(testMovieId) } returns Result.Success(movie)
    }
} 