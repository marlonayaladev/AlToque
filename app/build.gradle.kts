

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.altoque"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.altoque"
        minSdk = 26
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
    // --- DEPENDENCIAS ESENCIALES PARA COMPOSE Y LA APP ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom)) // BOM para gestionar versiones de Compose

    // --- DEPENDENCIAS DE UI CON COMPOSE ---
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // Material 3 para Compose (recomendado)

    // --- DEPENDENCIAS PARA LA SPLASH SCREEN (LA CLAVE DEL PROBLEMA) ---
    // 1. La API de Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")
    // 2. La librería de Material Components (XML) que CONTIENE el tema "Theme.SplashScreen.Icon"
    implementation("com.google.android.material:material:1.12.0")

    // --- DEPENDENCIA PARA LA ANIMACIÓN LOTTIE ---
    implementation("com.airbnb.android:lottie-compose:6.4.0")

    // --- DEPENDENCIAS PARA TESTING (sin cambios) ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.airbnb.android:lottie:6.1.0")

    // --- DEPENDENCIAS REDUNDANTES O INNECESARIAS (las hemos quitado) ---
    // implementation(libs.androidx.appcompat) // No es estrictamente necesario para una app 100% Compose
    // implementation(libs.material) // Redundante, ya la declaramos arriba sin alias
    // implementation(libs.androidx.activity) // Redundante, activity-compose ya la incluye
    // implementation(libs.androidx.constraintlayout) // Para vistas XML, no para Compose

}