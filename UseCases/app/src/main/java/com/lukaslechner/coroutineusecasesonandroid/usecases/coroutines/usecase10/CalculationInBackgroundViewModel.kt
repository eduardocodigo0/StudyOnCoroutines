package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase10

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.math.BigInteger
import kotlin.system.measureTimeMillis

class CalculationInBackgroundViewModel : BaseViewModel<UiState>() {

    fun performCalculation(factorialOf: Int) {
        uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                var result = BigInteger.ONE
                var resultString = ""

                val computationDuration = measureTimeMillis {
                    result = calculateFactorial(factorialOf)
                }

                val conversionDuration = measureTimeMillis {
                    resultString = result.toString()
                }

                uiState.postValue(UiState.Success(resultString, computationDuration, conversionDuration))
            } catch (ex: Exception) {
                uiState.postValue(UiState.Error("Falhou"))
            }

        }
    }

    private fun calculateFactorial(number: Int): BigInteger {
        var factorial = BigInteger.ONE

        (1..number).forEach {
            factorial = factorial.multiply(it.toBigInteger())
        }

        return factorial
    }
}