package com.example.letswatch

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GenreJsonAPI {
    @GET("3/genre/movie/list")
    fun getGenres(@Query("api_key")api_key: String,
                  @Query("language")language: String): Call<genreAPIdata>
}