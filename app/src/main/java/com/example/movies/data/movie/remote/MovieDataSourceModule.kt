package com.example.movies.data.movie.remote

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */

@InstallIn(SingletonComponent::class)
@Module
abstract class MovieDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindRemoteMovieDataSource(
        retrofitRemoteMovieDataSource: RetrofitRemoteMovieDataSource
    ): RemoteMovieDataSource
}