package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase6

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class RetryNetworkRequestViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    private suspend fun requestVersions() {
        val recentAndroidVersions = api.getRecentAndroidVersions()
        uiState.value = UiState.Success(recentAndroidVersions)
    }

    private suspend fun <T> retry(
        numOfRequests: Int,
        initialDelayMillis: Long = 500,
        maxDelayMillis: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T): T {

        var currentDelay = initialDelayMillis
        repeat(numOfRequests) {
            try {
                return block()
            } catch (ex: Exception) {
                Timber.e(ex)
            }
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)

        }
        delay(currentDelay)
        return block()
    }

    fun performNetworkRequest() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            val numberOfRetries = 4
            try {
                retry(numberOfRetries) {
                    requestVersions()
                }
            } catch (ex: Exception) {
                uiState.value = UiState.Error("Num vai dar não, cumpadi")
            }
        }

    }

}