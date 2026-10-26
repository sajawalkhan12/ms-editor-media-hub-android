
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "2.0.21"
}

android {
    namespace = "com.mseditor.mediahub"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mseditor.mediahub"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
