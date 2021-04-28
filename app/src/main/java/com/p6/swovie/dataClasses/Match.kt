package com.p6.swovie.dataClasses

data class Match(
    val movieId: String? = null,
    var title: String? = null,
    val matchPercentage: String? = null,
    var posterPath: String? = null,
)
