plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.echovision"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.echovision"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.mlkit.face.detection)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)





//    implementation("com.android.volley:volley:1.2.1")
//    implementation("com.squareup.picasso:picasso:2.8")
//
//    //fingerprint
//    implementation("androidx.biometric:biometric:1.2.0-alpha05")
//
//    // CameraX
//
//
//
//
//
//
//    implementation ("androidx.camera:camera-camera2:1.3.0")
//    implementation ("androidx.camera:camera-lifecycle:1.3.0")
//    implementation ("androidx.camera:camera-view:1.3.0")
//    implementation("androidx.core:core-ktx:1.12.0")
//
////Add ML Kit OCR Dependency
//    implementation("com.google.mlkit:text-recognition:16.0.0")
//
////    implementation("com.google.mlkit:text-recognition-latin:16.0.0")
//
//
//    implementation("androidx.camera:camera-core:1.3.0")
//
//
//
//    //mapp
//
//
//
//        implementation ("com.google.android.gms:play-services-maps:18.2.0")
//        implementation ("com.google.android.gms:play-services-location:21.0.1")
//
//    implementation("com.google.mlkit:translate:17.0.2")
//    implementation ("com.google.android.exoplayer:exoplayer-core:2.18.2")
//    implementation ("com.google.android.exoplayer:exoplayer-ui:2.18.2")
//
//
//
//    // Glide for image loading
//        implementation ("com.github.bumptech.glide:glide:4.15.1")
//    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
//
//// ML Kit Face Detection
//    implementation ("com.google.mlkit:face-detection:17.1.2")
//
//
//    implementation("com.google.mlkit:face-detection:17.1.2")





    //try



        // Networking
        implementation ("com.android.volley:volley:1.2.1")
        implementation ("com.squareup.picasso:picasso:2.8")

        // Fingerprint / Biometric
        implementation ("androidx.biometric:biometric:1.2.0-alpha05")

        // CameraX
        implementation ("androidx.camera:camera-core:1.3.0")
        implementation ("androidx.camera:camera-camera2:1.3.0")
        implementation ("androidx.camera:camera-lifecycle:1.3.0")
        implementation ("androidx.camera:camera-view:1.3.0")

        // AndroidX Core
        implementation ("androidx.core:core-ktx:1.12.0")

    // ML Kit Face Detection
    implementation("com.google.mlkit:face-detection:16.1.5")

    // ML Kit Text Recognition
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // ML Kit Translate
    implementation("com.google.mlkit:translate:17.0.2")



        // Google Maps & Location
        implementation ("com.google.android.gms:play-services-maps:18.2.0")
        implementation ("com.google.android.gms:play-services-location:21.0.1")

        // ExoPlayer
        implementation ("com.google.android.exoplayer:exoplayer-core:2.18.2")
        implementation ("com.google.android.exoplayer:exoplayer-ui:2.18.2")

        // Image Loading
        implementation ("com.github.bumptech.glide:glide:4.15.1")
        annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")


// TensorFlow Lite
    implementation("org.tensorflow:tensorflow-lite:2.13.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.3")






}