plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
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

    // Asegúrate de que esta sea la UNICA versión BOM definida
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.01"))

    // --- DEPENDENCIAS BÁSICAS Y ANDROIDX VIEWS ---
    implementation(libs.androidx.core.ktx)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    // Compatibilidad con ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // --- FIREBASE Y AUTENTICACIÓN ---
    // Usar la BOM (Bill of Materials) para gestionar todas las versiones de Firebase
    implementation(platform(libs.firebase.bom))

    // Dependencias específicas de Firebase
    implementation("com.google.firebase:firebase-firestore-ktx") // Firestore
    implementation("com.google.firebase:firebase-auth-ktx") // Autenticación (Google y Teléfono)
    implementation("com.google.android.gms:play-services-auth:20.7.0") // Google Sign In Services

    // Kotlin Extensions (Usando la sintaxis de comillas dobles que es compatible con KTS)
    implementation("androidx.core:core-ktx:1.9.0")


    // --- DEPENDENCIAS DE COMPOSE ---
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // --- OTRAS DEPENDENCIAS ---
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.airbnb.android:lottie-compose:6.4.0")

    // --- DEPENDENCIAS PARA TESTING ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
