import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

group = "com.lalilu.knr.core"
version = "1.0.0"

kotlin {
    jvmToolchain(17)

    androidTarget {
//        publishLibraryVariants("release")
    }
    jvm()

//    js { browser() }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {

    }


    sourceSets {
        commonMain.dependencies {
            api(libs.navigation.compose)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }

    //https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations["main"].compileTaskProvider.configure {
            compilerOptions {
                freeCompilerArgs.add("-Xexport-kdoc")
            }
        }
    }

}

android {
    namespace = "com.lalilu.knr.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }
}
