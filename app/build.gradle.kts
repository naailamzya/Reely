plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.reely"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.reely"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // TMDB API Key
        buildConfigField("String", "TMDB_API_KEY", "\"eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0MzU3YTY3ZGIzOTVhZmY2NDZmMWE5MmQ2YTVhMTk3ZiIsIm5iZiI6MTc2NzY2NDgxOS4yMjU5OTk4LCJzdWIiOiI2OTVjNmNiMzhjODMwNTJjYWI2NGI4YzQiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.HUAWbUKgyIve9Q411SY5BABWrs8a3Ioeb--WmqcAM_A\"")
        buildConfigField("String", "TMDB_BASE_URL", "\"https://api.themoviedb.org/3/\"")
        buildConfigField("String", "TMDB_IMAGE_BASE_URL", "\"https://image.tmdb.org/t/p/\"")
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    implementation(libs.swiperefresh)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)

    implementation(libs.shimmer)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}