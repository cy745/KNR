package com.lalilu.knr.compiler.ext

import com.google.devtools.ksp.symbol.KSTypeReference

internal val KSTypeReference.typeQualifiedName: String?
    get() = resolve()
        .declaration
        .qualifiedName
        ?.asString()
