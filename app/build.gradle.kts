import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.navigation.safe.args)
}


val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("key.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.digitalwallet.mobilecards"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.digitalwallet.mobilecards"
        minSdk = 21
        targetSdk = 35
        versionCode = 125
        versionName = "2.5.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }

    ksp {
        arg("KOIN_CONFIG_CHECK", "true")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"].toString()
            keyPassword = keystoreProperties["keyPassword"].toString()
            storeFile = file(keystoreProperties["storeFile"].toString())
            storePassword = keystoreProperties["storePassword"].toString()
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            applicationIdSuffix = ".dev.debug"
        }


    }

    flavorDimensions.add("app")
    productFlavors {
        create("prod") {
            dimension = "app"
        }
        create("dev") {
            dimension = "app"
        }
    }

}

dependencies {
    implementation ("com.google.android.material:material:1.9.0")
  //  implementation project(path: ':myf')
    val billing_version = "7.1.1"
    implementation("com.android.billingclient:billing:$billing_version")
    implementation(libs.kotlin.reflect)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.zxing.android.embedded)
    implementation(libs.flexbox)
    implementation(libs.androidx.viewpager2)
    implementation(libs.viewpagerdotsindicator)
    implementation(libs.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    implementation(libs.androidx.biometric.ktx)
    implementation(libs.timber)
    implementation(libs.androidx.security.crypto)
    ksp(libs.koin.ksp.compiler)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.glide)
    //implementation("com.example.my_flutter_module:flutter_debug:1.0")
    // profileImplementation 'com.example.my_flutter_module:flutter_profile:1.0'
   // add("profileImplementation", "com.example.flutter_module:flutter_profile:1.0")
}

//configurations {
//    getByName("profileImplementation") {
//    }
//}
