import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Load local.properties for secure credential storage
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.entrupy.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.entrupy.sample"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Partner Backend Configuration
        // These values are loaded from local.properties (gitignored) for security.
        // A sample backend URL is provided as default so the app compiles out of the box.
        // Developers should configure their own backend URL and credentials in local.properties.
        
        // Sample backend URL - replace with your own partner backend
        buildConfigField("String", "PARTNER_BACKEND_URL", "\"${localProperties.getProperty("partner.backend.url", "https://sample-partner-sdk-server.entrupy.com/")}\"")
        
        // Partner credentials - if not set, user will be prompted in the app UI
        buildConfigField("String", "PARTNER_USERNAME", "\"${localProperties.getProperty("partner.username", "")}\"")
        buildConfigField("String", "PARTNER_PASSWORD", "\"${localProperties.getProperty("partner.password", "")}\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Entrupy SDK
    implementation(libs.entrupy.sdk)

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Networking (for partner backend API calls)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
}
