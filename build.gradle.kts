import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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

buildscript {
    if (properties["local.development.enabled"] == "true") {
        val testGroup = libs.versions.group.get()
        val testVersion = libs.versions.version.get()

        dependencies { classpath("$testGroup:plugin:$testVersion") }
    }
}

if (properties["local.development.enabled"] == "true") {
    ext { set("targetInjectProjectName", "composeApp") }
    apply(plugin = "${libs.versions.group.get()}.plugin")
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }
}