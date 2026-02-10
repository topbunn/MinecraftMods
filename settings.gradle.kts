pluginManagement {
    repositories {
        google()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
        maven("https://artifactory-external.vkpartner.ru/artifactory/maven")
        maven("https://artifact.bytedance.com/repository/pangle")
    }
}

rootProject.name = "Minecraft Mods"
include(":app")
include(":domain")
include(":core")
include(":core:ui")
include(":core:android")
include(":data")
include(":features")
include(":navigation")
include(":features:main")
include(":features:splash")
include(":features:detail_mod")
include(":features:favorite")
include(":features:apps")
include(":features:instruction")
include(":features:tabs")

include(":features:fullscreen_ad")
