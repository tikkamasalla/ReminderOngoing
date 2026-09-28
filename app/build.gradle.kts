plugins {
    id("com.android.application")
}

android {
    namespace = "com.tikkamasalla.reminderongoing"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.tikkamasalla.reminderongoing"
        minSdk = 29
        targetSdk = 34
        versionCode = 5
        versionName = "1.4"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    compileOnly("de.robv.android.xposed:api:82")
}
