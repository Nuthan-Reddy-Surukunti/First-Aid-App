import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
}

android {
    namespace = "com.example.firstaidapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.firstaidapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Load API key from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        
        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties.getProperty("GEMINI_API_KEY", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Lifecycle components
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Navigation Component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.ai)
    ksp(libs.androidx.room.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Gson for JSON parsing
    implementation(libs.gson)

    // RecyclerView
    implementation(libs.androidx.recyclerview)

    // CardView
    implementation(libs.androidx.cardview)

    // ViewPager2
    implementation(libs.androidx.viewpager2)

    // FlexboxLayout
    implementation(libs.flexbox)

    // Firebase AI Logic
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation(libs.firebase.ai)

    // Google AI Client for Gemini
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

    // Force Ktor 2.3.12 to match Google AI Client expectations and avoid conflicts
    val ktorVersion = "2.3.12"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    // Add missing Ktor dependencies that the AI client needs
    implementation("io.ktor:ktor-utils:$ktorVersion")
    implementation("io.ktor:ktor-http:$ktorVersion")
    implementation("io.ktor:ktor-io:$ktorVersion")
    implementation("io.ktor:ktor-events:$ktorVersion")

    // Force all Ktor dependencies to use the same version to avoid conflicts
    configurations.all {
        resolutionStrategy {
            force("io.ktor:ktor-client-core:$ktorVersion")
            force("io.ktor:ktor-client-core-jvm:$ktorVersion")
            force("io.ktor:ktor-client-okhttp:$ktorVersion")
            force("io.ktor:ktor-client-content-negotiation:$ktorVersion")
            force("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            force("io.ktor:ktor-client-logging:$ktorVersion")
            force("io.ktor:ktor-utils:$ktorVersion")
            force("io.ktor:ktor-http:$ktorVersion")
            force("io.ktor:ktor-io:$ktorVersion")
            force("io.ktor:ktor-events:$ktorVersion")
            force("io.ktor:ktor-websockets:$ktorVersion")
            force("io.ktor:ktor-serialization:$ktorVersion")
        }
    }

    // Kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")


    // Additional dependencies for audio handling
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)


    // Voice Assistant Dependencies (speech recognition and animations)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.lottie)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.room.testing)
}

