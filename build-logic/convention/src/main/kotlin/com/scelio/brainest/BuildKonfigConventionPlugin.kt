package com.scelio.brainest

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import com.scelio.brainest.convention.pathToPackageName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class BuildKonfigConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.codingfeline.buildkonfig")
            }

            extensions.configure<BuildKonfigExtension> {
                packageName = target.pathToPackageName()
                defaultConfigs {
                    val apiKey = gradleLocalProperties(rootDir, rootProject.providers)
                        .getProperty("API_KEY")
                        ?: throw IllegalStateException(
                            "Missing API_KEY property in local.properties"
                        )
                    val deepinfraKey = gradleLocalProperties(rootDir, rootProject.providers)
                        .getProperty("DEEPINFRA_API_KEY")
                        ?: apiKey
                    val supabaseUrl = gradleLocalProperties(rootDir, rootProject.providers)
                        .getProperty("SUPABASE_URL")
                        ?: throw IllegalStateException(
                            "Missing SUPABASE_URL property in local.properties"
                        )
                    val supabaseKey = gradleLocalProperties(rootDir, rootProject.providers)
                        .getProperty("SUPABASE_KEY")
                        ?: throw IllegalStateException(
                            "Missing SUPABASE_KEY property in local.properties"
                        )
                    buildConfigField(FieldSpec.Type.STRING, "API_KEY", apiKey)
                    buildConfigField(FieldSpec.Type.STRING, "DEEPINFRA_API_KEY", deepinfraKey)
                    buildConfigField(FieldSpec.Type.STRING, "SUPABASE_URL", supabaseUrl)
                    buildConfigField(FieldSpec.Type.STRING, "SUPABASE_KEY", supabaseKey)
                }
            }
        }
    }
}
