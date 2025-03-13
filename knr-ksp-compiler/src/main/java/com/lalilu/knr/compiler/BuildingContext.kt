package com.lalilu.knr.compiler

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

interface BuildingContext {
    fun getCodeGenerator(): CodeGenerator
    fun getEnvironment(): SymbolProcessorEnvironment
    fun log(message: String) = getEnvironment().logger.info(message)
}