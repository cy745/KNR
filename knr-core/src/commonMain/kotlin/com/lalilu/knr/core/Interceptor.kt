package com.lalilu.knr.core

interface Interceptor {
    fun shouldIntercept(route: String, params: Map<String, Any?>): Boolean
    fun intercept(route: String, params: Map<String, Any?>): Map<String, Any?>
}