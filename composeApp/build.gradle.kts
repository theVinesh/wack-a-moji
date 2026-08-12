import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.gradle.api.tasks.Copy

private val appVersionName = run {
    val versionFile = rootProject.file("version.txt")
    if (versionFile.exists()) versionFile.readText().trim() else "1.0.1"
}
// Monotonic versionCode shared across every workflow (GITHUB_RUN_NUMBER is per-workflow,
// so two pipelines would collide). Minutes since epoch: always increasing, fits Int for decades.
private val androidVersionCode = (System.currentTimeMillis() / 60_000L).toInt()
private val canonicalBackgroundMusicSource = "src/androidMain/res/raw/loop.mp3"
private val canonicalWackSoundEffectSource = "src/androidMain/res/raw/wack.mp3"
private val canonicalClickSoundEffectSource = "src/androidMain/res/raw/click.mp3"
private val generatedBackgroundMusicAndroidResDir = "generated/backgroundMusic/android/res"
private val generatedBackgroundMusicAndroidDir = "$generatedBackgroundMusicAndroidResDir/raw"
private val generatedBackgroundMusicIosDir = "generated/backgroundMusic/ios"
private val generatedBackgroundMusicWasmDir = "generated/backgroundMusic/wasmJs/resources"
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
        browser {
            commonWebpackConfig {
                // Honor Conductor workspace ports when set; otherwise keep webpack defaults.
                val conductorPort = System.getenv("CONDUCTOR_PORT")?.toIntOrNull()
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    open = false
                    if (conductorPort != null) {
                        port = conductorPort
                    }
                }
            }
        }
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
        val wasmJsMain by getting {
            resources.srcDir(layout.buildDirectory.dir(generatedBackgroundMusicWasmDir))
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

        versionCode = androidVersionCode
        versionName = appVersionName
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

    sourceSets["main"].res.apply {
        setSrcDirs(
            listOf(
                file("src/main/res"),
                layout.buildDirectory.dir(generatedBackgroundMusicAndroidResDir).get().asFile,
            )
        )
    }
}

dependencies {
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.8")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")
}

val copyCanonicalBackgroundMusicForAndroid by tasks.registering(Copy::class) {
    group = "audio"
    description = "Copies the canonical loop.mp3 into the generated Android raw resource directory."

    from(layout.projectDirectory.file(canonicalBackgroundMusicSource))
    into(layout.buildDirectory.dir(generatedBackgroundMusicAndroidDir))
}

val copyCanonicalWackSoundEffectForAndroid by tasks.registering(Copy::class) {
    group = "audio"
    description = "Copies the canonical wack.mp3 into the generated Android raw resource directory."

    from(layout.projectDirectory.file(canonicalWackSoundEffectSource))
    into(layout.buildDirectory.dir(generatedBackgroundMusicAndroidDir))
}

val copyCanonicalClickSoundEffectForAndroid by tasks.registering(Copy::class) {
    group = "audio"
    description = "Copies the canonical click.mp3 into the generated Android raw resource directory."

    from(layout.projectDirectory.file(canonicalClickSoundEffectSource))
    into(layout.buildDirectory.dir(generatedBackgroundMusicAndroidDir))
}

val copyAndroidResourcesForPackaging by tasks.registering(Copy::class) {
    group = "audio"
    description = "Copies Android packaging resources into the generated directory while sourcing loop.mp3, wack.mp3, and click.mp3 from canonical assets."

    dependsOn(
        copyCanonicalBackgroundMusicForAndroid,
        copyCanonicalWackSoundEffectForAndroid,
        copyCanonicalClickSoundEffectForAndroid,
    )
    from(layout.projectDirectory.dir("src/androidMain/res")) {
        exclude("raw/loop.mp3")
        exclude("raw/wack.mp3")
        exclude("raw/click.mp3")
    }
    into(layout.buildDirectory.dir(generatedBackgroundMusicAndroidResDir))
}

val copyCanonicalBackgroundMusicForIos by tasks.registering(Copy::class) {
    group = "audio"
    description = "Copies the canonical loop.mp3 into the generated iOS bundle-resource staging directory."

    from(layout.projectDirectory.file(canonicalBackgroundMusicSource))
    into(layout.buildDirectory.dir(generatedBackgroundMusicIosDir))
    rename { "background-music-loop.mp3" }
}

val copyCanonicalWackSoundEffectForIos by tasks.registering(Copy::class) {
    group = "audio"
    description = "Copies the canonical wack.mp3 into the generated iOS bundle-resource staging directory."

    from(layout.projectDirectory.file(canonicalWackSoundEffectSource))
    into(layout.buildDirectory.dir(generatedBackgroundMusicIosDir))
}

val copyCanonicalClickSoundEffectForIos by tasks.registering(Copy::class) {
    group = "audio"
    description = "Copies the canonical click.mp3 into the generated iOS bundle-resource staging directory."

    from(layout.projectDirectory.file(canonicalClickSoundEffectSource))
    into(layout.buildDirectory.dir(generatedBackgroundMusicIosDir))
}

val copyCanonicalBackgroundMusicForWasm by tasks.registering(Copy::class) {
    group = "audio"
    description = "Copies the canonical loop.mp3 into the generated wasm/web resource staging directory."

    from(layout.projectDirectory.file(canonicalBackgroundMusicSource))
    into(layout.buildDirectory.dir(generatedBackgroundMusicWasmDir))
    rename { "background-music-loop.mp3" }
}

val copyCanonicalWackSoundEffectForWasm by tasks.registering(Copy::class) {
    group = "audio"
    description = "Copies the canonical wack.mp3 into the generated wasm/web resource staging directory."

    from(layout.projectDirectory.file(canonicalWackSoundEffectSource))
    into(layout.buildDirectory.dir(generatedBackgroundMusicWasmDir))
}

val copyCanonicalClickSoundEffectForWasm by tasks.registering(Copy::class) {
    group = "audio"
    description = "Copies the canonical click.mp3 into the generated wasm/web resource staging directory."

    from(layout.projectDirectory.file(canonicalClickSoundEffectSource))
    into(layout.buildDirectory.dir(generatedBackgroundMusicWasmDir))
}

tasks.register("prepareBackgroundMusicBuildCopies") {
    group = "audio"
    description = "Stages generated background-music copies for Android, iOS, and wasm/web packaging from the canonical loop.mp3 source."

    dependsOn(
        copyCanonicalBackgroundMusicForAndroid,
        copyCanonicalBackgroundMusicForIos,
        copyCanonicalBackgroundMusicForWasm,
    )
}

tasks.register("prepareSoundEffectBuildCopies") {
    group = "audio"
    description = "Stages generated one-shot sound-effect copies for Android, iOS, and wasm/web packaging from the canonical wack.mp3 and click.mp3 sources."

    dependsOn(
        copyCanonicalWackSoundEffectForAndroid,
        copyCanonicalClickSoundEffectForAndroid,
        copyCanonicalWackSoundEffectForIos,
        copyCanonicalClickSoundEffectForIos,
        copyCanonicalWackSoundEffectForWasm,
        copyCanonicalClickSoundEffectForWasm,
    )
}

tasks.named("preBuild") {
    dependsOn(copyAndroidResourcesForPackaging)
}

tasks.matching { it.name.startsWith("wasmJs") && it.name.contains("ProcessResources") }.configureEach {
    dependsOn(
        copyCanonicalBackgroundMusicForWasm,
        copyCanonicalWackSoundEffectForWasm,
        copyCanonicalClickSoundEffectForWasm,
    )
}
