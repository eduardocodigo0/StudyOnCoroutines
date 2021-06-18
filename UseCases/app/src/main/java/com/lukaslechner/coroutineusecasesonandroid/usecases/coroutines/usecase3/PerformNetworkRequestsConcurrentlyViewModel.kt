package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase3

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class PerformNetworkRequestsConcurrentlyViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequestsSequentially() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val oreoFeatures = mockApi.getAndroidVersionFeatures(27)
                val piFeatures = mockApi.getAndroidVersionFeatures(28)
                val android10Features = mockApi.getAndroidVersionFeatures(29)

                val versionFeatures = listOf(oreoFeatures, piFeatures, android10Features)
                uiState.value = UiState.Success(versionFeatures)

            } catch (err: Exception) {
                uiState.value = UiState.Error("Problema na rede!")
            }
        }
    }

    fun performNetworkRequestsConcurrently() {
        uiState.value = UiState.Loading

        val deferredOreo = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(27)
        }

        val deferredPie = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(28)
        }

        val deferred10 = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(29)
        }

        viewModelScope.launch {
            try {

                val versionFeatures = awaitAll(deferredOreo, deferredPie, deferred10)

                uiState.value = UiState.Success(versionFeatures)
            } catch (err: java.lang.Exception) {
                uiState.value = UiState.Error("Problema na rede!")
            }

        }
    }
}