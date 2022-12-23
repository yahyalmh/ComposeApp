plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")

}

android {
    namespace = "com.example.compose"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = "com.example.compose"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

        testInstrumentationRunner = AppConfig.androidTestInstrumentation
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Version.KOTLIN_COMPILER_EXTENSION_VERSION
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    androidxCore()
    lifecycle()
    compose()
    composeMaterial()
    composeNavigation()
    hilt()
    junit()
    espresso()
    composeTest()
    retrofit()

    moduleDependency(":data:datastore")
    implementation(project(":ui:home"))
    implementation(project(":ui:main"))
    implementation(project(":ui:detail"))
    implementation(project(":ui:search"))
    implementation(project(":ui:common"))
    implementation(project(":ui:setting"))
    implementation(project(":ui:favorite"))
}