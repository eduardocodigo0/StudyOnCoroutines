package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2.callbacks

import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.AndroidVersion
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SequentialNetworkRequestsCallbacksViewModel(
    private val mockApi: CallbackMockApi = mockApi()
) : BaseViewModel<UiState>() {

    private var getAndroidVersionsCall: Call<List<AndroidVersion>>? = null
    private var getAndroidFeaturesCall: Call<VersionFeatures>? = null

    fun perform2SequentialNetworkRequest() {
        uiState.value = UiState.Loading

        getAndroidVersionsCall = mockApi.getRecentAndroidVersions()

        getAndroidVersionsCall!!.enqueue(object : Callback<List<AndroidVersion>> {
            override fun onResponse(
                call: Call<List<AndroidVersion>>,
                response: Response<List<AndroidVersion>>
            ) {
                if (response.isSuccessful) {
                    val mostRecentVersion = response.body()!!.last()
                    getAndroidFeaturesCall =
                        mockApi.getAndroidVersionFeatures(mostRecentVersion.apiLevel)
                    getAndroidFeaturesCall!!.enqueue(object : Callback<VersionFeatures> {
                        override fun onResponse(
                            call: Call<VersionFeatures>,
                            response: Response<VersionFeatures>
                        ) {
                            if (response.isSuccessful) {
                                val featuresOfMostRecentAndroidVersion = response.body()!!
                                uiState.value = UiState.Success(featuresOfMostRecentAndroidVersion)

                            } else {
                                uiState.value = UiState.Error("A segunda chamada a rede falhou")
                            }
                        }

                        override fun onFailure(call: Call<VersionFeatures>, t: Throwable) {
                            uiState.value = UiState.Error("Deu ruim no segundo CallBack")
                        }

                    })
                } else {
                    uiState.value = UiState.Error("A chamada a rede falhou")
                }
            }

            override fun onFailure(call: Call<List<AndroidVersion>>, t: Throwable) {
                uiState.value = UiState.Error("Deu ruim no CallBack")
            }

        })


    }

    override fun onCleared() {
        super.onCleared()

        getAndroidFeaturesCall?.cancel()
        getAndroidVersionsCall?.cancel()
    }
}