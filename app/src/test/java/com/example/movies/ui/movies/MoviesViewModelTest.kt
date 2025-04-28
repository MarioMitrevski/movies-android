package com.example.movies.ui.movies

import com.example.movies.domain.core.Error
import com.example.movies.domain.core.PagingData
import com.example.movies.domain.core.Result
import com.example.movies.domain.movie.model.Movie
import com.example.movies.domain.movie.usecase.GetMoviesUseCase
import com.example.movies.domain.movie.usecase.SearchMoviesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
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
class MoviesViewModelTest {
    private lateinit var getMoviesUseCase: GetMoviesUseCase
    private lateinit var searchMoviesUseCase: SearchMoviesUseCase
    private lateinit var viewModel: MoviesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        setupMocks()
        createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupMocks() {
        getMoviesUseCase = mockk()
        searchMoviesUseCase = mockk()
        
        // Set up initial state mock
        val initialMovie = Movie(
            id = 1,
            title = "Test Movie",
            description = "Test Description",
            posterPath = "/test.jpg",
            voteAverage = 8.5f,
            voteCount = 100
        )
        val pagingData = PagingData(
            data = listOf(initialMovie),
            totalPages = 3
        )
        coEvery { getMoviesUseCase(1) } returns Result.Success(pagingData)
    }

    private fun createViewModel() {
        viewModel = MoviesViewModel(getMoviesUseCase, searchMoviesUseCase, testDispatcher)
    }

    @Test
    fun `initial state loads first page of movies`() = runTest {
        // When - advance the scheduler to allow the init block to complete
        advanceUntilIdle()
        
        // Then
        val state = viewModel.state.value
        assertEquals(1, state.movies.size)
        assertEquals("Test Movie", state.movies[0].title)
        assertFalse(state.isLoading)
        assertTrue(state.hasMorePages)
        assertEquals(2, state.currentPage)
        assertNull(state.error)
    }

    @Test
    fun `loadNextPage loads next page of movies successfully`() = runTest {
        // Given
        val nextPageMovie = Movie(
            id = 2,
            title = "Test Movie 2",
            description = "Test Description 2",
            posterPath = "/test2.jpg",
            voteAverage = 7.5f,
            voteCount = 200
        )
        val pagingData = PagingData(
            data = listOf(nextPageMovie),
            totalPages = 3
        )
        coEvery { getMoviesUseCase(2) } returns Result.Success(pagingData)

        // When
        viewModel.onIntent(MoviesUiIntent.LoadNextPage)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(2, state.movies.size)
        assertEquals("Test Movie 2", state.movies[1].title)
        assertFalse(state.isLoading)
        assertTrue(state.hasMorePages)
        assertEquals(3, state.currentPage)
    }

    @Test
    fun `search query updates movies list`() = runTest {
        // Given
        val searchMovie = Movie(
            id = 3,
            title = "Search Result",
            description = "Search Description",
            posterPath = "/search.jpg",
            voteAverage = 9.0f,
            voteCount = 300
        )
        val pagingData = PagingData(
            data = listOf(searchMovie),
            totalPages = 2
        )
        coEvery { searchMoviesUseCase(1, "test") } returns Result.Success(pagingData)

        // When
        viewModel.onIntent(MoviesUiIntent.OnSearchQueryChange("test"))
        
        // Advance past the debounce delay (300ms)
        advanceTimeBy(300)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(1, state.movies.size)
        assertEquals("Search Result", state.movies[0].title)
        assertEquals("test", state.searchQuery)
    }

    @Test
    fun `error state is handled correctly`() = runTest {
        // Given
        val error = Error.Remote.NO_INTERNET
        
        // Clear previous mocks and set up error response
        coEvery { getMoviesUseCase(any()) } returns Result.Error(error)

        // When
        viewModel.onIntent(MoviesUiIntent.LoadNextPage)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(error, state.error)
        assertFalse(state.isLoading)
    }
}
