package com.lalilu.knr.compiler.ext

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.lalilu.knr.compiler.Constants


/**
 * 获取列表中被标记 start = true 的类，并确保该类是 object 可直接获取对象
 */
internal fun getStartDestination(collectedMap: List<KSClassDeclaration>): KSClassDeclaration? {
    return collectedMap.firstOrNull {
        it.requireAnnotation(Constants.QUALIFIED_NAME_DESTINATION)
            ?.arguments
            ?.firstOrNull { it.name?.asString() == "start" }
            ?.value as? Boolean == true
    }?.also {
        assert(it.classKind == ClassKind.OBJECT) { "start destination must be object" }
    }
}