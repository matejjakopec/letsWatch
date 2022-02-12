package com.example.letswatch

data class MovieAPI(
    var id: String? = null,
    var backdrop_path: String? = null,
    var title: String? = null,
    var release_date: String? = null,
    var genres: Array<Genre>? = null,
    var vote_average: String? = null,
    var vote_count: String? = null,
    var overview: String? = null
)
