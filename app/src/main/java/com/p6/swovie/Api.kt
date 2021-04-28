package com.p6.swovie


import com.p6.swovie.dataClasses.Movie
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface Api {

    companion object {
        private const val PARAM_MOVIE_ID = "movieId"
        private const val PARAM_LANGUAGE = "language"
        private const val PARAM_PAGE = "page"
        private const val UPCOMING_MOVIES = "movie/upcoming"
        private const val MOVIE_DETAILS = "movie/{$PARAM_MOVIE_ID}"
    }

    @GET("movie/popular")
    fun getPopularMovies(
            @Query("api_key") apiKey: String = "9870f62e69820872d263749cf1055bc1",
            @Query("page") page: Int
    ): Call<GetMoviesResponse>

    @GET(MOVIE_DETAILS)
    fun getMovieDetails(@Path(PARAM_MOVIE_ID) movieId: Int
    //                 , @Query(PARAM_LANGUAGE) language: String = "en-US"
    ): Call<Movie>

    /* Search but doesn't work yet
    @GET("search/movie")
    fun getMoviesList(@Query("query") query: String?,@Query("api_key") api_key:String, @Query("page") page: Int?
    ): Single<SearchResponse>
     */
}