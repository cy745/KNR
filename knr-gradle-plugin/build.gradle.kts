import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.build.config)
    alias(libs.plugins.vanniktech.publish)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = libs.versions.group.get()
version = libs.versions.version.get()

buildConfig {
    packageName("com.lalilu.knr")

    buildConfigField("pluginGroup", group.toString())
    buildConfigField("pluginVersion", version.toString())
}

dependencies {
    implementation(gradleApi())
    implementation(libs.ksp.gradle)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin.api)
}

gradlePlugin {
    plugins {
        create("plugin") {
            id = "$group.plugin"
            displayName = "plugin"
            implementationClass = "com.lalilu.knr.plugin.RouterPlugin"
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = group.toString(),
        artifactId = "plugin",
        version = version.toString()
    )

    configure(
        GradlePlugin(
            javadocJar = JavadocJar.Javadoc(),
            sourcesJar = true,
        )
    )

    pom {
        name = "KNR Plugin"
        description = "plugin module of KNR"
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
