package com.example.letswatch
import java.io.Serializable

data class Movie(
    var id: String? = null,
    var backdrop_path: String? = null,
    var title: String? = null,
    var release_date: String? = null,
    var genre_ids: Array<String>? = null,
    var vote_average: String? = null,
    var vote_count: String? = null,
    var overview: String? = null
): Serializable
