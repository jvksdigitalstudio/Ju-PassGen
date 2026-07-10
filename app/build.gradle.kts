import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val keystoreFile = rootProject.file("app/keystore.jks")
val localProps   = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}
fun env(key: String): String = System.getenv(key) ?: localProps.getProperty(key) ?: ""

android {
    namespace   = "com.ju.passgen"
    compileSdk  = 35

    defaultConfig {
        applicationId = "com.ju.passgen"
        minSdk        = 26
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        // AdMob App ID → manifest placeholder
        val admobAppId = env("ADMOB_APP_ID").ifEmpty {
            "ca-app-pub-3940256099942544~3347511713"   // Google Test App ID
        }
        manifestPlaceholders["admobAppId"] = admobAppId

        // AdMob Unit IDs → BuildConfig (accesibles desde Kotlin)
        val bannerTest        = "ca-app-pub-3940256099942544/6300978111"
        val interstitialTest  = "ca-app-pub-3940256099942544/1033173712"

        buildConfigField("String", "ADMOB_BANNER_ID",
            "\"${env("ADMOB_BANNER_ID").ifEmpty { bannerTest }}\"")
        buildConfigField("String", "ADMOB_INTERSTITIAL_ID",
            "\"${env("ADMOB_INTERSTITIAL_ID").ifEmpty { interstitialTest }}\"")
        buildConfigField("Boolean", "IS_DEBUG", "true")
    }

    buildFeatures {
        compose     = true
        buildConfig = true   // habilitar BuildConfig
    }

    signingConfigs {
        create("release") {
            if (keystoreFile.exists()) {
                storeFile     = keystoreFile
                storePassword = env("STORE_PASSWORD")
                keyAlias      = env("KEY_ALIAS")
                keyPassword   = env("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix   = "-debug"
            isDebuggable        = true
            isMinifyEnabled     = false
            buildConfigField("Boolean", "IS_DEBUG", "true")
        }
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (keystoreFile.exists()) signingConfig = signingConfigs.getByName("release")
            buildConfigField("Boolean", "IS_DEBUG", "false")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.viewmodel.compose)
    implementation(libs.androidx.datastore.prefs)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.play.services.ads)
    implementation(libs.commons.compress)
    implementation(libs.tukaani.xz)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.ext)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
