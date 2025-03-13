package com.lalilu.knr.compiler.code

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Nullability
import com.lalilu.knr.compiler.BuildingContext
import com.lalilu.knr.compiler.Constants
import com.lalilu.knr.compiler.ext.combinations
import com.lalilu.knr.compiler.ext.getDynamicParams
import com.lalilu.knr.compiler.ext.getRoutesFromAnnotation
import com.lalilu.knr.compiler.ext.requireAnnotation
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.joinToCode
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

fun BuildingContext.buildTrieRootProperty(): PropertySpec {
    val type = ClassName.bestGuess("com.lalilu.knr.core.ext.TrieNode")
        .parameterizedBy(ClassName.bestGuess(Constants.QUALIFIED_NAME_ROUTE))

    return PropertySpec
        .builder(name = "root", type = type)
        .addModifiers(KModifier.PRIVATE)
        .initializer(CodeBlock.of("TrieNode.createRootNode<Route>()"))
        .build()
}

fun BuildingContext.buildInsertRouteFunc(): FunSpec {
    return FunSpec.builder("insertRoute")
        .addParameter(name = "routes", type = String::class, modifiers = listOf(KModifier.VARARG))
        .addParameter(name = "route", type = ClassName.bestGuess(Constants.QUALIFIED_NAME_ROUTE))
        .addModifiers(KModifier.OVERRIDE)
        .addCode(buildCodeBlock {
            beginControlFlow("routes.forEach { pattern ->")
            add("val parts = pattern.split('/').filter { it.isNotEmpty() }\n")
            add("root.insert(pattern, parts, route, 0)\n")
            endControlFlow()
        })
        .build()
}

fun BuildingContext.buildInitRouterBlock(
    collectedMap: List<KSClassDeclaration>,
): CodeBlock {
    val map = collectedMap.associateWith { getRoutesFromAnnotation(it) }

    return buildCodeBlock {
        addStatement("println(\"Init Router Map\")")

        map.forEach { (clazz, routes) ->
            beginControlFlow(
                "insertRoute(%L) { params ->",
                routes.joinToString { "\"$it\"" }
            )
            buildRouterConstructorInject(clazz)
            endControlFlow()
        }
    }
}

fun BuildingContext.buildGetRouterFunc(): FunSpec {
    val mapType = ClassName.bestGuess(Constants.QUALIFIED_NAME_ROUTE_WITH_PARAMS)

    val codeBlock = CodeBlock.builder()
        .addStatement("val searchParts = baseRoute.split('/').filter { it.isNotEmpty() }")
        .addStatement(
            "return root.search(searchParts, 0)\n" +
                    "?.let { result -> result.first.route?.let { it to result.second } }\n" +
                    "?: throw IllegalArgumentException(%P)",
            "Route [\$baseRoute] Not Found."
        )
        .build()

    return FunSpec.builder("getRoute")
        .addParameter("baseRoute", type = String::class)
        .addModifiers(KModifier.OVERRIDE)
        .returns(mapType)
        .addCode(codeBlock)
        .build()
}

