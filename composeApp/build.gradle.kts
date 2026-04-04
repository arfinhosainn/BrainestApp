
plugins {
    alias(libs.plugins.convention.cmp.application)


}

kotlin {

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.core.splashscreen)
            implementation(libs.koin.android)



        }
        commonMain.dependencies {

            implementation(projects.core.data)
            implementation(projects.core.domain)
            implementation(projects.core.designsystem)
            implementation(projects.core.presentation)

            implementation(projects.feature.auth.domain)
            implementation(projects.feature.auth.presentation)
            implementation(projects.feature.onboarding.data)
            implementation(projects.feature.onboarding.domain)
            implementation(projects.feature.onboarding.presentation)

            implementation(projects.feature.chat.data)
            implementation(projects.feature.chat.database)
            implementation(projects.feature.chat.domain)
            implementation(projects.feature.chat.presentation)
            implementation(projects.feature.study.data)
            implementation(projects.feature.study.domain)
            implementation(projects.feature.study.presentation)
            implementation(projects.feature.home.presentation)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.bundles.koin.common)
            implementation(libs.supabase.postgrest.kt)




            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.runtime)

            implementation(compose.materialIconsExtended)
        }

    }
}
