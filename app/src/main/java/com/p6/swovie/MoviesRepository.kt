package com.p6.swovie


import com.p6.swovie.dataClasses.Movie

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MoviesRepository {

    private val api: Api

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        api = retrofit.create(Api::class.java)
    }


    fun getPopularMovies(page: Int = 1, onSuccess: (movies: List<Movie>) -> Unit, onError: () -> Unit) {
        api.getPopularMovies(page = page).enqueue(object : Callback<MoviesList> {
                    override fun onResponse(
                        call: Call<MoviesList>,
                        list: Response<MoviesList>
                    ) {
                        if (list.isSuccessful) {
                            val responseBody = list.body()

                            if (responseBody != null) {
                                onSuccess.invoke(responseBody.movies)
                            } else {
                                onError.invoke()
                            }
                        } else {
                            onError.invoke()
                        }
                    }

                    override fun onFailure(call: Call<MoviesList>, t: Throwable) {
                        onError.invoke()
                    }
                })
    }

    fun getSearchedMovies(query: String, page: Int, onSuccess: (movies: List<Movie>) -> Unit, onError: () -> Unit) {
        api.getMoviesList(query = query, page = page).enqueue(object : Callback<MoviesList> {
            override fun onResponse(
                call: Call<MoviesList>,
                list: Response<MoviesList>
            ) {
                if (list.isSuccessful) {
                    val responseBody = list.body()

                    if (responseBody != null) {
                        onSuccess.invoke(responseBody.movies)
                    } else {
                        onError.invoke()
                    }
                } else {
                    onError.invoke()
                }
            }

            override fun onFailure(call: Call<MoviesList>, t: Throwable) {
                onError.invoke()
            }
        })
    }

    fun getMovieDetails(movieId: Int, onSuccess: (movie: Movie) -> Unit, onError: () -> Unit) {
        api.getMovieDetails(movieId).enqueue(object : Callback<Movie> {
            override fun onResponse(
                call: Call<Movie>,
                response: Response<Movie>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body() as Movie

                    onSuccess.invoke(responseBody)
                } else {
                    onError.invoke()
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                onError.invoke()
            }
        })
    }
}
