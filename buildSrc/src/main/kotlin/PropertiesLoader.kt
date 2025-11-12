package ru.topbun.buildSrc

import java.io.File
import java.util.*

object PropertiesLoader {

    private val configDir = File("properties")

    fun availableMods(): List<String> =
        configDir.listFiles { f -> f.extension == "properties" }
            ?.map { it.nameWithoutExtension }
            ?.sorted()
            ?: emptyList()

    fun loadModConfig(modName: String): ModConfig {
        val file = configDir.resolve("$modName.properties")
        require(file.exists()) { "Properties file for mod '$modName' not found at ${file.path}" }
        val props = Properties().apply { load(file.inputStream()) }

        val gradlePropertiesFile = File("gradle.properties")
        val gradleProperties = Properties().apply { load(gradlePropertiesFile.inputStream()) }

        fun getOrThrow(props: Properties, key: String) =
            props.getProperty(key) ?: throw IllegalArgumentException("Missing '$key' in $modName.properties")


        return ModConfig(
            applovinSdkKey = getOrThrow(props, "applovin_sdk_key"),
            metricKey = getOrThrow(props, "metric_key"),
            applicationId = getOrThrow(props, "applicationId"),
            appId = getOrThrow(props, "app_id"),
            primaryColor = getOrThrow(props, "primary_color"),
            percentShowNativeAd = getOrThrow(props, "percent_show_native_ad"),
            percentShowInterAd = getOrThrow(props, "percent_show_inter_ad"),
            forRuStore = getOrThrow(gradleProperties, "rustore")
        )
    }
}