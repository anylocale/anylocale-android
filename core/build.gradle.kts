import co.touchlab.skie.configuration.DefaultArgumentInterop
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    id("com.android.library")
    alias(libs.plugins.atomicfu)
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.native.cocoapods")
    alias(libs.plugins.dokka)
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.skie)
    alias(libs.plugins.vanniktech.publish)
    `maven-publish`
    signing
}

val libGroup = "com.anylocale"
val libName = "sdk"
val appleFramework = "KMPAnylocale"

// The libVersion should be defined, usually in gradle.properties or a parent build.gradle
// If it's missing, it might cause other issues. Assuming it's available.
val libVersion = libs.versions.library.get()

group = libGroup
version = libVersion

dokka {
    moduleName.set("Core")
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(file("src"))
            remoteUrl("https://github.com/anylocale/anylocale-android/tree/master/core/src")
        }
    }
}

skie {
    build {
        produceDistributableFramework()
        enableSwiftLibraryEvolution.set(true)
        noClangModuleBreadcrumbsInStaticFrameworks.set(true)
    }
    features {
        enableSwiftUIObservingPreview.set(true)
        enableFutureCombineExtensionPreview.set(true)
        enableFlowCombineConvertorPreview.set(true)

        group {
            DefaultArgumentInterop.Enabled(true)
        }
    }
    analytics {
        disableUpload.set(true)
    }
}

kotlin {
    androidTarget {
        publishAllLibraryVariants()
    }

    androidNativeX64()
    androidNativeX86()
    androidNativeArm64()
    androidNativeArm32()

    jvm()
    jvmToolchain(21)

    val xcf = XCFramework(appleFramework)
    cocoapods {
        name = appleFramework
        version = libVersion
        license = "Apache License 2.0"
        homepage = "https://github.com/anylocale/anylocale-android"
        summary = "Kotlin Multiplatform over-the-air localization client for anylocale"

        framework {
            baseName = appleFramework
        }
    }

    iosX64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    iosArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    iosSimulatorArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }

    tvosX64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    tvosArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    tvosSimulatorArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }

    watchosX64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    watchosArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    watchosArm32 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    watchosSimulatorArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }

    macosX64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }
    macosArm64 {
        binaries {
            framework {
                baseName = appleFramework
                xcf.add(this)
            }
        }
    }

    linuxX64()
    linuxArm64()

    mingwX64()

    js(IR) {
        nodejs()
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
        browser()
        binaries.executable()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }

        commonMain.dependencies {
            implementation(libs.immutable)
            implementation(libs.ktor)
            implementation(libs.serialization.json)
            implementation(libs.tooling)

            // Does not support androidNative and linuxArm64 yet (https://github.com/comahe-de/i18n4k/pull/75)
            api(libs.i18n4k)
            implementation(libs.i18n4k.plural)
            implementation(libs.datetime)
            implementation(libs.coroutines)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.android)

            implementation(libs.ktor.android)
            implementation(libs.coroutines.android)
        }

        androidNativeMain.dependencies {
            implementation(libs.ktor.cio)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.java)
        }

        appleMain.dependencies {
            implementation(libs.ktor.darwin)
        }

        linuxMain.dependencies {
            implementation(libs.ktor.curl)
        }

        mingwMain.dependencies {
            implementation(libs.ktor.winhttp)
        }

        jsMain.dependencies {
            implementation(libs.ktor.js)
        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.js)
        }
    }
}

android {
    compileSdk = 36
    namespace = "com.anylocale.sdk"

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    // JitPack and a plain publishToMavenLocal have no key; only a release
    // to Maven Central carries one, through the signingInMemoryKey property.
    if (providers.gradleProperty("signingInMemoryKey").isPresent) {
        signAllPublications()
    }

    coordinates(
        groupId = libGroup,
        artifactId = libName,
        version = libVersion
    )

    pom {
        name.set(libName)

        description.set("Kotlin Multiplatform over-the-air localization client for anylocale")
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
