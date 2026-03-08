import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.scelio.brainest.convention.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.androidx.room.gradle.plugin)
    implementation(libs.buildkonfig.gradlePlugin)
    implementation(libs.buildkonfig.compiler)

}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "com.scelio.brainest.convention.android.application"
            implementationClass = "com.scelio.brainest.AndroidApplicationConventionPlugin"
        }
        register("androidComposeApplication") {
            id = "com.scelio.brainest.convention.android.application.compose"
            implementationClass = "com.scelio.brainest.AndroidApplicationComposeConventionPlugin"
        }
        register("cmpApplication") {
            id = "com.scelio.brainest.convention.cmp.application"
            implementationClass = "com.scelio.brainest.CmpApplicationConventionPlugin"
        }
        register("kmpLibrary") {
            id = "com.scelio.brainest.convention.kmp.library"
            implementationClass = "com.scelio.brainest.KmpLibraryConventionPlugin"
        }
        register("cmpLibrary") {
            id = "com.scelio.brainest.convention.cmp.library"
            implementationClass = "com.scelio.brainest.CmpLibraryConventionPlugin"
        }
        register("cmpFeature") {
            id = "com.scelio.brainest.convention.cmp.feature"
            implementationClass = "com.scelio.brainest.CmpFeatureConventionPlugin"
        }
        register("buildKonfig") {
            id = "com.scelio.brainest.convention.buildkonfig"
            implementationClass = "com.scelio.brainest.BuildKonfigConventionPlugin"
        }
        register("room") {
            id = "com.scelio.brainest.convention.room"
            implementationClass = "com.scelio.brainest.RoomConventionPlugin"
        }
    }
}