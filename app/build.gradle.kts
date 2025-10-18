import ru.topbun.buildSrc.PropertiesLoader

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.youlovehamit.app"
    compileSdk = 36

    flavorDimensions += "mods"

    productFlavors {
        PropertiesLoader.availableMods().forEach { mod ->
            val config = PropertiesLoader.loadModConfig(mod)
            create(mod) {
                dimension = "mods"
                applicationId = config.applicationId
                buildConfigField("String", "METRIC_KEY", "\"${config.metricKey}\"")
                buildConfigField("String", "APPLOVIN_SDK_KEY", "\"${config.applovinSdkKey}\"")
                buildConfigField("Integer", "APP_ID", config.appId)
                buildConfigField("Integer", "PRIMARY_COLOR", config.primaryColor)
                buildConfigField("Integer", "PERCENT_SHOW_NATIVE_AD", config.percentShowNativeAd)
                buildConfigField("Integer", "PERCENT_SHOW_INTER_AD", config.percentShowInterAd)
            }
        }
    }

    defaultConfig {

        val versionCode = property("versionCode")?.toString() ?: error("Not found versionCode properties")
        val versionName = property("versionName")?.toString() ?: error("Not found versionName properties")

        this.versionCode = versionCode.toInt()
        this.versionName = versionName

        minSdk = 24
        targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
}

dependencies {

    implementation(libs.voyager.navigator)
    implementation(libs.play.services.ads)

    // Ads
    implementation (libs.mobileads.yandex)
    implementation(libs.applovin.sdk)
    implementation(libs.analytics)
    implementation(libs.google.adapter)
    implementation(libs.facebook.adapter)
    implementation(libs.mintegral.adapter)

    // Koin
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    // Push Notifications
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(project(":features:main"))
    implementation(project(":features:splash"))
    implementation(project(":features:detail_mod"))
    implementation(project(":features:favorite"))
    implementation(project(":features:apps"))
    implementation(project(":features:instruction"))
    implementation(project(":features:tabs"))
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(project(":navigation"))
    implementation(project(":core:ui"))
    implementation(project(":core:android"))

}