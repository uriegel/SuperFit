plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "de.uriegel.superfit"
    compileSdk = 34

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
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation(platform("androidx.compose:compose-bom:2024.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("org.mapsforge:mapsforge-core:0.16.0")
    implementation("org.mapsforge:mapsforge-map:0.16.0")
    implementation("org.mapsforge:mapsforge-map-reader:0.16.0")
    implementation("org.mapsforge:mapsforge-themes:0.16.0")
    implementation("org.mapsforge:mapsforge-map-android:0.16.0")
    implementation("com.google.accompanist:accompanist-permissions:0.28.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("com.github.JamalMulla:ComposePrefs3:1.0.4")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.4")
    ksp("androidx.room:room-compiler:2.6.1")
}