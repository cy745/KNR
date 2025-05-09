import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.vanniktech.publish)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    // 用于测试ksp处理器
    testImplementation("dev.zacsweers.kctfork:ksp:0.5.1")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation(libs.junit)

    implementation(libs.ksp.api)
    implementation("com.squareup:kotlinpoet:1.18.1")
    implementation("com.squareup:kotlinpoet-ksp:1.18.1")
}

group = libs.versions.group.get()
version = libs.versions.version.get()

mavenPublishing {
    coordinates(
        groupId = group.toString(),
        artifactId = "compiler",
        version = version.toString()
    )

    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Javadoc(),
            sourcesJar = true,
        )
    )

    pom {
        name = "KNR Compiler"
        description = "compiler module of KNR"
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
