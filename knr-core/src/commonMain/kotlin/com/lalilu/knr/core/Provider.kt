package com.lalilu.knr.core

interface Provider {
    fun getRoute(baseRoute: String): (Map<String, Any?>) -> Screen
}