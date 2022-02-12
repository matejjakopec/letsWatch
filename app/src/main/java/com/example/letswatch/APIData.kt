package com.example.letswatch

import java.io.Serializable

data class APIData(
    var page: String? = null,
    var results: Array<Movie>? = null,
    var total_pages: String? = null,
    var total_results: String? = null,
): Serializable
