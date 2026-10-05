plugins {

    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

android {
    namespace = "com.rogue.gymquest"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.rogue.gymquest"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

dependencies {


    implementation(libs.androidx.core)

    implementation(libs.activity.compose)


    // Compose

    implementation(platform(libs.compose.bom))

    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)


    // Koin

    implementation(libs.koin.android)

    implementation(libs.koin.compose)


    // Serialization (used for JSON export/import of local data)

    implementation(libs.kotlin.serialization)


    // Room

    implementation(libs.room.runtime)

    implementation(libs.room.ktx)

    ksp(libs.room.compiler)


    // WorkManager (rest-timer notifications while the app is backgrounded)
    implementation(libs.workmanager)

    // Navigation
    implementation(libs.navigation.compose)

    // DataStore (settings: theme, weight unit, language)
    implementation(libs.datastore.preferences)

    // Vico (evolution/volume charts)
    implementation(libs.vico.compose)

}
