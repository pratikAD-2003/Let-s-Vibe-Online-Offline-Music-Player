plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.musicplayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.musicplayer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.google.code.gson:gson:2.11.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    val sqlite_version = "2.3.1"
    // Java language implementation
    implementation("androidx.sqlite:sqlite:$sqlite_version")

    // Kotlin
    implementation("androidx.sqlite:sqlite-ktx:$sqlite_version")

    // Implementation of the AndroidX SQLite interfaces via the Android framework APIs.
    implementation("androidx.sqlite:sqlite-framework:$sqlite_version")
    implementation ("com.karumi:dexter:6.2.3")
    val lottieVersion = "3.4.0"
    implementation ("com.airbnb.android:lottie:$lottieVersion")
}