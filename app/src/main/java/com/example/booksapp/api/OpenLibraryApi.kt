package com.example.booksapp.api

import com.example.booksapp.entities.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBooks(@Query("q") query: String): SearchResponse
}