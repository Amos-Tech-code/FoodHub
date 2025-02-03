plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.amos_tech_code.foodhub"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.amos_tech_code.foodhub"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //SplashScreen
    implementation(libs.core.splashscreen)
    //System UI Controller
    implementation(libs.accompanist.systemuicontroller)
    //Dagger-Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    //Logging
    implementation(libs.logging.interceptor)
    //Lifecycle
    implementation(libs.lifecycle.runtime.compose)
    //Navigation
    implementation(libs.androidx.navigation.compose)
    //Serialization
    implementation(libs.kotlinx.serialization.json)
    //Google
    implementation ("androidx.credentials:credentials:1.3.0")
    implementation ("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    //Facebook
    implementation ("com.facebook.android:facebook-login:17.0.0")
    //Coil
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")
    // Compose Foundation
    implementation("androidx.compose.foundation:foundation:1.7.7")
    // Compose Animation
    implementation("androidx.compose.animation:animation:1.7.7")


}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}