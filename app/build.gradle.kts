plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")  // ← AGREGADO FIREBASE
}

android {
    namespace = "com.example.omg"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.omg"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Lottie
    implementation("com.airbnb.android:lottie:6.1.0")

    // Firebase BoM - Gestiona versiones automáticamente
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))

    // Firebase Authentication (SIN -ktx)
    implementation("com.google.firebase:firebase-auth")

    // Firebase Firestore (SIN -ktx)
    implementation("com.google.firebase:firebase-firestore")

    // Google Play Services para Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Firebase Storage para fotos
    implementation("com.google.firebase:firebase-storage")
    
    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")


    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Para cargar imágenes de perfil
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Glide para cargar imágenes
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Lottie
    implementation("com.airbnb.android:lottie:6.1.0")

    // CardView
    implementation("androidx.cardview:cardview:1.0.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}