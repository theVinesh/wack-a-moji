import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

private val githubRunNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1
private val androidReleaseSigningEnvVars = listOf(
    "ANDROID_RELEASE_KEYSTORE_PATH",
    "ANDROID_RELEASE_KEYSTORE_PASSWORD",
    "ANDROID_RELEASE_KEY_ALIAS",
    "ANDROID_RELEASE_KEY_PASSWORD"
)
private val hasAndroidReleaseSigning = androidReleaseSigningEnvVars.all { !System.getenv(it).isNullOrBlank() }
private val hasAnyAndroidReleaseSigning = androidReleaseSigningEnvVars.any { !System.getenv(it).isNullOrBlank() }

check(!hasAnyAndroidReleaseSigning || hasAndroidReleaseSigning) {
    "Set all Android release signing env vars or none of them: ${androidReleaseSigningEnvVars.joinToString()}"
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.junit)
            implementation(libs.androidx.testExt.junit)
            implementation(libs.androidx.espresso.core)
            implementation("androidx.compose.ui:ui-test-junit4:1.6.8")
            implementation("tools.fastlane:screengrab:2.1.1")
        }
    }
}

android {
    namespace = "com.thevinesh.wackamoji"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.thevinesh.wackamoji"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        versionCode = githubRunNumber
        versionName = "1.0.$githubRunNumber"
    }

    signingConfigs {
        if (hasAndroidReleaseSigning) {
            create("release") {
                storeFile = file(System.getenv("ANDROID_RELEASE_KEYSTORE_PATH")!!)
                storePassword = System.getenv("ANDROID_RELEASE_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("ANDROID_RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("ANDROID_RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = if (hasAndroidReleaseSigning) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.8")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")
}
