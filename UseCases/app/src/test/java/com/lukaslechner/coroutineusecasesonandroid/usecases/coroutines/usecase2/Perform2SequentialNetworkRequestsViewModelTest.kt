package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockAndroidVersions
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesAndroid10
import com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase1.PerformSingleNetworkRequestViewModel
import com.lukaslechner.coroutineusecasesonandroid.util.MainCoroutineScopeRule
import org.junit.*

import org.junit.Assert.*
import org.junit.rules.TestRule

class Perform2SequentialNetworkRequestsViewModelTest {
    private val receivedUiStates = mutableListOf<UiState>()

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }
    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Test
    fun `perform 2 Sequential Network Request SUCCESS`() {

        val fakeAPI = FakeSuccessAPI2()
        val vm = Perform2SequentialNetworkRequestsViewModel(fakeAPI)

        observeViewModel(vm)

        vm.perform2SequentialNetworkRequest()

        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(mockVersionFeaturesAndroid10)
            ),
            receivedUiStates
        )

    }

    @Test
    fun `perform 2 Sequential Network Request First SUCCESS Second FAIL `() {

        val fakeAPI = FakeSecondFailAPI()
        val vm = Perform2SequentialNetworkRequestsViewModel(fakeAPI)

        observeViewModel(vm)

        vm.perform2SequentialNetworkRequest()

        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Error("Deu ruim na chamada de rede")
            ),
            receivedUiStates
        )

    }


    @Test
    fun `perform 2 Sequential Network Request First FAIL Second SUCCESS`() {

        val fakeAPI = FakeFirstFailAPI()
        val vm = Perform2SequentialNetworkRequestsViewModel(fakeAPI)

        observeViewModel(vm)

        vm.perform2SequentialNetworkRequest()

        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Error("Deu ruim na chamada de rede")
            ),
            receivedUiStates
        )

    }

    private fun observeViewModel(viewModel: Perform2SequentialNetworkRequestsViewModel) {
        viewModel.uiState().observeForever { state ->
            state?.let {
                receivedUiStates.add(it)
            }
        }
    }
}