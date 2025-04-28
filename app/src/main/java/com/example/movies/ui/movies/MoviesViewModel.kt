package com.example.movies.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movies.domain.core.onError
import com.example.movies.domain.core.onSuccess
import com.example.movies.domain.movie.usecase.GetMoviesUseCase
import com.example.movies.domain.movie.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
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

        viewModelScope.launch {
            _state.value = currentState.copy(isLoading = true, error = null)

            val result = if (currentState.searchQuery.isBlank()) {
                getMoviesUseCase(currentState.currentPage)
            } else {
                searchMoviesUseCase(currentState.currentPage, currentState.searchQuery)
            }

            withContext(Dispatchers.Default) {
                result.onSuccess { pagingData ->
                    val newMovies = if (currentState.currentPage == 1) {
                        pagingData.data
                    } else {
                        currentState.movies + pagingData.data
                    }

                    withContext(Dispatchers.Main) {
                        _state.value = _state.value.copy(
                            movies = newMovies,
                            isLoading = false,
                            hasMorePages = pagingData.totalPages != currentState.currentPage,
                            currentPage = currentState.currentPage + 1
                        )
                    }
                }.onError { error ->
                    withContext(Dispatchers.Main) {
                        _state.value = _state.value.copy(isLoading = false, error = error)
                    }
                }
            }
        }
    }

    private fun updateSearchQuery(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            _state.value = _state.value.copy(searchQuery = query)
            _effect.emit(MoviesEffect.ScrollToTop)

            delay(300) // Debounce time in milliseconds
            _state.value =
                _state.value.copy(currentPage = 1, movies = emptyList(), hasMorePages = true)
            loadMovies()
        }
    }

    private fun retryMovies() {
        viewModelScope.launch {
            _state.value = _state.value.copy(currentPage = 1, movies = emptyList())
            loadMovies()
        }
    }

    private fun navigateToDetails(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(MoviesEffect.NavigateToDetails(movieId))
        }
    }
} 