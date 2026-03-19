package pion.tech.pionbase.util

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.util.Result

/**
 * Extension functions to reduce boilerplate code for data handling (API and local operations)
 */

sealed interface UiState<out T> {
    data object None : UiState<Nothing>

    data object Loading : UiState<Nothing>

    data class Success<T>(
        val data: T,
    ) : UiState<T>

    data class Error(
        val exception: Throwable,
    ) : UiState<Nothing>
}

/**
 * Extension function to handle API calls with automatic state management
 */
inline fun <T, R> ViewModel.handleApiCall(
    stateFlow: MutableStateFlow<UiState<T>>,
    crossinline apiCall: suspend () -> Flow<Result<R>>,
    crossinline transform: (R) -> T,
    skipIfInProgress: Boolean = true,
) {
    launchIO {
        if (skipIfInProgress && (stateFlow.value is UiState.Loading || stateFlow.value is UiState.Success)) {
            return@launchIO
        }

        stateFlow.value = UiState.Loading
        apiCall().collect { result ->
            result
                .onSuccess { data ->
                    stateFlow.value = UiState.Success(transform(data))
                }.onError { exception ->
                    stateFlow.value = UiState.Error(exception)
                }
        }
    }
}

/**
 * Extension function for simple API calls without transformation
 */
inline fun <T> ViewModel.handleApiCall(
    stateFlow: MutableStateFlow<UiState<T>>,
    crossinline apiCall: suspend () -> Flow<Result<T>>,
    skipIfInProgress: Boolean = true,
) {
    handleApiCall(stateFlow, apiCall, { it }, skipIfInProgress)
}

/**
 * Extension function for API calls without needing to track UI state
 * Useful when you just need to process the result without updating UI state
 */
inline fun <R> ViewModel.handleApiCall(
    crossinline apiCall: suspend () -> Flow<Result<R>>,
    crossinline onSuccess: (R) -> Unit = {},
    crossinline onError: (Throwable) -> Unit = {},
) {
    launchIO {
        apiCall().collect { result ->
            result
                .onSuccess { data ->
                    onSuccess(data)
                }.onError { exception ->
                    onError(exception)
                }
        }
    }
}

/**
 * Extension function for API calls with data transformation
 * Useful when you need to transform the data before processing it
 */
inline fun <R, T> ViewModel.handleApiCall(
    crossinline apiCall: suspend () -> Flow<Result<R>>,
    crossinline transform: (R) -> T,
    crossinline onSuccess: (T) -> Unit = {},
    crossinline onError: (Throwable) -> Unit = {},
) {
    launchIO {
        apiCall().collect { result ->
            result
                .onSuccess { data ->
                    onSuccess(transform(data))
                }.onError { exception ->
                    onError(exception)
                }
        }
    }
}

/**
 * Extension function to extract data from Flow<Result<T>> with a default fallback value.
 * This eliminates the need for repetitive when expressions when working with Result types.
 *
 * @param defaultValue The value to return if the Result is not Success
 * @return The data from Result.Success or the defaultValue
 */
suspend fun <T> Flow<Result<T>>.getDataOrDefault(defaultValue: T): T =
    this.first().let { result ->
        if (result is Result.Success) result.data else defaultValue
    }

/**
 * Extension function to extract data from Flow<Result<T>> with null as fallback.
 * Useful for nullable types where null is an acceptable fallback.
 *
 * @return The data from Result.Success or null
 */
suspend fun <T> Flow<Result<T>>.getDataOrNull(): T? =
    this.first().let { result ->
        if (result is Result.Success) result.data else null
    }

/**
 * Extension function to check if Flow<Result<T>> contains successful data.
 *
 * @return true if Result is Success, false otherwise
 */
suspend fun <T> Flow<Result<T>>.isSuccess(): Boolean = this.first() is Result.Success

/**
 * Extension function to get Result<T> directly from Flow<Result<T>>.
 * Useful when you need to work with the Result wrapper itself.
 *
 * @return The Result<T> from the flow
 */
suspend fun <T> Flow<Result<T>>.getResult(): Result<T> = this.first()

/**
 * Extension function to handle UI state changes with loading management
 */
inline fun <T> UiState<T>.handleUiState(
    crossinline onNone: () -> Unit = {},
    crossinline onLoading: () -> Unit = {},
    crossinline onSuccess: (T) -> Unit = {},
    crossinline onError: (Throwable) -> Unit = {},
) {
    when (this) {
        is UiState.None -> onNone()
        is UiState.Loading -> onLoading()
        is UiState.Success -> onSuccess(data)
        is UiState.Error -> onError(exception)
    }
}
