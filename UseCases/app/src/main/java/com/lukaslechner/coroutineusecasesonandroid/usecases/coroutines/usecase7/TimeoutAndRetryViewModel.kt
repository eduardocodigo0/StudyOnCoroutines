package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase7

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import kotlinx.coroutines.*
import timber.log.Timber

class TimeoutAndRetryViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {


    private suspend fun <T> retry(
        numberOfRetryies: Int = 2,
        minDelay: Long = 100,
        maxDelay: Long = 1000,
        factor: Double = 2.0,
        action: suspend () -> T
    ): T {

        var currentDelay = minDelay
        repeat(numberOfRetryies) {
            try {
                return action()

            } catch (ex: Exception) {
                Timber.e(ex)
            }
            delay(currentDelay)
            currentDelay = currentDelay.times(factor).toLong().coerceAtMost(maxDelay)
        }

        return action()
    }


    fun performNetworkRequest() {
        uiState.value = UiState.Loading

        val timeout = 1000L

        val api27features = viewModelScope.async {
            retry {
                withTimeout(timeout) {
                    api.getAndroidVersionFeatures(27)
                }
            }
        }

        val api28features = viewModelScope.async {
            retry {
                withTimeout(timeout) {
                    api.getAndroidVersionFeatures(28)
                }
            }
        }


        viewModelScope.launch {
            try {
                val features = awaitAll(api27features, api28features)
                uiState.value = UiState.Success(features)
            }catch (err: Exception){

                uiState.value = UiState.Error("Não vai dar não!")
            }
        }


    }
}