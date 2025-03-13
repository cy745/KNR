package com.lalilu.knr.compiler

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

interface BuildingContext {
    fun getEnvironment(): SymbolProcessorEnvironment
    fun getResolver(): Resolver
    fun log(message: String) = getEnvironment().logger.warn(message)
}