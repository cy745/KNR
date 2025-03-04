plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.build.config) apply false
    alias(libs.plugins.vanniktech.publish) apply false
}
