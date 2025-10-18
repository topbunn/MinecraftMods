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

        fun getOrThrow(key: String) =
            props.getProperty(key) ?: throw IllegalArgumentException("Missing '$key' in $modName.properties")

        return ModConfig(
            applovinSdkKey = getOrThrow("applovin_sdk_key"),
            metricKey = getOrThrow("metric_key"),
            applicationId = getOrThrow("applicationId"),
            appId = getOrThrow("app_id"),
            primaryColor = getOrThrow("primary_color"),
            percentShowNativeAd = getOrThrow("percent_show_native_ad"),
            percentShowInterAd = getOrThrow("percent_show_inter_ad"),
        )
    }
}