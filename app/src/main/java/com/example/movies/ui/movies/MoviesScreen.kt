package com.example.movies.ui.movies

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.movies.R
import com.example.movies.domain.movie.model.Movie
import com.example.movies.ui.components.AppSearchBar
import com.example.movies.ui.components.ErrorContent
import com.example.movies.ui.theme.MoviesTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
@Composable
fun MoviesScreen(
    viewModel: MoviesViewModel = hiltViewModel(),
    onNavigateToDetails: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        viewModel.effect
            .collectLatest { effect ->
                when (effect) {
                    is MoviesEffect.NavigateToDetails -> onNavigateToDetails(effect.movieId)
                    is MoviesEffect.ScrollToTop -> gridState.scrollToItem(0)
                }
            }
    }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collectLatest { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= state.movies.size - 10) {
                    viewModel.onIntent(MoviesUiIntent.LoadNextPage)
                }
            }
    }

    MoviesUi(state = state, gridState = gridState, onIntent = viewModel::onIntent)
}

@Composable
fun MoviesUi(state: MoviesUiState, gridState: LazyGridState, onIntent: (MoviesUiIntent) -> Unit) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AppSearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.spacing_medium)),
                query = state.searchQuery,
                onQueryChange = { onIntent(MoviesUiIntent.OnSearchQueryChange(it)) }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading && state.movies.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (state.error != null) {
                    ErrorContent(
                        onRetry = { onIntent(MoviesUiIntent.OnRetry) },
                        text = stringResource(R.string.error_loading_movies),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    MoviesGrid(
                        movies = state.movies,
                        onMovieClick = { onIntent(MoviesUiIntent.OnMovieClick(it)) },
                        gridState = gridState,
                        isLoadingNextPage = state.isLoading
                    )
                }
            }
        }
    }
}

@Composable
private fun MoviesGrid(
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit,
    gridState: LazyGridState,
    modifier: Modifier = Modifier,
    isLoadingNextPage: Boolean = false
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(dimensionResource(R.dimen.spacing_medium)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small)),
        modifier = modifier
    ) {
        items(
            items = movies,
            key = { it.id },
            contentType = { "movie" }
        ) { movie ->
            MovieItem(
                posterPath = movie.posterPath,
                title = movie.title,
                onClick = { onMovieClick(movie.id) }
            )
        }

        if (isLoadingNextPage) {
            item(
                span = { GridItemSpan(maxLineSpan) },
                contentType = "loader"
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.spacing_medium)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun MovieItem(
    posterPath: String?,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2 / 3f)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .diskCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .data(posterPath)
                .build(),
            contentDescription = title,
            contentScale = ContentScale.Crop
        )
    }
}

@Preview
@Composable
private fun MoviesUiPreview() {
    MoviesTheme {
        MoviesUi(MoviesUiState(), rememberLazyGridState(), {})
    }
}