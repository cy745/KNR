package com.lalilu.knr.compiler.ext

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueArgument
import com.lalilu.knr.compiler.Constants
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ksp.toClassName


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

/**
 * 将类转换为对象
 */
internal fun classToObject(clazz: KSClassDeclaration?): CodeBlock {
    return CodeBlock.builder()
        .apply {
            when (clazz?.classKind) {
                ClassKind.CLASS -> add("%T()", clazz.toClassName())
                ClassKind.OBJECT -> add("%T", clazz.toClassName())
                else -> add("%L", clazz?.toClassName())
            }
        }
        .build()
}

internal fun getDeclarationFromArgument(argument: KSValueArgument): KSClassDeclaration? {
    return when (val type = argument.value) {
        is KSClassDeclaration -> type
        is KSTypeReference -> type.resolve().declaration.asClassDeclaration()
        is KSType -> type.declaration.asClassDeclaration()
        else -> null
    }
}