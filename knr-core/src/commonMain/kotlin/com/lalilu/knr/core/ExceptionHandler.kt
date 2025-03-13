package com.lalilu.knr.core

fun interface ExceptionHandler {
    fun onException(e: Throwable)

    companion object {
        val DEFAULT = ExceptionHandler { e -> e.printStackTrace() }
    }
}
