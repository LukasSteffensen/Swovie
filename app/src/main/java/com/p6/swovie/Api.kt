package com.p6.swovie


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface Api {

    @GET("movie/popular")
    fun getPopularMovies(
            @Query("api_key") apiKey: String = "9870f62e69820872d263749cf1055bc1",
            @Query("page") page: Int
    ): Call<GetMoviesResponse>


}