plugins {
    kotlin("jvm")
    kotlin("kapt")
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
    alias(libs.plugins.vanniktech.publish)
}

val libGroup = "com.anylocale"
val libName = "sdk-compiler-plugin"

group = libGroup
version = libVersion

dokka {
    moduleName.set("Compiler Plugin")
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(file("src"))
            remoteUrl("https://github.com/anylocale/anylocale-android/tree/master/compiler-plugin/src")
        }
    }
}

dependencies {
    compileOnly(libs.auto.service)
    kapt(libs.auto.service)

    compileOnly(libs.kotlin.compiler.embeddable)
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

        description.set("Kotlin compiler plugin for the anylocale SDK")
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