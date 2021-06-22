package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase14

import com.lukaslechner.coroutineusecasesonandroid.mock.AndroidVersion
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class AndroidVersionRepository(
    private var database: AndroidVersionDao,
    private val scope: CoroutineScope,
    private val api: MockApi = mockApi()
) {

    suspend fun getLocalAndroidVersions(): List<AndroidVersion> {
        return database.getAndroidVersions().mapToUiModelList()
    }

    suspend fun loadAndStoreRemoteAndroidVersions(): List<AndroidVersion> {

        return scope.async {
            val recentversions = api.getRecentAndroidVersions()
            Timber.d("Versoes recentes do android foram carregadas")

            recentversions.forEach { ver ->
                Timber.d("Inseriu -> $ver no banco")
                database.insert(ver.mapToEntity())
            }
            recentversions
        }.await()
    }

    fun clearDatabase() {
        scope.launch {
            database.clear()
        }
    }
}