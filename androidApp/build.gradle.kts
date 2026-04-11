plugins {
    alias(libs.plugins.convention.android.application.compose)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.scelio.brainest.android"
}

dependencies {
    implementation(projects.composeApp)
    implementation(libs.androidx.activity.compose)
    implementation(libs.core.splashscreen)
    implementation(libs.koin.android)
}
