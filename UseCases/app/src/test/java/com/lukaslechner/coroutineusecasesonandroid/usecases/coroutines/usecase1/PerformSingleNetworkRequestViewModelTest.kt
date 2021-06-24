package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase1

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockAndroidVersions
import com.lukaslechner.coroutineusecasesonandroid.util.MainCoroutineScopeRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*

import org.junit.Assert.*
import org.junit.rules.TestRule

class PerformSingleNetworkRequestViewModelTest {

    @Before
    fun setUp() {
        //Dispatchers.setMain(dispatecher)
    }

    @After
    fun tearDown() {
        //Dispatchers.resetMain()
        //dispatecher.cleanupTestCoroutines()
    }

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()
    //val dispatecher = TestCoroutineDispatcher()
    private val receivedUiStates = mutableListOf<UiState>()

    @Test
    fun `should return success when network request is sucessful`() {
        //Arrange


        val fakeAPi = FakeSuccessApi()
        val viewModel = PerformSingleNetworkRequestViewModel(fakeAPi)

        observeViewModel(viewModel)

        //Act
        viewModel.performSingleNetworkRequest()

        //Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(mockAndroidVersions)
                ),
            receivedUiStates
        )

    }


    @Test
    fun `should return error when network request fails`() {
        //Arrange

        val fakeAPi = FakeErrorApi()
        val viewModel = PerformSingleNetworkRequestViewModel(fakeAPi)

        observeViewModel(viewModel)

        //Act
        viewModel.performSingleNetworkRequest()

        //Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Error("Deu ruim na rede")
            ),
            receivedUiStates
        )


    }


    private fun observeViewModel(viewModel: PerformSingleNetworkRequestViewModel) {
        viewModel.uiState().observeForever { state ->
            state?.let {
                receivedUiStates.add(it)
            }
        }
    }
}