package com.lalilu.knr.compiler.code

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName

fun buildHandleParamsFunction(): FunSpec = FunSpec.builder("handleParams")
    .addModifiers(KModifier.PRIVATE, KModifier.INLINE)
    .addTypeVariable(
        TypeVariableName("T", Any::class.asTypeName())
            .copy(reified = true)
    )
    .receiver(
        Map::class.asClassName()
            .parameterizedBy(
                String::class.asTypeName(),
                Any::class.asTypeName()
                    .copy(nullable = true)
            )
    )
    .addParameter("name", String::class)
    .addParameter(
        ParameterSpec.builder("castable", Boolean::class)
            .defaultValue("false")
            .build()
    )
    .returns(ClassName("", "ParamState"))
    .addStatement("if (!this.containsKey(name)) return ParamState.NotProvided(name)")
    .addStatement("val value = this[name] ?: return ParamState.ProvidedButNull(name)")

    .beginControlFlow("if (T::class.isInstance(value)) {")
    .addStatement("return ParamState.Provided(name, value as T)")
    .endControlFlow()

    .beginControlFlow("if (!castable) {")
    .addStatement(
        "return ParamState.ProvidedButWrongType(name, %P)",
        "Parameter [\$name] expects a [\${T::class.qualifiedName}], but a [\${value::class.qualifiedName}] is provided."
    )
    .endControlFlow()

    .beginControlFlow("if (value !is String) {")
    .addStatement(
        "return ParamState.ProvidedButWrongType(name, %P)",
        "Parameter [\$name] expects a [\${T::class.qualifiedName}], but a [\${value::class.qualifiedName}] is provided."
    )
    .endControlFlow()

    .beginControlFlow("val casted = when {")
    .addStatement("T::class.isInstance(0) -> value.toIntOrNull()")
    .addStatement("T::class.isInstance(0L) -> value.toLongOrNull()")
    .addStatement("T::class.isInstance(0f) -> value.toFloatOrNull()")
    .addStatement("T::class.isInstance(0.0) -> value.toDoubleOrNull()")
    .addStatement("T::class.isInstance(false) -> value.toBooleanStrictOrNull()")
    .addStatement("else -> null")
    .endControlFlow()

    .beginControlFlow("if (casted == null) {")
    .addStatement(
        "return ParamState.ProvidedButWrongType(name, %P)",
        "Parameter [\$name] expects a [\${T::class.qualifiedName}], but a [\${value::class.qualifiedName}] is provided."
    )
    .endControlFlow()

    .addStatement("return ParamState.Provided(name, casted as T)")
    .build()

