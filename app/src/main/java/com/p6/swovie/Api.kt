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

    //This does not work yet
   // @GET("movie/{movie_id}")
   // fun getMovieFromId(
   //     @Query("api_key") apiKey: String = "9870f62e69820872d263749cf1055bc1",
   //     @Query("id") id: Int
   // ): Call<GetMoviesResponse>

}