package com.example.movies.domain.core

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */
data class PagingData<T>(
    val data: List<T>,
    val totalPages: Int
)