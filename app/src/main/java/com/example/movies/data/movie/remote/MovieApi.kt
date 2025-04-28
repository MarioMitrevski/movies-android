package com.example.movies.data.movie.remote

import com.example.movies.data.movie.remote.dto.MovieDto
import com.example.movies.data.movie.remote.dto.MovieListDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
interface MovieApi {
    @GET("discover/movie")
    suspend fun getMovies(@Query("page") page: Int): Response<MovieListDto>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("page") page: Int,
        @Query("query") query: String
    ): Response<MovieListDto>

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(@Path("movieId") movieId: Int): Response<MovieDto>

}
