package ru.topbun.domain.entity.mod

enum class ModType {

    WORLD,
    ADDON,
    TEXTURE_PACK,
    SKIN_PACK;

    fun toExtension(): String = when (this) {
        ADDON -> ".mcaddon"
        WORLD -> ".mcworld"
        TEXTURE_PACK -> ".mcpack"
        SKIN_PACK -> ".mcpack"
    }
}