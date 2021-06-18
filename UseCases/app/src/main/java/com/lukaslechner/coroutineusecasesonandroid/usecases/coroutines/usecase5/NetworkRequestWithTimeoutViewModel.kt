package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase5

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class NetworkRequestWithTimeoutViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest(timeout: Long) {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val recentAndroidVersions = withTimeout(timeout){
                    mockApi().getRecentAndroidVersions()
                }
                uiState.value = UiState.Success(recentAndroidVersions)

            }catch (err: TimeoutCancellationException){
                uiState.value = UiState.Error("Demorou demais, meu irmão")
            }
            catch (err: Exception){
                uiState.value = UiState.Error("Num vai dar não")
            }
        }
    }

}