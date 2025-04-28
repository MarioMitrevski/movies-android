package com.example.movies.ui.moviedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movies.domain.core.onError
import com.example.movies.domain.core.onSuccess
import com.example.movies.domain.movie.usecase.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _effect = MutableSharedFlow<MovieDetailsEffect>()
    val effect: SharedFlow<MovieDetailsEffect> = _effect

    init {
        loadMovieDetails()
    }

    fun onIntent(intent: MovieDetailsUiIntent) {
        when (intent) {
            MovieDetailsUiIntent.OnRetry -> loadMovieDetails()
            MovieDetailsUiIntent.OnBackClick -> navigateBack()
        }
    }

    private fun loadMovieDetails() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            getMovieDetailsUseCase(movieId)
                .onSuccess { movie ->
                    _state.value = _state.value.copy(
                        movie = movie,
                        isLoading = false
                    )
                }
                .onError { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error
                    )
                }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _effect.emit(MovieDetailsEffect.NavigateBack)
        }
    }
} 