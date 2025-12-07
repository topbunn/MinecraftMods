package ru.topbun.data.repository

import ru.topbun.data.api.ModsApi
import ru.topbun.data.api.dto.mods.toEntity
import ru.topbun.data.database.dao.FavoriteDao
import ru.topbun.data.database.entity.FavoriteEntity
import ru.topbun.data.mappers.ModMapper
import ru.topbun.data.saveFile
import ru.topbun.data.storage.DataStoreStorage
import ru.topbun.domain.entity.ConfigEntity
import ru.topbun.domain.entity.IssueEntity
import ru.topbun.domain.entity.mod.ModEntity
import ru.topbun.domain.entity.mod.ModSortType
import ru.topbun.domain.entity.mod.ModType
import ru.topbun.domain.entity.StorageKeys
import ru.topbun.domain.entity.modConfig.ModConfigProvider

class ModRepository(
    private val favoriteDao: FavoriteDao,
    private val api: ModsApi,
    private val modMapper: ModMapper,
    private val dataStore: DataStoreStorage,
    private val configProvider: ModConfigProvider
) {

    suspend fun getApps() = runCatching {
        api.getApps().toEntity(configProvider.getConfig().applicationId)
    }

    suspend fun downloadFile(url: String, filename: String) = runCatching {
        api.downloadFile(url).saveFile(filename)
    }


    suspend fun getMods(
        q: String,
        offset: Int,
        type: ModType?,
        sortType: ModSortType,
        limit: Int = 6,
    ) = runCatching {
        val response = api.getMods(
            q = q,
            skip = offset,
            category = type,
            sortKey = sortType.toString(),
            take = limit,
            appId = configProvider.getConfig().appId
        )
        modMapper.toEntity(response.mods)
    }


    suspend fun getMod(id: Int) = runCatching {
        val mod = api.getMod(id)
        modMapper.toEntity(mod)
    }


    suspend fun getFavoriteSize() = favoriteDao.getFavorites().size

    suspend fun getFavoriteMods(offset: Int, limit: Int = 6) = runCatching {
        val favoriteIds = favoriteDao.getFavorites()
            .drop(offset)
            .take(limit)
            .map { it.modId }
        val mods = mutableListOf<ModEntity>()
        favoriteIds.forEach {
            try {
                val mod = api.getMod(it)
                mods.add(modMapper.toEntity(mod))
            } catch (_: Exception){}
        }
       return@runCatching mods
    }


    suspend fun addFavorite(favorite: FavoriteEntity) {
        val oldFavorite = favoriteDao.getFavorite(favorite.modId)
       favoriteDao.addFavorite(favorite.copy(id = oldFavorite?.id ?: 0))
    }

    suspend fun sendIssue(issue: IssueEntity) = runCatching{
        api.createIssue(
            issue = issue,
            id = configProvider.getConfig().appId
        )
    }

    private suspend fun loadConfig() = runCatching {
        val info = api.loadConfig(configProvider.getConfig().appId)
        info.sdk?.let {
            dataStore.save(StorageKeys.AD_IS_ENABLED, info.sdk.isAdEnabled.toString())
            dataStore.save(StorageKeys.APPLOVIN_OPEN, info.sdk.applovinOpen)
            dataStore.save(StorageKeys.APPLOVIN_INTER, info.sdk.applovinInter)
            dataStore.save(StorageKeys.APPLOVIN_NATIVE, info.sdk.applovinNative)
            dataStore.save(StorageKeys.YANDEX_OPEN, info.sdk.yandexOpen)
            dataStore.save(StorageKeys.YANDEX_INTER, info.sdk.yandexInter)
            dataStore.save(StorageKeys.YANDEX_NATIVE, info.sdk.yandexNative)
        }
    }

    suspend fun getConfig(): ConfigEntity {
        loadConfig()

        val isAdEnabled = dataStore.get(StorageKeys.AD_IS_ENABLED, null)?.toBoolean() ?: true
        val applovinOpen = dataStore.get(StorageKeys.APPLOVIN_OPEN, null)
        val applovinInter = dataStore.get(StorageKeys.APPLOVIN_INTER, null)
        val applovinNative = dataStore.get(StorageKeys.APPLOVIN_NATIVE, null)
        val yandexOpen = dataStore.get(StorageKeys.YANDEX_OPEN, null)
        val yandexInter = dataStore.get(StorageKeys.YANDEX_INTER, null)
        val yandexNative = dataStore.get(StorageKeys.YANDEX_NATIVE, null)
        return ConfigEntity(isAdEnabled, applovinOpen, applovinInter, applovinNative, yandexOpen, yandexInter, yandexNative)
    }

}