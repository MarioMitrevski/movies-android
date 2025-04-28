package com.example.movies.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movies.core.IoDispatcher
import com.example.movies.domain.core.onError
import com.example.movies.domain.core.onSuccess
import com.example.movies.domain.movie.usecase.GetMoviesUseCase
import com.example.movies.domain.movie.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow(MoviesUiState())
    val state: StateFlow<MoviesUiState> = _state

    private val _effect = MutableSharedFlow<MoviesEffect>()
    val effect: SharedFlow<MoviesEffect> = _effect

    private var searchJob: Job? = null

    init {
        loadMovies()
    }

    fun onIntent(intent: MoviesUiIntent) {
        when (intent) {
            MoviesUiIntent.LoadNextPage -> loadMovies()
            is MoviesUiIntent.OnSearchQueryChange -> updateSearchQuery(intent.query)
            is MoviesUiIntent.OnRetry -> retryMovies()
            is MoviesUiIntent.OnMovieClick -> navigateToDetails(intent.movieId)
        }
    }

    private fun loadMovies() {
        val currentState = _state.value
        if (currentState.isLoading || !currentState.hasMorePages) return

        viewModelScope.launch(ioDispatcher) {
            _state.update {
                it.copy(isLoading = true, error = null)
            }

            val result = if (currentState.searchQuery.isBlank()) {
                getMoviesUseCase(currentState.currentPage)
            } else {
                searchMoviesUseCase(currentState.currentPage, currentState.searchQuery)
            }

            result.onSuccess { pagingData ->
                val newMovies = if (currentState.currentPage == 1) {
                    pagingData.data
                } else {
                    currentState.movies + pagingData.data
                }

                _state.update {
                    it.copy(
                        movies = newMovies,
                        isLoading = false,
                        hasMorePages = pagingData.totalPages != currentState.currentPage,
                        currentPage = currentState.currentPage + 1
                    )
                }
            }.onError { error ->
                _state.update {
                    it.copy(isLoading = false, error = error)
                }
            }
        }
    }

    private fun updateSearchQuery(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch(ioDispatcher) {
            _state.update {
                it.copy(searchQuery = query)
            }
            _effect.emit(MoviesEffect.ScrollToTop)

            delay(300) // Debounce time in milliseconds
            _state.update {
                it.copy(currentPage = 1, movies = emptyList(), hasMorePages = true)
            }
            loadMovies()
        }
    }

    private fun retryMovies() {
        _state.update {
            it.copy(currentPage = 1, movies = emptyList())
        }
        loadMovies()
    }

    private fun navigateToDetails(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(MoviesEffect.NavigateToDetails(movieId))
        }
    }
} 