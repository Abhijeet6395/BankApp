plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt") // Ensure this is for Kotlin annotation processing
    id("com.google.dagger.hilt.android") // Hilt Android plugin
    id("dagger.hilt.android.plugin") // Dagger Hilt plugin
}

android {
    namespace = "com.example.bankapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bankapplication"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Use compatible Compose version
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val room_version = "2.6.1" // Room version

    // Core dependencies
    implementation(libs.androidx.core.ktx.v190)
    implementation(libs.androidx.lifecycle.runtime.ktx.v262)
    implementation(libs.androidx.activity.compose.v181)

    // Compose BOM (Bill of Materials)
    implementation(platform(libs.androidx.compose.bom.v20240901))
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.ui.ui.graphics)
    implementation(libs.androidx.compose.ui.ui.tooling.preview)
    implementation(libs.androidx.compose.material3.material3)

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom.v20230300))
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit4)

    // Debug dependencies
    debugImplementation(libs.androidx.compose.ui.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest)

    // Room dependencies (Remove annotationProcessor)
    implementation(libs.androidx.room.runtime)
    kapt("androidx.room:room-compiler:$room_version") // Use kapt for Room
    implementation(libs.androidx.room.ktx)

    // Hilt dependencies
    implementation(libs.hilt.android) // Latest Hilt
    kapt(libs.hilt.android.compiler)  // Matching Hilt compiler

    // Hilt Navigation for Compose
    implementation(libs.androidx.hilt.navigation.compose)

    // Lifecycle dependencies
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v262)

    // Navigation dependencies (Updated)
    implementation(libs.androidx.navigation.compose.v280) // Update navigation-compose
}