fun CodeBlock.Builder.buildRouterConstructorInject(clazz: KSClassDeclaration) {
    val parameters = clazz.primaryConstructor?.parameters
        ?: emptyList()

    val routes = getRoutesFromAnnotation(clazz)
    val dynamicParameterNames = getDynamicParams(routes)

    // 初始化参数
    parameters.forEach { parameter ->
        val parameterName = parameter.routeParamName // 参数的映射名称
        val parameterType = parameter.type.resolve() // 参数的类型
        val targetInjectType = parameterType.requireParameterizedClassName()
        val targetInjectName = parameter.name?.asString() ?: ""

        addStatement(
            "val %L = params.handleParams<%T>(name = %S, castable = %L)",
            "${targetInjectName}_$parameterName",
            targetInjectType,
            parameterName,
            parameterName in dynamicParameterNames
        )
    }

    // 检测校验参数
    parameters.forEach { parameter ->
        val paramAnnotation = parameter.requireAnnotation(Constants.QUALIFIED_NAME_PARAM)
        val parameterType = parameter.type.resolve() // 参数的类型
        val parameterName = parameter.routeParamName // 参数的映射名称
        val targetInjectName = parameter.name?.asString() ?: ""

        // 是否为可空类型
        val isNullable = parameterType.nullability != Nullability.NOT_NULL

        // 是否必须填写的参数，其次若没有默认值，则为必填
        val isRequired = paramAnnotation?.arguments
            ?.firstOrNull { it.name?.asString() == "required" }
            ?.value == true || !parameter.hasDefault

        val flags = mutableListOf<String>()
        flags.add("ParamState.CHECK_TYPE_FLAG")
        if (isRequired) flags.add("ParamState.CHECK_PROVIDED_FLAG")
        if (!isNullable) flags.add("ParamState.CHECK_IS_NOT_NULL_FLAG")
        val flagsCode = flags.joinToString(separator = " or ")

        addStatement("${targetInjectName}_${parameterName}.checkSelf(%L)", flagsCode)
    }

    // 获取所有必须提供值的Parameter
    val paramsMustBeProvided = parameters
        .filter { !it.hasDefault }

    // 获取所有可选参数的组合
    val combinations = parameters
        .filter { it.hasDefault }
        .combinations()
        .sortedByDescending { it.size }

    // 生成覆盖所有情况的when条件判断
    beginControlFlow("when")
    for (conditionParams in combinations) {
        val targetInjectParams = paramsMustBeProvided + conditionParams

        if (targetInjectParams.isEmpty()) {
            beginControlFlow("else ->")
            when (clazz.classKind) {
                ClassKind.CLASS -> addStatement("%T()", clazz.toClassName())
                ClassKind.OBJECT -> addStatement("%T", clazz.toClassName())
                else -> addStatement(
                    "throw IllegalArgumentException(%S)",
                    "Unsupported class kind: ${clazz.classKind}"
                )
            }
            endControlFlow()
            continue
        }

        val condition = conditionParams.takeIf { it.isNotEmpty() }?.run {
            joinToString(separator = " && ") {
                val parameterName = it.routeParamName
                val targetInjectName = it.name?.asString() ?: ""

                "${targetInjectName}_${parameterName} is ParamState.Provided<*>"
            }
        } ?: "else"

        beginControlFlow("$condition ->")
        val parameterCodeResult = targetInjectParams.joinToCode(separator = ",\n") {
            val parameterType = it.type.resolve()
            val parameterName = it.routeParamName
            val isNullable = parameterType.nullability != Nullability.NOT_NULL
            val targetInjectType = parameterType.requireParameterizedClassName()
            val targetInjectName = it.name?.asString() ?: ""

            var sentence = when {
                it in conditionParams -> "${it.name?.asString()} = ${targetInjectName}_${parameterName}.value as %T"
                else -> "${it.name?.asString()} = (${targetInjectName}_${parameterName} as ParamState.Provided<*>).value as %T"
            }
            if (isNullable) {
                sentence = sentence.replace(".value", "?.value")
                    .replace(" as ", " as? ")
            }

            buildCodeBlock { add(sentence, targetInjectType) }
        }
        addStatement("%T(%L)", clazz.toClassName(), parameterCodeResult)
        endControlFlow()
    }
    endControlFlow()
}

private val routeParamsNameCache = mutableMapOf<KSNode, String?>()
val KSValueParameter.routeParamName: String?
    get() = routeParamsNameCache.getOrPut(this) {
        val paramAnnotation = this
            .requireAnnotation(qualifiedName = Constants.QUALIFIED_NAME_PARAM)

        return paramAnnotation?.arguments
            ?.firstOrNull { it.name?.asString() == "name" }
            ?.let { it.value as? String }
            ?.takeIf(String::isNotBlank)
            ?: name?.asString()
    }

fun KSType.requireParameterizedClassName() = toClassName().let { typeName ->
    when {
        arguments.isNotEmpty() -> {
            typeName.parameterizedBy(
                arguments.mapNotNull { it.type?.resolve()?.toTypeName() }
            )
        }

        else -> typeName
    }
}