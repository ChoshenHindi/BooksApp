package com.example.booksapp.entities

data class Book(
    val title: String?,
    val author_name: List<String>?,
    val first_publish_year: Int?,
    val cover_i: Int?
)

data class SearchResponse(
    val docs: ArrayList<Book>
)