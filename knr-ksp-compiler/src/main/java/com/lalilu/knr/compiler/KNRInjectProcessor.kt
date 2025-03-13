package com.lalilu.knr.compiler

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.lalilu.knr.compiler.code.buildGetRouterMapFunc
import com.lalilu.knr.compiler.code.buildHandleParamsFunction
import com.lalilu.knr.compiler.code.buildNavHostFunc
import com.lalilu.knr.compiler.code.buildParamStateClass
import com.lalilu.knr.compiler.ext.asClassDeclaration
import com.lalilu.knr.compiler.ext.requireAnnotation
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

/**
 * 真正实现路由注入的处理器，继承自 [KNRCollectProcessor]
 * 收集完所在模块后才会执行注入操作
 */
class KNRInjectProcessor(
    environment: SymbolProcessorEnvironment
) : KNRCollectProcessor(environment) {

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val resultList = super.process(resolver)

        // 若存在生成的文件，则说明收集到了路由信息，还会触发一次process，此时可跳过注入操作
        if (environment.codeGenerator.generatedFile.isNotEmpty()) {
            return resultList
        }

        val generatedItems = resolver.getDeclarationsFromPackage(GENERATED_SHARED_PACKAGE)
        val propertiesItems = generatedItems
            .mapNotNull { (it as? KSClassDeclaration)?.getDeclaredProperties() }
            .flatten()

        val collectedMap = propertiesItems
            .map { it.type.resolve().declaration.asClassDeclaration() }
            .filter { it.requireAnnotation(Constants.QUALIFIED_NAME_DESTINATION) != null }
            .toList()

        // 确保所有类都有 @Serializable 注解
        if (collectedMap.any { it.requireAnnotation("kotlinx.serialization.Serializable") == null }) {
            throw IllegalStateException("All Destination classes must have @Serializable annotation")
        }

        val buildingContext = object : BuildingContext {
            override fun getResolver(): Resolver = resolver
            override fun getEnvironment(): SymbolProcessorEnvironment = environment
        }

        writeToFile(buildingContext, environment.codeGenerator, collectedMap)

        return resultList
    }

    private fun writeToFile(
        buildingContext: BuildingContext,
        codeGenerator: CodeGenerator,
        collectedMap: List<KSClassDeclaration>
    ) {
        if (collectedMap.isEmpty()) return

        val className = "KNRInjectMap"
        val classSpec = TypeSpec.objectBuilder(className)
            .addKdoc(CLASS_KDOC)
            .addSuperinterface(ClassName.bestGuess(Constants.QUALIFIED_NAME_PROVIDER))
            .addFunction(buildingContext.buildNavHostFunc(collectedMap))
            .addFunction(buildGetRouterMapFunc(collectedMap))
            .addType(buildParamStateClass())
            .addFunction(buildHandleParamsFunction())
            .build()

        val fileSpec = FileSpec.builder(GENERATED_SHARED_PACKAGE, className)
            .addType(classSpec)
            .indent("    ")
            .build()

        // 将涉及到的类所涉及的文件作为依赖传入，方便增量编译
        val dependencies = collectedMap
            .mapNotNull { it.containingFile }
            .distinct()
            .toTypedArray()

        kotlin.runCatching {
            fileSpec.writeTo(
                codeGenerator = codeGenerator,
                dependencies = Dependencies(aggregating = true, *dependencies)
            )
        }
    }
}