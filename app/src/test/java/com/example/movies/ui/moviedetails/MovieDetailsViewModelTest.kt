package com.example.movies.ui.moviedetails

import app.cash.turbine.test
import androidx.lifecycle.SavedStateHandle
import com.example.movies.domain.core.Error
import com.example.movies.domain.core.Result
import com.example.movies.domain.movie.model.Movie
import com.example.movies.domain.movie.usecase.GetMovieDetailsUseCase
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailsViewModelTest {
    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: MovieDetailsViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testMovieId = 1

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getMovieDetailsUseCase = mockk()
        savedStateHandle = SavedStateHandle().apply {
            set("id", testMovieId)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads movie details`() = runTest {
        // Given
        mockGetMovieDetailsUseCaseSuccess()

        viewModel = MovieDetailsViewModel(getMovieDetailsUseCase, savedStateHandle)

        viewModel.state.test {
            // Initial state
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            assertNull(initialState.movie)
            assertNull(initialState.error)

            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.movie)
            assertNull(loadingState.error)

            // Loaded state
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals("Test Movie", loadedState.movie?.title)
            assertEquals("Test Description", loadedState.movie?.description)
            assertEquals("/test.jpg", loadedState.movie?.posterPath)
            assertEquals(8.5f, loadedState.movie?.voteAverage)
            assertEquals(100, loadedState.movie?.voteCount)
            assertNull(loadedState.error)
        }
    }

    @Test
    fun `error state is handled correctly`() = runTest {
        // Given
        val error = Error.Remote.NO_INTERNET
        mockGetMovieDetailsUseCaseError(error)

        viewModel = MovieDetailsViewModel(getMovieDetailsUseCase, savedStateHandle)

        viewModel.state.test {
            // Initial state
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            assertNull(initialState.movie)
            assertNull(initialState.error)

            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.movie)
            assertNull(loadingState.error)

            // Error state
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertNull(errorState.movie)
            assertEquals(error, errorState.error)
        }
    }

    @Test
    fun `retry loads movie details again`() = runTest {
        // Given
        val error = Error.Remote.NO_INTERNET
        mockGetMovieDetailsUseCaseError(error)

        viewModel = MovieDetailsViewModel(getMovieDetailsUseCase, savedStateHandle)

        viewModel.state.test {
            // Skip initial states (initial, loading, error)
            skipItems(3)

            // When
            mockGetMovieDetailsUseCaseSuccess()
            viewModel.onIntent(MovieDetailsUiIntent.OnRetry)

            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.movie)
            assertNull(loadingState.error)

            // Loaded state
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals("Test Movie", loadedState.movie?.title)
            assertEquals("Test Description", loadedState.movie?.description)
            assertEquals("/test.jpg", loadedState.movie?.posterPath)
            assertEquals(8.5f, loadedState.movie?.voteAverage)
            assertEquals(100, loadedState.movie?.voteCount)
            assertNull(loadedState.error)
        }
    }

    private fun mockGetMovieDetailsUseCaseSuccess() {
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

    private fun mockGetMovieDetailsUseCaseError(error: Error) {
        coEvery { getMovieDetailsUseCase(testMovieId) } returns Result.Error(error)
    }
} 