package com.p6.swovie

import com.google.gson.annotations.SerializedName
import com.p6.swovie.dataClasses.Movie

data class GetMoviesResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val movies: List<Movie>,
    @SerializedName("total_pages") val pages: Int
)

