package com.lalilu.knr.core

typealias Route = (Map<String, Any?>) -> Screen
typealias RouteWithParams = Pair<Route, Map<String, String>>

interface Provider {
    fun insertRoute(vararg routes: String, route: Route)
    fun getRoute(baseRoute: String): RouteWithParams
}