package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase13

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception
import kotlin.coroutines.coroutineContext

class ExceptionHandlingViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun handleExceptionWithTryCatch() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val oreo = api.getAndroidVersionFeatures(27)
            } catch (err: Exception) {
                uiState.value = UiState.Error("Deu erro no request -> $err")
            }
        }


    }

    fun handleWithCoroutineExceptionHandler() {
        uiState.value = UiState.Loading

        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            uiState.value = UiState.Error("Deu erro no request")
        }


        viewModelScope.launch(exceptionHandler) {
            launch {
                api.getAndroidVersionFeatures(27)
            }

        }
    }

    fun showResultsEvenIfChildCoroutineFails() {
        uiState.value = UiState.Loading
        viewModelScope.launch {

           val oreo =  async() {
                api.getAndroidVersionFeatures(27)
            }

            val pie =  async {
                api.getAndroidVersionFeatures(28)
            }

            val a10 =  async {
                api.getAndroidVersionFeatures(29)
            }

            val versions = listOf(oreo, pie, a10).map {
                try{
                    it.await()
                }catch (err: Exception){
                    if(err is CancellationException){
                        throw err
                    }
                    Timber.e("erro na versao")
                    null
                }

            }.filterNotNull()



            uiState.value = UiState.Success(versions as List<VersionFeatures>)

        }

    }
}