package com.example.movies.ui.movies

import app.cash.turbine.test
import com.example.movies.domain.core.Error
import com.example.movies.domain.core.PagingData
import com.example.movies.domain.core.Result
import com.example.movies.domain.movie.model.Movie
import com.example.movies.domain.movie.usecase.GetMoviesUseCase
import com.example.movies.domain.movie.usecase.SearchMoviesUseCase
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
    private val testMovies = listOf(
        Movie(
            id = 1,
            title = "Test Movie 1",
            description = "Test Description 1",
            posterPath = "/test1.jpg",
            voteAverage = 8.5f,
            voteCount = 100
        ),
        Movie(
            id = 2,
            title = "Test Movie 2",
            description = "Test Description 2",
            posterPath = "/test2.jpg",
            voteAverage = 7.5f,
            voteCount = 200
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getMoviesUseCase = mockk()
        searchMoviesUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads first page of movies`() = runTest {
        // Given
        mockGetMoviesUseCaseSuccess(1, testMovies)
        viewModel = MoviesViewModel(getMoviesUseCase, searchMoviesUseCase, testDispatcher)

        viewModel.state.test {
            // Initial state
            val initialState = awaitItem()
            assertTrue(initialState.movies.isEmpty())
            assertFalse(initialState.isLoading)
            assertTrue(initialState.hasMorePages)
            assertEquals(1, initialState.currentPage)
            assertNull(initialState.error)

            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertTrue(loadingState.movies.isEmpty())
            assertNull(loadingState.error)

            // Loaded state
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals(testMovies, loadedState.movies)
            assertTrue(loadedState.hasMorePages)
            assertEquals(2, loadedState.currentPage)
            assertNull(loadedState.error)
        }

        coVerify { getMoviesUseCase(1) }
    }

    @Test
    fun `load next page appends movies to list`() = runTest {
        // Given
        val nextPageMovies = listOf(
            Movie(
                id = 3,
                title = "Test Movie 3",
                description = "Test Description 3",
                posterPath = "/test3.jpg",
                voteAverage = 9.0f,
                voteCount = 300
            )
        )
        mockGetMoviesUseCaseSuccess(1, testMovies)
        mockGetMoviesUseCaseSuccess(2, nextPageMovies)
        viewModel = MoviesViewModel(getMoviesUseCase, searchMoviesUseCase, testDispatcher)

        viewModel.state.test {
            // Skip initial states
            skipItems(3)

            // When
            viewModel.onIntent(MoviesUiIntent.LoadNextPage)

            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertEquals(testMovies, loadingState.movies)

            // Loaded state with appended movies
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals(testMovies + nextPageMovies, loadedState.movies)
            assertEquals(3, loadedState.currentPage)
        }

        coVerify { getMoviesUseCase(1) }
        coVerify { getMoviesUseCase(2) }
    }

    @Test
    fun `search query updates movies list`() = runTest {
        // Given
        val searchQuery = "test"
        val searchResults = listOf(
            Movie(
                id = 4,
                title = "Search Result",
                description = "Search Description",
                posterPath = "/search.jpg",
                voteAverage = 8.0f,
                voteCount = 150
            )
        )
        mockGetMoviesUseCaseSuccess(1, testMovies)
        mockSearchMoviesUseCaseSuccess(1, searchQuery, searchResults)
        viewModel = MoviesViewModel(getMoviesUseCase, searchMoviesUseCase, testDispatcher)

        viewModel.state.test {
            // Skip initial states
            skipItems(3)

            // When
            viewModel.onIntent(MoviesUiIntent.OnSearchQueryChange(searchQuery))

            // Initial state
            val initialState = awaitItem()
            assertEquals(searchQuery, initialState.searchQuery)

            // Clear state
            val clearState = awaitItem()
            assertEquals(searchQuery, clearState.searchQuery)
            assertEquals(1, clearState.currentPage)
            assertTrue(clearState.movies.isEmpty())
            assertTrue(clearState.hasMorePages)

            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            // Loaded state with search results
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals(searchResults, loadedState.movies)
            assertEquals(2, loadedState.currentPage)
        }

        coVerify { getMoviesUseCase(1) }
        coVerify { searchMoviesUseCase(1, searchQuery) }
    }


    @Test
    fun `pagination works correctly after search query`() = runTest {
        // Given
        val searchQuery = "test"
        val searchResults = listOf(
            Movie(4, "Search 1", "Desc", "/s1.jpg", 7.0f, 100)
        )
        val nextPageResults = listOf(
            Movie(5, "Search 2", "Desc", "/s2.jpg", 6.0f, 50)
        )

        mockGetMoviesUseCaseSuccess(1, testMovies)
        mockSearchMoviesUseCaseSuccess(1, searchQuery, searchResults)
        mockSearchMoviesUseCaseSuccess(2, searchQuery, nextPageResults)
        viewModel = MoviesViewModel(getMoviesUseCase, searchMoviesUseCase, testDispatcher)

        viewModel.state.test {
            skipItems(3) // Skip initial loading

            viewModel.onIntent(MoviesUiIntent.OnSearchQueryChange(searchQuery))
            skipItems(4) // Query change states

            viewModel.onIntent(MoviesUiIntent.LoadNextPage)

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertEquals(searchResults, loadingState.movies)

            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals(searchResults + nextPageResults, loadedState.movies)
        }

        coVerify { searchMoviesUseCase(1, searchQuery) }
        coVerify { searchMoviesUseCase(2, searchQuery) }
    }

    @Test
    fun `search with no results updates movies to empty list`() = runTest {
        val searchQuery = "empty"
        mockGetMoviesUseCaseSuccess(1, testMovies)
        mockSearchMoviesUseCaseSuccess(1, searchQuery, emptyList())

        viewModel = MoviesViewModel(getMoviesUseCase, searchMoviesUseCase, testDispatcher)

        viewModel.state.test {
            skipItems(3)

            viewModel.onIntent(MoviesUiIntent.OnSearchQueryChange(searchQuery))

            skipItems(3) // State changes

            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertTrue(loadedState.movies.isEmpty())
        }

        coVerify { searchMoviesUseCase(1, searchQuery) }
    }

    @Test
    fun `rapid search query changes result in latest query being loaded`() = runTest {
        val query1 = "bat"
        val query2 = "batman"
        val result2 = listOf(Movie(6, "Batman", "Hero", "/bat.jpg", 9.0f, 300))

        mockGetMoviesUseCaseSuccess(1, testMovies)
        mockSearchMoviesUseCaseSuccess(1, query2, result2)

        viewModel = MoviesViewModel(getMoviesUseCase, searchMoviesUseCase, testDispatcher)

        viewModel.state.test {
            skipItems(3)

            viewModel.onIntent(MoviesUiIntent.OnSearchQueryChange(query1))
            viewModel.onIntent(MoviesUiIntent.OnSearchQueryChange(query2))

            // Final result should be from query2
            skipItems(3)
            val loadedState = awaitItem()

            assertEquals(query2, loadedState.searchQuery)
            assertEquals(result2, loadedState.movies)

            coVerify(exactly = 1) { searchMoviesUseCase(1, query2) }
        }
    }

    @Test
    fun `error state is handled correctly`() = runTest {
        // Given
        val error = Error.Remote.NO_INTERNET
        mockGetMoviesUseCaseError(1, error)
        viewModel = MoviesViewModel(getMoviesUseCase, searchMoviesUseCase, testDispatcher)

        viewModel.state.test {
            // Skip initial states
            skipItems(2)

            // Error state
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertTrue(errorState.movies.isEmpty())
            assertEquals(error, errorState.error)
        }

        coVerify { getMoviesUseCase(1) }
    }

    @Test
    fun `error during search shows correct error state`() = runTest {
        val query = "joker"
        val error = Error.Remote.UNKNOWN
        mockGetMoviesUseCaseSuccess(1, testMovies)
        coEvery { searchMoviesUseCase(1, query) } returns Result.Error(error)

        viewModel = MoviesViewModel(getMoviesUseCase, searchMoviesUseCase, testDispatcher)

        viewModel.state.test {
            skipItems(3)

            viewModel.onIntent(MoviesUiIntent.OnSearchQueryChange(query))
            skipItems(3)

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals(error, errorState.error)
            assertTrue(errorState.movies.isEmpty())
        }

        coVerify { searchMoviesUseCase(1, query) }
    }

    @Test
    fun `retry resets state and loads movies again`() = runTest {
        // Given
        val error = Error.Remote.NO_INTERNET
        mockGetMoviesUseCaseError(1, error)
        viewModel = MoviesViewModel(getMoviesUseCase, searchMoviesUseCase, testDispatcher)

        viewModel.state.test {
            // Skip initial states
            skipItems(3)

            mockGetMoviesUseCaseSuccess(1, testMovies)

            // When
            viewModel.onIntent(MoviesUiIntent.OnRetry)

            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertTrue(loadingState.movies.isEmpty())
            assertNull(loadingState.error)

            // Loaded state
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals(testMovies, loadedState.movies)
            assertNull(loadedState.error)
        }

        coVerify(exactly = 2) { getMoviesUseCase(1) }
    }

    private fun mockGetMoviesUseCaseSuccess(page: Int, movies: List<Movie>) {
        coEvery { getMoviesUseCase(page) } returns Result.Success(
            PagingData(
                data = movies,
                totalPages = 3
            )
        )
    }

    private fun mockGetMoviesUseCaseError(page: Int, error: Error) {
        coEvery { getMoviesUseCase(page) } returns Result.Error(error)
    }

    private fun mockSearchMoviesUseCaseSuccess(page: Int, query: String, movies: List<Movie>) {
        coEvery { searchMoviesUseCase(page, query) } returns Result.Success(
            PagingData(
                data = movies,
                totalPages = 2
            )
        )
    }
}
