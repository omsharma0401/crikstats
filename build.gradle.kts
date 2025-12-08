// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.dynamic.feature) apply false

    // Hilt Gradle plugin
    id("com.google.dagger.hilt.android") version "2.57.2" apply false

    // Kotlin Symbol Processing Gradle plugin
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" apply false
}