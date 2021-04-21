package com.p6.swovie

interface ResultListener {
    fun onResult(inGroup: Boolean)
    fun onError(error: Throwable)
}