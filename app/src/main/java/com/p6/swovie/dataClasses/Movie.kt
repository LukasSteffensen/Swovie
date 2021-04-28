package com.p6.swovie.dataClasses

import com.google.gson.annotations.SerializedName

data class Movie(
        @SerializedName("id") val id: Long,
        @SerializedName("title") val title: String,
        @SerializedName("overview") val overview: String,
        @SerializedName("poster_path") val posterPath: String
        //@SerializedName("backdrop_path") val backdropPath: String,
        //@SerializedName("vote_average") val rating: Float,
        //@SerializedName("release_date") val releaseDate: String
) {
        constructor() : this(-1, "",
                "", ""
        )

        override fun toString(): String {
                return "id: ${this.id}," +
                        " title: ${this.title}," +
                        " overview: ${this.overview}," +
                        " poster_path: ${this.posterPath}"
        }
}
