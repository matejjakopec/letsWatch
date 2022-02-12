package com.example.letswatch

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface JsonAPI {
    @GET("movie?")
    fun getMovies(@Query("api_key")api_key: String,
                  @Query("language")language: String,
                  @Query("sort_by")sort_by: String,
                  @Query("include_adult")include_adult: String,
                  @Query("include_video")include_video: String,
                  @Query("page")page: String,
                  @Query("with_watch_monetization_types")with_watch_monetization_types: String ): Call<APIData>

}