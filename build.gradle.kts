// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}

// Force Ktor version 2.3.12 across all subprojects to prevent conflicts
subprojects {
    afterEvaluate {
        configurations.all {
            resolutionStrategy {
                // Force all dependencies to use a compatible version of Ktor
                force("io.ktor:ktor-client-core-jvm:2.3.12")
                force("io.ktor:ktor-client-core:2.3.12")
                force("io.ktor:ktor-client-okhttp:2.3.12")
                force("io.ktor:ktor-client-content-negotiation:2.3.12")
                force("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
                force("io.ktor:ktor-client-logging:2.3.12")
                force("io.ktor:ktor-utils:2.3.12")
                force("io.ktor:ktor-http:2.3.12")
                force("io.ktor:ktor-io:2.3.12")
                force("io.ktor:ktor-events:2.3.12")
                force("io.ktor:ktor-websockets:2.3.12")
                force("io.ktor:ktor-serialization:2.3.12")
            }
        }
    }
}

