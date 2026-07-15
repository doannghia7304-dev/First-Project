plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    id("kotlin-parcelize")
    id("kotlin-android")
}

android {
    namespace = "pion.tech.pionbase"
    compileSdk =
        libs.versions.compileSdkVersion
            .get()
            .toInt()
    defaultConfig {
        applicationId = "co.piontech.pionbase"
        minSdk =
            libs.versions.minSdkVersion
                .get()
                .toInt()
        targetSdk =
            libs.versions.targetSdkVersion
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0.0-debug"

        base.archivesName.set("pionbase_$versionName")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://api.piontech.site/stores/\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://api.piontech.site/stores/\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            ndk {
                abiFilters += listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    lint {
        // Disable problematic lint detectors that cause crashes
        disable += "NullSafeMutableLiveData"
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

kotlin {
    jvmToolchain(19)
}

dependencies {

    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Crash recovery
    implementation(libs.lib.recovery)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Koin
    implementation(libs.koin.android)

    // Glide
    api(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.config.ktx)

    // Nav component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Retrofit2
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Okhttp3
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp3.okhttp)
    implementation(libs.okhttp3.logging.interceptor)

    // Material dialog
    implementation(libs.core)
    implementation(libs.lifecycle)
    implementation(libs.bottomsheets)

    // Auto dimen
    implementation(libs.autodimension)

    // Rounded Image View
    implementation(libs.roundedimageview)

    // Timber
    implementation(libs.timber)

    // Lottie
    implementation(libs.lottie)

    // Chucker
    debugImplementation(libs.chucker.library)
    //debugImplementation(libs.leakcanary)
    releaseImplementation(libs.chucker.library.no.op)

    // Room
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Data store
    implementation(libs.androidx.datastore.preferences)

    // Roundable layout
    implementation(libs.roundablelayout)
}
