package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2.rx

import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class SequentialNetworkRequestsRxViewModel(
    private val mockApi: RxMockApi = mockApi()
) : BaseViewModel<UiState>() {

    private val disposable = CompositeDisposable()

    fun perform2SequentialNetworkRequest() {
        uiState.value = UiState.Loading

        mockApi.getRecentAndroidVersions()
            .flatMap { androidVersions ->
                val recentVersions = androidVersions.last()
                mockApi.getAndroidVersionFeatures(recentVersions.apiLevel)
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { featureVersions ->

                    uiState.value = UiState.Success(featureVersions)
                },
                onError = {
                    uiState.value = UiState.Error("Deu ruim na chamada de rede!")
                }
            )
            .addTo(disposable)


    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}