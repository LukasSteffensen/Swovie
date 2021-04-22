package com.p6.swovie.dataClasses

import com.p6.swovie.BuildConfig
import java.security.SecureRandom

private val charPool : List<Char> = ('A'..'Z') + ('0'..'9')

val STRING_LENGTH = 4
val ALPHANUMERIC_REGEX = "[A-Z0-9]+"

data class Group (
    val users: ArrayList<String>? = null,
    //val swipedMovieIDs: HashMap<String, List<String>>
)

fun generateGroupId(): String {
    val random = SecureRandom()
    val bytes = ByteArray(STRING_LENGTH)
    random.nextBytes(bytes)

    val randomString = (bytes.indices)
        .map { i -> charPool[random.nextInt(charPool.size)] }
        .joinToString("")

    if (BuildConfig.DEBUG && !randomString.matches(Regex(ALPHANUMERIC_REGEX))) {
        error("Assertion failed")
    } else if (BuildConfig.DEBUG && randomString.length!= STRING_LENGTH) {
        error("Assertion failed")
    } else {
        return randomString
    }
}