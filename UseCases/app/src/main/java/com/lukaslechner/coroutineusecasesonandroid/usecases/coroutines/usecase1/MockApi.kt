package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase1

import com.google.gson.Gson
import com.lukaslechner.coroutineusecasesonandroid.mock.createMockApi
import com.lukaslechner.coroutineusecasesonandroid.mock.mockAndroidVersions
import com.lukaslechner.coroutineusecasesonandroid.utils.MockNetworkInterceptor

fun mockApi() =
    createMockApi(
        MockNetworkInterceptor()
            .mock(
                "http://localhost/recent-android-versions",
                "Deu ruim no server",//Gson().toJson(mockAndroidVersions),
                500,//200,
                1500
            )
    )