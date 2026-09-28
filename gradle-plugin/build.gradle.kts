plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.dokka)
    alias(libs.plugins.serialization)
    alias(libs.plugins.vanniktech.publish)
    `maven-publish`
    signing
}

val libGroup = "com.anylocale"
val libName = "sdk-gradle-plugin"

group = libGroup
version = libVersion

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = libVersion
    }
}

dokka {
    moduleName.set("Gradle Plugin")
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(file("src"))
            remoteUrl("https://github.com/anylocale/anylocale-android/tree/master/gradle-plugin/src")
        }
    }
}

ktorfit {
    kotlinVersion.set("-")
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    testImplementation(kotlin("test"))
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.android.tools)

    implementation(libs.kommand)
    implementation(libs.ktor.okhttp)
    implementation(libs.ktorfit)
    implementation(libs.semver)
    implementation(libs.serialization)
    implementation(libs.serialization.json)
    implementation(libs.serialization.kaml)
    implementation(libs.tooling)
}

gradlePlugin {
    plugins {
        website.set("https://github.com/anylocale/anylocale-android")
        vcsUrl.set("https://github.com/anylocale/anylocale-android")

        create("anylocalePlugin") {
            id = "com.anylocale.sdk"
            implementationClass = "com.anylocale.AnylocalePlugin"
            displayName = "anylocale SDK Plugin"
            description = "Gradle plugin for the anylocale SDK"
        }
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates(
        groupId = libGroup,
        artifactId = libName,
        version = libVersion
    )

    pom {
        name.set(libName)

        description.set("Gradle plugin for the anylocale SDK")
        url.set("https://github.com/anylocale/anylocale-android")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        scm {
            url.set("https://github.com/anylocale/anylocale-android")
            connection.set("scm:git:git://github.com/anylocale/anylocale-android.git")
        }

        developers {
            developer {
                id.set("anylocale")
                name.set("anylocale")
                url.set("https://github.com/anylocale")
            }
        }
    }
}