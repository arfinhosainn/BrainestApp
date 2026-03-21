rootProject.name = "Brainest"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}


include(":composeApp")

include(":core:designsystem")
include(":core:domain")
include(":core:data")
include(":core:presentation")

include(":feature:auth:presentation")
include(":feature:auth:domain")
include(":feature:chat:presentation")
include(":feature:chat:domain")
include(":feature:chat:data")
include(":feature:chat:database")
include(":feature:onboarding:presentation")
include(":feature:onboarding:domain")
include(":feature:onboarding:data")
include(":feature:onboarding:database")

include(":feature:scan:domain")
include(":feature:scan:presentation")
include(":feature:scan:database")
include(":feature:scan:data")
include(":feature:flashcards")
include(":feature:flashcards:data")
include(":feature:flashcards:database")
include(":feature:flashcards:domain")
include(":feature:flashcards:presentation")

