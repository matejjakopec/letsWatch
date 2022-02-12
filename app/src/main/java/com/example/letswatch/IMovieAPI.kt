package com.example.letswatch

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IMovieAPI {
    @GET("{Id}?")
    fun getMovie(@Path("Id")Id: String,
                  @Query("api_key")api_key: String,
                  @Query("language")language: String): Call<MovieAPI>
}