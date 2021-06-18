package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase4

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.lang.Exception

class VariableAmountOfNetworkRequestsViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequestsSequentially() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val recentVersions = mockApi.getRecentAndroidVersions()
                val versionFeatures = recentVersions.map {
                    mockApi.getAndroidVersionFeatures(it.apiLevel)
                }

                uiState.value = UiState.Success(versionFeatures)
            } catch (err: Exception) {
                uiState.value = UiState.Error("Deu ruim na chama de rede")
            }
        }

    }

    fun performNetworkRequestsConcurrently() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val recentVersions = mockApi.getRecentAndroidVersions()
                val versionFeatures = recentVersions.map {
                    async {
                        mockApi.getAndroidVersionFeatures(it.apiLevel)
                    }
                }
                uiState.value = UiState.Success(versionFeatures.awaitAll())
            } catch (err: Exception) {
                uiState.value = UiState.Error("Deu ruim na chama de rede")
            }
        }

    }
}