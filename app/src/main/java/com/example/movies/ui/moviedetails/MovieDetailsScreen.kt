package com.example.movies.ui.moviedetails

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.movies.R
import com.example.movies.ui.components.BackButton
import com.example.movies.ui.components.ErrorContent
import com.example.movies.ui.theme.MoviesTheme
import kotlinx.coroutines.flow.collectLatest

private val BACKDROP_HEIGHT = 400.dp

@Composable
fun MovieDetailsScreen(
    viewModel: MovieDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                MovieDetailsEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    MovieDetailsUi(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun MovieDetailsUi(
    state: MovieDetailsUiState,
    onIntent: (MovieDetailsUiIntent) -> Unit,
) {
    val scrollState = rememberScrollState()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.error != null) {
                ErrorContent(
                    onRetry = { onIntent(MovieDetailsUiIntent.OnRetry) },
                    text = stringResource(R.string.error_loading_movie_details),
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                state.movie?.let { movie ->
                    Box(modifier = Modifier.fillMaxSize()) {

                        MovieDetailsImage(
                            posterPath = movie.posterPath,
                            title = movie.title,
                            scrollState = scrollState
                        )

                        // Scrollable content
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                                .verticalScroll(scrollState)
                        ) {
                            Spacer(modifier = Modifier.height(BACKDROP_HEIGHT - 100.dp))

                            Text(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                text = movie.title,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Text(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                text = stringResource(
                                    R.string.rating,
                                    movie.voteAverage,
                                    movie.voteCount
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))

                            Text(
                                text = movie.description,
                                style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Justify),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        BackButton(
                            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_small)),
                            onBackClick = { onIntent(MovieDetailsUiIntent.OnBackClick) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieDetailsImage(
    posterPath: String?,
    title: String,
    scrollState: ScrollState
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(BACKDROP_HEIGHT)
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Parallax effect - image moves at 0.3x the scroll speed for a more dramatic effect
                    translationY = -scrollState.value * 0.3f
                    // Fade out effect - image becomes more transparent as we scroll (from 100% to 0%)
                    alpha =
                        1f - (scrollState.value / scrollState.maxValue.toFloat()).coerceIn(0f, 1f)
                },
            model = ImageRequest.Builder(LocalContext.current)
                .diskCachePolicy(CachePolicy.ENABLED)
                .data(posterPath)
                .crossfade(true)
                .build(),
            contentDescription = title,
            contentScale = ContentScale.Crop
        )

        // Dynamic shadow overlay based on scroll
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = (scrollState.value / scrollState.maxValue.toFloat()) * 1f),
                            MaterialTheme.colorScheme.background
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
    }
}

@Preview
@Composable
private fun MovieDetailsUiPreview() {
    MoviesTheme {
        MovieDetailsUi(MovieDetailsUiState(), {})
    }
}