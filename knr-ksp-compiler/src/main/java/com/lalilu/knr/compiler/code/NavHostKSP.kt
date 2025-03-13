package com.lalilu.knr.compiler.code

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.lalilu.knr.compiler.BuildingContext
import com.lalilu.knr.compiler.ext.getStartDestination
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toClassName

fun BuildingContext.buildNavHostFunc(
    collectedMap: List<KSClassDeclaration>,
): FunSpec {
    val startDestination = getStartDestination(collectedMap)

    return FunSpec.builder("NavHostBind")
        .addAnnotation(ClassName.bestGuess("androidx.compose.runtime.Composable"))
        .addParameters(buildNavHostParameters(startDestination))
        .addCode(buildNavHostCodeBlock(collectedMap))
        .build()
}

private fun BuildingContext.buildNavHostComposableBind(
    builder: CodeBlock.Builder,
    collectMap: List<KSClassDeclaration>
) = builder.apply {
    for (destination in collectMap) {
        add("\n")
        beginControlFlow(
            "%M<%T>{ backStackEntry ->",
            MemberName("androidx.navigation.compose", "composable"),
            destination.toClassName()
        )
        add(
            "backStackEntry.%M<%T>().Content(backStackEntry.savedStateHandle)",
            MemberName("androidx.navigation", "toRoute"),
            destination.toClassName(),
        )
        endControlFlow()
    }
}

private fun BuildingContext.buildNavHostCodeBlock(
    collectMap: List<KSClassDeclaration>
): CodeBlock {
    return CodeBlock.builder()
        .addStatement(
            "%T(modifier = modifier, startDestination = startDestination, navController = navController, builder = %L)",
            ClassName.bestGuess("androidx.navigation.compose.NavHost"),
            CodeBlock.builder()
                .beginControlFlow("{")
                .add("builder()")
                .let { buildNavHostComposableBind(it, collectMap) }
                .endControlFlow()
                .build()
        )
        .build()
}

private fun buildNavHostParameters(
    startDestination: KSClassDeclaration? = null
): List<ParameterSpec> {
    return listOf(
        // modifier: Modifier = Modifier,
        ParameterSpec.builder(
            "modifier",
            ClassName.bestGuess("androidx.compose.ui.Modifier")
        ).defaultValue("%L", "Modifier")
            .build(),

        // navController: NavHostController = rememberNavController(),
        ParameterSpec.builder(
            "navController",
            ClassName.bestGuess("androidx.navigation.NavHostController")
        ).defaultValue("%M()", MemberName("androidx.navigation.compose", "rememberNavController"))
            .build(),

        // startDestination: Any
        ParameterSpec.builder(
            "startDestination",
            ClassName.bestGuess("kotlin.Any")
        ).also {
            if (startDestination != null) {
                it.defaultValue("%L", startDestination)
            }
        }.build(),

        // builder: NavGraphBuilder.() -> Unit = {}
        ParameterSpec.builder(
            "builder",
            LambdaTypeName.get(
                receiver = ClassName.bestGuess("androidx.navigation.NavGraphBuilder"),
                returnType = ClassName("kotlin", "Unit")
            )
        ).defaultValue("{}")
            .build()
    )
}