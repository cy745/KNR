package com.lalilu.knr.core.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Param(
    val name: String = "",
    val remark: String = "",
    val required: Boolean = false
)