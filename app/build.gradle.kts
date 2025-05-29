plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "de.uriegel.superfit"
    compileSdk = 35

    signingConfigs {
        create("signing") {
            storeFile = file("/home/uwe/Dokumente/Entwicklung/AndroidKeyStore/keystore.jks")
            storePassword = extra["ANDROID_STORE_PASSWORD"].toString()
            keyAlias = "androidKey"
            keyPassword = extra["ANDROID_KEY_PASSWORD"].toString()
        }
    }

    defaultConfig {
        applicationId = "de.uriegel.superfit2"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
            signingConfig = signingConfigs.getByName("signing")
        }
        debug {
            signingConfig = signingConfigs.getByName("signing")
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
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.11"
//    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.preferences.ktx)
    implementation(libs.mapsforge.core)
    implementation(libs.mapsforge.map)
    implementation(libs.mapsforge.map.reader)
    implementation(libs.mapsforge.themes)
    implementation(libs.mapsforge.map.android)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.layout.compose)
    implementation(libs.composeprefs3)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.google.play.services.location)
    implementation(libs.androidx.compose.livedata)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.ksp)
}