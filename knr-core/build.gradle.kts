import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl


plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.publish)
}

group = "com.lalilu.knr.core"
version = "1.0.0"

kotlin {
    androidTarget()
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    js {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
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

group = libs.versions.group.get()
version = libs.versions.version.get()

mavenPublishing {
    coordinates(
        groupId = group.toString(),
        artifactId = "core",
        version = version.toString()
    )

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true,
        )
    )

    pom {
        name = "KNR Core"
        description = "core module of KNR"
        inceptionYear = "2024"
        url = "https://github.com/cy745/knr/"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        developers {
            developer {
                id = "cy745"
                name = "cy745"
                url = "https://github.com/cy745/"
            }
        }

        scm {
            url = "https://github.com/cy745/knr/"
            connection = "scm:git:git://github.com/cy745/knr.git"
            developerConnection = "scm:git:ssh://git@github.com/cy745/knr.git"
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
//    signAllPublications()
}