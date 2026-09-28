import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.android)
//  alias(libs.plugins.anylocale) // uncomment to enable compiler plugin
}

android {
  namespace = "com.anylocale.demo.exampleandroid"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.anylocale.demo.exampleandroid"
    minSdk = 21
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
  }

  buildFeatures {
    buildConfig = true
  }

  buildTypes {
    release {
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_17)
  }
}

// Uncomment to change configuration of compiler plugin
//anylocale {
//  // change compile time behavior
//  compilerPlugin {
//    android {
//      // Replaces Context.getString occurrences with Context.getStringT (anylocale extension)
//      replaceGetString.set(true) // default true
//      replacePluralString.set(true) // default true
//    }
//  }
//}

dependencies {
  implementation(libs.android)
  implementation(libs.activity)

  implementation(libs.coroutines.android)
  implementation(project(":core"))
}