package com.example.movies.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.movies.ui.moviedetails.MovieDetailsScreen
import com.example.movies.ui.movies.MoviesScreen
import com.example.movies.ui.theme.MoviesTheme

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */

@Composable
@Preview
fun App() {
    MoviesTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Route.MoviesGraph
        ) {
            navigation<Route.MoviesGraph>(
                startDestination = Route.Movies
            ) {
                composable<Route.Movies>(
                    enterTransition = {
                        fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(150))
                    },
                    popEnterTransition = {
                        fadeIn(animationSpec = tween(300))
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(150))
                    }
                ) {
                    MoviesScreen { id ->
                        navController.navigate(Route.MovieDetails(id))
                    }
                }
                composable<Route.MovieDetails>(
                    enterTransition = {
                        scaleIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeIn(
                            animationSpec = tween(150)
                        )
                    },
                    exitTransition = {
                        scaleOut(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeOut(
                            animationSpec = tween(150)
                        )
                    },
                    popEnterTransition = {
                        scaleIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeIn(
                            animationSpec = tween(150)
                        )
                    },
                    popExitTransition = {
                        scaleOut(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeOut(
                            animationSpec = tween(150)
                        )
                    }
                ) {
                    MovieDetailsScreen {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}