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
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE}"
            pickFirsts += "/META-INF/{AL2.0,LGPL2.1,LICENSE*}"
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
    junit4()
    espresso()
    composeTest()
    retrofit()

    moduleDependency(":data:datastore")
    moduleDependency(":data:common")
    moduleDependency(":ui:home")
    moduleDependency(":ui:main")
    moduleDependency(":ui:detail")
    moduleDependency(":ui:search")
    moduleDependency(":ui:common")
    moduleDependency(":ui:setting")
    moduleDependency(":ui:favorite")
}