package com.p6.swovie.dataClasses

data class User(
    val userID: String? = null,
    val name: String? = null,
    val email: String? = null,
    val swipedMovieIDs: List<String>? = null
)
