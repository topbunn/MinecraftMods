package ru.topbun.domain.entity

interface KeyValueStorage {

    suspend fun save(key: StorageKeys, value: String)

    suspend fun get(key: StorageKeys, defaultValue: String?): String?

}