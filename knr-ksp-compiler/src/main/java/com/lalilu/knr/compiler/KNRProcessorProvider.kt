package com.lalilu.knr.compiler

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class KNRProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val processorType = environment.options["knrType"]

        // todo 需要讨论一下如果没有指定ksp处理器的类型，是直接按照收集的来执行？
        // 还是抛出异常让用户确认是否把ksp处理器加到了不必要处理的模块上
        // if (processorType == null) {
        //     environment.logger.warn("kRouterType is null, please set kRouterType.")
        // }

        return when (processorType) {
            "inject" -> KNRInjectProcessor(environment)
            "collect" -> KNRCollectProcessor(environment)
            else -> KNRCollectProcessor(environment)
        }
    }
}