package com.example.movies.ui.moviedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movies.domain.core.onError
import com.example.movies.domain.core.onSuccess
import com.example.movies.domain.movie.usecase.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow(MovieDetailsUiState())
    val state: StateFlow<MovieDetailsUiState> = _state

    init {
        loadMovieDetails()
    }

    fun onIntent(intent: MovieDetailsUiIntent) {
        when (intent) {
            MovieDetailsUiIntent.OnRetry -> loadMovieDetails()
            MovieDetailsUiIntent.OnBackClick -> {}
        }
    }

    private fun loadMovieDetails() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, error = null)
            }

            getMovieDetailsUseCase(movieId)
                .onSuccess { movie ->
                    _state.update {
                        it.copy(movie = movie, isLoading = false)
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(isLoading = false, error = error)
                    }
                }
        }
    }
} 