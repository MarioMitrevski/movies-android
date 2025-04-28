package com.example.movies.data.movie.mappers

import com.example.movies.data.movie.remote.dto.MovieDto
import com.example.movies.data.movie.remote.dto.MovieListDto
import org.junit.Assert.assertEquals
import org.junit.Test

class MovieMappersTest {

    @Test
    fun `asMovie correctly maps MovieDto to Movie`() {
        // Given
        val movieDto = MovieDto(
            id = 1,
            title = "Test Movie",
            description = "Test Description",
            posterPath = "/test.jpg",
            voteAverage = 8.5f,
            voteCount = 100
        )

        // When
        val movie = movieDto.asMovie()

        // Then
        assertEquals(movieDto.id, movie.id)
        assertEquals(movieDto.title, movie.title)
        assertEquals(movieDto.description, movie.description)
        assertEquals(movieDto.posterPath, movie.posterPath)
        assertEquals(movieDto.voteAverage, movie.voteAverage)
        assertEquals(movieDto.voteCount, movie.voteCount)
    }

    @Test
    fun `asPagingData correctly maps MovieListDto to PagingData`() {
        // Given
        val movieDto1 = MovieDto(
            id = 1,
            title = "Test Movie 1",
            description = "Test Description 1",
            posterPath = "/test1.jpg",
            voteAverage = 8.5f,
            voteCount = 100
        )
        
        val movieDto2 = MovieDto(
            id = 2,
            title = "Test Movie 2",
            description = "Test Description 2",
            posterPath = "/test2.jpg",
            voteAverage = 7.5f,
            voteCount = 200
        )
        
        val movieListDto = MovieListDto(
            page = 1,
            results = listOf(movieDto1, movieDto2),
            totalPages = 10,
        )

        // When
        val pagingData = movieListDto.asPagingData()

        // Then
        assertEquals(2, pagingData.data.size)
        assertEquals("Test Movie 1", pagingData.data[0].title)
        assertEquals("Test Movie 2", pagingData.data[1].title)
        assertEquals(10, pagingData.totalPages)
    }
} 