plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.convention.buildkonfig)


}

kotlin {
    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)

                implementation(projects.core.domain)

                implementation(libs.bundles.ktor.common)
                implementation(libs.ktor.client.core)
                implementation(libs.touchlab.kermit)

                implementation(libs.supabase.postgrest.kt)

                implementation(libs.koin.core)

                implementation(libs.kotlinx.datetime)

                implementation(libs.kotlinx.serialization.json)

                implementation(libs.supabase.auth.kt) // Add this line
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.kotlinx.datetime) // ADD THIS





            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)


            }
        }
    }

}