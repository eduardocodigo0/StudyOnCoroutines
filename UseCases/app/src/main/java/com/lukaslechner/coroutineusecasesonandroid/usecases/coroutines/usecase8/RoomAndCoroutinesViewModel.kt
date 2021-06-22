package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase8

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.launch
import java.lang.Exception

class RoomAndCoroutinesViewModel(
    private val api: MockApi,
    private val database: AndroidVersionDao
) : BaseViewModel<UiState>() {

    fun loadData() {
        uiState.value = UiState.Loading.LoadFromDb

        viewModelScope.launch {
            val localVer = database.getAndroidVersions()
            if(localVer.isEmpty()){
                uiState.value = UiState.Error(DataSource.DATABASE, "Tá vazio, parça!")
            }else{
                uiState.value = UiState.Success(DataSource.DATABASE, localVer.mapToUiModelList())
            }

            uiState.value = UiState.Loading.LoadFromNetwork
            try{
                val remoteVer = api.getRecentAndroidVersions()
                remoteVer.forEach {
                    database.insert(it.mapToEntity())
                }
                uiState.value = UiState.Success(DataSource.NETWORK, remoteVer)
            }catch (err: Exception){
                uiState.value = UiState.Error(DataSource.NETWORK, "Essa conexão tá ruim")
            }

        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            database.clear()
        }
    }
}

enum class DataSource(val dataSourceName: String) {
    DATABASE("Database"),
    NETWORK("Network")
}