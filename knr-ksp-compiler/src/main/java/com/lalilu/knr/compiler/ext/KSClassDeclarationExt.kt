package com.lalilu.knr.compiler.ext

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation

internal inline fun <reified T> KSAnnotated.requireAnnotation(): KSAnnotation {
    return annotations.first {
        it.typeDeclaration.asClassDeclaration().qualifiedName?.asString() == T::class.qualifiedName
    }
}

internal inline fun <reified T> KSAnnotated.requestAnnotation(): KSAnnotation? {
    return annotations.firstOrNull {
        it.typeDeclaration.asClassDeclaration().qualifiedName?.asString() == T::class.qualifiedName
    }
}

internal fun KSAnnotated.requireAnnotation(qualifiedName: String): KSAnnotation? {
    return annotations.firstOrNull {
        it.typeDeclaration.asClassDeclaration().qualifiedName?.asString() == qualifiedName
    }
}